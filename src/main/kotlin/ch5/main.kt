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
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("欢迎使用CH编译器，开发人：曹鸿，毕业院校：桂林理工大学 信息科学与工程学院。毕业年份：2021年")
        println(
            """
   ______    __  __          ______                                _     __              
  / ____/   / / / /         / ____/  ____    ____ ___     ____    (_)   / /  ___    _____
 / /       / /_/ /         / /      / __ \  / __ `__ \   / __ \  / /   / /  / _ \  / ___/
/ /___    / __  /         / /___   / /_/ / / / / / / /  / /_/ / / /   / /  /  __/ / /    
\____/   /_/ /_/          \____/   \____/ /_/ /_/ /_/  / .___/ /_/   /_/   \___/ /_/     
                                                      /_/                                
        """.trimIndent()
        )
        println("使用方法：\njava -jar ch5.jar -c 1.ch5 1.exe")
        return
    }
    if (args[0] == "-c") {
        if (args.size != 3) {
            println("命令需要3个参数！")
            return
        }
        val input = File(args[1])
        if (!input.exists()) {
            println("${input.absoluteFile}文件不存在！")
            return
        }
        Compiler.compile(input, File(args[2]))
    }
}
