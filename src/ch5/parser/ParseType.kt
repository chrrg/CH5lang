package ch5.parser

class ParseType {

}

/**
 * 描述一个数据类型
 */
open class DataType(val name: String) {
    var array = 0
    var general = arrayOf<DataType>()//泛型

    companion object {
        //todo class作为一个类
//        fun getDataType():DataType{
//
//        }
    }

    fun equalsArray(other: Array<DataType>): Boolean {
        if (other.size != general.size) return false
        for (i in other.indices) {
            if (other[i] != general[i]) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) return true
        if (other is DataType) {
            return name == other.name && array == other.array && equalsArray(other.general)
        }
        return false
    }
}