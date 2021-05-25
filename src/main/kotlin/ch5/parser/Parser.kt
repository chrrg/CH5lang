package ch5.parser

import ch5.build.*

/**
 * Application
 * 解析语法时用的应用程序 一个编译程序对应同一个Application对象
 * @property buildStruct
 * @constructor Create empty Application
 */
class Application(val buildStruct: BuildStruct) {
    val heap = AddrSection(buildStruct.dataSection.add(DwordSection(0)), buildStruct.dataSection)//堆空间开始地址
    var entry: Fun? = null
    val list = arrayListOf<Space>()
    val alloc = Fun(0, 4)
}

/**
 * Space
 * 解析空间的抽象对象
 * @property app
 * @constructor Create empty Space
 */
abstract class Space(val app: Application) {
    val data = BuildSection()
    val code = BuildSection()
    var isUsed = false

    /**
     * Build
     * 构建 在写入时会调用
     */
    open fun build() {}

    /**
     * Use
     * 使用过了会标记已使用
     */
    fun use() {
        isUsed = true
    }
}


//object Parser {
//    fun parse(ch5.ast: ASTContainer): BuildStruct {
//
//    }
//}