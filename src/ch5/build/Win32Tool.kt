package ch5.build

object DOSHeader : ByteArraySection() {
    init {
        dword(0x805A4D)
        dword(0x1)
        dword(0x100004)
        dword(0xFFFF)
        dword(0x140)
        dword(0x0)
        dword(0x40)
        dword(0x0)
        dword(0x0)
        dword(0x0)
        dword(0x0)
        dword(0x0)
        dword(0x0)
        dword(0x0)
        dword(0x0)
        dword(0x80)//pe开始的文件偏移地址
    }
}

object DOSStub : ByteArraySection() {
    init {
        dword(0xEBA1F0E)
        dword(0xCD09B400.toInt())
        dword(0x4C01B821)
        dword(0x687421CD)
        dword(0x70207369)
        dword(0x72676F72)
        dword(0x63206D61)
        dword(0x6F6E6E61)
        dword(0x65622074)
        dword(0x6E757220)
        dword(0x206E6920)
        dword(0x20534F44)
        dword(0x65646F6D)
        dword(0x240A0D2E)
        dword(0x0)
        dword(0x0)
    }
}

class PEHeader : FixableSection() {//输出PE头
init {
    //输出这里的时候section应该已经生成了
    dword(0x4550)                     //Signature = "PE"
    word(0x14C)                       //Machine 0x014C;i386
    word(0, "NumberOfSections")
    // word(this.NumberOfSections());            //NumberOfSections = 4
    dword(0x0)                        //TimeDateStamp
    dword(0x0)                        //PointerToSymbolTable = 0
    dword(0x0)                        //NumberOfSymbols = 0
    word(0xE0)                        //SizeOfOptionalHeader
    // if(this.IsDLL)
    // word(0x210E);                      //Characteristics
    // else
    word(0x818F)                      //Characteristics

    word(0x10B)                       //Magic
    byte(0x5)                         //MajorLinkerVersion
    byte(0x0)                         //MinerLinkerVersion
    dword(0, "SizeOfCode")          //SizeOfCode
    dword(0, "SizeOfInitializedData")    //SizeOfInitializedData
    dword(0, "SizeOfUnInitializedData")  //SizeOfUnInitializedData
    dword(0, "AddressOfEntryPoint") //AddressOfEntryPoint
    dword(0, "BaseOfCode")          //BaseOfCode
    dword(0, "BaseOfData")          //BaseOfData
    dword(0x400000)                   //ImageBase 镜像基址
    dword(0x1000)                     //SectionAlignment 内存对齐大小
    dword(0x200)                      //FileAlignment 文件对齐大小
    word(0x1)                         //MajorOSVersion
    word(0x0)                         //MinorOSVersion
    word(0x0)                         //MajorImageVersion
    word(0x0)                         //MinorImageVersion
    word(0x4)                         //MajorSubSystemVerion
    word(0x0)                         //MinorSubSystemVerion
    dword(0x0)                        //Win32VersionValue
    dword(0, "SizeOfImage")         //SizeOfImage
    dword(0, "SizeOfHeaders")       //SizeOfHeaders
    dword(0x0)                        //CheckSum
    word(3)//AppType               //SubSystem = 2:GUI; 3:CUI
    word(0x0)                         //DllCharacteristics
    dword(0x10000)                    //SizeOfStackReserve
    dword(0x10000)                    //SizeOfStackCommit
    dword(0x10000)                    //SizeOfHeapReserve
    dword(0x0)                        //SizeOfHeapRCommit
    dword(0x0)                        //LoaderFlags
    dword(0x10)                       //NumberOfDataDirectories

    dword(0, "ExportTable.Entry")
    dword(0, "ExportTable.Size")

    dword(0, "ImportTable.Entry")
    dword(0, "ImportTable.Size")

    dword(0, "ResourceTable.Entry")
    dword(0, "ResourceTable.Size")

    dword(0x0); dword(0x0)      //Exception_Table
    dword(0x0); dword(0x0)      //Certificate_Table
    //dword(0,"ExceptionTable.Entry",4,0);
    //dword(0,"ExceptionTable.Size",4,0);
    //dword(0,"CertificateTable.Entry",4,0);
    //dword(0,"CertificateTable.Size",4,0);

    dword(0, "RelocationTable.Entry")
    dword(0, "RelocationTable.Size")

    dword(0x0); dword(0x0)      //Debug_Data
    dword(0x0); dword(0x0)      //Architecture
    dword(0x0); dword(0x0)      //Global_PTR
    dword(0x0); dword(0x0)      //TLS_Table
    dword(0x0); dword(0x0)      //Load_Config_Table
    dword(0x0); dword(0x0)      //BoundImportTable
    dword(0x0); dword(0x0)      //ImportAddressTable
    dword(0x0); dword(0x0)      //DelayImportDescriptor
    dword(0x0); dword(0x0)      //COMplusRuntimeHeader
    dword(0x0); dword(0x0)      //Reserved
}
}

