package ch5.amt

import ch5.ast.ASTContainer
import ch5.ast.ASTSyntax

//abstract class AmtParseTask {
//    var isCompleted = false
//    abstract fun task(): Int
//    fun doTask(): Int {
//        if (isCompleted) return 0
//        return task()
//    } //0 解析完成 1 解析成功(可能未解析完全) 2 解析失败(遇到需要修复的，延迟解析)
//}

class AmtStaticTask(val ast: ASTContainer) : AmtTask<AmtStatic>() {
    override fun task(taskControl: TaskControl): AmtStatic? {
        val result=AmtStatic()
        for (i in ast.container) {
            taskControl.addSubTask(AmtSyntaxTask(i))
        }
        return result
    }
}
open class AmtSyntax
class AmtSyntaxTask(val syntax: ASTSyntax):AmtTask<AmtSyntax>(){
    override fun task(taskControl: TaskControl): AmtSyntax? {
        TODO("Not yet implemented")
    }
}
