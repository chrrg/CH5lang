package ch5.token


/**
 * Tokenizer
 * 分词器
 * @property code
 * @constructor Create empty Tokenizer
 */
class Tokenizer(var code: String) {
    //filePath: String
//    private var code:String
//    private var codeFile: CodeFile
    private var index = 0
    private var tokens = arrayListOf<Token>()
    private var wordIndex = 0

    init {
//        code=getFileCode(filePath)
//        codeFile=CodeFile(filePath,code)
    }

    override fun toString() = tokens.toString()

    /**
     * Fetch char
     * 取出一个字符
     * @return
     */
    fun fetchChar(): Char? {
        if (index >= code.length) return null
        return code[index++]
    }

    /**
     * Is blank
     * 是否是空的
     * @param c
     * @return
     */
    fun isBlank(c: Char): Boolean {
        return charArrayOf(' ', '\t').indexOf(c) != -1
    }

    /**
     * Get operator
     * 获取一个操作符
     * @param c
     * @return
     */
    fun getOperator(c: String): operateSymbol? {
        return Operator_Symbol.get(c)
    }

    /**
     * Prev
     * 切换前一个token
     * @return
     */
    fun prev(): Token? {
        if (wordIndex <= 0) return null
        --wordIndex
        if (wordIndex <= 0) return null
        return tokens[wordIndex - 1]
    }

    /**
     * Value
     * 当前指向的token
     * @return
     */
    fun value(): Token? {
        if (wordIndex <= 0 || wordIndex > tokens.size) return null
        return tokens[wordIndex - 1]
    }

    /**
     * Need op
     * 需要某操作符
     * @param op
     */
    fun needOp(op: operateSymbol) {
        val item = this.next()
        if (item is Token_Operator) {
            if (item.operator == op) return
        }
        throw Exception("期待操作符[" + op.word + "]")
    }

    /**
     * Is operator
     * 判断是否是某操作符
     * @param op
     * @return
     */
    fun isOperator(op: operateSymbol): Boolean {
        next()?.let {
            prev()
            if (it is Token_Operator && it.operator === op) return true
        }
        return false
    }

    /**
     * Get operator
     * 获取操作符
     * @return
     */
    fun getOperator(): Token_Operator? {
        next()?.let {
            if (it is Token_Operator) return it
            prev()
        }
        return null
    }

    /**
     * Get string
     * 获取字符串
     * @return
     */
    fun getString(): Token_String? {
        next()?.let {
            if (it is Token_String) return it
            prev()
        }
        return null
    }

    /**
     * Get crlf
     * 获取换行符
     * @return
     */
    fun getCrlf(): Boolean {
        next()?.let {
            if (it is Token_Crlf) return true
            prev()
        }
        return false
    }

    /**
     * Is null
     * 是不是没有了
     * @return
     */
    fun isNull(): Boolean {
        next()?.let {
            prev()
            return false
        } ?: let {
            prev()
            return true
        }
    }

    /**
     * Get crlf null
     * 获取回车换行或者没有了
     * @return
     */
    fun getCrlfNull(): Boolean {
        next()?.let {
            if (it is Token_Crlf) return true
            prev()
            return false
        }
        return true
    }

    /**
     * Get single operator
     * 获取单个字符的操作符
     * @param op
     * @return
     */
    fun getSingleOperator(op: operateSymbol): Boolean {
        next(true)?.let {
            if (it is Token_Operator && it.operator === op) return true
            prev()
        }
        return false
    }

    /**
     * Get operator
     * 获取操作符
     * @param op
     * @return
     */
    fun getOperator(op: operateSymbol): Boolean {
        next()?.let {
            if (it is Token_Operator && it.operator === op) return true
            prev()
        }
        return false
    }

    /**
     * Is int
     * 是否是整数类型
     * @return
     */
    fun isInt(): Boolean {
        next()?.let {
            prev()
            if (it is Token_Int) return true
        }
        return false
    }

    /**
     * Is double
     * 是否是小数类型
     * @return
     */
    fun isDouble(): Boolean {
        next()?.let {
            prev()
            if (it is Token_Double) return true
        }
        return false
    }

    /**
     * Is string
     * 是否是字符串
     * @return
     */
    fun isString(): Boolean {
        next()?.let {
            prev()
            if (it is Token_String) return true
        }
        return false
    }

    /**
     * Is word
     * 是否是单词
     * @return
     */
    fun isWord(): Boolean {
        next()?.let {
            prev()
            if (it is Token_Word) return true
        }
        return false
    }

    /**
     * Is word
     * 是否是某单词
     * @param value
     * @return
     */
    fun isWord(value: String): Boolean {
        next()?.let {
            prev()
            if (it is Token_Word && it.value == value) return true
        }
        return false
    }

    /**
     * Get word
     * 获取单词
     * @return
     */
    fun getWord(): Token_Word? {
        next()?.let {
            if (it is Token_Word) return it
            prev()
        }
        return null
    }

