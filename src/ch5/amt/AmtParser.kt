package ch5.amt


import ch5.ast.ASTContainer
import ch5.ast.ASTOuterFun
import ch5.ast.ASTOuterVar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AmtParser {
    private val parseTaskPool = AmtParseTaskPool()

    abstract class AmtParseTask {
        var job: Job? = null
        abstract suspend fun task()
        fun fail() {
            job!!
            job!!.
        }

        fun doTask(): Int {//0 解析完成 1 解析成功(可能未解析完全) 2 解析失败(遇到需要修复的，延迟解析)
            var myJob=job
            if (myJob == null) {
                myJob = GlobalScope.launch {
                    task()
                }
            }
            if (myJob.isCompleted) return 0

        }
    }

    class AmtParseTaskPool : AmtPool<AmtParseTask>() {
        fun remove(item: AmtParseTask) {
            list.remove(item)
        }
    }

    fun parseStatic(ast: ASTContainer): AmtStatic {
        val static = AmtStatic()

        for (i in ast.container) {
            //在这里碰到的语句需要在外层全部解析完成后进行解析，即：
            if (i is ASTOuterVar) {
                parseOuterVar(i)
            } else if (i is ASTOuterFun) {
                val func = AmtFun()
                static.funPool.add(func)
                static.defineFunPool.add(AmtDefineFun(i.name.getName(), func))
            }
            println(i)
        }
        return static
    }

    fun parseOuterVar(i: ASTOuterVar) {
        assert(i.names.size == 1)
        static.variablePool.add(AmtVariable(i.names[0].name.value, i.const, 0))
    }

    fun link(): Boolean {//链接修复
        //这里要进行链接任务
        //循环进行链接
        //链接就是把变量等串起来
        //循环链接

        while (true) {
            var neverSuccess = true
            var status = 0//2 解析失败
            for (i in parseTaskPool) {
                when (i.doTask()) {
                    2 -> status = 2
                    1 -> neverSuccess = false
                }
//                if (result == 0) TODO("remove task")
            }
            if (neverSuccess) {
                throw Exception("无法解析！")
            }
            if (status == 0) {
                break;//解析完成退出循环
            }
        }
        return false
    }
}