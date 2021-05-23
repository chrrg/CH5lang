package ch5.parser

import ch5.Tokenizer
import ch5.ast.AST
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

/**
 * 预编译的static
 */
open class PreStatic(app: Application) : MyStatic(app) {

}

/**
 * 预编译的
 */
class PreFile(app: Application, val file: File) : PreStatic(app) {

    init {
        val tokenizer = Tokenizer(getFileCode(file))//并没有进行分词
        val ast = AST(tokenizer).parse()
//        println(ast.toString())
        //文件转语法树结束

        //预编译命名空间树
        //todo 将ast转为MyStatic 并写入代码
        //todo 将MyStatic的main函数赋值到app.entry上面

    }
    fun preCompile(){

    }
    private fun getFileCode(file: File): String {
        val input = BufferedInputStream(FileInputStream(file))
        var len: Int
        val temp = ByteArray(1024)
        val sb = StringBuilder()
        while (input.read(temp).also { len = it } != -1) sb.append(String(temp, 0, len))
        return sb.toString()
    }
}

/**
 * 解析程序
 */
class ParseProgram(val app: Application, val entryFile: String) {
    private val fileList = ArrayList<PreFile>()


    //todo 输入ast 输出MyStatic


    private fun parseFile(file: File): PreFile {
        //文件转语法树
        fileList.find { it.file == file }?.let {
            return it
        }?: run {
            val preFile=PreFile(app, file)
            fileList.add(preFile)
            return preFile
        }
    }

    init {
        parseFile(File(entryFile))
//        app.entry =


    }
}

