package ch5.parser

import ch5.build.*

/**
 * Run time
 * 这个类会插入在每个程序的代码段的最前面。
 * 需要初始化堆空间并且调用入口函数。
 * @constructor
 *
 * @param app
 */
class RunTime(app: Application) : Space(app) {
    private val callEntry = CodeBox()//不是入口函数
    fun addEntryFun(entryFun: Fun) {
        Call(entryFun).addTo(callEntry)//todo 拿到main函数的地址
    }

    init {
        val buildStruct = app.buildStruct

        val getProcessHeap = buildStruct.importManager.use("KERNEL32.DLL", "GetProcessHeap")
        val exitProcess = buildStruct.importManager.use("KERNEL32.DLL", "ExitProcess");

        val initHeap = CodeBox().addTo(code)//初始化堆
        callEntry.addTo(code)//调用入口对象main方法的代码
        val exit = CodeBox().addTo(code)//退出程序

        push(0).addTo(exit);Invoke(exitProcess).addTo(exit)//最后写退出程序

        // 初始化堆空间开始
        Invoke(getProcessHeap).addTo(initHeap)//获取程序堆
        mov(app.heap, EAX).addTo(initHeap) //将eax存入heap中
        // 初始化堆空间结束


        val alloc = app.alloc//分配堆空间的函数
        // 待函数传入的参数 dwBytes是分配堆内存的大小
        code.add(alloc)
        val heapAlloc = buildStruct.importManager.use("KERNEL32.DLL", "HeapAlloc")
        push(8).addTo(alloc.code) // dwFlags是分配堆内存的标志。包括HEAP_ZERO_MEMORY=8，即使分配的空间清零。
        mov(EAX, app.heap).addTo(alloc.code) //将heap取出到eax
        push(EAX).addTo(alloc.code) // hHeap是进程堆内存开始位置。
        Invoke(heapAlloc).addTo(alloc.code)//分配空间
        //这里要完成所有static,class的构建
        use()
    }

}