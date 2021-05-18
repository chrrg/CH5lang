package ch5.amt

import ch5.build.BuildSection
import ch5.build.ByteArraySection
import ch5.build.ByteSection
import ch5.build.FixableSection


open class AmtConst

//open class AmtPointer : AmtConst()//指针
//open class AmtValue : AmtConst()//值
//class AmtConstString(var value: String = "") : AmtPointer()
//class AmtByte(var value: Int) : AmtValue()
//class AmtWord(var value: Int) : AmtValue()
//class AmtDword(var value: Int) : AmtValue()
open class AmtPool<T> {
    private val list = arrayListOf<T>()
    fun add(item: T): T {
        list.add(item)
        return item
    }

    fun iterator() = list.iterator()
}

class AmtConstPool : AmtPool<AmtConst>()//全局常量池
class AmtImport()//全局导入
class AmtImportPool : AmtPool<AmtImport>()

class AmtData
class AmtVariable(val name: String, val changeable: Boolean, val defaultValue: Int)

class AmtVariablePool : AmtPool<AmtVariable>() {

}

class AmtFun {//函数 无名函数

}

class AmtFunPool : AmtPool<AmtFun>()
class AmtDefineFun(val name: String, val func: AmtFun)
class AmtDefineFunPool : AmtPool<AmtDefineFun>()

class AmtStatic {//一个静态对象 只能存在一个
    //静态对象不存储字符串常量 字符串常量在application中存储
    //静态对象存储的变量 var和val
    val amtVariablePool = AmtVariablePool()//对象里面存储的变量池
    val amtFunPool = AmtFunPool()//包含匿名函数？
    val amtDefineFunPool = AmtDefineFunPool()
}

class AmtStaticPool : AmtPool<AmtStatic>()//所有的静态对象列表

class AmtApplication {//一个app的语义树
    //就是一个应用
    val constPool = AmtConstPool()//常量池
    val importPool = AmtImportPool()//导入池
    val staticList = AmtStaticPool()//静态对象池
    var entryStatic: AmtStatic? = null//入口对象
}