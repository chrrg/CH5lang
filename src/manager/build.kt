package manager

import compiler.AMT.AMT
import compiler.AST.AST
import compiler.Build.Build
import compiler.Platform.Platform_build
import compiler.Tokenizer.Token
import compiler.Tokenizer.Tokenizer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

class CompileResult{

}
class CompileError(val errMsg:String,val token:Token?=null):Exception(){

}
interface interface_build{
    fun compile(mainFile:String,output:String):CompileResult
}
object CH5_compiler:interface_build{
    fun getFileCode(mainFile: String): String {
        val inputFilePath= File(mainFile).absolutePath
        val input = BufferedInputStream(FileInputStream(inputFilePath))
        var len = 0
        val temp = ByteArray(1024)
        val sb = StringBuilder()
        while (input.read(temp).also({ len = it }) != -1)sb.append(String(temp, 0, len))
        return sb.toString()
    }
    override fun compile(mainFile: String, output: String): CompileResult {
        /*
        * 编译过程：
        * 分词
        * 词转AST
        * AST转语义树(amt)
        * 语义树检查分析
        * 生成机器码
        */
//        val tokenResult=Tokenizer(mainFile).parse()//分词
//        Tokenizer.print(tokenResult)//打印分词结果
        val code=getFileCode(mainFile)
        val tokenResult=Tokenizer(code)



        val ast=AST.parse(tokenResult)

//        if(true)return CompileResult()
//        val ast=AST(tokenResult).parse()//构建语法树
        println("开始打印语法树：")
        ast.print()
        println("打印语法树完毕")
        println("开始语义分析：")

        val app=AMT.parse(ast)
//        AMT.print(app)

        println("语义分析完成！")
//        AST_Wrapper(ast)
//        AMT(AST_Wrapper(ast))
        Build(Platform_build("win32"),app,"ch5.exe")

        return CompileResult()
    }

}