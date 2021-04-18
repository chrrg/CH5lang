package compiler.AST

import compiler.Tokenizer.*
import java.util.*
import kotlin.collections.ArrayList

//class ast_frame//类关键字结构父类
//class ast_expr//体关键字结构父类
//class ast_class
//class ast_type
//class ast_operator
class ast_var_name(val name:Token_Word,var type: ast_dataType?=null)
open class ast_define:ast_expr(){
    val names=ArrayList<ast_var_name>()
    var expr:ast_expr?=null
    override fun toString(): String {
        val sb=StringBuilder()
        var spl=""
        for(i in names){
            sb.append(spl)
            sb.append(i.name)
            i.type?.let{
                sb.append(' ')
                sb.append(it)
            }
            spl=","
        }
        expr?.let{
            sb.append('=')
            sb.append(it)
        }
        return sb.toString()
    }
}
class ast_var:ast_define(){
    override fun toString(): String {
        return "var "+super.toString()
    }
}
class ast_val:ast_define(){
    override fun toString(): String {
        return "val "+super.toString()
    }
}
//class ast_auto
class ast_fun{
    lateinit var name:ast_words
    val param=ArrayList<ast_param>()
    var funbody:ast_funbody?=null
    var type:ast_dataType?=null
    var from:ast_from?=null
    override fun toString(): String {
        val sb=StringBuilder()
        sb.append("fun ")
        sb.append(name)
        sb.append('(')
        var s=""
        for(i in param){
            sb.append(s)
            sb.append(i.toString())
            s=","
        }
        sb.append(')')
        if(type!=null){
            sb.append(':')
            sb.append(type.toString())
        }
        if(from!=null){
            sb.append(' ')
            sb.append(from.toString())
        }
        if(funbody!=null)
            sb.append(funbody.toString())
        return sb.toString()
    }
}

open class ast_param(var name:String,var isArr:Boolean)
class ast_funParam(name:String, isArr:Boolean,var type:ast_dataType):ast_param(name,isArr){
    override fun toString(): String {
        if(isArr)
            return ".."+name+" "+type.toString()
        else
            return name+" "+type.toString()
    }
}
class ast_funParamDefault(name:String, isArr:Boolean,var default:ast_expr):ast_param(name,isArr){
    override fun toString(): String {
        if(isArr)
            return ".."+name+"="+default.toString()
        else
            return name+"="+default.toString()
    }
}
open class ast_object{
    val funs= LinkedList<ast_fun>()
    val imports=LinkedList<ast_import>()
    val defines=LinkedList<ast_define>()
    val classs=LinkedList<ast_class>()
    val types=LinkedList<ast_type>()
    fun addDefine(define:ast_define){
        defines.add(define)
    }
    fun addImport(import:ast_import){
        imports.add(import)
    }
    fun addFun(func:ast_fun){
        funs.add(func)
    }
    fun addClass(clazz:ast_class){
        classs.add(clazz)
    }
    fun addType(type:ast_type){
        types.add(type)
    }
}
open class ast_dataType(var canNull:Boolean=false){
    override fun toString(): String {
        return if(canNull)"?" else ""
    }
}
class ast_typeWord(val value:ast_words,val generic: ast_typeGeneric=ast_typeGeneric()): ast_dataType(){
    override fun toString(): String {
        return value.toString()+generic.toString()+super.toString()
    }
}
class ast_typeGeneric{
    val types=ArrayList<ast_dataType>()
    override fun toString(): String {
        if(types.size==0)return ""
        val sp=StringBuilder()
        sp.append("<")
        var comma=""
        for (i in types){
            sp.append(comma)
            sp.append(i.toString())
            comma=","
        }
        sp.append(">")
        return sp.toString()
    }
}
class ast_typeArray(val word:ast_dataType, val expr:ast_expr): ast_dataType(){
    override fun toString(): String {
        return "$word[$expr]"+super.toString()
    }
}
class ast_static:ast_object()
class ast_class(val name:Token_Word):ast_object(){
    var extend:Token_Word?=null
    override fun toString(): String {
        extend?.let{
            return "class "+name.value+":"+it.value
        }?:let{
            return "class "+name.value
        }
    }
}
class ast_type(val name:Token_Word):ast_object(){
    var extend:Token_Word?=null
    override fun toString(): String {
        extend?.let{
            return "type "+name.value+":"+it.value
        }?:let{
            return "type "+name.value
        }
    }
}
class ast_funbody{
    val exprs=ArrayList<ast_expr>()
    override fun toString(): String {
        val sp=StringBuilder()
        sp.append("{")
        for(i in exprs){
            sp.append(i.toString())
            sp.append(";")
        }
        sp.append("}")
        return sp.toString()
    }
}
class ast_words(val word:Token_Word){
    var next:ast_words?=null
    fun hasNext()=next!=null
    override fun toString(): String {
        next?.let{
            return word.value+"."+it
        }
        return word.value
    }
}

class ast_tuple:ast_expr(){
    val exprs=ArrayList<ast_expr>()
    override fun toString(): String {
        if(exprs.size==1)return exprs[0].toString()
        val sp=StringBuilder()
        sp.append("(")
        var comma=""
        for (i in exprs){
            sp.append(comma)
            sp.append(i.toString())
            comma=","
        }
        sp.append(")")
        return sp.toString()
    }
}
open class ast_expr{
    override fun toString()="void"
}
class ast_nodeString(var value: Token_String):ast_expr(){
    override fun toString()= "\"$value\""
}//字符串常量字面量节点
class ast_nodeInt(var value: Token_Int):ast_expr(){
    override fun toString()=value.toString()
}//整数常量字面量节点
class ast_nodeDouble(var value:Token_Double):ast_expr(){
    override fun toString()=value.toString()
}//小数常量字面量节点
class ast_nodeWord(var value:Token_Word):ast_expr(){
    override fun toString()=value.toString()
}//单词节点

open class ast_operator(var operator: Token_Operator):ast_expr()
class ast_binary(operator: Token_Operator, var left:ast_expr, var right:ast_expr):ast_operator(operator){
    override fun toString(): String {
//        val rr=right
//        if(rr is ast_operator){
//            val r=getPriority(rr.operator)
//        }
        return left.toString()+operator+right.toString()
    }
}//双目运算符
class ast_unaryLeft(operator:Token_Operator,var value:ast_expr):ast_operator(operator){
    override fun toString(): String {
        return operator.toString()+value.toString()
    }
}//右目运算符 a++
class ast_unaryRight(operator:Token_Operator,var value:ast_expr):ast_operator(operator){
    override fun toString(): String {
        return value.toString()+operator
    }
}//左目运算符 --a
class ast_call(var param:ast_expr,var next:ast_expr):ast_expr(){
    override fun toString(): String {
        return param.toString()+" "+next.toString()
    }
}


class ast_container(val static:ast_static){//语法树容器
//
    fun print(){

    }
}
class ast_import(val arr: ArrayList<ast_words> =arrayListOf()){
    var isAll =false
    var from:ast_from?=null
    override fun toString(): String {
        val sb=StringBuilder()
        sb.append("import ")
        if(isAll){
            sb.append("* ")
        }else{
            var sp=""
            for(i in arr){
                sb.append(sp)
                sb.append(i.toString())
                sp=","
            }
            sb.append(' ')
        }
        from?.let{
            sb.append(it.toString())
        }
        return sb.toString()
    }
}
class ast_from(val path:String,val name:String){
    override fun toString(): String {
        if(name==""){
            return "from \"$path\""
        }
        return "from \"$path:$name\""
    }
}

