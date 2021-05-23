package ch5.parser

import ch5.ast.ASTContainer
import ch5.build.*

class Application(val buildStruct: BuildStruct) {
    val heap = AddrSection(buildStruct.dataSection.add(DwordSection(0)), buildStruct.dataSection)//堆空间开始地址
    var entry: Fun? = null
    val list = arrayListOf<Space>()

}

open class Space(val app: Application) {
    val data = BuildSection()
    val code = BuildSection()
}


object Parser {
    fun parse(ast: ASTContainer): BuildStruct {
        val buildStruct = BuildStruct()
        val data = buildStruct.dataSection
        val code = buildStruct.codeSection
        val app = Application(buildStruct)
        val runtime = Runtime(app)
        app.list.add(runtime)//todo 初始化堆 实例化入口static,并且调用main方法
        ParseProgram(app)

        //预编译命名空间树
        //todo 将ast转为MyStatic 并写入代码
        //todo 将MyStatic的main函数赋值到app.entry上面


        app.list.forEach {
            data.add(it.data)
            code.add(it.code)
        }
        data.add(DwordSection(0x7FFFFFFF))
        return buildStruct
    }
}