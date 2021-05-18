package ch5.amt

open class AmtPointer//指针
open class AmtValue//值
class AmtConstString(var value: String = "") : AmtPointer()
class AmtByte(var value: Int) : AmtValue()
class AmtWord(var value: Int) : AmtValue()
class AmtDword(var value: Int) : AmtValue()
class AmtPool{

}
class AmtApplication {
    val constPool = 0//常量池
    val importPool = 0//导入池


}