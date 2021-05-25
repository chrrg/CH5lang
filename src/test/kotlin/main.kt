package test

import ch5.Compiler
import java.io.*
import java.text.DecimalFormat
import kotlin.math.max


fun performance(block: () -> Unit): Pair<Long, Long> {
    val r = Runtime.getRuntime()
    r.gc() //计算内存前先垃圾回收一次

    val start = System.nanoTime() //开始Time
    val startMem = r.freeMemory() // 开始Memory
    block()
    val endMem = r.freeMemory() // 末尾Memory
    val end = System.nanoTime() //末尾Time
    return Pair(end - start, startMem - endMem)
//    println("代码运行时间: " + (end - start).toString() + "ms")
//    println("内存消耗: " + ((startMem - endMem) / 1024).toString() + "KB")
}

fun run(cmd: File): Pair<BufferedReader, BufferedReader>? {
    try {
        //执行exe  cmd可以为字符串(exe存放路径)也可为数组，调用exe时需要传入参数时，可以传数组调用(参数有顺序要求)
        val p: Process = Runtime.getRuntime().exec(cmd.path)
        return Pair(
            BufferedReader(InputStreamReader(p.inputStream, "GBK")),
            BufferedReader(InputStreamReader(p.errorStream, "GBK"))
        )
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {

    }
    return null
}


fun double3(value: Long, value2: Int): String {
    return DecimalFormat("0.000").format(value.toDouble() / value2)
}

fun getContent(bf: BufferedReader): String {
    return bf.use(BufferedReader::readText)
}

fun main(args: Array<String>) {
    val result = StringBuffer()
    //获取其file对象
    val dir = File("src/test/testcase/")
    val fs = dir.listFiles() ?: throw Exception("目录获取失败！") //遍历path下的文件和目录，放在File数组中
    //jvm 预热环境
//    if (fs.isEmpty()) {
//        println("没有测试用例！")
//        return
//    }
//    repeat(10) {
//        Compiler.compile(fs[0].path, "jit.cache")
//    }
    result.append("# CH编译器 测试用例报告\n")
    var num = 0
    fs.filter { it.isFile && it.name.endsWith(".ch5") }.forEach { file ->
        println("测试用例：${file.path}")

        try {
            num++
            result.append("## 测试用例$num\n")
            result.append("用例：`${file.path}`  \n")
            result.append("\n```\n")
            result.append(FileReader(file).readText())
            result.append("\n```\n")
            result.append("> 编译结果  \n\n")

            try {
                Compiler.compile(file, File(file.parent, file.name + ".exe"))//JVM预热
                result.append("编译成功！ \n")

            } catch (e: Exception) {
                result.append("编译出错：  \n")
                result.append("\n```\n")
                result.append(e.stackTraceToString())
                result.append("\n```\n")
                return@forEach
            }


            result.append("> 编译性能测试  \n\n")

            result.append("\n|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|\n")
            result.append("| ------ | ------ | ------ | ------ |\n")

            val executeFile = File(file.parent, file.name + ".exe")
            arrayOf(1, 10, 100).forEach { count ->
                var maxCompileTime: Long = 0//最长编译时间
                var totalTime: Long = 0//总耗时
                var totalMemory: Long = 0
                repeat(count) {
                    val compilePerformance = performance {
                        Compiler.compile(file, executeFile)
                    }
                    totalTime += compilePerformance.first
                    totalMemory += compilePerformance.second
                    maxCompileTime = max(maxCompileTime, compilePerformance.first)
                }
                result.append(
                    "|${count}次|${
                        double3(
                            totalTime,
                            count * 1000000
                        )
                    }ms|${double3(maxCompileTime, 1000000)}ms|${totalMemory / count / 1024}KB|\n"
                )
            }
            result.append("> 运行结果测试  \n\n")
            val resultFile = File(file.parent, file.name + ".txt")

            run(executeFile)?.let {
                //运行结果
                result.append("测试用例运行输出：  \n")
                val output = getContent(it.first)
                result.append("\n```\n")
                result.append(output)
                result.append("\n```\n")

                if (resultFile.exists()) {
                    val resultContent = FileReader(resultFile).readText()
                    if (resultContent == output) {
                        result.append("测试通过  \n")
                    } else {
                        result.append("测试不通过  \n\n")

                        result.append("期待结果：  \n")
                        result.append("\n```\n")
                        result.append(resultContent)
                        result.append("\n```\n")
                    }
                } else {
                    FileWriter(resultFile).let { writer ->
                        writer.write(output)
                        writer.flush()
                    }
                }
                result.append("测试完成！  \n")

            } ?: run {
                result.append("测试失败！\n")

            }
            result.append("---\n")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val writer = FileWriter(File(dir, "result.md"))
    writer.write(result.toString())
    writer.flush()
    println("测试结果已经保存到了文件中！")
}