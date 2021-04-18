package compiler.Platform

import compiler.AMT.amt_application
import compiler.Platform.win32.Platform_win32
import manager.CompileError
import sun.misc.Unsafe
import java.io.FileOutputStream

interface Platform {
    companion object{

    }
    fun build(app:amt_application,output:String):Section
}
class Section{
    private var data:ByteArray?=null
    private var target:Any?=null
    private var size:Int=0
    private var child:ArrayList<Section>?=null
    constructor()
    constructor(bytes: ByteArray){
        setData(bytes)
    }
    constructor(secs: Array<Section>){
        for(i in secs)addChild(i)
    }
    constructor(any: Any){
        target=any
        size=getObjectSize(any).toInt()
    }
    private val unsafe=getUnsafeInstance()
    operator fun get(index:Int)=child!![index]
    fun setData(bytes: ByteArray){
        data=bytes
    }
//    fun isEmpty(index:Int):Boolean{
//        child?.let{
//            if(index>=it.size)return true
//            return it[index].getSize()==0
//        }?:return true
//    }
//    fun getTarget():Any?{
//        return target
//    }
    private fun getObjectSize(target:Any):Long{
        var size =0L
        for(i in target.javaClass.declaredFields) {
            if (i.modifiers == 1) continue//加了lateinit的跳过
            size += when (i.type) {
//                Nothing::class.java -> 0
                Int::class.java -> 4
                Short::class.java -> 2
                Byte::class.java -> 1
//                Any::class.java -> throw Exception("Any?")
//                Array<Int>::class.java -> (unsafe.getObject(target, unsafe.objectFieldOffset(i)) as Array<*>).size * 4L
//                Array<Short>::class.java -> (unsafe.getObject(
//                    target,
//                    unsafe.objectFieldOffset(i)
//                ) as Array<*>).size * 2L
//                Array<Byte>::class.java -> (unsafe.getObject(target, unsafe.objectFieldOffset(i)) as Array<*>).size * 1L
                Section::class.java->(unsafe.getObject(target, unsafe.objectFieldOffset(i)) as Section).getSize().toLong()
                ByteArray::class.java -> (unsafe.getObject(target, unsafe.objectFieldOffset(i)) as ByteArray).size.toLong()
                String::class.java -> (unsafe.getObject(
                    target,
                    unsafe.objectFieldOffset(i)
                ) as String).toByteArray().size.toLong()
                else -> {
//                    if (i.type.isArray) {
//                        var tmp = 0L
//                        val arr = unsafe.getObject(target, unsafe.objectFieldOffset(i)) as Array<*>
//                        for (item in arr) {
//                            if (item == null) continue
//                            tmp += getObjectSize(item)
//                        }
//                        tmp
//                    } else
                        throw Exception("err")
                }
            }
        }
        return size
    }
    private fun getUnsafeInstance(): Unsafe {
        val f = Unsafe::class.java.getDeclaredField("theUnsafe");
        f.isAccessible = true
        return f.get(null) as Unsafe
    }
//    private fun writeObjectToByteArray(index:Int,bytes:ByteArray,obj: Any):ByteArray{
//
//    }
    private fun objectToStream(stream:FileOutputStream,obj:Any){
        val clazz=obj.javaClass
        val fields=clazz.declaredFields

        for(i in fields){
            if(i.modifiers==1)continue
            when(i.type){
                Int::class.java-> {
                    val value=unsafe.getInt(obj, unsafe.objectFieldOffset(i))
                    stream.write(byteArrayOf((value and 0xff).toByte(),(value and 0xff00 shr 8).toByte(),(value and 0xff0000 shr 16).toByte(),(value and -0x1000000 shr 24).toByte()))
                }
                Short::class.java->{
                    val value=unsafe.getShort(obj, unsafe.objectFieldOffset(i))
                    stream.write(byteArrayOf((value.toInt() and 0x00ff).toByte(),(value.toInt() and 0xff00 shr 8).toByte()))
                }
                Byte::class.java->{
                    stream.write(byteArrayOf(unsafe.getByte(obj, unsafe.objectFieldOffset(i))))
                }
                Section::class.java->{
                    (unsafe.getObject(target, unsafe.objectFieldOffset(i)) as Section).output(stream)
                }
                ByteArray::class.java->{
                    stream.write(unsafe.getObject(target, unsafe.objectFieldOffset(i)) as ByteArray)
                }
                String::class.java->{
                    stream.write((unsafe.getObject(target, unsafe.objectFieldOffset(i)) as String).toByteArray())
                }
                else->{
                    throw Exception("Err!")
                }
            }
        }
    }
//    private fun objectToByteArray(obj: Any):ByteArray{
//        val clazz=obj.javaClass
//        val fields=clazz.declaredFields
//        val size=getObjectSize(obj)
//        val result=ByteArray(size.toInt())
//        var offset=0
//        for(i in fields){
//            writeObjectToByteArray()
//            when(i.type){
//                Int::class.java-> {
//                    val value=unsafe.getInt(obj, unsafe.objectFieldOffset(i))
//                    result[offset]=(value and 0xff).toByte()
//                    result[offset+1]=(value and 0xff00 shr 8).toByte()
//                    result[offset+2]=(value and 0xff0000 shr 16).toByte()
//                    result[offset+3]=(value and -0x1000000 shr 24).toByte()
//                    offset+=4
//                }
//                Short::class.java->{
//                    val value=unsafe.getShort(obj, unsafe.objectFieldOffset(i))
//                    result[offset]=(value.toInt() and 0x00ff).toByte()
//                    result[offset+1]=(value.toInt() and 0xff00 shr 8).toByte()
//                    offset+=2
//                }
//                Byte::class.java->{
//                    result[offset]=unsafe.getByte(obj, unsafe.objectFieldOffset(i))
//                    offset+=1
//                }
////                ByteArray::class.java->{
////                    val byteArray=unsafe.getObject(target, unsafe.objectFieldOffset(i)) as ByteArray
////                    offset+=byteArray.size
////                }
////                String::class.java->{
////                    val byteArray=(unsafe.getObject(target, unsafe.objectFieldOffset(i)) as String).toByteArray()
////                    offset+=byteArray.size
////                }
//                else->throw Exception("err")
//            }
//        }
//        return result
//    }
    fun iterator(): MutableIterator<Section> {
        if(child==null)child=ArrayList()
        return child!!.iterator()
//        child?.let{
//            return it.iterator()
//        }?:let{
//            return ArrayList<Section>(0).iterator()
//        }
    }
    fun getSize():Int{
        var tmp=0
        size?.let{
            tmp+=it
        }
        data?.let{
            tmp+=it.size
        }
        child?.let{
            for(i in it)tmp+=i.getSize()
        }
        return tmp
    }
    fun addChild(i:Section){
        child?.let {
            it.add(i)
        }?:let{
            child= arrayListOf(i)
        }
    }
    fun output(file: FileOutputStream){
        target?.let {
            objectToStream(file,it)
//            file.write(objectToByteArray(it))
        }
        data?.let{
            file.write(it)
        }
        child?.let{
            for(i in it)i.output(file)
        }
    }
}


fun Platform_build(platform: String): Platform {
    return when(platform){
        "win32"->Platform_win32()
        else->throw CompileError("无此平台！",null)
    }

}