package ch5.ast

import ch5.*

//import compiler.Tokenizer.Token_Group

/**
 * 语法树
 * 重构 by CH 20201222
 * 文件转语法树
 * 目前无语义
 * 每条表达式需体现元组
 * 输出文件的语法树容器
 */
class AST(private val tokens: Tokenizer) {
    companion object{
        fun OperateType(node: Token_Operator): Int {
            when (node.operator.word) {
                "+", "-", "*", "/", "%", ".", ",", "=", "/=", "*=", "%=", "+=", "-=", "<<=", ">>=", "&=", "^=", "|=", "=>", "/=>", "*=>", "%=>", "+=>", "-=>", "<<=>", ">>=>", "&=>", "^=>", "|=>", "||", "&&", "|", "&", "==", "!=", "^", ">", "<", ">=", "<=", ">>", "<<" -> return 2 //双目运算符
                "++", "--" -> return 4 //两边都可以的单目运算符
                "!" -> return 5 //单目运算符仅右目
//                "?" -> return 3 //三目运算符
            }
            throw Exception("不支持的操作符类型：" + node.operator)
        }
        fun getPriority(node: Token_Operator): Int {
            return when (node.operator.word) {
                "=", "/=", "*=", "%=", "+=", "-=", "<<=", ">>=", "&=", "^=", "|=" -> 17
                "," -> 15
                "=>", "/=>", "*=>", "%=>", "+=>", "-=>", "<<=>", ">>=>", "&=>", "^=>", "|=>" -> 14
                "?", ":" -> 13
                "||" -> 12
                "&&" -> 11
                "|" -> 10
                "^" -> 9
                "&" -> 8
                "==", "!=" -> 7
                ">", ">=", "<", "<=" -> 6
                "<<", ">>" -> 5
                "+", "-" -> 4
                "*", "/", "%" -> 3
                "!" -> 2
                ".", "++", "--" -> 1
                else -> throw Exception("不支持此操作符:" + node.operator)
            }
        }
    }
    fun parse():ASTContainer {
        val result=ASTContainer()
        while (true) {
            val token = tokens.next()
            token ?: break
            when (token) {
                is Token_Comment -> {
                }
                is Token_Annotation -> {
                }
                is Token_Crlf -> {
                }
                is Token_Word -> {
                    when (val keyword = token.value) {
                        "class" -> {
                            val name=tokens.next() as Token_String
                            tokens.needOp(op_bigbracket1)
                            result.container.add(ASTClass(name))
                            tokens.needOp(op_bigbracket2)
                        }
//                        "type" -> {
//                            obj.addStatement(parseType())
//                        }
//                        "import", "from" -> {
//                            tokens.prev()
//                            obj.addStatement(parseImport())
//                        }
                        "var", "val" -> {
                            tokens.prev()
                            result.container.add(parseOuterVar())
                        }
                        "fun" -> {
                            result.container.add(parseOuterFun())
                        }
                        "init" -> {
                            tokens.prev()
                            result.container.add(parseOuterFun())
                        }
                        "free" -> {
                            tokens.prev()
                            result.container.add(parseOuterFun())
                        }
                        else -> {
                            throw Exception("err keyword $keyword")
                        }
                    }
                }
                is Token_Operator -> {
                    when (token.operator) {
                        op_bigbracket2 -> {
                            tokens.prev()
                            break
                        }
                        else -> {
                            throw Exception("Err Operator " + token.operator.javaClass.simpleName)
                        }
                    }
                }
                else -> throw Exception("Err" + token.javaClass.simpleName)
            }
        }
        return result
    }
    fun parseOuterVar(): ASTOuterVar {
        val result=ASTOuterVar()
        when {
            tokens.getWord("var") -> {

            }
            tokens.getWord("val") -> {
                result.const = true
            }
            else -> throw Exception("err")
        }
        return result
    }
    fun parseDataType(): ASTDataType {
        //int
        //int[]
        //int[10]
        //int[a]
        //int[10][]
        //int<int,int>[10][]
        val name = getWords()
        name ?: throw Exception("需要类型！")
        var type: ASTDataType = ASTTypeWord(name)
        if (tokens.getSingleOperator(op_less)) {//<
            while (true) {
                (type as ASTTypeWord).generic.types.add(parseDataType())
                if (!tokens.getSingleOperator(op_comma)) break
            }
            if (!tokens.getSingleOperator(op_greater)) throw Exception("err")
        }
        while (true) {
            if (!tokens.getSingleOperator(op_bracket1)) break
            type = ASTTypeArray(type, ASTVoid)
            if (!tokens.getSingleOperator(op_bracket2)) throw Exception("err")
        }
        if (tokens.getSingleOperator(op_question)) type.canNull = true
        return type
    }
    fun parseOuterFun(): ASTOuterFun {
        val result=ASTOuterFun()
        getWords().let {
            result.name = it!!
        }
        //fun a value int
        while (true) {
            val isArr: Boolean
            isArr = tokens.getOperator(op_dotdot)
            if (!tokens.isWord()) {
                if (isArr) throw Exception("err!")
                break
            }
            val paramName = tokens.getWord()//参数变量名
            paramName ?: throw Exception("err")
            val name = paramName.value
            val paramObj: ASTParam
            if (tokens.getOperator(op_assign)) {//fun a value=100,
                throw java.lang.Exception("ss")
//                paramObj = ASTFunParamDefault(
//                    name,
//                    isArr,
//                    getExpr(abort_symbol = arrayOf(op_comma, op_assign, op_colon))!!//初始值
//                )
            } else {//fun a value int
                paramObj = ASTFunParam(name, isArr, parseDataType())
            }
            result.param.add(paramObj)
            if (!tokens.getOperator(op_comma)) break
        }

        if (tokens.getOperator(op_colon)) {//:
            result.type = parseDataType()
        }
        if (tokens.isWord("from")) {
//            from = parseFrom()
            return result
        }
        if (tokens.getOperator(op_assign)) {//=就是return
            //fun a=1
            tokens.prev()
            result.exprbody = parseExpression()
//            exprbody
//            exprbody.exprs.add(getExpr()!!)

        } else if (tokens.isOperator(op_bigbracket1)) {//fun a{}

            result.exprbody = parseExpression()

        }
        return result
    }
    fun parseExpression(priority: Int = 100, abort_symbol: Array<operateSymbol> = arrayOf()):ASTExpression? {//得到表达式
        val token = tokens.next()
        token ?: return ASTVoid
        var result: ASTExpression
        if(token is Token_Word) {
            if (token.value=="var" || token.value=="val") {
                tokens.prev()
                return parseInnerVar()
            }
            if (token.value=="if") {
                return parseIf()
            }
            if (token.value=="for") {
                return parseFor()
            }
        }

        when (token) {
            is Token_Annotation -> {
                return parseExpression(priority, abort_symbol)
            }
            is Token_Comment -> {
                return parseExpression(priority, abort_symbol)
            }
            is Token_Crlf -> {
                return parseExpression(priority, abort_symbol)
            }
            is Token_Int -> {
                result = ASTNodeInt(token)
            }
            is Token_String -> {
                result = ASTNodeString(token)
            }
            is Token_Double -> {
                result = ASTNodeDouble(token)
            }
            is Token_Word -> {
                result = ASTNodeWord(token)
            }
            is Token_Operator -> {//++i +5+6
                when (token.operator) {
                    op_bigbracket1->{
                        tokens.prev()
                        result=parseExpressionContainer()
                        tokens.needOp(op_bigbracket2)
                        return result
                    }
                    in abort_symbol -> {
                        tokens.prev()
                        return null
                    }
                    op_comma -> {
                        result = ASTTuple()
                        result.tuples.add(ASTVoid)
                    }
                    op_tinybracket1 -> {
                        val tup = ASTTuple()
                        parseExpression(abort_symbol = abort_symbol)?.let { tup.tuples.add(it) }
                        result = tup
                        if (!tokens.getOperator(op_tinybracket2)) throw Exception("err")
                    }
                    op_tinybracket2, op_bracket2, op_bigbracket2 -> {
//                            throw Exception("err")
                        tokens.prev()
                        return null
                    }
                    else -> {
                        val opType = OperateType(token)
                        when (opType) {
                            2 -> {
                                result = ASTBinary(token, ASTVoid, parseExpression(getPriority(token), abort_symbol)!!)
                            }
                            4, 5 -> {
                                result = ASTUnaryLeft(token, parseExpression(getPriority(token), abort_symbol)!!)
                            }
                            else -> {
                                throw Exception("未知的运算符类型：" + token.operator)
                            }
                        }
                    }
                }
            }
            else -> {
                throw Exception("不支持的符号：" + token::class.simpleName)
            }
        }
        while (true) {
            val next = if (result is ASTUnaryRight) tokens.next(true) else tokens.next()
            if (next == null) return result
            when (next) {
                is Token_Operator -> {
                    when (next.operator) {
                        in abort_symbol -> {
                            tokens.prev();return result
                        }
                        op_tinybracket1, op_bracket1, op_bigbracket1 -> {
                            tokens.prev();return result
                        }
                        op_tinybracket2, op_bracket2, op_bigbracket2 -> {
                            tokens.prev();return result
                        }
                    }
                    val myPriority = getPriority(next)
                    if (priority <= myPriority) {
                        tokens.prev();return result
                    }
                    when (next.operator) {
                        op_comma -> {
                            if (result is ASTTuple) {
                                result.tuples.add(parseExpression(myPriority, abort_symbol)!!)
                            } else {
                                val new = ASTTuple()
                                new.tuples.add(result)
                                new.tuples.add(parseExpression(myPriority, abort_symbol)!!)
                                result = new
                            }
                        }
                        else -> {
                            val opType = OperateType(next)
                            when (opType) {//k++ i++
                                4 -> {
                                    result = ASTUnaryRight(next, result)
                                }
                                2 -> {
                                    result = ASTBinary(next, result, parseExpression(myPriority, abort_symbol)!!)
                                }
                                else -> {
                                    throw Exception("不支持2元操作符")
                                }
                            }
                        }
                    }
                }
                is Token_Word, is Token_Int, is Token_Double, is Token_String -> {
                    val myPriority = 16
                    if (priority <= myPriority) {
                        tokens.prev();return result
                    }
                    tokens.prev();
                    result = ASTCall(parseExpression(15, abort_symbol)!!,result)
                }
                is Token_Crlf -> {
                    tokens.prev()
                    return result
                }
                else -> {
                    throw Exception("不支持的符号！" + next::class.simpleName)
                }
            }
        }
    }
    fun parseExpressionContainer():ASTExpressionContainer{
        val result=ASTExpressionContainer()
        if (!tokens.getOperator(op_bigbracket1)) {
            throw java.lang.Exception("except {")
//            result.container.add(parseExpression()!!)
//            return result
        }
        while (true) {
            if (tokens.isNull()) throw Exception("err except}")
            if (tokens.getCrlf()) continue

            val tmp = parseExpression()
            tmp?.let {
                result.container.add(it)
            }
            if (tokens.getOperator(op_bigbracket2)){tokens.prev(); break}
            if (tmp == null) throw Exception("err!")
        }
        return result
    }
    fun parseIf(): ASTIf {
        val condition = parseExpression()!!
        var trueBranch: ASTExpression? = null
        var falseBrach: ASTExpression? = null
        trueBranch = parseExpression()
        tokens.next()
        tokens.skipBlank()
        val token2 = tokens.value()
        if (token2 is Token_Word && token2.value == "else") {
            falseBrach = parseExpression()
        } else {
            tokens.prev()
        }
        return ASTIf(condition, trueBranch, falseBrach)
    }
    fun parseFor(): ASTFor {
        if (tokens.getOperator(op_bigbracket1)) {//死循环
            /**
             * for{
             *  a++
             * }
             */
            tokens.prev()
            return ASTFor(ASTVoid, parseExpression(), null)
        }
        val condition = parseExpression()!!
        var trueBranch: ASTExpression? = null
        var falseBrach: ASTExpression? = null
        trueBranch = parseExpression()
        val token2 = tokens.next()
        if (token2 is Token_Word && token2.value == "else") {
            falseBrach = parseExpression()
        } else {
            tokens.prev()
        }

        return ASTFor(condition, trueBranch, falseBrach)
    }
    fun parseInnerVar(): ASTInnerVar {
        val define: ASTInnerVar

        if (tokens.getWord("var")) {
            define = ASTInnerVar()
        } else if (tokens.getWord("val")) {
            define = ASTInnerVar()

        } else throw Exception("err")
        while (true) {
            val name = tokens.getWord()
            if (name == null) throw Exception("err")

            val v = ASTVarName(name)
            if (tokens.isWord()) v.type = parseDataType()
            define.names.add(v)
            if (tokens.getSingleOperator(op_comma)) continue
            break
        }
        if (tokens.getCrlfNull()) return define
        if (tokens.getSingleOperator(op_assign)) {
            define.expr = parseExpression()
        }
        if (!tokens.getCrlfNull()) throw Exception("err")
        return define
    }
    fun getWords(): ASTWords? {
        val word = tokens.getWord()
        word ?: return null
        val words = ASTWords(word)
        if (!tokens.getSingleOperator(op_dot)) return words
        words.next = getWords()
        return words
    }

}

