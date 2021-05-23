package ch5.parser

import ch5.build.DwordSection
import ch5.build.Fun

class MyStatic(app: Application) : MyClass(app) {
    val entry = DwordSection(0)

    // 在静态对象里,每一个函数前面都要调用初始化的函数(判断初始化了就不初始化)
    fun writeFun() {
        code.add(Fun())
    }

    init {

        data.add(entry)
    }
}