open class AlignSection(val align: Int) : BuildSection() {
    override fun getSize(): Int {
        val size = super.getSize()
        if (size % align == 0) return size
        return size - size % align + align
    }

    override fun getByteArray(): ByteArray {
        val size = getSize()
        val byteArr = super.getByteArray()
        return byteArr.copyOf(size)
    }
}

interface DataItem : Section
class DataByteItem(val value: Int) : DataItem {
    override fun getByteArray() = byteArrayOf(value.toByte())
    override fun getSize() = 1
}

class DataSection : AlignSection(0x200) {
    fun add(item: DataItem) {
        super.add(item)
    }
}

interface CodeItem : Section
class CodeSection : AlignSection(0x200) {
    fun add(item: CodeItem) {
        super.add(item)
    }
}

interface ImportItem : Section
class ImageImportDescriptor : FixableSection() {
    init {
        dword(0)
        dword(0)
        dword(0)
        dword(0, "NAME")
        dword(0, "TABLE")
    }
}

class ImageThunkData32 : FixableSection() {
    init {
        dword(0, "ENTRY")
    }
}

class ImportLibraryItem(val dllPath: String, val Method: String) {

}

class ImportManager {
    private val list = mutableListOf<ImportLibraryItem>()
    private val iids = hashMapOf<String, ImageImportDescriptor>()
    private val itds = hashMapOf<ImportLibraryItem, ImageThunkData32>()
    fun getLibraryList(): List<String> {
        val result = mutableListOf<String>()
        for (i in list) {
            if (result.contains(i.dllPath)) continue
            result.add(i.dllPath)
        }
        return result.toList()
    }

    fun get() = list.toList()
    fun get(library: String) = list.filter { it.dllPath == library }.toList()


    fun setIID(library: String, iid: ImageImportDescriptor) {
        iids[library] = iid
    }

    fun getIID(library: String): ImageImportDescriptor {
        return iids[library]!!
    }


    fun setITD(ili: ImportLibraryItem, imageThunkData32: ImageThunkData32) {
        itds[ili] = imageThunkData32
    }

    fun getITD(ili: ImportLibraryItem): ImageThunkData32 {
        return itds[ili]!!
    }

    fun add(ili: ImportLibraryItem) {
        list.add(ili)
    }
}

class ImportSection(val im: ImportManager) : AlignSection(0x200) {
    // 1.写入所有dll
    // 2.写入空白
    // 3.对每一个dll写入对应的函数
    // 4.写入dll名称 0结束 并且修复1步骤的地址
    // 5.写入函数名称 0结束 并且修复3步骤的地址
    init {

//        val import = arrayOf(
//            Pair("KERNEL32.DLL", "GetProcessHeap"),
//            Pair("KERNEL32.DLL", "HeapAlloc"),
//            Pair("USER32.DLL", "MessageBoxA"),
//        )
        val librariesList = im.getLibraryList()
        for (i in librariesList) {
            val iid = ImageImportDescriptor()
            im.setIID(i, iid)
            add(iid)
        }
        if (librariesList.isNotEmpty()) add(ImageImportDescriptor())
        for (i in librariesList) {
            im.getIID(i).dwordFix(0, "TABLE")
            for (j in im.get(i)) {
                val itd = ImageThunkData32()
                im.setITD(j, itd)
                add(itd)
            }
            add(ImageThunkData32())
        }
        for (i in librariesList){
            im.getIID(i).dwordFix(0,"ENTRY")
            add(UTF8ByteArray(i))
        }
        for (i in im.get()){
            im.getITD(i).dwordFix(0,"ENTRY")
            add(WordSection(0))
        }



    }

    private fun add(item: ImportItem) {
        super.add(item)
    }
}

class Symbol(val desc: String) {
    companion object {
        fun new(): Symbol {
            return Symbol("")
        }

        fun new(desc: String): Symbol {
            return Symbol(desc)
        }
    }
}

class SectionTableItem(name: String, Characteristics: Int) : FixableSection() {
    init {
        val l = name.length
        for (i in 0 until l)
            byte(name[i].toInt())
        for (i in l until 8)
            byte(0)
        dword(0, "VirtualSize")
        dword(0, "VirtualAddress")
        dword(0, "SizeOfRawData")
        dword(0, "PointerToRawData")
        dword(0, "PointerToRelocations")
        dword(0x0)                        //PointerToLinenumbers
        word(0x0)                         //NumberOfRelocations
        word(0x0)                         //NumberOfLinenumbers
        dword(Characteristics)
    }
}

fun main() {
    println(DOSHeader.getSize())

    println(DOSStub.getSize())

    println(PEHeader().getSize())

}