package ch5.amt

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//abstract class AmtParseTask {
//    var isCompleted = false
//    abstract fun task(): Int
//    fun doTask(): Int {
//        if (isCompleted) return 0
//        return task()
//    } //0 解析完成 1 解析成功(可能未解析完全) 2 解析失败(遇到需要修复的，延迟解析)
//}


abstract class AmtParseTask {
    var thread: Job? = null
    val lock = Object()
    abstract fun task(task: AmtParseTask): Int

    init {
//        Thread{
//            while (true) {
//                Thread.sleep(1000)
//                synchronized(lock) {
//                    lock.notify()
//                }
//            }
//        }.start()
        val that = this
        thread = GlobalScope.launch {
            synchronized(lock) {
                lock.wait()
                task(that)
            }
        }
        thread?.start()
//
    }


    fun doTask(): Int {//0 解析完成 1 解析成功(可能未解析完全) 2 解析失败(遇到需要修复的，延迟解析)
//        runBlocking {
//            while (!thread!!.isActive) {
//            }
//        }
        synchronized(lock) {
            lock.notify()
        }
        return 0
    }
}

class Task1 : AmtParseTask() {
    override fun task(task: AmtParseTask): Int {
        println("begin run")
        task.lock.wait()
        println("ok")
        task.lock.wait()
        println("ok2")
        task.lock.wait()
        println("ok3")

        return 0
    }

}


//class AmtParseOuterVar(val static: AmtStatic, val i: ASTOuterVar) :AmtParseTask(){
//    override fun task(): Int {
//
//    }
//}

fun main() = runBlocking {
    GlobalScope.launch {
        val task = Task1()
        task.doTask()
//        while (true) {
//            delay(1000)
//            task.doTask()
//        }
    }.join()

}