    /**
     * Get word
     * 获取某单词
     * @param value
     * @return
     */
    fun getWord(value: String): Boolean {
        next()?.let {
            if (it is Token_Word && it.value == value) return true
            prev()
        }
        return false
    }

    /**
     * Is blank
     * 是否是空白符
     * @return
     */
    fun isBlank(): Boolean {
        val value = this.value()
        return value is Token_Comment || value is Token_Crlf
    }

    /**
     * Skip blank
     * 跳过所有连的空白符
     */
    fun skipBlank() {
        while (isBlank()) this.next()
    }

    /**
     * Next
     * 解析下一个
     * @param singleOperator
     * @return
     */
    fun next(singleOperator: Boolean = false): Token? {//singleOperator 单符号模式
        if (wordIndex > tokens.size) return null
        if (wordIndex < tokens.size) return tokens[wordIndex++]
        if (index >= code.length) return null
        val token = fetchTokenBody(singleOperator)
        if (token != null) {
            wordIndex++
            if (tokens.size < wordIndex) tokens.add(token)
            index--//默认消耗一个字符需补上
            if (token is Token_Word) token.flush()
            if (token is Token_String) token.flush()
            if (token is Token_Comment) token.flush()
        }
        return token
    }

    /**
     * Next1
     * 解析下一个
     * @return
     */
    fun next1(): Token? {
        return next(false)
    }

    /**
     * Next2
     * 解析下一个 但如果遇到操作符纸解析单个字符的操作符。
     * @return
     */
    fun next2(): Token? {
        return next(true)
    }

    private fun fetchTokenBody(singleOperator: Boolean = false): Token? {
        var token: Token
        while (true) {
            val c = fetchChar()
            if (c == null) {
                index++;return null
            }
            if (c == '\r') continue//兼容
            if (c == '\n' || c == ';') {
                if (tokens.size == 0 || tokens.last() is Token_Crlf) {
                    return fetchTokenBody(singleOperator)
                }
                index++//处理的字符属于自己一部分时需要index++
                return Token_Crlf()
            }
            if (isBlank(c)) continue
            if (c in '0'..'9') {
                token = Token_Int(c.toInt() - 48)
                break
            }
            val op = getOperator(c.toString())
            if (op != null) {
                token = Token_Operator(op)
                if (singleOperator) {
                    index++;return token
                }
                break
            }
            if (c == '\'') {//单引号的字符串
                token = Token_String(1)
                break
            }
            if (c == '"') {//双引号的字符串
                token = Token_String(2)
                break
            }
            token = Token_Word()
            token.append(c)
            break
//            throw Exception("err")
        }
        while (true) {
            var c = fetchChar()
            if (c == null) {
                if (token is Token_String) {
                    throw Exception("err")
                }
                index++
                return token
            }
            if (c == '\r') continue//防止Win32和Linux平台差异
            if (token !is Token_String && token !is Token_Comment) {
                if (c == '\n' || c == ';' || isBlank(c)) {
                    return token
                }
            }
            when (token) {

                is Token_Int -> {
                    when (c) {
                        in '0'..'9' -> token.number = token.number * 10 + c.toInt() - 48
                        '.' -> {
                            token = Token_Double(token.number.toDouble())
                        }
                        else -> {
                            return token
                        }
                    }
                }
                is Token_Double -> {
                    when (c) {
                        '.' -> {
                            index--
                            token = Token_Int(token.number.toInt())
                            return token
                        }
                        in '0'..'9' -> {
                            token.pos *= 0.1
                            token.number += (c.toInt() - 48) * token.pos
                        }
                        else -> {
                            return token
                        }
                    }
                }
                is Token_Operator -> {
                    if (token.operator is op_division && c == '/') {//说明是单行注释
                        token = Token_Comment(1)
                    } else {
                        val op = getOperator(token.operator.word + c)
                        if (op == null) return token
                        token.operator = op
                    }
                }
                is Token_Comment -> {
                    if (token.type == 1) {//单引号
                        if (c == '\n') {
                            return token
                        }
                    } else {//双引号
                        if (c == '*') {//
                            val verify = fetchChar()
                            if (verify == null) throw Exception("err")
                            if (verify == '/') {
                                index++;return token
                            }
                            index--
                        }
                    }
                    token.append(c)
                }
                is Token_String -> {
                    if (token.type == 1) {//单引号
                        if (c == '\'') {
                            index++;return token
                        }
                    } else if (token.type == 2) {//双引号
                        if (c == '\"') {
                            index++;return token
                        }
                        if (c == '\\') {
                            val cc = fetchChar()
                            if (cc == null) throw Exception("err")
                            c = when (cc) {
                                '0' -> 0.toChar()
                                'r' -> '\r'
                                'n' -> '\n'
                                't' -> '\t'
                                else -> c
                            }
                        }
                    }
                    token.append(c)
                }
                is Token_Word -> {
                    if (getOperator(c.toString()) != null) return token
                    token.append(c)
                }
                else -> {
                    throw Exception("err")
                }
            }
        }
    }
}