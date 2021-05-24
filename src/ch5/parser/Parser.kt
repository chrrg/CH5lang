package ch5.parser

import ch5.build.*

class Application(val buildStruct: BuildStruct) {
    val heap = AddrSection(buildStruct.dataSection.add(DwordSection(0)), buildStruct.dataSection)//堆空间开始地址
    var entry: Fun? = null
    val list = arrayListOf<Space>()
}

abstract class Space(val app: Application) {
    val data = BuildSection()
    val code = BuildSection()
    var isUsed = false
    open fun build() {}
    fun use() {
        isUsed = true
    }
}


//object Parser {
//    fun parse(ast: ASTContainer): BuildStruct {
//
//    }
//}