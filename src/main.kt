import manager.CH5_compiler
import manager.CompileError

fun main(){

//    try {
    var result = CH5_compiler.compile("src/code/main.ch5", "test.exe")
//    var result = CH5_compiler.compile("src/code/syntax/fun.ch5", "test.exe")

    if(true)return
    try {

    }catch (e: CompileError){
        println("CompileError")
        println(e.errMsg)
        println(
            e.token?.let{
//                val start=it.getStartPos()
//                val end=it.getEndPos()
//                "["+it.Token_Pos.file.filePath+":第"+start[0]+"行第"+start[1]+"列~第"+end[0]+"行第"+end[1]+"列"+it.getToken()+"]"
            } ?: "[unknown pos]"
        )
        e.printStackTrace()
    }catch (e: Exception){
        println("Exception")
        e.printStackTrace()
    }

}
