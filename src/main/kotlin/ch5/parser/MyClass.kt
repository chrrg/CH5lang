package ch5.parser

import ch5.build.CodeBox


/**
 * My class
 * 一个类,跟编译无关
 * @constructor
 *
 * @param app
 */
open class MyClass(app: Application) : Space(app) {
    var heapSize = 0//每个对象需要消耗的堆空间
    val importList= arrayListOf<DefImport>()
    val varList = arrayListOf<DefVariable>()//定义的变量
    val funList = arrayListOf<DefFunction>()//定义的函数
    val initCode = CodeBox()//初始化代码要做的事情 每次class或者object实例化的时候需要运行这个

    //import
    //定义的变量2
    //定义的方法1
    //命名空间:
    //class
    //static

    /**
     * Write variable init code
     * 把所有变量初始化的代码加到initCode里面去
     */
    fun writeVariableInitCode() {
        for (i in varList) {
            i.initCode?.addTo(initCode)//
        }
    }

    fun calcHeapSize() {
        heapSize = 0
        varList.forEach {
            heapSize += it.type!!.getSize()
        }
    }

    /**
     * Build
     * 写入所有函数体
     *  写入内置的init方法
     */
    override fun build() {
        if (!isUsed) return
        writeVariableInitCode()
        calcHeapSize()
        funList.forEach {
            code.add(it.func)
        }

    }


}