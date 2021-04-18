package compiler.Platform.win32

import compiler.Platform.Section
import java.nio.charset.Charset

class DOSHeader{
    val k00: Int = 0x805A4D
    val k01: Int = 0x1
    val k02: Int = 0x100004
    val k03: Int = 0xFFFF
    val k04: Int = 0x140
    val k05: Int = 0x0
    val k06: Int = 0x40
    val k07: Int = 0x0
    val k08: Int = 0x0
    val k09: Int = 0x0
    val k10: Int = 0x0
    val k11: Int = 0x0
    val k12: Int = 0x0
    val k13: Int = 0x0
    val k14 = 0x0
    val k15 = 0x80
}
class DOSStub{
    val k0:Int=0xEBA1F0E
    val k1:Int=0xCD09B400.toInt()
    val k2:Int=0x4C01B821
    val k3:Int=0x687421CD
    val k4:Int=0x70207369
    val k5:Int=0x72676F72
    val k6:Int=0x63206D61
    val k7:Int=0x6F6E6E61
    val k8:Int=0x65622074
    val k9:Int=0x6E757220
    val k10:Int=0x206E6920
    val k11:Int=0x20534F44
    val k12:Int=0x65646F6D
    val k13:Int=0x240A0D2E
    val k14:Int=0x0
    val k15:Int=0x0
}
class PEHeader{
    //第一部分 24size 0x80~0x98
    val Signature:Int=0x4550
    val Machine:Short=0x14C//机器数 标识CPU的数字。
    var NumberOfSections:Short=0//节数	节的数目。Windows加载器限制节的最大数目为96。
    val TimeDateStamp:Int=0//时间/日期标记 UTC时间1970年1月1日00:00起的总秒数的低32位，它指出文件何时被创建。
    val PointerToSymbolTable:Int=0//已经废除
    val NumberOfSymbols:Int=0//已经废除
    val SizeOfOptionalHeader:Short=0xE0//可选头大小 第2部分+第3部分的总大小。这个大小在32位和64位文件中是不同的。对于32位文件来说，它是224；对于64位文件来说，它是240。
    val FileCharacteristics:Short= 0x818F.toShort()//Characteristics 指示文件属性的标志。
    //第二部分 96size 0x98~0xF8
    val Magic:Short=0x10B//魔数 这个无符号整数指出了镜像文件的状态。0x10B表明这是一个32位镜像文件。0x107表明这是一个ROM镜像。0x20B表明这是一个64位镜像文件。
    val MajorLinkerVersion:Byte=0x5//链接器的主版本号。
    val MinorLinkerVersion:Byte=0x0//链接器的次版本号。
    var SizeOfCode:Int=0 //代码节大小 一般放在“.text”节里。如果有多个代码节的话，它是所有代码节的和。必须是FileAlignment的整数倍，是在文件里的大小。
    var SizeOfInitializedData:Int=0//已初始化数大小 一般放在“.data”节里。如果有多个这样的节话，它是所有这些节的和。必须是FileAlignment的整数倍，是在文件里的大小。
    var SizeOfUninitializedData:Int=0//未初始化数大小 一般放在“.bss”节里。如果有多个这样的节话，它是所有这些节的和。必须是FileAlignment的整数倍，是在文件里的大小。
    var AddressOfEntryPoint:Int=0//入口点 当可执行文件被加载进内存时其入口点RVA。对于一般程序镜像来说，它就是启动地址。为0则从ImageBase开始执行。对于dll文件是可选的。
    var BaseOfCode:Int=0//代码基址	当镜像被加载进内存时代码节的开头RVA。必须是SectionAlignment的整数倍。
    var BaseOfData:Int=0//数据基址  当镜像被加载进内存时数据节的开头RVA。（在64位文件中此处被并入紧随其后的ImageBase中。）必须是SectionAlignment的整数倍。
    val ImageBase:Int=0x400000//ImageBase 镜像基址 当加载进内存时镜像的第1个字节的首选地址。它必须是64K的倍数。DLL默认是10000000H。Windows CE 的EXE默认是00010000H。Windows 系列的EXE默认是00400000H。
    val SectionAlignment:Int=0x1000//内存对齐 当加载进内存时节的对齐值（以字节计）。它必须≥FileAlignment。默认是相应系统的页面大小。
    val FileAlignment:Int=0x200//文件对齐 用来对齐镜像文件的节中的原始数据的对齐因子（以字节计）。它应该是界于512和64K之间的2的幂（包括这两个边界值）。默认是512。如果SectionAlignment小于相应系统的页面大小，那么FileAlignment必须与SectionAlignment相等。
    val MajorOperatingSystemVersion:Short=1//主系统的主版本号
    val MinorOperatingSystemVersion:Short=0//主系统的次版本号
    val MajorImageVersion:Short=0//镜像的主版本号
    val MinorImageVersion:Short=0//镜像的次版本号
    val MajorSubsystemVersion:Short=4//子系统的主版本号
    val MinorSubsystemVersion:Short=0//子系统的次版本号
    val Win32VersionValue:Int=0//保留，必须为0
    var SizeOfImage:Int=0//镜像大小
    var SizeOfHeaders:Int=0//头大小
    val CheckSum:Int=0//校验和
    val Subsystem:Short=3//SubSystem = 2:GUI; 3:CUI
    val DllCharacteristics:Short=0//DLL标识
    val SizeOfStackReserve:Int=0x10000//堆栈保留大小 最大栈大小。CPU的堆栈。默认是1MB。
    val SizeOfStackCommit:Int=0x10000//堆栈提交大小	初始提交的堆栈大小。默认是4KB。
    val SizeOfHeapReserve:Int=0x10000//堆保留大小 最大堆大小。编译器分配的。默认是1MB。
    val SizeOfHeapCommit:Int=0//堆栈交大小 初始提交的局部堆空间大小。默认是4KB。
    val LoaderFlags:Int=0//保留，必须为0
    val NumberOfRvaAndSizes:Int=0x10//目录项数目 数据目录项的个数。由于以前发行的Windows NT的原因，它只能为16。
    //第三部分 128size 0xF8~0x178
    var ExportTable_Address:Int=0//导出表的地址
    var ExportTable_Size:Int=0//导出表的大小
    var ImportTable_Address:Int=0//导入表的地址 0x100~0x104
    var ImportTable_Size:Int=0//导入表的大小 0x104
    val ResourceTable_Address:Int=0//资源表的地址
    val ResourceTable_Size:Int=0//资源表的大小
    val ExceptionTable_Address:Int=0//异常表的地址
    val ExceptionTable_Size:Int=0//异常表的大小
    val CertificateTable_Address:Int=0//属性证书表的地址
    val CertificateTable_Size:Int=0//属性证书表的大小
    val BaseRelocationTable_Address:Int=0//基址重定位表的地址
    val BaseRelocationTable_Size:Int=0//基址重定位表的大小
    val DebugTable_Address:Int=0//调试数据起始地址
    val DebugTable_Size:Int=0//调试数据起始大小
    val Architecture:Int=0//保留，必须为0
    val Architecture2:Int=0//保留，必须为0
    val GlobalPtr:Int=0//将被存储在全局指针寄存器中的一个值的RVA。这个结构的Size域必须为0
    val GlobalPtr2:Int=0
    val TLSTable_Address:Int=0//线程局部存储（TLS）表的起始地址
    val TLSTable_Size:Int=0//线程局部存储（TLS）表的起始大小
    val LoadConfig_Address:Int=0//加载配置表的起始地址
    val LoadConfig_Size:Int=0//加载配置表的起始大小
    val BoundImport_Address:Int=0//绑定导入查找表的起始地址
    val BoundImport_Size:Int=0//绑定导入查找表的起始大小
    val IAT_Address:Int=0//导入地址表的起始地址
    val IAT_Size:Int=0//导入地址表的起始大小
    val DelayImportDescriptor_Address:Int=0//延迟导入描述符的起始地址
    val DelayImportDescriptor_Size:Int=0//延迟导入描述符的大小
    val CLRRuntimeHeader_Address:Int=0//CLR运行时头部的地址和大小。(已废除)
    val CLRRuntimeHeader_Size:Int=0//CLR运行时头部的地址和大小。(已废除)
    val padding:Int=0//保留，必须为0
    val padding2:Int=0//保留，必须为0
}
class SectionHeader{
    var Title=ByteArray(8)
    var VirtualSize:Int=0
    var VirtualAddress:Int=0
    var SizeOfRawData:Int=0
    var PointerToRawData:Int=0
    var PointerToRelocations:Int=0
    val PointerToLinenumbers:Int=0
    val NumberOfRelocations:Short=0
    val NumberOfLinenumbers:Short=0
    var Characteristics:Int=0
    fun setName(name:String):SectionHeader{
        for(i in 0 until name.length)Title[i]=name[i].toByte()
        return this
    }
    fun setCharacteristics(value:Int):SectionHeader{
        Characteristics=value
        return this
    }
}
class Image_Import_Directory{
    val OriginalFirstThunk:Int=0//ImportThunkData数组RVA值
    val TimeDataStamp:Int=0//日期时间记录
    val ForwarderChain:Int=0//正向链接索引
    var Name:Int=0//DLL名字符串的RVA值
    var FirstThunk:Int=0//ImportThunkData数组RVA值
}
class ImportFunctionEntry{
    var entry:Int=0
}
class ImportPathName(s: ByteArray){
    val name:ByteArray = s
    val padding:Byte=0
}
class ImportFunctionBody(name:String){
    val k0:Short=0
    val Name:String=name
    val padding:Byte=0
}
//class ImportApiItem(var name:String,var path:String)
//class ImportPath(val path:String,var ImportApiItem:Array<ImportApiItem>)
class ImportFunction(val name:String){
    var address:Int=0
    var entry:ImportFunctionEntry?=null
}
class ImportPath(val name:String){
    val item=ArrayList<ImportFunction>()
    var image:Image_Import_Directory?=null
    fun getIterator()=item.iterator()
    fun getItem(name:String):ImportFunction?{
        for(i in item)
            if(i.name==name)return i
        return null
    }
    fun addFunc(name:String):ImportFunction{
        getItem(name)?.let{
            return it
        }?:let{
            val importFunction=ImportFunction(name)
            item.add(importFunction)
            return importFunction
        }
    }
}
class ImportFunctionManager{
    private val data=ArrayList<ImportPath>()
    fun getIterator()=data.iterator()
//    val iterator=data.iterator()
    fun getPath(name: String): ImportPath? {
        for(i in data)
            if(i.name==name)return i
        return null
    }
    fun addFunc(path:String,name:String):ImportFunction{
        getPath(path)?.let{
            return it.addFunc(name)
        }?:let{
            val importPath=ImportPath(path)
            data.add(importPath)
            return importPath.addFunc(name)
        }
    }
}
class Data_ConstantString(str:String){
    val string:ByteArray=str.toByteArray(Charset.forName("GBK"))
    val padding:Byte=0
}
class Func(){
    val body= Section()
}