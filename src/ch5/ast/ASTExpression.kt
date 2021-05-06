package ch5.ast

import ch5.*
import java.util.*
import kotlin.test.todo


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
 * 函数内使用的var和val
 */
class ASTInnerVar : ASTExpression(){
    var const =false
    val names = ArrayList<ASTVarName>()
    var expr: ASTExpression? = null
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(if (const)"val " else "var ")
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

open class ASTExpression : ASTInner()//表达式  函数内使用
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
 * 一组函数内语句的集合
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

class ASTBinary(val op: Token_Operator, val left: ASTExpression, val right: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return left.toString() + op.toString() + right.toString()
    }
}

object ASTVoid : ASTExpression() {
    override fun toString(): String {
        return "void"
    }
}

class ASTNodeString(var value: Token_String) : ASTExpression() {
    override fun toString() = "\"$value\""
}//字符串常量字面量节点

class ASTNodeInt(var value: Token_Int) : ASTExpression() {
    override fun toString() = value.toString()
}//整数常量字面量节点

class ASTNodeDouble(var value: Token_Double) : ASTExpression() {
    override fun toString() = value.toString()
}//小数常量字面量节点

class ASTNodeWord(var value: Token_Word) : ASTExpression() {
    override fun toString() = value.toString()
}//单词节点

class ASTUnaryLeft(val operator: Token_Operator, val value: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return "$operator$value"
    }
}

class ASTUnaryRight(val operator: Token_Operator, val value: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return "$value$operator"
    }
}

class ASTCall(val caller: ASTExpression, val value: ASTExpression) : ASTExpression() {
    override fun toString(): String {
        return "$value $caller"
    }
}