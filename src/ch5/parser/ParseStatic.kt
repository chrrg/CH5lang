package ch5.parser

import ch5.ast.ASTContainer
import ch5.ast.ASTOuterFun
import ch5.ast.ASTOuterVar
import ch5.build.*

class DefFunParam(val name: String) {
    var type: DataType? = null
}

class DefFun(private val ast: ASTOuterFun, private val obj: StaticObject) : Fun(obj.app) {
    val name = ast.name.getName()
    val param = arrayListOf<DefFunParam>()

    fun use(): DefFun {
        obj.useInit()// 解析所在对象的init函数 && 解析var的表达式
        ParseExpr(obj).parse(ast.exprbody).addTo(code)// 解析当前函数
        return this
    }
}

class DefVar(val ast: ASTOuterVar, val obj: StaticObject) {
    val name = ast.names[0].name.value
    val expr = CodeBox(obj.app.codeSection)
    var type: DataType? = null
    var offset = 0

    init {
        assert(ast.names.size == 1)

    }
}

class DefType(val name: String) {

}

//静态对象
class StaticObject(val ast: ASTContainer, app: BuildStruct) : NameSpace(app) {
    val staticEntry = DwordSection(0)
    var size = 0//对象所占空间

    init {
        //预编译
        for (i in ast.container) {
            //todo import
            if (i is ASTOuterVar) {
                defVarList.add(DefVar(i, this))
            } else if (i is ASTOuterFun) {
                val defFun = DefFun(i, this)
                code.add(defFun)
                defFunList.add(defFun)
                //todo 预编译需要类型
            } else {
                throw Exception("??")
            }
        }
    }

    //使用了init函数,那么解析var表达式和init函数体
    fun useInit() {
        use()
        for (i in defVarList) {
            i.expr.add(ParseExpr(this).parse(i.ast.expr))
        }
        val initCode = Fun(app)
        val heapAlloc = app.importManager.use("KERNEL32.DLL", "HeapAlloc")
        push(32).addTo(initCode.code) // dwBytes是分配堆内存的大小。
        push(8).addTo(initCode.code) // dwFlags是分配堆内存的标志。包括HEAP_ZERO_MEMORY，即使分配的空间清零。
        mov(EAX, app.heap!!).addTo(initCode.code) //将heap取出到eax
        push(EAX).addTo(initCode.code) // hHeap是进程堆内存开始位置。
        Invoke(heapAlloc).addTo(initCode.code)//分配空间
        mov(AddrSection(staticEntry, app.dataSection), EAX).addTo(initCode.code) // 将eax存入对象的地址
        defFunList.add(initCode)//将对象初始化的函数插入到代码里面

        getFun("init")?.use()//如果有init就初始化init函数
        //计算对象的大小 就是所有变量的大小

        //todo 预编译完成需要进行变量的类型推导
        defVarList.forEach {
            it.type = ParseExpr(this).parseType(it.ast.expr)
            size += 4
        }
        defFunList.forEach {
            it.code.getBefore().add(Call(initCode))
        }
    }

    //todo 函数参数
    fun getFun(funName: String, param: Array<DataType> = arrayOf()): DefFun? {
        defFunList.find { it is DefFun && it.name == funName }?.let {
            return it as DefFun
        } ?: run {
            return null
        }
    }
}

