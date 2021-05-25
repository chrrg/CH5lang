package ch5.parser

import ch5.ast.*
import ch5.build.*
import ch5.token.*

/**
 * Light scope
 * 轻量作用域，用于在函数内的轻作用域
 * @constructor Create empty Light scope
 */
class LightScope(val func: DefFunction, var parent: LightScope? = null) {
    val varList = arrayListOf<DefLocalVariable>()
    fun findVariable(name: String): DefLocalVariable? {
        varList.find { it.name == name }?.let {
            return it
        }
        parent?.let {
            return it.findVariable(name)
        }
        return null
    }

    fun findFunction(name: String, param: ArrayList<DataType>): DefFunction {
        for (i in func.space!!.funList) {
            if (i.name != name) continue
            if (i.param.size != param.size) continue
            var typeSuccessNum = 0
            for (j in param.indices) {
                if (param[j] != i.param[j].type) {
                    break
                }
                typeSuccessNum++
            }
            if (typeSuccessNum == param.size)
                return i
        }
        throw java.lang.Exception("未定义的方法：$name")
    }
}

/**
 * My static
 * 编译的静态对象
 * @constructor
 *
 * @param app
 */
open class MyStatic(app: Application) : MyClass(app) {
    val entry = DwordSection(0)//全局变量 存静态对象的地址用的
    val entryAddr = AddrSection(entry, app.buildStruct.dataSection)
    // 在静态对象里,每一个函数前面都要调用初始化的函数(判断初始化了就不初始化)
//    fun writeFun() {
//        code.add(Fun())
//    }
    /**
     * Write fun
     * 把函数写到Space的Code上
     */
    fun writeFun() {
        //写入所有fun代码 但是代码前面要加上判断对象是否初始化的函数
        val initStaticCode = Fun()//初始化对象的函数 如果初始化过了会跳过
        mov(EAX, entryAddr).addTo(initStaticCode.code)
        cmp(EAX, 0).addTo(initStaticCode.code)
        val retCode = CodeBox()
        leave().addTo(retCode)
        ret().addTo(retCode)
        jz(retCode).addTo(initStaticCode.code)
        push(heapSize).addTo(initStaticCode.code) // dwBytes是分配堆内存的大小。
        Call(app.alloc).addTo(initStaticCode.code) // 调用分配函数 会消耗掉上面压栈的dword
        mov(entryAddr, EAX).addTo(initStaticCode.code) // 将eax存入对象的地址
        funList.find { it.name == "init" }?.let {
            Call(it.func).addTo(initStaticCode.code)//调用init函数
        }
        //到这里完成了初始化
        code.add(initStaticCode)
        funList.forEach {
            Call(initStaticCode).addTo(it.func.code.getBefore())
            code.add(it.func)
        }
    }

    //解析简单表达式 不能解析return
    fun parse(ast: ASTExpression?): Pair<DataType, CodeBox> {//返回数据类型和代码盒子
        var type: DataType = VoidType
        val codeBox = CodeBox()
        if (ast == null) return Pair(VoidType, codeBox)
        when (ast) {
            is ASTNodeInt -> {
                type = IntType
                mov(EAX, ast.value.number).addTo(codeBox)
            }
            is ASTExpressionContainer -> {
                type = VoidType
                ast.container.forEach {
                    val result = parse(it)
                    type = result.first
                    codeBox.add(result.second)
                }
            }
            else -> {
                throw Exception("无法解析：" + ast.javaClass.simpleName)
            }
        }
        return Pair(type, codeBox)
    }

    fun parseDataType(type: ASTDataType): DataType {
        if (type is ASTTypeWord) {
            val name = type.value.getName()
            return when (name) {
                "int" -> IntType
                "string" -> StringType
                "bool" -> BoolType
                else -> throw java.lang.Exception("无法找到类型")
            }
        }
        throw java.lang.Exception("无法解析类型")
    }

    fun parseVariable(scope: LightScope, name: String): Pair<DataType, Addr> {
        val variable = scope.findVariable(name)
        variable?.let {
            return Pair(it.type!!, Addr(EBP, variable.offset))
        }
        for (i in 0 until scope.func.param.size) {
            val it = scope.func.param[i]
            if (it.name != name) continue
            return Pair(it.type, Addr(EBP, (scope.func.param.size - i) * 4 + 4))//scope.func.param.indexOf(it) * 4
        }
        throw Exception("未定义变量：$name")
    }

