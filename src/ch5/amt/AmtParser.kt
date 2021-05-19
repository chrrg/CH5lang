package ch5.amt

import ch5.ast.ASTContainer

abstract class AmtTask<T> {
    interface TaskControl {
        fun addSubTask(subTask: AmtTask<*>)
    }

    private var result: T? = null
    abstract fun task(taskControl: TaskControl): T?
    private val subTaskPool = AmtPool<AmtTask<*>>()
    fun isSubTaskSolved(): Boolean {
        //子任务解决了自己才是解决了
    }

    fun solve() {
        //先运行自己是否能够完成
        //如果完成了这个任务就解决了
        //如果没完成,就等待解决
        //重复提交任务怎么办?

    }

    fun use(): T {
        result?.let { return it }

//        result = task(object : TaskControl {
//            override fun addSubTask(subTask: AmtTask<*>) {
//                subTaskPool.add(subTask)
//            }
//        })
//        isSubTaskSolved()
        result?.let { return it }
        throw Exception("无法解析对象!")
    }
}

class AmtParser {
    private val parseTaskPool = AmtParseTaskPool()

    class AmtParseTaskPool : AmtPool<AmtParseTask>() {
        fun remove(item: AmtParseTask) {
            list.remove(item)
        }
    }

    fun parseStatic(ast: ASTContainer): AmtStaticTask {
        return AmtStaticTask(ast)
    }
//    fun parseStatic(ast: ASTContainer): AmtStatic {
//        val static = AmtStatic()
//
//
//
//        for (i in ast.container) {
//            //在这里碰到的语句需要在外层全部解析完成后进行解析，即：
//            if (i is ASTOuterVar) {
//                //解析var时，这个时候变量指定的类型可能无法解析（类型在后面定义但是没有暂时没有解析到那里去）表达式也可能无法解析（表达式的函数可能在后面定义的）
//                //可以解析的：变量名称
////                AmtParseOuterVar(static, i)
//
//                parseOuterVar(i)
//            } else if (i is ASTOuterFun) {
//                //解析fun时，可能无法解析的：函数参数类型，函数体的代码块
//                //可以解析的：函数名称
//                val func = AmtFun()
//                static.funPool.add(func)
//                static.defineFunPool.add(AmtDefineFun(i.name.getName(), func))
//            }
//            println(i)
//        }
//
//        //解析static对象 这个时候继承关系可能无法解析 需要延迟解析
//        //所以这里只是将对象存入队列中，如果在后续有使用这个对象的时候才进行解析
//
//
//        return static
//    }
//
//    fun parseOuterVar(i: ASTOuterVar) {
//        assert(i.names.size == 1)
////        static.variablePool.add(AmtVariable(i.names[0].name.value, i.const, 0))
//    }

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