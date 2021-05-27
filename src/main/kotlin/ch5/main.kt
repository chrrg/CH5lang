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
        println("欢迎使用CH编译器！")
        println("可以将你的代码编译为Win32下可执行文件！")
        println("开发人：曹鸿")
        println("毕业院校：桂林理工大学 信息科学与工程学院")
        println("毕业年份：2021年")
        println("")
        println("使用方法：")
        println("java -jar ch5.jar -c 1.ch5 1.exe")
        println("java -jar ch5.jar -c 1")
        println("-c inputFile [outputFile]")
        println("inputFile 源代码文件路径，以ch5后缀 可省略后缀")
        println("outputFile 输出可执行文件的路径，以exe后缀 可省略后缀 默认为源代码的文件名")

        return
    } else if (args[0] == "-c") {
        when (args.size) {
            1 -> {
                println("参数数量不够 两个或三个参数！")
                return
            }
            2 -> {
                var input = File(args[1])
                if (!input.exists() || input.isDirectory) {
                    input = File(args[1] + ".ch5")
                    if (!input.exists() || input.isDirectory) {
                        println("代码文件：${input.absoluteFile}不存在！")
                        return
                    }
                }
                val offset = input.name.lastIndexOf(".")
                var output = if (offset != -1 && input.name.substring(offset) == ".ch5") {
                    input.name.substring(0, offset)
                } else {
                    input.name
                }
                if (!output.endsWith(".exe")) output += ".exe"
                Compiler.compile(input, File(output))
                println("CH5 Compiler: 编译完成！")
            }
            3 -> {
                var input = File(args[1])
                if (!input.exists() || input.isDirectory) {
                    input = File(args[1] + ".ch5")
                    if (!input.exists() || input.isDirectory) {
                        println("代码文件：${input.absoluteFile}不存在！")
                        return
                    }
                }
                var output = args[2]
                if (!output.endsWith(".exe")) output += ".exe"
                Compiler.compile(input, File(output))
                println("CH5 Compiler: 编译完成！")
            }
            else -> {
                println("参数数量不匹配！")
                return
            }
        }
    }
}