    fun parseFunExpression(scope: LightScope, ast: ASTExpression?): Pair<DataType, CodeBox> {
        val function = scope.func
        var type: DataType = VoidType
        val codeBox = CodeBox()
        if (ast == null) return Pair(VoidType, codeBox)
        when (ast) {
            is ASTIf -> {
                val result = parseFunExpression(scope, ast.condition)
                if (result.first !is BoolType) throw Exception("if语句只支持bool类型的表达式")
                result.second.addTo(codeBox)
                cmp(EAX, 0).addTo(codeBox)
                val trueBranch = parseFunExpression(scope, ast.trueBranch)
                val falseBranch = parseFunExpression(scope, ast.falseBrach)
                jz(trueBranch.second, falseBranch.second).addTo(codeBox)
                return if (trueBranch.first == falseBranch.first) {
                    Pair(trueBranch.first, codeBox)
                } else {
                    Pair(VoidType, codeBox)
                }
            }
            is ASTNodeString -> {
                val str = app.buildStruct.dataSection.add(GBKByteArray(ast.value.value))
                lea(EAX, AddrSection(str, app.buildStruct.dataSection)).addTo(codeBox)
                return Pair(StringType, codeBox)
            }
            is ASTNodeWord -> {
                val variable = parseVariable(scope, ast.value.value)
                mov(EAX, variable.second).addTo(codeBox)
                return Pair(variable.first, codeBox)
            }
            is ASTInnerVar -> {
                val variable = DefLocalVariable()
                if (ast.names.size != 1) TODO()
                scope.varList.find { it.name == ast.names[0].name.value }?.let {
                    throw Exception("重复定义的局部变量" + it.name + "！")
                }
                variable.name = ast.names[0].name.value
                ast.names[0].type?.let {
                    type = parseDataType(it)
                }
                ast.expr?.let {
                    val result = parseFunExpression(scope, ast.expr)
                    if (type == VoidType) {
                        type = result.first
                    } else {
                        if (type != result.first) throw Exception("变量${variable.name}类型不匹配")
                    }
                    codeBox.add(result.second)//先运行初始化代码
                }
                if (type == VoidType) throw Exception("变量${variable.name}需要指定类型")
                variable.type = type
                variable.offset = function.func.allocStack(type.getSize())
                mov(Addr(EBP, variable.offset), EAX).addTo(codeBox)//然后赋值到栈中
                scope.varList.add(variable)
                return Pair(type, codeBox)
            }
            is ASTExpressionContainer -> {
                ast.container.forEach {
                    val result = parseFunExpression(scope, it)
                    codeBox.add(result.second)
                }
                return Pair(type, codeBox)
            }
            is ASTTuple -> {
                val tuples = ast.tuples
                if (tuples.size == 1) {
                    val result = parseFunExpression(scope, tuples[0])
                    return Pair(result.first, result.second)
                } else TODO()
            }
            is ASTBinary -> {
                val left = ast.left
                val right = ast.right
                val operator = ast.op.operator
                when (operator) {
                    op_dot -> {
                        if (left == ASTVoid) {
                            if (right is ASTNodeWord) {
                                //调用当前作用域（static或者class）的方法
                                val func = scope.findFunction(right.value.value, arrayListOf())
                                Call(func.func).addTo(codeBox)
                                return Pair(func.type, codeBox)
                            }
                        } else TODO()
                    }
                    op_assign -> {
                        //return todo 需要验证类型是否匹配
                        if (left == ASTVoid) {
                            //return
                            TODO()
                        } else {
                            //赋值
                            val result = parseFunExpression(scope, right)
                            codeBox.add(result.second)
                            if (left is ASTNodeWord) {
                                val variable = parseVariable(scope, left.value.value)
                                if (variable.first != result.first) {
                                    //赋值两边变量类型要相等
                                    throw Exception("赋值变量" + left.value.value + "类型不匹配！")
                                }
                                mov(variable.second, EAX).addTo(codeBox)
                            } else TODO()
                            return Pair(type, codeBox)
                        }
                    }
                    op_add -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        if (result1.first == IntType && result2.first == IntType) {
                            pop(EDX).addTo(codeBox)
                            add(EAX, EDX).addTo(codeBox)
                            return Pair(IntType, codeBox)
                        } else TODO()
                    }
                    op_minus -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        if (result1.first == IntType && result2.first == IntType) {
                            push(EAX).addTo(codeBox)
                            pop(EDX).addTo(codeBox)
                            pop(EAX).addTo(codeBox)
                            sub(EAX, EDX).addTo(codeBox)
                            return Pair(IntType, codeBox)
                        } else TODO()
                    }
                    op_mutil -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        if (result1.first == IntType && result2.first == IntType) {
                            pop(EDX).addTo(codeBox)
                            mul(EDX).addTo(codeBox)
                            return Pair(IntType, codeBox)
                        } else TODO()
                    }
                    op_division -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        if (result1.first == IntType && result2.first == IntType) {
                            push(EAX).addTo(codeBox)
                            pop(EBX).addTo(codeBox)
                            pop(EAX).addTo(codeBox)
                            mov(EDX, 0).addTo(codeBox)
                            div(EBX).addTo(codeBox)
                            return Pair(IntType, codeBox)
                        } else TODO()
                    }
                    op_equal, op_notEqual -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        if ((result1.first == IntType && result2.first == IntType) || result1.first == BoolType && result2.first == BoolType) {
                            pop(EDX).addTo(codeBox)
                            cmp(EAX, EDX).addTo(codeBox)
                            lahf().addTo(codeBox)
                            shr(EAX, 0x0E).addTo(codeBox)
                            if (operator is op_notEqual) xor(EAX, 1).addTo(codeBox)
                            and(EAX, 1).addTo(codeBox)
                            return Pair(BoolType, codeBox)
                        } else TODO()
                    }
                    is op_and, op_or -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        if ((result1.first == IntType && result2.first == IntType) || result1.first == BoolType && result2.first == BoolType) {
                            pop(EDX).addTo(codeBox)
                            if (operator is op_and)
                                and(EAX, EDX).addTo(codeBox)
                            else
                                or(EAX, EDX).addTo(codeBox)
                            return Pair(BoolType, codeBox)
                        } else TODO()
                    }
                    is op_left, op_right -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        if ((result1.first == IntType && result2.first == IntType) || result1.first == BoolType && result2.first == BoolType) {
                            pop(ECX).addTo(codeBox)
                            pop(EAX).addTo(codeBox)
                            if (operator is op_left)
                                shl(EAX, CL).addTo(codeBox)
                            else
                                shr(EAX, CL).addTo(codeBox)
                            return Pair(BoolType, codeBox)
                        } else TODO()
                    }
                    op_greater, op_greaterEqual, op_less, op_lessEqual -> {
                        val result1 = parseFunExpression(scope, left)
                        result1.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        val result2 = parseFunExpression(scope, right)
                        result2.second.addTo(codeBox)
                        if (result1.first == IntType && result2.first == IntType) {
                            pop(EDX).addTo(codeBox)//EAX 大于符号右边的值 EDX:第一个值 左边的值
                            cmp(EAX, EDX).addTo(codeBox)
                            val code1 = CodeBox()
                            val code2 = CodeBox()
                            when (operator) {
                                is op_greater, op_greaterEqual -> {
                                    mov(EAX, 1).addTo(code1)
                                    mov(EAX, 0).addTo(code2)
                                }
                                is op_less, op_lessEqual -> {
                                    mov(EAX, 0).addTo(code1)
                                    mov(EAX, 1).addTo(code2)
                                }
                            }
                            when (operator) {
                                is op_greater, op_less -> jg(code1, code2).addTo(codeBox)
                                is op_greaterEqual, op_lessEqual -> jge(code1, code2).addTo(codeBox)
                            }

                            return Pair(BoolType, codeBox)
                        } else TODO()
                    }
                    op_inc, op_dec -> {
                        if (left !is ASTVoid && right is ASTVoid) {
//                            a++
//                            val result = parseFunExpression(scope, left)
                            if (left is ASTNodeWord) {
                                val variable = parseVariable(scope, left.value.value)
                                if (variable.first !is IntType) TODO()
                                mov(EAX, variable.second).addTo(codeBox)
                                mov(EDX, EAX).addTo(codeBox)
                                when (operator) {
                                    is op_inc -> add(EDX, 1).addTo(codeBox)
                                    is op_dec -> sub(EDX, 1).addTo(codeBox)
                                }
                                mov(variable.second, EDX).addTo(codeBox)
                                return Pair(IntType, codeBox)
                            } else TODO()
                        } else if (left is ASTVoid && right !is ASTVoid) {
//                            ++a
                            if (left is ASTNodeWord) {
                                val variable = parseVariable(scope, left.value.value)
                                if (variable.first !is IntType) TODO()
                                mov(EAX, variable.second).addTo(codeBox)
                                when (operator) {
                                    is op_inc -> add(EAX, 1).addTo(codeBox)
                                    is op_dec -> sub(EAX, 1).addTo(codeBox)
                                }
                                mov(variable.second, EAX).addTo(codeBox)
                                return Pair(IntType, codeBox)
                            } else TODO()
                        } else TODO()
                    }
                    else -> {
                        throw Exception("暂不支持的操作符${ast.op.operator.javaClass.simpleName}：${ast.op.operator.word}")
                    }
                }
            }
            is ASTCall -> {
                val caller = ast.caller
                val expr = ast.value
                if (caller is ASTNodeWord) {
                    val typeArray = arrayListOf<DataType>()
                    if (expr is ASTTuple) {
                        for (i in 0 until expr.tuples.size) {
                            val result = parseFunExpression(scope, expr.tuples[i])
                            result.second.addTo(codeBox)
                            push(EAX).addTo(codeBox)
                            typeArray.add(result.first)
                        }
                    } else if (expr is ASTVoid) {

                    } else {
                        //只有一个参数
                        val result = parseFunExpression(scope, expr)
                        result.second.addTo(codeBox)
                        push(EAX).addTo(codeBox)
                        typeArray.add(result.first)
                    }
                    val func = scope.findFunction(caller.value.value, typeArray)
                    Call(func.func).addTo(codeBox)
                    return Pair(func.type, codeBox)
                } else TODO()
            }
        }
        return parse(ast)
    }

    override fun build() {
        if (!isUsed) return
        data.add(entry)//把静态对象的对象地址写入Space
        //这里开始真正的解析
        //先解析变量的表达式
        //再解析函数的表达式
        //将import的函数替换成自己的函数
        funList.filter { func ->
            func.ast?.exprbody == null
        }.forEach { defFunction ->
            //找到了匹配的函数
            val import = importList.find {
                defFunction.name == it.alias
            } ?: throw Exception("函数${defFunction.name}不允许空方法体！")
            import.func = defFunction
            var offset = 8
            defFunction.param.forEach {
                push(Addr(EBP, offset)).addTo(defFunction.func.code)
                offset += 4
            }
            defFunction.func.setParamSize(defFunction.param.size * 4)
            Invoke(import.ili).addTo(defFunction.func.code)
        }

//        importList.forEach {
//            funList.find { func ->
//                func.name == it.alias && func.ch5.ast?.exprbody == null
//            }?.let { defFunction ->
//                //找到了匹配的函数
//                it.func = defFunction
//                var offset = 8
//                defFunction.param.forEach {
//                    push(Addr(ESP, offset)).addTo(defFunction.func.code)
//                    offset += 4
//                }
//                Invoke(it.ili).addTo(defFunction.func.code)
//            } ?: run {
//                throw Exception("未实现" + it.alias + "函数！")
//            }
//        }

        varList.forEach {
            val result = parse(it.ast?.expr)
            it.type = result.first
            it.initCode = result.second
        }
        funList.forEach {
            val result = parseFunExpression(LightScope(it), it.ast?.exprbody)
            it.type = result.first//todo
            it.func.code.add(result.second)
        }
        //到这里就解析完了
        writeVariableInitCode()
        calcHeapSize()
        writeFun()

//        super.ch5.build()
    }

}