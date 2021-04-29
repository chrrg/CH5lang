package ch5.AST

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
object AST {
    //file root
    fun parse(tokens: Tokenizer): ast_container {
        //一个文件就是一个static对象
        return ast_parser(tokens).parse()
    }

    fun getParse(tokens: Tokenizer): ast_parser {
        //一个文件就是一个static对象
        return ast_parser(tokens)
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
            "<<", ">>", "..", "..." -> 5
            "+", "-" -> 4
            "*", "/", "%" -> 3
            "!" -> 2
            ".", "++", "--" -> 1
            else -> throw java.lang.Exception("不支持此操作符:" + node.operator)
        }
    }

    fun OperateType(node: Token_Operator): Int {
        when (node.operator.word) {
            "+", "-", "*", "/", "%", ".", ",", "=", "/=", "*=", "%=", "+=", "-=", "<<=", ">>=", "&=", "^=", "|=", "=>", "/=>", "*=>", "%=>", "+=>", "-=>", "<<=>", ">>=>", "&=>", "^=>", "|=>", "||", "&&", "|", "&", "==", "!=", "^", ">", "<", ">=", "<=", ">>", "<<", "..", "..." -> return 2 //双目运算符
            "++", "--" -> return 4 //两边都可以的单目运算符
            "!" -> return 5 //单目运算符仅右目
//                "?" -> return 3 //三目运算符
        }
        throw Exception("不支持的操作符类型：" + node.operator)
    }

    class ast_parser(val tokens: Tokenizer) {
        override fun toString(): String = tokens.code
        fun parse(): ast_container {
            val s = parseStatic()
            if (!isNull()) throw Exception("err")
            return ast_container(s)
        }

        fun parseStatic(): ast_static {
            val obj = ast_static()
            parseObject(obj)
            return obj
        }

        fun parseClass(): ast_class {
            val obj = ast_class(getWord()!!)
            if (getSingleOperator(op_colon)) {
                obj.extend = getWord()!!
            }
            if (getCrlfNull()) return obj
            if (!getSingleOperator(op_bigbracket1)) throw Exception("err")
            parseObject(obj)
            if (!getSingleOperator(op_bigbracket2)) throw Exception("err")
            return obj
        }

        fun parseType(): ast_type {
            val obj = ast_type(getWord()!!)
            if (getSingleOperator(op_colon)) {
                obj.extend = getWord()!!
            }
            if (getCrlfNull()) return obj
            if (!getSingleOperator(op_bigbracket1)) throw Exception("err")
            parseObject(obj)
            if (!getSingleOperator(op_bigbracket2)) throw Exception("err")
            return obj
        }

        fun parseFrom(): ast_from {
            //import *
            //from "" import
            //import * from ""
            if (!getWord("from")) throw Exception("err")

            getString()?.let {
                val from: ast_from
                if (it.value.contains(':')) {
                    val p = it.value.split(':')
                    from = ast_from(p[0], p[1])
                } else {
                    from = ast_from(it.value, "")
                }
                return from
            } ?: throw Exception("err!")

        }

        fun parseDefine(): ast_define {
            val define: ast_define

            if (getWord("var")) {
                define = ast_var()

            } else if (getWord("val")) {
                define = ast_val()

            } else throw Exception("err")
            while (true) {
                val name = getWord()
                if (name == null) throw Exception("err")

                val v = ast_var_name(name)
                if (isWord()) v.type = getType()
                define.names.add(v)
                if (getSingleOperator(op_comma)) continue
                break
            }
            if (getCrlfNull()) return define
            if (getSingleOperator(op_assign)) {
                define.expr = getExpr()
            }
            if (!getCrlfNull()) throw Exception("err")
            return define
        }

        fun parseImport(): ast_import {
            var from: ast_from? = null
            if (isWord("from")) {
                from = parseFrom()
            }
            if (!getWord("import")) throw Exception("err")
//            if(!isString())throw Exception("err")
            //import *
            //import * from "./1.ch5"

            val import = ast_import()
            import.from = from
            if (getOperator(op_mutil)) {
                import.isAll = true
                if (getCrlfNull()) return import
            } else {
                while (true) {
                    import.arr.add(getWords()!!)
                    if (!getOperator(op_comma)) break
                }
                if (getCrlfNull()) return import
            }
            if (isWord("from")) {
                if (import.from != null) throw Exception("err")
                import.from = parseFrom()
            }
            if (!getCrlfNull()) throw Exception("err")
            return import
        }

        private fun parseObject(obj: ast_object) {
            while (true) {
                val token = tokens.next()
                token ?: return
                when (token) {
                    is Token_Comment -> {
                    }
                    is Token_Annotation -> {
                    }
                    is Token_Crlf -> {
                    }
                    is Token_Word -> {
                        val keyword = token.value
                        when (keyword) {
                            "class" -> {
                                obj.addStatement(parseClass())
//                                obj.addClass(parseClass())
                            }
                            "type" -> {
                                obj.addStatement(parseType())
                            }
                            "import", "from" -> {
                                tokens.prev()
                                obj.addStatement(parseImport())
                            }
                            "var", "val" -> {
                                tokens.prev()
                                obj.addStatement(parseDefine())
                            }
                            "fun" -> {
                                obj.addStatement(parseFun())
                            }
                            "init" -> {
                                tokens.prev()
                                obj.addStatement(parseFun())
                            }
                            "free" -> {
                                tokens.prev()
                                obj.addStatement(parseFun())
                            }
                            else -> {
                                throw Exception("err keyword " + keyword)
                            }
                        }
                    }
                    is Token_Operator -> {
                        when (token.operator) {
                            op_bigbracket2 -> {
                                tokens.prev()
                                return
                            }
                            else -> {
                                throw Exception("Err Operator " + token.operator.javaClass.simpleName)
                            }
                        }
                    }
                    else -> throw Exception("Err" + token.javaClass.simpleName)
                }
            }
        }

        fun parseIf(): ast_if {
            val condition = parseExprBody()
            var trueBranch: ast_expr? = null
            var falseBrach: ast_expr? = null
            trueBranch = parseExprBody()
            val token2 = tokens.next()
            if (token2 is Token_Word && token2.value == "else") {
                falseBrach = parseExprBody()
            } else {
                tokens.prev()
            }

            return ast_if(condition, trueBranch, falseBrach)
        }

        fun parseFor(): ast_for {
            if (getOperator(op_bigbracket1)) {//死循环
                /**
                 * for{
                 *  a++
                 * }
                 */
                tokens.prev()
                return ast_for(ast_expr(), parseExprBody(), null)
            }
            val condition = parseExprBody()
            var trueBranch: ast_expr? = null
            var falseBrach: ast_expr? = null
            trueBranch = parseExprBody()
            val token2 = tokens.next()
            if (token2 is Token_Word && token2.value == "else") {
                falseBrach = parseExprBody()
            } else {
                tokens.prev()
            }

            return ast_for(condition, trueBranch, falseBrach)
        }

        fun getWords(): ast_words? {
            val word = getWord()
            word ?: return null
            val words = ast_words(word)
            if (!getSingleOperator(op_dot)) return words
            words.next = getWords()
            return words
        }

        private fun isOperator(op: operateSymbol): Boolean {
            tokens.next()?.let {
                tokens.prev();
                if (it is Token_Operator && it.operator === op) return true
            }
            return false
        }

        private fun getOperator(): Token_Operator? {
            tokens.next()?.let {
                if (it is Token_Operator) return it
                tokens.prev()
            }
            return null
        }

        fun getString(): Token_String? {
            tokens.next()?.let {
                if (it is Token_String) return it
                tokens.prev()
            }
            return null
        }

        //        fun isCrlf(): Boolean {
//            tokens.next()?.let {
//                if (it is Token_Crlf)return true
//                tokens.prev()
//                return false
//            }
//            return true
//        }
        fun getCrlf(): Boolean {
            tokens.next()?.let {
                if (it is Token_Crlf) return true
                tokens.prev()
            }
            return false
        }

        fun isNull(): Boolean {
            tokens.next()?.let {
                tokens.prev()
                return false
            } ?: let {
                tokens.prev()
                return true
            }
        }

        fun getCrlfNull(): Boolean {
            tokens.next()?.let {
                if (it is Token_Crlf) return true
                tokens.prev()
                return false
            }
            return true
        }

        fun getSingleOperator(op: operateSymbol): Boolean {
            tokens.next(true)?.let {
                if (it is Token_Operator && it.operator === op) return true
                tokens.prev()
            }
            return false
        }

        fun getOperator(op: operateSymbol): Boolean {
            tokens.next()?.let {
                if (it is Token_Operator && it.operator === op) return true
                tokens.prev()
            }
            return false
        }

        fun isInt(): Boolean {
            tokens.next()?.let {
                tokens.prev();
                if (it is Token_Int) return true
            }
            return false
        }

        fun isDouble(): Boolean {
            tokens.next()?.let {
                tokens.prev();
                if (it is Token_Double) return true
            }
            return false
        }

        fun isString(): Boolean {
            tokens.next()?.let {
                tokens.prev();
                if (it is Token_String) return true
            }
            return false
        }

        fun isWord(): Boolean {
            tokens.next()?.let {
                tokens.prev();
                if (it is Token_Word) return true
            }
            return false
        }

        fun isWord(value: String): Boolean {
            tokens.next()?.let {
                tokens.prev();
                if (it is Token_Word && it.value == value) return true
            }
            return false
        }

        private fun getWord(): Token_Word? {
            tokens.next()?.let {
                if (it is Token_Word) return it
                tokens.prev()
            }
            return null
        }

        private fun getWord(value: String): Boolean {
            tokens.next()?.let {
                if (it is Token_Word && it.value == value) return true
                tokens.prev()
            }
            return false
        }

        fun getType(): ast_dataType {
            //int
            //int[]
            //int[10]
            //int[a]
            //int[10][]
            //int<int,int>[10][]
            val name = getWords()
            name ?: throw Exception("需要类型！")
            var type: ast_dataType = ast_typeWord(name)
            if (getSingleOperator(op_less)) {//<
                while (true) {
                    (type as ast_typeWord).generic.types.add(getType())
                    if (!getSingleOperator(op_comma)) break
                }
                if (!getSingleOperator(op_greater)) throw Exception("err")
            }
            while (true) {
                if (!getSingleOperator(op_bracket1)) break
                type = ast_typeArray(type, getExpr()!!)
                if (!getSingleOperator(op_bracket2)) throw Exception("err")
            }
            if (getSingleOperator(op_question)) type.canNull = true
            return type
        }

        fun getExpr(priority: Int = 100, abort_symbol: Array<operateSymbol> = arrayOf()): ast_expr? {//得到表达式
            val token = tokens.next()
            token ?: return null
            var result: ast_expr
            if (token is Token_Word) {
                if (token.value == "var" || token.value == "val") {
                    tokens.prev()
                    return parseDefine()
                }
                if (token.value == "if") {
                    return parseIf()
                }
                if (token.value == "for") {
                    return parseFor()
                }
            }

            when (token) {
                is Token_Annotation -> {
                    return getExpr(priority, abort_symbol)
                }
                is Token_Comment -> {
                    return getExpr(priority, abort_symbol)
                }
                is Token_Crlf -> {
                    return getExpr(priority, abort_symbol)
                }
                is Token_Int -> {
                    result = ast_nodeInt(token)
                }
                is Token_String -> {
                    result = ast_nodeString(token)
                }
                is Token_Double -> {
                    result = ast_nodeDouble(token)
                }
                is Token_Word -> {
                    result = ast_nodeWord(token)
                }
                is Token_Operator -> {//++i +5+6
                    when (token.operator) {
                        in abort_symbol -> {
                            tokens.prev()
                            return null
                        }
                        op_comma -> {
                            result = ast_tuple()
                            result.exprs.add(ast_expr())
                        }
                        op_tinybracket1 -> {
                            val tup = ast_tuple()
                            getExpr(abort_symbol = abort_symbol)?.let { tup.exprs.add(it) }
                            result = tup
                            if (!getOperator(op_tinybracket2)) throw Exception("err")
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
                                    result = ast_binary(token, ast_expr(), getExpr(getPriority(token), abort_symbol)!!)
                                }
                                4, 5 -> {
                                    result = ast_unaryLeft(token, getExpr(getPriority(token), abort_symbol)!!)
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
                val next = if (result is ast_unaryRight) tokens.next(true) else tokens.next()
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
                                if (result is ast_tuple) {
                                    result.exprs.add(getExpr(myPriority, abort_symbol)!!)
                                } else {
                                    val new = ast_tuple()
                                    new.exprs.add(result)
                                    new.exprs.add(getExpr(myPriority, abort_symbol)!!)
                                    result = new
                                }
                            }
                            else -> {
                                val opType = OperateType(next)
                                when (opType) {//k++ i++
                                    4 -> {
                                        result = ast_unaryRight(next, result)
                                    }
                                    2 -> {
                                        result = ast_binary(next, result, getExpr(myPriority, abort_symbol)!!)
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
                        result = ast_call(result, getExpr(15, abort_symbol)!!)
                    }
                    is Token_Crlf -> {
                        tokens.prev()
                        return result
                    }
                    is Token_Comment -> {

                    }
                    else -> {
                        throw Exception("不支持的符号！" + next::class.simpleName)
                    }
                }
            }
        }

        private fun parseFun(): ast_fun {
            val func = ast_fun()

            getWords()?.let {
                func.name = it
            } ?: throw Exception("err")

            //fun a value int
            while (true) {
                val isArr: Boolean
                isArr = getOperator(op_dotdot)
                if (!isWord()) {
                    if (isArr) throw Exception("err!")
                    break
                }
                val paramName = getWord()//参数变量名
                paramName ?: throw Exception("err")
                val name = paramName.value
                val paramObj: ast_param
                if (getOperator(op_assign)) {//fun a value=100,
                    paramObj = ast_funParamDefault(
                        name,
                        isArr,
                        getExpr(abort_symbol = arrayOf(op_comma, op_assign, op_colon))!!
                    )
                } else {//fun a value int
                    paramObj = ast_funParam(name, isArr, getType())
                }
                func.param.add(paramObj)
                if (!getOperator(op_comma)) break
            }

            if (getOperator(op_colon)) {//:
                func.type = getType()
            }
            var exprbody: ast_exprbody? = null
            if (isWord("from")) {
                func.from = parseFrom()
                return func
            }
            if (getOperator(op_assign)) {//=就是return
                //fun a=1
                tokens.prev()
                exprbody = ast_exprbody()
                exprbody.exprs.add(getExpr()!!)

            } else if (isOperator(op_bigbracket1)) {
                //fun a{}
//                tokens.prev()
                exprbody = parseExprBody()
            }
            func.exprbody = exprbody
            return func
        }

        private fun parseExprBody(): ast_exprbody {//解析函数体
            val exprbody = ast_exprbody()

            if (!getOperator(op_bigbracket1)) {
                exprbody.exprs.add(getExpr()!!)
                return exprbody
            }
            while (true) {
                if (isNull()) throw Exception("err except}")
                if (getCrlf()) continue

                val tmp = getExpr()
                tmp?.let {
                    exprbody.exprs.add(it)
                }
                if (getOperator(op_bigbracket2)) break
                if (tmp == null) throw Exception("err!")
            }
            return exprbody
        }

    }
}

