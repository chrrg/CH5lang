package ch5.ast

import ch5.*
import java.util.*

open class ASTSyntax //语法 一行代码

open class ASTInner : ASTSyntax()//函数内使用的
open class ASTOuter : ASTSyntax()//函数外使用的
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
}//函数外使用的 包裹语句的容器

class ASTStatic(val name: Token_String) : ASTContainer()
class ASTClass(val name: Token_String) : ASTContainer()
class ASTImport: ASTOuter(){
//    import as from "as.ch5"
    // import "as.ch5" = as.ch5
    override fun toString(): String {

    }
}

//class ASTImport : ASTOuter(){
//    val arr: ArrayList<ASTWords> = arrayListOf()
//    var isAll = false
//    var from: ASTFrom? = null
//    override fun toString(): String {
//        val sb = StringBuilder()
//        sb.append("import ")
//        if (isAll) {
//            sb.append("* ")
//        } else {
//            var sp = ""
//            for (i in arr) {
//                sb.append(sp)
//                sb.append(i.toString())
//                sp = ","
//            }
//            sb.append(' ')
//        }
//        from?.let {
//            sb.append(it.toString())
//        }
//        return sb.toString()
//    }
//}
class ASTWords(private val word: Token_Word) {
    var next: ASTWords? = null
    fun hasNext() = next != null
    override fun toString(): String {
        next?.let {
            return word.value + "." + it
        }
        return word.value
    }
}

class ASTTypeWord(private val value: ASTWords, val generic: ASTTypeGeneric = ASTTypeGeneric()) : ASTDataType() {
    override fun toString(): String {
        return value.toString() + generic.toString() + super.toString()
    }
}

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

class ASTTypeArray(private val word: ASTDataType, private val value: ASTExpression) : ASTDataType() {
    override fun toString(): String {
        return "$word[$value]" + super.toString()
    }
}

open class ASTDataType(var canNull: Boolean = false) {
    override fun toString(): String {
        return if (canNull) "?" else ""
    }
}

class ASTVarName(val name: Token_Word, var type: ASTDataType? = null)
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

open class ASTParam(var name: String, var isArr: Boolean)
class ASTFunParam(name: String, isArr: Boolean, var type: ASTDataType) : ASTParam(name, isArr) {
    override fun toString(): String {
        if (isArr)
            return "..$name $type"
        else
            return "$name $type"
    }
}

class ASTFunParamDefault(name: String, isArr: Boolean, var default: ASTDataType) : ASTParam(name, isArr) {
    override fun toString(): String {
        if (isArr)
            return "..$name=$default"
        else
            return "$name=$default"
    }
}

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
