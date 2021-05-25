package ch5

import ch5.build.Build
import ch5.build.BuildStruct
import ch5.build.DwordSection
import ch5.parser.Application
import ch5.parser.ParseProgram
import ch5.parser.RunTime
import java.io.File


object Compiler {
    fun compile(entryFilePath: File, output: File) {
        val buildStruct = BuildStruct()
        val data = buildStruct.dataSection
        val code = buildStruct.codeSection
        val app = Application(buildStruct)
        val runtime = RunTime(app)
        app.list.add(runtime)//todo 初始化堆 实例化入口static,并且调用main方法
        ParseProgram(app, entryFilePath)
        runtime.addEntryFun(app.entry!!)

        app.list.forEach {
            if (it.isUsed) {
                it.build()
                data.add(it.data)
                code.add(it.code)
            }
        }
        data.add(DwordSection(0x7FFFFFFF))//没什么用 只是标志着数据段最后4个字节
        Build.build(buildStruct, output)
//        println("\u001B[0;32m编译完成！\u001B[0m")
    }
}

/**
 * Main
 * 编译的入口函数
 */
fun main() {

}
