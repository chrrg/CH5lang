package ch5.build

/**
 * Build struct
 * 构建结构体 构建器需要关注的
 * @constructor Create empty Build struct
 */
class BuildStruct {
    val dataSection = DataSection()
    val codeSection = CodeSection()
    val importManager = ImportManager()
}