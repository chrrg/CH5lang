package ch5.ast

import ch5.*
import java.util.*

/**
 * Astif
 *
 * @property condition
 * @property trueBranch
 * @property falseBrach
 * @constructor Create empty If Syntax
 */
class ASTIf(val condition: ASTExpression, val trueBranch: ASTExpression?, val falseBrach: ASTExpression?) :
    ASTExpression() {
    override fun toString(): String {
        val buffer = StringBuilder()
        buffer.append("if ")
        buffer.append(condition.toString())
        buffer.append(' ')
        trueBranch?.let {
            buffer.append(it.toString())
        }
        falseBrach?.let {
            buffer.append(" else ")
            buffer.append(it.toString())
            buffer.append("\n")
        }
        return buffer.toString()
    }
}

/**
 * Astfor
 *
 * @property condition
 * @property trueBranch
 * @property falseBrach
 * @constructor Create empty For Syntax
 */
class ASTFor(val condition: ASTExpression, val trueBranch: ASTExpression?, val falseBrach: ASTExpression?) :
    ASTExpression() {
    override fun toString(): String {
        val buffer = StringBuilder()
        buffer.append("for ")
        buffer.append(condition.toString())
        buffer.append(' ')
        trueBranch?.let {
            buffer.append(it.toString())
        }
        falseBrach?.let {
            buffer.append(" else ")
            buffer.append(it.toString())
            buffer.append("\n")
        }
        return buffer.toString()
    }
}

/**
 * Ast inner var
 * 函数内使用的var和val
 * @constructor Create empty inner var Syntax
 */
class ASTInnerVar : ASTExpression() {
    var const = false
    val names = ArrayList<ASTVarName>()
    var expr: ASTExpression? = null
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(if (const) "val " else "var ")
        var spl = ""
        for (i in names) {
            sb.append(spl)
            sb.append(i.name)
            i.type?.let {
                sb.append(' ')
                sb.append(it)
            }
            spl = ","
        }
        expr?.let {
            sb.append(" = ")
            sb.append(it)
        }
        return sb.toString()
    }
}
//class ASTInnerFun : ASTExpression()
/**
 * Ast expression
 * 所有函数内的表达式
 * @constructor Create empty Ast expression
 */
open class ASTExpression : ASTInner()//表达式  函数内使用

/**
 * Ast tuple
 * 元组
 * @constructor Create empty Ast tuple
 */
class ASTTuple : ASTExpression() {
    //元组
    val tuples = LinkedList<ASTExpression>()
    override fun toString(): String {
        if (tuples.size == 1) return tuples[0].toString()
        val sp = StringBuilder()
        sp.append("(")
        var comma = ""
        for (i in tuples) {
            sp.append(comma)
            sp.append(i.toString())
            comma = ", "
        }
        sp.append(")")
        return sp.toString()
    }
}

/**
 * ASTExpressionContainer
 * 一组函数内语句的集合
 * @constructor Create empty 表达式列表
 */
class ASTExpressionContainer() : ASTExpression() {
    //函数内的多条语句
    val container = LinkedList<ASTExpression>()
    override fun toString(): String {
        val buffer = StringBuffer()
        buffer.append('{')
        buffer.append('\n')
        for (i in container) {
            buffer.append(i.toString())
            buffer.append('\n')
        }
        buffer.append('}')
        return buffer.toString()
    }
}

/**
 * Ast binary
 *
 * @property op 操作服。
 * @property left 左操作数
 * @property right 右操作数
 * @constructor Create empty Ast binary
 */
class ASTBinary(val op: Token_Operator, val left: ASTExpression, val right: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return left.toString() + op.toString() + right.toString()
    }
}

/**
 * Ast void
 * 空语句
 * @constructor Create empty Ast void
 */
object ASTVoid : ASTExpression() {
    override fun toString(): String {
        return "void"
    }
}

/**
 * Ast node string
 * 字符串常量字面量节点
 * @property value
 * @constructor Create empty Ast node string
 */
class ASTNodeString(var value: Token_String) : ASTExpression() {
    override fun toString() = "\"$value\""
}

/**
 * Ast node int
 * 整数常量字面量节点
 * @property value
 * @constructor Create empty Ast node int
 */
class ASTNodeInt(var value: Token_Int) : ASTExpression() {
    override fun toString() = value.toString()
}

/**
 * Ast node double
 * 小数常量字面量节点
 * @property value
 * @constructor Create empty Ast node double
 */
class ASTNodeDouble(var value: Token_Double) : ASTExpression() {
    override fun toString() = value.toString()
}

/**
 * Ast node word
 * 单词节点
 * @property value
 * @constructor Create empty Ast node word
 */
class ASTNodeWord(var value: Token_Word) : ASTExpression() {
    override fun toString() = value.toString()
}//

/**
 * Ast unary left
 * 左目运算
 * @property operator
 * @property value
 * @constructor Create empty Ast unary left
 */
class ASTUnaryLeft(val operator: Token_Operator, val value: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return "$operator$value"
    }
}

/**
 * Ast unary right
 * 右目运算
 * @property operator
 * @property value
 * @constructor Create empty Ast unary right
 */
class ASTUnaryRight(val operator: Token_Operator, val value: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return "$value$operator"
    }
}

/**
 * Ast call
 * 函数调用
 * @property caller
 * @property value
 * @constructor Create empty Ast call
 */
class ASTCall(val caller: ASTExpression, val value: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return "$value $caller"
    }
}