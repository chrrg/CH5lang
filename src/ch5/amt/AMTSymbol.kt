package ch5.amt


open class AmtConst
class AmtConstUtf8String(val value: String) : AmtConst()
class AmtConstGBKString(val value: String) : AmtConst()
open class AmtPool<T> {
    protected val list = arrayListOf<T>()
    fun add(item: T): T {
        list.add(item)
        return item
    }

    operator fun iterator() = list.iterator()
}

class AmtConstPool : AmtPool<AmtConst>()//全局应用程序的常量池

class AmtImportDll(val name: String, val path: String)//全局导入的动态链接库
class AmtImportDllPool : AmtPool<AmtImportDll>()//全局导入的dll池

class AmtVariable(val name: String, val changeable: Boolean, val defaultValue: Int)//定义在对象或类中的变量或常量 存储在堆中

class AmtVariablePool : AmtPool<AmtVariable>()
class AmtCode
class AmtCodePool : AmtPool<AmtCode>()
class AmtFun {
    //函数 无名函数
    val codePool = AmtCodePool()
}

class AmtFunPool : AmtPool<AmtFun>()
class AmtDefineFun(val name: String, val func: AmtFun)
class AmtDefineFunPool : AmtPool<AmtDefineFun>()

open class AmtStatic {
    //一个静态对象 只能存在一个
    //静态对象不存储字符串常量 字符串常量在application中存储
    //静态对象存储的变量 var和val
    val variablePool = AmtVariablePool()//对象里面存储的变量池
    val funPool = AmtFunPool()//包含匿名函数？
    val defineFunPool = AmtDefineFunPool()
    val nameSpace = AmtNameSpace()//命名空间
    val initCodePool = AmtCodePool()//初始化对象的时候需要执行的代码列表 比如在初始化时需要执行变量的初始操作就是写在这里
}

class AmtClass : AmtStatic()

class AmtStaticPool : AmtPool<AmtStatic>()//所有的静态对象列表
class AmtClassPool : AmtPool<AmtClass>()//所有的静态对象列表
class AmtNameSpace {
    val staticPool = AmtStaticPool()//这个命名空间下的静态对象池
    val classPool = AmtClassPool()//这个命名空间下的静态对象池
}

class AmtApplication() {
    //一个app的语义树
    //就是一个应用
    val constPool = AmtConstPool()//全局字符串常量池
    val importDllPool = AmtImportDllPool()//导入池
    var entryStatic: AmtStatic? = null//入口对象

}

