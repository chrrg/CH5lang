
import ch5.build.Build
import ch5.build.BuildStruct
import ch5.build.DwordSection
import ch5.parser.Application
import ch5.parser.ParseProgram
import ch5.parser.RunTime
import java.io.BufferedReader
import java.io.InputStreamReader


object Compiler {
    fun compile(entryFilePath: String, output: String) {
        val buildStruct = BuildStruct()
        val data = buildStruct.dataSection
        val code = buildStruct.codeSection
        val app = Application(buildStruct)
        val runtime = RunTime(app)
        app.list.add(runtime)//todo 初始化堆 实例化入口static,并且调用main方法
        ParseProgram(app, entryFilePath)
        runtime.addEntryFun(app.entry!!)
//        app.entry.getAfter()

        app.list.forEach {
            if (it.isUsed) {
                it.build()
                data.add(it.data)
                code.add(it.code)
            }
        }
        data.add(DwordSection(0x7FFFFFFF))
        Build.build(buildStruct, output)
        println("\u001B[0;32m编译完成！\u001B[0m")
    }

    fun run(cmd: String) {
        println("--------------------------------")
        var br: BufferedReader? = null
        try {
            //执行exe  cmd可以为字符串(exe存放路径)也可为数组，调用exe时需要传入参数时，可以传数组调用(参数有顺序要求)
            val p: Process = Runtime.getRuntime().exec(cmd)
            var line: String?
            br = BufferedReader(InputStreamReader(p.inputStream, "GBK"))
            val brError = BufferedReader(InputStreamReader(p.errorStream, "GBK"))
            while (br.readLine().also { line = it } != null || brError.readLine().also { line = it } != null) {
                //输出exe输出的信息以及错误信息
                println(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

fun performance(block: () -> Unit) {
    val r = Runtime.getRuntime()
    r.gc() //计算内存前先垃圾回收一次
    val start = System.currentTimeMillis() //开始Time
    val startMem = r.freeMemory() // 开始Memory
    block()
    val endMem = r.freeMemory() // 末尾Memory
    val end = System.currentTimeMillis() //末尾Time
    println("代码运行时间: " + (end - start).toString() + "ms")
    println("内存消耗: " + ((startMem - endMem) / 1024).toString() + "KB")
}

/**
 * Main
 * 编译的入口函数
 */
fun main() {
    while (true) {
        performance {
            Compiler.compile("src/code/app.ch5", "1.exe")
        }
        Compiler.run("1.exe")
        System.`in`.read()

    }
}
