package compiler.AMT

import compiler.AST.*
import compiler.Pool.ConstantPool
import compiler.Pool.ImportItem
import compiler.Pool.ImportPool

class amt_application{
    lateinit var main: amt_container
    var importPool=ImportPool()
    var constantPool=ConstantPool()

}
class amt_container(val app:amt_application,var ast:ast_container){//每一个是文件的对象
    val main=ast.static
    val func=arrayListOf<amt_fun>()
    val imports= arrayListOf<amt_import>()
    init{
        for(i in ast.static.imports){
            i.from?.let{
                if(it.path.contains(".dll")) {
                    if (it.name != "") {
                        if(i.arr.size>1)throw Exception("err")//import a,b from "user32.dll:func1"
                        if(i.arr[0].hasNext())throw Exception("err")//import a.b from "user32.dll:func1"
                        imports.add(amt_import(i.arr[0].word.value,app.importPool.add(it.path, it.name)))
                    }else {
                        for (i2 in i.arr) {//import a,b from "user32.dll"
                            if(i2.hasNext())throw Exception("err!")//import a.b from "user32.dll"
                            imports.add(amt_import(i2.word.value,app.importPool.add(it.path,i2.word.value)))
                        }
                    }
                }
            }
        }
        for(i in ast.static.funs){

            if(i.name.hasNext())throw Exception("Err")//fun a.b

            var import:amt_import?=null
            val from=i.from
            if(from!=null){
                if(i.funbody!=null)throw Exception("err")
                val myfun=amt_fun(i)
                if(from.name!=""){//fun init from "kernel32.dll:Func1"
                    import=amt_import(from.name,app.importPool.add(from.path,from.name))
                }else{////fun init from "kernel32.dll"
                    import=amt_import(i.name.word.value,app.importPool.add(from.path,i.name.word.value))
                }
                func.add(myfun)
                continue
            }else{
                val myfun=amt_fun(i)
                val funbody=i.funbody
                if(funbody!=null){

                    func.add(myfun)
                }

            }
        }
        link()
    }
    fun link(){//链接

    }
}
class amt_import(val name:String,val item:ImportItem){

}
class amt_fun(){
    var ast: ast_fun?=null
    var returnType:amt_type?=null
    constructor(ast:ast_fun) : this() {
        this.ast=ast
    }
}
open class amt_struct(name:String){//数据类型的基类

}
open class amt_basicType(name:String):amt_struct(name){

}
class amt_type(name:String,var parent:amt_struct): amt_basicType(name) {

}
class amt_class(name:String,var parent:amt_struct): amt_struct(name) {

}

val type_byte=amt_basicType("byte")//1字节
val type_word=amt_basicType("word")//2字节
val type_dword=amt_basicType("dword")//4字节
//val type_qword=amt_basicType("qword")//8字节 64位系统可用

val type_any=amt_type("any",type_dword)//系统位宽 32位下为4字节 64位下为8字节
val type_object=amt_type("object",type_any)//所有数据的基类
val type_Object=amt_class("Object",type_object)//所有对象的基类



class amt_dataType(var type: amt_struct) {
    var ast:amt_struct?=null


}
