package ch5

/**
 * Token
 * 所有token的父类。
 * @constructor Create empty Token
 */
open class Token()

/**
 * Token_int
 * 整数
 * @property number
 * @constructor Create empty Token_int
 */
class Token_Int(var number: Int) : Token() {
    override fun toString() = number.toString()
}

/**
 * Token_double
 * 小数
 * @property number
 * @property pos
 * @constructor Create empty Token_double
 */
class Token_Double(var number: Double, var pos: Double = 1.0) : Token() {
    override fun toString() = number.toString()
}

/**
 * Token_word
 * 单词
 * @constructor Create empty Token_word
 */
class Token_Word : Token() {
    private val buffer = StringBuilder()
    var value: String = ""

    /**
     * Append
     * 单词追加一个字符
     * @param c
     */
    fun append(c: Char) {
        buffer.append(c)
    }

    /**
     * Flush
     * 转字符串
     */
    fun flush() {
        value = buffer.toString()
    }

    override fun toString() = value
}

/**
 * Token_operator
 * 操作符
 * @property operator
 * @constructor Create empty Token_operator
 */
class Token_Operator(var operator: operateSymbol) : Token() {
    override fun toString() = operator.toString()
}

/**
 * Token_crlf
 * 换行符
 * @constructor Create empty Token_crlf
 */
class Token_Crlf() : Token() {
    override fun toString() = ";"
}

/**
 * Token_comment
 * 注释
 * @property type
 * @constructor Create empty Token_comment
 */
class Token_Comment(val type: Int) : Token() {
    //注释1单行2多行
    private val buffer = StringBuilder()
    var value: String = ""

    /**
     * Append
     * 插入一个字符
     * @param c
     */
    fun append(c: Char) {
        buffer.append(c)
    }

    /**
     * Flush
     * 转字符串
     */
    fun flush() {
        value = buffer.toString()
    }

    override fun toString() = value
}

/**
 * Token_annotation
 * 注解
 * @constructor Create empty Token_annotation
 */
class Token_Annotation() : Token() {
    init {
        TODO("err")
    }
}

/**
 * Token_string
 * 字符串
 * @property type
 * @constructor Create empty Token_string
 */
open class Token_String(val type: Int) : Token() {
    private val buffer = StringBuilder()
    var value: String = ""

    /**
     * Append
     * 插入一个字符
     * @param c
     */
    fun append(c: Char) {
        buffer.append(c)
    }

    /**
     * Flush
     * 转字符串
     */
    fun flush() {
        value = buffer.toString()
    }

    override fun toString() = value
}

/**
 * Operate symbol
 * 所有操作符的父类
 * @property word
 * @constructor Create empty Operate symbol
 */
open class operateSymbol(val word: String) {
    override fun toString() = word
}

object op_add : operateSymbol("+")//+
object op_minus : operateSymbol("-")//-
object op_mutil : operateSymbol("*")//*
object op_division : operateSymbol("/")///
object op_mod : operateSymbol("%")//%
object op_inc : operateSymbol("++")//++
object op_dec : operateSymbol("--")//--
object op_assign : operateSymbol("=")//=
object op_rightAssign : operateSymbol("=>")//=>
object op_equal : operateSymbol("==")//==
object op_notEqual : operateSymbol("!=")//!=
object op_left : operateSymbol("<<")//<<
object op_right : operateSymbol(">>")//>>
object op_comma : operateSymbol(",")//,
object op_and : operateSymbol("&")//&
object op_or : operateSymbol("|")//|
object op_andAnd : operateSymbol("&&")//&&
object op_orOr : operateSymbol("||")//||
object op_less : operateSymbol("<")//<
object op_lessEqual : operateSymbol("<=")//<=
object op_greater : operateSymbol(">")//>
object op_greaterEqual : operateSymbol(">=")//>=
object op_question : operateSymbol("?")//?
object op_bit : operateSymbol("^")//^
object op_at : operateSymbol("@")//@
object op_pound : operateSymbol("#")//#
object op_excl : operateSymbol("!")//!
object op_dot : operateSymbol(".")//.
object op_dotdot : operateSymbol("..")//..
object op_dotdotdot : operateSymbol("...")//...
object op_colon : operateSymbol(":")//:
object op_backslash : operateSymbol("\\")
object op_bigbracket1 : operateSymbol("{")
object op_bigbracket2 : operateSymbol("}")
object op_bracket1 : operateSymbol("[")
object op_bracket2 : operateSymbol("]")
object op_tinybracket1 : operateSymbol("(")
object op_tinybracket2 : operateSymbol(")")

object Operator_Symbol {
    var op_list = arrayOf(
        op_add,
        op_minus,
        op_mutil,
        op_division,
        op_mod,
        op_inc,
        op_dec,
        op_assign,
        op_rightAssign,
        op_equal,
        op_notEqual,
        op_left,
        op_right,
        op_comma,
        op_dot,
        op_dotdot,
        op_dotdotdot,
        op_and,
        op_andAnd,
        op_or,
        op_orOr,
        op_less,
        op_greater,
        op_lessEqual,
        op_greaterEqual,
        op_question,
        op_bit,
        op_at,
        op_pound,
        op_excl,
        op_colon,
        op_backslash,
        op_bigbracket1,
        op_bigbracket2,
        op_bracket1,
        op_bracket2,
        op_tinybracket1,
        op_tinybracket2
    )

    fun get(op: String): operateSymbol? {
        for (i in op_list)
            if (i.word == op)
                return i
        return null
    }

    fun get(op: operateSymbol) = op.word
}
