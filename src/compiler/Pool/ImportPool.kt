package compiler.Pool

class ImportPool{//系统动态链接库的导入
    var strs=ArrayList<ImportItem>()
    fun add(path:String,name:String): ImportItem {
        val paths=path.toUpperCase()
        val names=name.toUpperCase()
        for(i in strs)if(i.path==paths&&i.name==names)return i
        val item=ImportItem(paths,names)
        strs.add(item)
        return item
    }
}
class ImportItem(var path:String,var name:String)