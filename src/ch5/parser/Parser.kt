package ch5.parser

import ch5.ast.ASTContainer
import ch5.build.BuildStruct
import ch5.build.Call
import ch5.build.CodeBox

//open class BuildObject {
//    //static对象
//    val size = 0//对象的字节大小
//
//    val initFun = Fun()//初始化这个对象的函数
//    val funcList = ArrayList<Fun>()//所有函数
//
//    init {
//
//    }
//}

open class NameSpace(val app: BuildStruct) {
    val defFunList = arrayListOf<DefFun>()
    val defVarList = arrayListOf<DefVar>()
    val defType = arrayListOf<DefType>()
    private var used = false
    private var children = arrayListOf<NameSpace>()
    private var parent: NameSpace? = null
    val code = CodeBox()
    fun flat(): Collection<NameSpace> {
        val result = arrayListOf(this)
        for (i in children) result.addAll(i.flat())
        return result
    }

    fun link() {
        for (i in defFunList) {
            code.add(i.func)
        }
    }

    fun use() {
        used = true
    }

    fun setParent(nameSpace: NameSpace) {
        if (parent != null && parent != nameSpace) throw Exception("already exist parent namespace")
        parent = nameSpace
        nameSpace.addChild(this)
    }

    fun addChild(nameSpace: NameSpace) {
        children.add(nameSpace)
        nameSpace.parent = this
    }

    //获取变量
    fun getVariable(name: String): DefVar {
        defVarList.find { it.name == name }?.let {
            return it
        }
        parent?.let {
            return it.getVariable(name)
        }
        throw Exception("变量${name}不存在!")
    }

    //todo 参数判断
    fun getFunction(name: String, param: Array<DataType>): DefFun {
        defFunList.find { it.name == name }?.let {
            return it
        }
        parent?.let {
            return it.getFunction(name, param)
        }
        throw Exception("函数${name}不存在!")
    }

    /**
     * 通过名字获取类型实体
     */
    fun getType(name: String): DefType {
        defType.find { it.name == name }?.let {
            return it
        }
        parent?.let {
            return it.getType(name)
        }
        throw Exception("类型${name}不存在!")
    }
}

//class BuildClass : BuildObject()
object Parser {
    fun parse(ast: ASTContainer): BuildStruct {
        // 预编译static
        val app = BuildStruct()
        val code = app.codeSection
        val root = NameSpace(app)//根命名空间 存放一些基础的内容 builtin类型函数在这里
        root.defType.add(DefType("int"))//builtin int类型

        val entry = StaticObject(ast, app)
        root.addChild(entry)
//        entry.setParent(root)

        Call(entry.getFun("main")!!.func).addTo(root.code)

        val nameSpaceList = root.flat()//平铺所有命名空间
        for (i in nameSpaceList) code.add(i.code)//将命名空间的代码写入代码段

        return app
//        return win32Build()
    }

//    private fun win32Build(): BuildStruct {
//        val app = BuildStruct()
//        val buildList = ArrayList<BuildObject>()
//
//        buildList.add(BuildObject())
//        val code = app.codeSection
//
//        //写入调用主函数的代码
//        val getProcessHeap = app.importManager.use("KERNEL32.DLL", "GetProcessHeap")
//        val heapAlloc = app.importManager.use("KERNEL32.DLL", "HeapAlloc")
//
//        val heap = AddrSection(app.dataSection.add(DwordSection(0)), app.dataSection)//堆空间开始地址
//        Invoke(getProcessHeap).addTo(code)//获取程序堆
//        mov(heap, EAX).addTo(code) //将eax存入heap中
//
//
//        Call(buildList[0].initFun).addTo(code) // todo 调用入口函数
//
//        val exitProcess = app.importManager.use("KERNEL32.DLL", "ExitProcess");
//        push(0).addTo(code);Invoke(exitProcess).addTo(code)//最后写退出程序
//        // todo 写入各个会使用到的类和对象的方法
//
//        //写入所有static对象的函数
//        for (i in buildList) {
//            val staticAddress = AddrSection(app.dataSection.add(DwordSection(0)), app.dataSection)//堆空间开始地址
//            val initMe = i.initFun
//            // 判断是否为0 不为0则直接ret 说明已经初始化过了不要再初始化
//            mov(EAX, staticAddress).addTo(initMe)
//            val initCode = CodeBox()
//            push(32).addTo(initCode) // dwBytes是分配堆内存的大小。
//            push(8).addTo(initCode) // dwFlags是分配堆内存的标志。包括HEAP_ZERO_MEMORY，即使分配的空间清零。
//            mov(EAX, heap).addTo(initCode) //将heap取出到eax
//            push(EAX).addTo(initCode) // hHeap是进程堆内存开始位置。
//            Invoke(heapAlloc).addTo(initCode)//分配空间
//            mov(staticAddress, EAX).addTo(initCode) // 将eax存入对象的地址
//            jnz(initCode).addTo(initMe)//如果不是0就跳转到最后面
//            initMe.addTo(code)
//            for (j in i.funcList) {
//                j.addTo(code)
//            }
//        }
//
//        return app
//    }
}