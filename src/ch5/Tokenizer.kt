package ch5
import ch5.*

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
    fun fetchChar(): Char? {
        if (index >= code.length) return null
        return code[index++]
    }

    fun isBlank(c: Char): Boolean {
        return charArrayOf(' ', '\t').indexOf(c) != -1
    }

    fun getOperator(c: String): operateSymbol? {
        return Operator_Symbol.get(c)
    }

    fun prev(): Token? {
        if (wordIndex <= 0) return null
        --wordIndex
        if (wordIndex <= 0) return null
        return tokens[wordIndex - 1]
    }

    fun value(): Token? {
        if (wordIndex <= 0 || wordIndex >= tokens.size) return null
        return tokens[wordIndex - 1]
    }

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

    fun next1(): Token? {
        return next(false)
    }

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