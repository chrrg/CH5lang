package ch5.amt

import ch5.ast.ASTContainer
import ch5.ast.ASTExpression
import ch5.ast.ASTOuterFun
import ch5.ast.ASTOuterVar


open class AmtConst
class AmtConstUtf8String(val value: String) : AmtConst()
class AmtConstGBKString(val value: String) : AmtConst()
open class AmtPool<T> : Iterable<T> {
    protected val list = arrayListOf<T>()
    fun add(item: T): T {
        list.add(item)
        return item
    }

    override fun iterator(): Iterator<T> = list.iterator()
}

class AmtConstPool : AmtPool<AmtConst>()//全局应用程序的常量池

class AmtImportDll(val name: String, val path: String)//全局导入的动态链接库
class AmtImportDllPool : AmtPool<AmtImportDll>()//全局导入的dll池

class AmtVariable(val name: String, val changeable: Boolean, val defaultValue: Int)//定义在对象或类中的变量或常量 存储在堆中

class AmtVariablePool : AmtPool<AmtVariable>()
open class AmtCode
class AmtCall(func: AmtFun) : AmtCode() {
    init {
        func.use()
    }
}

class AmtCodePool : AmtPool<AmtCode>()
class AmtFun(val name: String, val obj: AmtStatic, val ast: ASTOuterFun) {
    //函数 无名函数
    private val code = AmtExpression(ast.exprbody!!)
    private var isParsed = false
    fun parse() {
//        解析参数和类型
//        这个时候函数定义和变量定义都提升了
        isParsed = true
        code.parse()//解析表达式

    }

    /**
     * 如果这个函数被使用了，就调用use函数，否则不会去真正解析这个函数！
     */
    fun use(): AmtFun {
        if (!isParsed) parse()
        return this
    }
}

class AmtFunPool : AmtPool<AmtFun>()

//class AmtDefineFun(val name: String, val func: AmtFun)//todo 参数 各类型类型 返回类型
//class AmtDefineFunPool : AmtPool<AmtDefineFun>()
class DeferFunPool : AmtPool<() -> Unit>()

open class AmtStatic(val ast: ASTContainer) : AmtNameSpace() {
    //一个静态对象 只能存在一个
    //静态对象不存储字符串常量 字符串常量在application中存储
    //静态对象存储的变量 var和val
    //解析顺序:
    //import
    val variablePool = AmtVariablePool()//对象里面存储的变量池
    val funPool = AmtFunPool()
    val initCodePool = AmtCodePool()//初始化对象的时候需要执行的代码列表 比如在初始化时需要执行变量的初始操作就是写在这里

    init {
        val defer = DeferFunPool()
        //定义提升
        for (i in ast.container) {
            if (i is ASTOuterVar) {
                assert(i.names.size == 1)
                val variable = variablePool.add(AmtVariable(i.names[0].name.value, i.const, 0))
                i.expr?.let {
                    //var定义变量如果有赋值
                    defer.add {
                        initCodePool.add(AmtAssignVariable(variable, AmtExpression(it)))
                    }
                }

            } else if (i is ASTOuterFun) {
                assert(i.param.size == 0)
                val func = funPool.add(AmtFun(i.name.getName(), this, i))
                defer.add {
                    func.parse()
                }
            } else throw Exception("unsupported syntax ${i::class.java.simpleName}")
        }
        for (i in defer) i()
    }
}

/**
 * 表达式计算之后赋值给变量
 */
class AmtAssignVariable(val variable: AmtVariable, val value: AmtCode) : AmtCode()

/**
 * 一个表达式
 */
class AmtExpression(private val expr: ASTExpression) : AmtCode() {

    fun parse() {
        println(expr)
//        if (expr is ASTExpressionContainer){
//            if (expr.container.size==1){
//                parse(expr.container[0])
//            }
//        }
    }
//
//    private fun parse(astExpression: ASTCall) {
//
//    }
}

class AmtClass(ast: ASTContainer) : AmtStatic(ast)

class AmtStaticPool : AmtPool<AmtStatic>()//所有的静态对象列表
class AmtClassPool : AmtPool<AmtClass>()//所有的静态对象列表
open class AmtNameSpace {
    val staticPool = AmtStaticPool()//这个命名空间下的静态对象池
    val classPool = AmtClassPool()//这个命名空间下的静态对象池
}

class AmtApplication {
    //一个app的语义树
    //就是一个应用
    val constPool = AmtConstPool()//全局字符串常量池
    val importDllPool = AmtImportDllPool()//导入池
    var entryStatic: AmtStatic? = null//入口对象
    val codePool = AmtCodePool()
}

