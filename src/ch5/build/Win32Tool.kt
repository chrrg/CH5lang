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
    word(0x4)                         //MajorSubSystemVersion
    word(0x0)                         //MinorSubSystemVersion
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
    dword(0x0); dword(0x0)      //COMPlusRuntimeHeader
    dword(0x0); dword(0x0)      //Reserved
}
}

open class AlignSection(private val align: Int) : BuildSection() {
    fun getRawSize(): Int {
        return super.getSize()
    }

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
    init {

    }
}

open class CodeItem() : FixableSection() {
    private var codeSection: CodeSection? = null
    fun getCodeSection(): CodeSection {
        return codeSection!!
    }

    fun setCodeSection(code: CodeSection) {
        codeSection = code
    }

    fun addTo(section: CodeSection) {
        setCodeSection(section)
        section.add(this)
    }

    fun addTo(box: CodeBox) {
        setCodeSection(box.codeSection)
        box.add(this)
    }
}

open class Addr {
    var register: Win32Register? = null
    var value = 0
}

class AddrSection(val section: Section, val parentSection: BuildSection) : Addr()


class CodeBox(val codeSection: CodeSection) : BuildSection() {
    fun addTo(code: CodeBox) {
        code.add(this)
    }
}

open class Fun(val app: BuildStruct) : BuildSection() {
    //    var offset = 0
    var code = CodeBox(app.codeSection)

    init {
        val size = 0//局部变量大小
        add(ByteSection(0xC8))//enter
        add(WordSection(size))//局部变量大小//sub esp,size2
        add(ByteSection(0))//固定
        add(code)
        add(ByteSection(0xC9))
        if (size == 0) {
            add(ByteSection(0xC3))
        } else {
            add(ByteSection(0xc2))
            add(WordSection(size))
        }
    }

//    fun addTo(codeSection: CodeSection) {
//        offset = codeSection.getRawSize()
//        codeSection.add(this)
//    }
}

class CodeSection() : AlignSection(0x200) {
    init {

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
        dword(0, "ENTRY")//MessageBoxA_ENTRY
    }
}

class ImportLibraryItem(val dllPath: String, val method: String) {
    var importManager: ImportManager? = null
    var offset = 0
}

class ImportManager {
    var idataSection: IdataSection? = null
    private val list = mutableListOf<ImportLibraryItem>()
    private val iid = hashMapOf<String, ImageImportDescriptor>()
    private val itd = hashMapOf<ImportLibraryItem, ImageThunkData32>()
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
        this.iid[library] = iid
    }

    fun getIID(library: String): ImageImportDescriptor {
        return iid[library]!!
    }


    fun setITD(ili: ImportLibraryItem, imageThunkData32: ImageThunkData32) {
        itd[ili] = imageThunkData32
    }

    fun getITD(ili: ImportLibraryItem): ImageThunkData32 {
        return itd[ili]!!
    }

    //    fun add(ili: ImportLibraryItem): ImportLibraryItem {
//        list.add(ili)
//        ili.importManager = this
//        return ili
//    }
    fun use(dllPath: String, method: String): ImportLibraryItem {
        val result = list.filter { it.dllPath == dllPath }.filter { it.method == method }
        if (result.isNotEmpty()) return result[0]
        val ili = ImportLibraryItem(dllPath, method)
        ili.importManager = this
        list.add(ili)
        return ili
    }
}

class IdataSection(im: ImportManager) : AlignSection(0x200) {
    // 1.写入所有dll
    // 2.写入空白
    // 3.对每一个dll写入对应的函数
    // 4.写入dll名称 0结束 并且修复1步骤的地址
    // 5.写入函数名称 0结束 并且修复3步骤的地址
    init {
        im.idataSection = this
        val librariesList = im.getLibraryList()
        println("加载了${librariesList.size}个动态链接库，共${im.get().size}个函数！")
        for (i in librariesList) {
            val iid = ImageImportDescriptor()
            im.setIID(i, iid)
            add(iid)
        }
        if (librariesList.isNotEmpty()) add(ImageImportDescriptor())
        for (i in librariesList) {
            im.getIID(i).fix(getRawSize(), "TABLE", fun(value: Int) = value + virtualAddressOf(this))
            for (j in im.get(i)) {
                j.offset = getRawSize()
                val itd = ImageThunkData32()//MessageBoxA_ENTRY
                im.setITD(j, itd)
                add(itd)
            }
            add(ImageThunkData32())
        }
        for (i in librariesList) {
            im.getIID(i).fix(getRawSize(), "NAME", fun(offset: Int) = offset + virtualAddressOf(this))
            add(UTF8ByteArray(i))
        }
        for (i in im.get()) {
            im.getITD(i).fix(getRawSize(), "ENTRY", fun(offset: Int) = offset + virtualAddressOf(this))
            add(WordSection(0))
            add(UTF8ByteArray(i.method))
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

class SectionTableItem(val name: String, Characteristics: Int) : FixableSection() {
    private var sectionBody: AlignSection? = null

    init {
        val l = name.length
        byte('.'.toInt())
        for (i in 0 until l)
            byte(name[i].toInt())
        for (i in l until 7)
            byte(0)
        dword(0, "VirtualSize")
        dword(0, "VirtualAddress")
        dword(0, "SizeOfRawData")
        dword(0, "PointerToRawData")
        dword(0, "PointerToRelocations")
        dword(0x0)                        //PointerToLineNumbers
        word(0x0)                         //NumberOfRelocations
        word(0x0)                         //NumberOfLineNumbers
        dword(Characteristics)
    }

    fun getSectionBody(): AlignSection {
        return sectionBody!!
    }

    fun setSectionBody(section: AlignSection) {
        sectionBody = section
    }
}

fun virtualAddressOf(buildSection: BuildSection): Int {
    var size = 0x1000
    for (j in buildSection.getParent()) {
        if (j == buildSection) break
        size += j.getSize(0x1000)//vra
    }
    return size
}

fun main() {
    println(DOSHeader.getSize())

    println(DOSStub.getSize())

    println(PEHeader().getSize())

}