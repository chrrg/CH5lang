package ch5.parser

import ch5.ast.ASTContainer
import ch5.build.*

open class BuildObject {
    val size = 0//对象的字节大小
    val func = ArrayList<CodeBox>()

}

class BuildClass : BuildObject()
object Parser {
    fun parse(ast: ASTContainer): BuildStruct {
        val app = BuildStruct()
        val buildList = ArrayList<BuildObject>()

        buildList.add(BuildObject())
        val code = app.codeSection

        //写入调用主函数的代码
        val getProcessHeap = app.importManager.use("KERNEL32.DLL", "GetProcessHeap")
        val heapAlloc = app.importManager.use("KERNEL32.DLL", "HeapAlloc")

        val heap = AddrSection(app.dataSection.add(DwordSection(0)), app.dataSection)//堆空间开始地址
//        val entryStatic = AddrSection(app.dataSection.add(DwordSection(0)), app.dataSection)//堆空间开始地址
//
        Invoke(getProcessHeap).addTo(code)//获取程序堆
        mov(heap, EAX).addTo(code) //将eax存入heap中
//
//        // 调用主函数先要实例化主函数所在的static对象 初始化静态对象
//
//        push(32).addTo(code) // dwBytes是分配堆内存的大小。
//        push(8).addTo(code) // dwFlags是分配堆内存的标志。包括HEAP_ZERO_MEMORY，即使分配的空间清零。
//        mov(EAX, heap).addTo(code) //将heap取出到eax
//
//        push(EAX).addTo(code) // hHeap是进程堆内存开始位置。
//        Invoke(heapAlloc).addTo(code)//获取程序堆
//        mov(entryStatic, EAX).addTo(code) // 将eax放到程序data段内

        val exitProcess = app.importManager.use("KERNEL32.DLL", "ExitProcess");
        push(0).addTo(code);Invoke(exitProcess).addTo(code)//最后写退出程序
        // todo 写入各个会使用到的类和对象的方法

        //写入所有static对象的函数
        for (i in buildList) {
            val staticAddress = AddrSection(app.dataSection.add(DwordSection(0)), app.dataSection)//堆空间开始地址
            val initMe = Fun()
            // 判断是否为0 不为0则直接ret
            mov(EAX, staticAddress).addTo(initMe)
            val initCode = CodeBox()
            push(32).addTo(initCode) // dwBytes是分配堆内存的大小。
            push(8).addTo(initCode) // dwFlags是分配堆内存的标志。包括HEAP_ZERO_MEMORY，即使分配的空间清零。
            mov(EAX, heap).addTo(initCode) //将heap取出到eax
            push(EAX).addTo(initCode) // hHeap是进程堆内存开始位置。
            Invoke(heapAlloc).addTo(initCode)//分配空间
            mov(staticAddress, EAX).addTo(initCode) // 将eax存入对象的地址

            jnz(initCode).addTo(initMe)//如果不是0就跳转到最后面
            initMe.addTo(code)

        }

        return app
    }
}