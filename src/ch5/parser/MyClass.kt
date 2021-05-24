package ch5.parser

import ch5.build.CodeBox

/**
 * 一个类,跟编译无关
 */
open class MyClass(app: Application) : Space(app) {
    val varList = arrayListOf<DefVariable>()//定义的变量
    val funList = arrayListOf<DefFunction>()//定义的函数
    //import
    //定义的变量2
    //定义的方法1
    //命名空间:
    //class
    //static


    //code:
    //写入所有函数体
    //写入内置的init方法
    override fun build() {
        val initCode = CodeBox()//初始化代码要做的事情 每次class或者object实例化的时候需要运行这个
        for (i in varList) {
            i.initCode.addTo(initCode)//把所有变量初始化的代码加到initCode里面去
        }


    }




}