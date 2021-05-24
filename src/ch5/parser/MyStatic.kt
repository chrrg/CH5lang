package ch5.parser

import ch5.build.DwordSection

/**
 * 编译的静态对象
 */
open class MyStatic(app: Application) : MyClass(app) {
    val entry = DwordSection(0)//全局变量 存静态对象的地址用的

    // 在静态对象里,每一个函数前面都要调用初始化的函数(判断初始化了就不初始化)
//    fun writeFun() {
//        code.add(Fun())
//    }

    override fun build() {
        if (!isUsed) return
        data.add(entry)//
        //编译
        //制作对象唉，不可能的呀的初始化函数
        //

        //写入所有fun代码

        super.build()
    }

}