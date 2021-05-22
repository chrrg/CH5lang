package ch5.parser

import ch5.ast.*
import ch5.build.*
import ch5.op_assign

object todo : Exception("暂未实现的功能!")

class ParseExpr(val namespace: NameSpace) {
    val app = namespace.app
    val rootCode = CodeBox(app.codeSection)

    fun parseType(expr: ASTExpression?): DataType {

        when (expr) {
            is ASTExpressionContainer -> {
                if (expr.container.size == 0) return DataType("void")
                return parseType(expr.container.last)
            }
            is ASTNodeInt -> {
                return DataType("int")
            }
            is ASTNodeDouble -> {
                TODO()
            }
            is ASTNodeString -> {
                return DataType("string")
            }
            is ASTBinary -> {
                if (expr.op.operator is op_assign) {
                    //todo 获取左边的变量定义的类型
                    val dataType = parseType(expr.right)//赋值表达式的类型
                    if (getVariable(expr.left).type != dataType) throw Exception("类型不匹配")

                    return parseType(expr.right)
                } else {
                    TODO()
                }
            }
            else -> {
                TODO()
            }
        }
    }

    fun getVariable(expr: ASTExpression): DefVar {
        //a.b.c
        if (expr is ASTNodeWord) {
            return namespace.getVariable(expr.value.value)
        } else {
            TODO()
        }
    }

    fun parse(expr: ASTExpression?): CodeBox {
        val code = CodeBox(app.codeSection)
        if (expr == null) return code
        when (expr) {
            is ASTExpressionContainer -> {
                for (i in expr.container) {
                    code.add(parse(i))
                }
            }
            is ASTNodeInt -> {
                mov(EAX, expr.value.number).addTo(code)
            }
            is ASTNodeDouble -> {
                throw Exception("?")
            }
            is ASTNodeString -> {
                mov(EAX, AddrSection(app.codeSection.add(GBKByteArray(expr.value.value)), app.codeSection)).addTo(code)
            }
            is ASTBinary -> {
                if (expr.op.operator is op_assign) {
                    parse(expr.right)
                    val offset = getVariable(expr.left).offset//获取左边的变量
                    //如果是static 那么是[static]+offset
                    if (namespace is StaticObject) {
                        mov(EBX, AddrSection(namespace.staticEntry, namespace.app.codeSection)).addTo(code)
                        val addr = Addr()
                        addr.register = EBX
                        addr.value = offset
                        mov(addr, EAX).addTo(code)//todo
                    }

                } else {
                    throw Exception("不支持的操作符:" + expr.op.operator.word)
                }
            }
            else -> {
                throw Exception("?")
            }
        }
        return code
    }

    fun addTo(codeBox: CodeBox) {
        codeBox.add(rootCode)
    }
}