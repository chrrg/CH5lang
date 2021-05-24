package ch5.parser

import ch5.build.*

/**
 * My static
 * 编译的静态对象
 * @constructor
 *
 * @param app
 */
open class MyStatic(app: Application) : MyClass(app) {
    val entry = DwordSection(0)//全局变量 存静态对象的地址用的
    val entryAddr = AddrSection(entry, app.buildStruct.dataSection)
    // 在静态对象里,每一个函数前面都要调用初始化的函数(判断初始化了就不初始化)
//    fun writeFun() {
//        code.add(Fun())
//    }

    override fun build() {
        if (!isUsed) return
        data.add(entry)//

        writeVariableInitCode()
        //写入所有fun代码 但是代码前面要加上判断对象是否初始化的函数
        val initStaticCode = CodeBox()
        mov(EAX, entryAddr).addTo(initStaticCode)
        val initCode = CodeBox()

        jz(initCode).addTo(initStaticCode)

        funList.forEach {
            it.code.getBefore().add(initStaticCode)
            code.add(it.code)
        }



        super.build()
    }

}