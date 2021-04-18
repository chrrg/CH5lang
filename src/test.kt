import compiler.AST.AST
import compiler.Platform.Section
import compiler.Platform.win32.DOSHeader
import compiler.Tokenizer.Tokenizer
import manager.CH5_compiler.getFileCode
import sun.misc.Unsafe
import java.util.*

class a{

    var a=100
    var b=a
    init{
        a=2
        println("init ok")
    }
    companion object{
        var a=4
        init{
            println("static init ok")
        }
    }
}
fun main(){
    println("Test Starting...")
    test()
    println("Test Finish!")
}
fun ok(): Int {
    return 100
}
//fun getUnsafe(): Unsafe {
//    val f = Unsafe::class.java.getDeclaredField("theUnsafe");
//    f.isAccessible = true
//    return f.get(null) as Unsafe
//}
//fun sizeOf(clazz:Class<*>) :Long{
//    var size=0L
//    for(i in clazz.declaredFields) {
//        when (i.type) {
//            Int::class.java -> {
//                size+=4
//            }
//            Short::class.java -> {
//                size+=2
//            }
//            Byte::class.java -> {
//                size+=1
//            }
//            else->{
//                throw Exception("err")
//            }
//        }
//    }
//    return size
////    val fields=clazz.declaredFields
////    if(fields.isEmpty())return 0
////    return unsafe.objectFieldOffset(fields.last()) + when(fields.last().type){
////        Int::class.java ->4
////        Short::class.java ->2
////        Byte::class.java->1
////        else->throw Exception("err")
////    }-unsafe.objectFieldOffset(fields.first());
//}
//fun addressOf(arr:Array<*>)=unsafe.arrayBaseOffset(arr.javaClass).toLong()
//
//fun addressOf(obj:Any):Long{
////    val rr=ByteArray::class.java.declaredFields
//    val arr=arrayOf(obj)
//    return when(unsafe.addressSize()){
//        4->unsafe.getInt(arr, unsafe.arrayBaseOffset(Array<Any>::class.java).toLong()).toLong()
//        8->unsafe.getLong(arr, unsafe.arrayBaseOffset(Array<Any>::class.java).toLong())
//        else->throw Exception("unsupport")
//    }
//}
//fun ByteArray.write(value:Int,offset:Int){
//    this[offset]=(value and 0xff).toByte()
//    this[offset+1]=(value and 0xff00 shr 8).toByte()
//    this[offset+2]=(value and 0xff0000 shr 16).toByte()
//    this[offset+3]=(value and -0x1000000 shr 24).toByte()
//}
//fun ByteArray.write(value:Short,offset:Int){
//    this[offset]=(value.toInt() and 0x00ff).toByte()
//    this[offset+1]=(value.toInt() and 0xff00 shr 8).toByte()
//}
//fun ByteArray.write(value:Byte,offset:Int){
//    this[offset]=value
//}
fun getUnsafeInstance(): Unsafe {
    val f = Unsafe::class.java.getDeclaredField("theUnsafe");
    f.isAccessible = true
    return f.get(null) as Unsafe
}
//open class a2(val s44: Int =2, var s4: Int =20){
//    lateinit var s01:Nothing
//    lateinit var s02:Nothing
//    var s0=1
//    var s=2
//    val b:Int get (){
//        return 80
//    }
//    val test:Byte=0
//}
class aoo{
    object b{
        var bb:Int=0
    }
    fun get(): Int {
        return b.bb
    }
    fun set(value:Int){
        b.bb=value
    }
}
fun getSS(s:Array<Int>,l:Int,h:Int):Int{
    var l=l
    var h=h
    val key=s[l]
    while(l<h){
        while(l<h&&s[h]>=key){
            h--
        }
        if(l<h){
            s[l]=s[h]
        }
        while(l<h&&s[l]<=key){
            l++
        }
        if(l<h){
            s[h]=s[l]
        }
    }
    s[l]=key
    return l
}
fun getss(s:Array<Int>,l:Int,h:Int){
    if(l<h){
        val pos=getSS(s,l,h)
        getss(s,l,pos-1)
        getss(s,pos+1,h)
    }
}

