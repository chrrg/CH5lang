package compiler.Platform.win32
interface descable<T>{
    fun update(value:T)
}
@Suppress("UNCHECKED_CAST")
class Watcher{
    val map=HashMap<Any,ArrayList<descable<*>>>()//未解决的
    val data=HashMap<Any,Any>()//解决方案
    fun <T> desc(obj:descable<T>,event:Any): descable<T> {
        data[event]?.let{
            obj.update(it as T)
            return obj
        }
        map[event]?.let{
            it.add(obj)
        }?:let{
            map[event]=arrayListOf(obj as descable<*>)
        }
        return obj
    }
    fun <T,T2> post(event:T2,value:T):T2{
        data[event as Any]=value as Any
        map[event]?.let{
            for(i2 in it)
                (i2 as descable<T>).update(value)
            map.remove(event)//有了解决方案 后续的订阅不再需要发布者发布解决方案了
        }
        return event
    }
}
class push_Eax{
    val k0:Byte=0x50
}
class push:descable<Int>{
    val k0:Byte=0x68
    var Value:Int=0
    override fun update(value: Int) {
        Value=value
    }
}

class mov_EaxEbp(value:Int){
    val k0:Short=0x858B.toShort()//mov eax,dword ptr ss:[ebp+8]
    val value:Int=value
}
class invoke:descable<Int>{
    val k0:Short=0x15FF
    var func:Int=0
    override fun update(value: Int) {
        func=value
    }
}
class enter(k:Short=0){
    val k0:Byte=0xC8.toByte()
    val k1:Short=k
    val k2:Byte=0
}
class ret0{
    val k0:Byte=0xC3.toByte()
}
class ret(k:Short=0){
    val k0:Byte=0xC2.toByte()
    val k1:Short=k
}
class leave{
    val k0:Byte=0xC9.toByte()
}
class call(currentAddress:Int):descable<Int>{
    val k0:Byte=0xE8.toByte()
    var func:Int=0
    @JvmField
    var cur:Int=currentAddress
    override fun update(value: Int) {
        func=value-cur-5
    }
}