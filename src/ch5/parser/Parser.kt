package ch5.parser

import ch5.ast.ASTContainer
import ch5.build.*

object Parser {
    fun parse(ast: ASTContainer): BuildStruct {
        val app = BuildStruct()
        val code = app.codeSection
        val exitProcess = app.importManager.use("KERNEL32.DLL", "ExitProcess")

//        ast.container
        //写入调用主函数的代码
        val getProcessHeap = app.importManager.use("KERNEL32.DLL", "GetProcessHeap")
        val heapAlloc = app.importManager.use("KERNEL32.DLL", "HeapAlloc")

        val heap = AddrSection(app.dataSection.add(DwordSection(0)), app.dataSection)//堆空间开始地址
        val entryStatic = AddrSection(app.dataSection.add(DwordSection(0)), app.dataSection)//堆空间开始地址

        Invoke(getProcessHeap).addTo(code)//获取程序堆
        mov(heap, EAX).addTo(code) //将eax存入heap中

        // 调用主函数先要实例化主函数所在的static对象 初始化静态对象

        push(32).addTo(code) // dwBytes是分配堆内存的大小。
        push(8).addTo(code) // dwFlags是分配堆内存的标志。包括HEAP_ZERO_MEMORY，即使分配的空间清零。
        mov(EAX, heap).addTo(code) //将heap取出到eax

        push(EAX).addTo(code) // hHeap是进程堆内存开始位置。
        Invoke(heapAlloc).addTo(code)//获取程序堆
        mov(entryStatic, EAX).addTo(code) // 将eax放到程序data段内


        push(0).addTo(code);Invoke(exitProcess).addTo(code)//最后写退出程序
        //todo 写入各个会使用到的类和对象的方法


        return app
    }
}