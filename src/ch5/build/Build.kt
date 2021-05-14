package ch5.build

import com.sun.xml.internal.ws.util.ByteArrayBuffer
import java.io.DataOutputStream
import java.io.FileOutputStream

/**
 * 一个build的二进制文件
 */
//class Build {
//    val data = DataManager()
//    val function = FunManager()
//    val entryIndex = 0//入口的函数id
//    fun output(file:String){
//        val bw = DataOutputStream(FileOutputStream(file))
//        bw.write(data.getByteArray())
//        bw.write(function.getByteArray())
//        bw.close()
//    }
//}

//abstract class DataItem {
//    abstract fun value(): ByteArray
//    fun size(): Int {
//        return value().size
//    }
//}
//
//abstract class FunItem {
//    abstract fun value(): ByteArray
//    fun size(): Int {
//        return value().size
//    }
//}
//
//class DataItemByte(val byte: Byte) : DataItem() {
//    override fun value(): ByteArray {
//        return byteArrayOf(byte)
//    }
//}
//
//class DataItemDword(val dword: Int) : DataItem() {
//    override fun value(): ByteArray {
//        TODO()
//    }
//
//}
//
//class DataItemString(val str: String) : DataItem() {
//    override fun value(): ByteArray {
//        return ("$str\\0").toByteArray()
//    }
//}

/**
 * data管理器
 */
//class DataManager {
//    private val list = arrayListOf<DataItem>()
//    fun size(): Int {
//        var result = 0
//        for (i in list) {
//            result += i.value().size
//        }
//        return result
//    }
//    fun getByteArray(): ByteArray {
//        val buf=ByteArrayBuffer()
//        for(i in list){
//            buf.write(i.value())
//        }
//        return buf.rawData
//    }
//}
//
//class FunManager {
//    private val list = arrayListOf<FunItem>()//下标就是索引值
//    fun size(): Int {
//        var result = 0
//        for (i in list) {
//            result += i.value().size
//        }
//        return result
//    }
//    fun getByteArray(): ByteArray {
//        val buf=ByteArrayBuffer()
//        for(i in list){
//            buf.write(i.value())
//        }
//        return buf.rawData
//    }
//}
//如何描述一个函数

//open class a {
//    var funStackSize = 0//函数栈大小
//    var paramSize = 0//参数栈空间
//}

/**
 * fun a param1 int,param2 int:int{
 *      val sum=param1+param2
 *      =sum
 * }
 */
//object fn_a : a() {
//    init {
//        funStackSize = 1
//
//    }
//}