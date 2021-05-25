import ch5.build.Build
import ch5.build.BuildStruct
import ch5.build.DwordSection
import ch5.parser.Application
import ch5.parser.ParseProgram
import ch5.parser.RunTime

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
        Build.build(buildStruct, "1.exe")

    }
}

/**
 * Main
 * 编译的入口函数
 */
fun main() {
    Compiler.compile("src/code/app.ch5", "compile/app.exe")

}
