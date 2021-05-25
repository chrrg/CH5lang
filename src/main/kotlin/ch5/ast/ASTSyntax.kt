package ch5.ast

import ch5.token.*
import java.util.*

/**
 * Ast syntax
 * 语法 一行代码
 * @constructor Create empty Ast syntax
 */
open class ASTSyntax

/**
 * Ast inner
 * 函数内使用的语法
 * @constructor Create empty Ast inner
 */
open class ASTInner : ASTSyntax()

/**
 * Ast outer
 * 函数外使用的语法
 * @constructor Create empty Ast outer
 */
open class ASTOuter : ASTSyntax()

/**
 * Ast container
 * 函数外使用的 包裹语句的容器
 * @constructor Create empty Ast container
 */
open class ASTContainer() : ASTOuter() {
    val container = LinkedList<ASTSyntax>()
    override fun toString(): String {
        val buffer = StringBuffer()
        for (i in container) {
            buffer.append(i.toString())
            buffer.append('\n')
        }
        return buffer.toString()
    }
}

/**
 * Ast static
 * 静态对象
 * @property name
 * @constructor Create empty Ast static
 */
class ASTStatic(val name: Token_String) : ASTContainer()

/**
 * Ast class
 * 类
 * @property name
 * @constructor Create empty Ast class
 */
class ASTClass(val name: Token_String) : ASTContainer()

/**
 * Ast import
 * 导入语句
 * @constructor Create empty Ast import
 */
class ASTImport: ASTOuter(){
//    import as
//    import a "github.com/chrrg/CH5/parse"
//    import "github.com/chrrg/CH5/parse"

    var name=""
    var from:String?=null
    override fun toString(): String {
        from?.let{
            return "import $name \"$it\""
        }?:return "import $name"
    }
}

/**
 * Ast words
 * 链式单词 如 a.b.c
 * @property word
 * @constructor Create empty Ast words
 */
class ASTWords(private val word: Token_Word) {
    var next: ASTWords? = null

    /**
     * Has next
     * 是否有下一个节点
     */
    fun hasNext() = next != null

    /**
     * Get name
     * 获取当前节点的字符串名称
     */
    fun getName() = word.value
    override fun toString(): String {
        next?.let {
            return word.value + "." + it
        }
        return word.value
    }
}

/**
 * Ast type word
 * 类型
 * @property value
 * @property generic
 * @constructor Create empty Ast type word
 */
class ASTTypeWord(val value: ASTWords, val generic: ASTTypeGeneric = ASTTypeGeneric()) : ASTDataType() {
    override fun toString(): String {
        return value.toString() + generic.toString() + super.toString()
    }
}

/**
 * Ast type generic
 * 范型类型
 * @constructor Create empty Ast type generic
 */
class ASTTypeGeneric {
    val types = ArrayList<ASTDataType>()
    override fun toString(): String {
        if (types.size == 0) return ""
        val sp = StringBuilder()
        sp.append("<")
        var comma = ""
        for (i in types) {
            sp.append(comma)
            sp.append(i.toString())
            comma = ","
        }
        sp.append(">")
        return sp.toString()
    }
}

/**
 * Ast type array
 * 数组类型 
 * @Deprecated
 * @property word
 * @property value
 * @constructor Create empty Ast type array
 */
class ASTTypeArray(private val word: ASTDataType, private val value: ASTExpression) : ASTDataType() {
    override fun toString(): String {
        return "$word[$value]" + super.toString()
    }
}

/**
 * Ast data type
 * 数据类型的父类
 * @property canNull
 * @constructor Create empty Ast data type
 */
open class ASTDataType(var canNull: Boolean = false) {
    override fun toString(): String {
        return if (canNull) "?" else ""
    }
}

/**
 * Ast var name
 * 变量名和变量类型
 * @property name
 * @property type
 * @constructor Create empty Ast var name
 */
class ASTVarName(val name: Token_Word, var type: ASTDataType? = null)

/**
 * Ast outer var
 * 函数外定义的变量
 * @constructor Create empty Ast outer var
 */
class ASTOuterVar() : ASTOuter() {
    var const = false;//false 是变量 true说明是常量
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

/**
 * Ast param
 * 定义的参数
 * @property name
 * @property isArr
 * @constructor Create empty Ast param
 */
open class ASTParam(var name: String, var isArr: Boolean)

/**
 * Ast fun param
 * 函数的一个参数
 * @property type
 * @constructor
 *
 * @param name
 * @param isArr
 */
class ASTFunParam(name: String, isArr: Boolean, var type: ASTDataType) : ASTParam(name, isArr) {
    override fun toString(): String {
        if (isArr)
            return "..$name $type"
        else
            return "$name $type"
    }
}

/**
 * Ast fun param default
 * 函数的一个参数 带默认值
 * @property default
 * @constructor
 *
 * @param name
 * @param isArr
 */
class ASTFunParamDefault(name: String, isArr: Boolean, var default: ASTDataType) : ASTParam(name, isArr) {
    override fun toString(): String {
        if (isArr)
            return "..$name=$default"
        else
            return "$name=$default"
    }
}

/**
 * Ast outer fun
 * 定义在函数外面的函数
 * @constructor Create empty Ast outer fun
 */
class ASTOuterFun() : ASTOuter() {
    lateinit var name: ASTWords
    val param = ArrayList<ASTParam>()
    var exprbody: ASTExpression? = null
    var type: ASTDataType? = null

    //    var from: ASTFrom? = null
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("fun ")
        sb.append(name)
        sb.append('(')
        var s = ""
        for (i in param) {
            sb.append(s)
            sb.append(i.toString())
            s = ","
        }
        sb.append(')')
        if (type != null) {
            sb.append(':')
            sb.append(type.toString())
        }
//        if (from != null) {
//            sb.append(' ')
//            sb.append(from.toString())
//        }
        if (exprbody != null)
            sb.append(exprbody.toString())
        return sb.toString()
    }
}
