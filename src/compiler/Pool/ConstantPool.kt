package compiler.Pool

class ConstantPool {
    var strs=ArrayList<ConstantItem>()
    fun add(str:String):ConstantItem{
        for(i in strs)if(i.str==str)return i
        val item=ConstantItem(str)
        strs.add(item)
        return item
    }
}
class ConstantItem(var str:String)