fun test(){
    val data=arrayOf(1,2,43,56,5,23,3,21,214,325,25,43,4,4,5,6,7,65,23,32,4)
    getss(data,0,data.size-1)
    for(i in data){
        println(i)
    }


    if(1==1)return
    val tokenResult= Tokenizer(getFileCode("src/code/test.ch5"))
    val ast= AST.getParse(tokenResult)
    val result=ast.parse()
//    val result=ast.getWords()
    val next=tokenResult.next()
    println("ok")
//    val a= TreeMap<String,String>()
//    a.put("1","1")1

}
fun test0(){


//    val a=DOSHeader()
//    val sec= Section(a)
//    val safe=getUnsafeInstance()
//    val field=DOSHeader::class.java.declaredFields
////    DOSHeader::class.java.getDeclaredField("test")
//    println(field)
//    val size=sec.getObjectSize(a)
//    println(size)
//    val r= objectToByteArray(a)
//    for(i in 0 until r.size){
//        println("0x"+Integer.toHexString(r[i].toInt() and 0xff))
//    }
//    val clazz=a.javaClass
//    val fields=clazz.declaredFields
//    val unsafe=unsafe
//    val size=sizeOf(clazz)
//    println(size)
//    val r=ByteArray(size.toInt())
//    var offset=0
//    for(i in fields){
//        when(i.type){
//            Int::class.java-> {
//                r.write(unsafe.getInt(a, unsafe.objectFieldOffset(i)),offset)
//                offset+=4
//            }
//            Short::class.java->{
//                r.write(unsafe.getShort(a, unsafe.objectFieldOffset(i)),offset)
//                offset+=2
//            }
//            Byte::class.java->{
//                r.write(unsafe.getByte(a, unsafe.objectFieldOffset(i)),offset)
//                offset+=1
//            }
//            else->throw Exception("err")
//        }
////        println(i.name+":"+unsafe.objectFieldOffset(i))
//    }
//    if(offset.toLong()!=size)throw Exception("size验证失败!")

//    val r=arrayOf<Byte>(64)

//    println(unsafe.objectFieldOffset(fields.first()))
//    println(ByteArray::class.java.declaredFields.size)
//    println(a::class.java.declaredFields.size)
//    println(a.javaClass.declaredFields.size)
//
//    unsafe.copyMemory(addressOf(a)-4,addressOf(r),size)

//    sizeOf(a)

//    for(i in fields){
//        println(i.name)
//        val type=i.type
//        if(type == Int::class.java){
//            println(unsafe.getInt(a, unsafe.objectFieldOffset(i)))
//        }else{
//            throw Throwable("仅支持Byte Short Int")
//        }
//    }
//    println(r is Array<*>)
//    println("finishcopy")
//    println("r.size="+r.size)
//    if(r.size>64)throw Exception("size invalid")
//    for(i in 0 until r.size){
//        println("0x"+Integer.toHexString(r[i].toInt() and 0xff))
//    }
//    for(i in r){
//        println(Integer.toHexString(i.toInt()))
//    }
//    println(Arrays.toString(r))
    println("ok")
//    File("output.txt").writeText("123")



//    val f = FileOutputStream("output.txt")
//    f.write(0x12)
//    f.write(0x34)
//    f.write(0x56)
//    f.write(0x12)

//    val a=ByteArray(20)
//    a.set(0,0x12)
//    a.set(1,0x34)
//    a.set(2,0x56)
//    f.write(a)
//    f.write(ss)
//    f.flush()
//    f.close()
//    unsafe
//    val objectOutputStream = ObjectOutputStream(FileOutputStream("output.txt"));
//    objectOutputStream.writeObject(a);
//    objectOutputStream.close();

}