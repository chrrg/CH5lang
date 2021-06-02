package ch5.build

object DOSHeader : ByteArraySection() {
    init {
        dword(0x805A4D)
        dword(0x1)
        dword(0x100004)
        dword(0xFFFFFFFF.toInt())
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

/**
 * Pe header
 * 输出PE头
 * @constructor Create empty P e header
 */
class PEHeader : FixableSection() {
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
        dword(0, "SizeOfImage")         //SizeOfImage offset: 0xD0
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

        dword(0, "ImportTable.Entry")//offset: 0x100
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

/**
 * Align section
 * 对齐的段，如果自己不满足对齐条件会自动对齐到指定的对齐值。如对齐512，字节500时，输出将填充12个0字节
 * @property align 对齐值
 * @constructor Create empty Align section
 */
open class AlignSection(private val align: Int) : BuildSection() {
    /**
     * Get raw size
     * 获取对齐前的大小。
     * @return
     */
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

/**
 * Data item
 * 数据段 代表这个段是查到数据当中的。
 * @constructor Create empty Data item
 */
interface DataItem : Section

/**
 * Data byte item
 * 一个字节的数据段
 * @property value
 * @constructor Create empty Data byte item
 */
class DataByteItem(val value: Int) : DataItem {
    override fun getByteArray() = byteArrayOf(value.toByte())
    override fun getSize() = 1
}

/**
 * Data section
 * 一个数据 所有数据的父类
 * @constructor Create empty Data section
 */
class DataSection : AlignSection(0x200) {
    init {

    }
}

/**
 * Code item
 * 一个代码 所有代码的父类
 * @constructor Create empty Code item
 */
open class CodeItem() : FixableSection() {
    /**
     * Add to
     *
     * @param buildSection
     */
    fun addTo(buildSection: BuildSection) {
        buildSection.add(this)
    }
}

/**
 * Addr
 * 地址 可以是寄存器或者偏移值或两者都有
 * @constructor Create empty Addr
 */
open class Addr(var register: Win32Register? = null, var value: Int = 0)


/**
 * Addr section
 * 相对于父段的偏移地址。
 * @property section
 * @property parentSection
 * @constructor Create empty Addr section
 */
class AddrSection(val section: Section, val parentSection: BuildSection) : Addr()


/**
 * Code box
 * 可以插入很多代码的代码盒子。
 * @constructor Create empty Code box
 */
class CodeBox : BuildSection() {
    /**
     * Add to
     *
     * @param buildSection
     * @return
     */

    fun addTo(buildSection: BuildSection): CodeBox {
        buildSection.add(this)
        return this
    }
}

/**
 * Fun
 * 包含完整指令一个函数。
 * @constructor Create empty Fun
 */
open class Fun(localVariableStackStackValue: Int = 0, paramStackValue: Int = 0) : BuildSection() {
    //stackSize 栈大小
    var code = BuildSection()
    private val localVariableStackSize = WordSection(localVariableStackStackValue)//本地栈大小
    private val paramStackSize = WordSection(paramStackValue)//入参栈大小


    /**
     * 设置入参参数的栈大小
     */
    fun setParamSize(value: Int) {
        paramStackSize.value = value
    }

    /**
     * Get param size
     * 获取入参栈大小
     */
    fun getParamSize() = paramStackSize.value

    fun allocStack(size: Int): Int {
        val oldOffset = localVariableStackSize.value
        localVariableStackSize.value += size
        return -oldOffset - 4
    }

    init {
        add(ByteSection(0xC8))//enter
        add(localVariableStackSize)//局部变量大小//sub esp,size2
        add(ByteSection(0))//固定
        add(code)
        add(ByteSection(0xC9))
//        if (stackSize == 0) {
//            add(ByteSection(0xC3))
//        } else {
        add(ByteSection(0xc2))
        add(paramStackSize)//入参的大小
//        }
    }
}


/**
 * Code section
 * 代码段
 * @constructor Create empty Code section
 */
class CodeSection() : AlignSection(0x200) {
    init {

    }
}

/**
 * Import item
 * 一个导入
 * @constructor Create empty Import item
 */
interface ImportItem : Section

/**
 * Image import descriptor
 *
 * @constructor Create empty Image import descriptor
 */
class ImageImportDescriptor : FixableSection() {
    init {
        dword(0)
        dword(0)
        dword(0)
        dword(0, "NAME")
        dword(0, "TABLE")
    }
}

/**
 * Image thunk data32
 *
 * @constructor Create empty Image thunk data32
 */
class ImageThunkData32 : FixableSection() {
    init {
        dword(0, "ENTRY")//MessageBoxA_ENTRY
    }
}

/**
 * Import library item
 *
 * @property dllPath
 * @property method
 * @constructor Create empty Import library item
 */
class ImportLibraryItem(val dllPath: String, val method: String) {
    var importManager: ImportManager? = null
    var offset = 0
}

/**
 * Import manager
 * 导入管理器 用这个类来管理这个程序所有导入的dll动态链接库。
 * @constructor Create empty Import manager
 */
class ImportManager {
    var idataSection: IdataSection? = null
    private val list = mutableListOf<ImportLibraryItem>()
    private val iid = hashMapOf<String, ImageImportDescriptor>()
    private val itd = hashMapOf<ImportLibraryItem, ImageThunkData32>()

    /**
     * Get library list
     *
     * @return
     */
    fun getLibraryList(): List<String> {
        val result = mutableListOf<String>()
        for (i in list) {
            if (result.contains(i.dllPath)) continue
            result.add(i.dllPath)
        }
        return result.toList()
    }

    /**
     * Get
     *
     */
    fun get() = list.toList()

    /**
     * Get
     *
     * @param library
     */
    fun get(library: String) = list.filter { it.dllPath == library }.toList()


    /**
     * Set iid
     *
     * @param library
     * @param iid
     */
    fun setIID(library: String, iid: ImageImportDescriptor) {
        this.iid[library] = iid
    }

    /**
     * Get iid
     *
     * @param library
     * @return
     */
    fun getIID(library: String): ImageImportDescriptor {
        return iid[library]!!
    }


    /**
     * Set itd
     *
     * @param ili
     * @param imageThunkData32
     */
    fun setITD(ili: ImportLibraryItem, imageThunkData32: ImageThunkData32) {
        itd[ili] = imageThunkData32
    }

    /**
     * Get itd
     *
     * @param ili
     * @return
     */
    fun getITD(ili: ImportLibraryItem): ImageThunkData32 {
        return itd[ili]!!
    }

    /**
     * Use
     * 使用了这个dll
     * @param dllPath
     * @param method
     * @return
     */
    fun use(dllPath: String, method: String): ImportLibraryItem {
        val result = list.filter { it.dllPath == dllPath }.filter { it.method == method }
        if (result.isNotEmpty()) return result[0]
        val ili = ImportLibraryItem(dllPath, method)
        ili.importManager = this
        list.add(ili)
        return ili
    }
}

/**
 * Idata section
 * 导入段体
 * @constructor
 *
 * @param im
 */
class IdataSection(im: ImportManager) : AlignSection(0x200) {
    // 1.写入所有dll
    // 2.写入空白
    // 3.对每一个dll写入对应的函数
    // 4.写入dll名称 0结束 并且修复1步骤的地址
    // 5.写入函数名称 0结束 并且修复3步骤的地址
    init {
        im.idataSection = this
        val librariesList = im.getLibraryList()
//        println("加载了${librariesList.size}个动态链接库，共${im.get().size}个函数！")
        for (i in librariesList) {
            val iid = ImageImportDescriptor()
            im.setIID(i, iid)
            add(iid)
        }
        if (librariesList.isNotEmpty()) add(ImageImportDescriptor())
        for (i in librariesList) {
            im.getIID(i).fix(getRawSize(), "TABLE", fun(value: Int, _) = value + virtualAddressOf(this))
            for (j in im.get(i)) {
                j.offset = getRawSize()
                val itd = ImageThunkData32()//MessageBoxA_ENTRY
                im.setITD(j, itd)
                add(itd)
            }
            add(ImageThunkData32())
        }
        for (i in librariesList) {
            im.getIID(i).fix(getRawSize(), "NAME", fun(offset: Int, _) = offset + virtualAddressOf(this))
            add(UTF8ByteArray(i))
        }
        for (i in im.get()) {
            im.getITD(i).fix(getRawSize(), "ENTRY", fun(offset: Int, _) = offset + virtualAddressOf(this))
            add(WordSection(0))
            add(UTF8ByteArray(i.method))
        }
    }

    private fun add(item: ImportItem) {
        super.add(item)
    }
}


/**
 * Section table item
 * 一个段表头
 * @property name
 * @constructor
 *
 * @param Characteristics
 */
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

    /**
     * Get section body
     *
     * @return
     */
    fun getSectionBody(): AlignSection {
        return sectionBody!!
    }

    /**
     * Set section body
     *
     * @param section
     */
    fun setSectionBody(section: AlignSection) {
        sectionBody = section
    }
}

/**
 * Virtual address of
 * 计算一个段的虚拟地址
 * @param buildSection
 * @return
 */
fun virtualAddressOf(buildSection: BuildSection): Int {
    var size = 0x1000
    for (j in buildSection.getParent()) {
        if (j == buildSection) break
        size += j.getSize(0x1000)//vra
    }
    return size
}

/**
 * Main
 * 测试
 */
fun main() {
    println(DOSHeader.getSize())

    println(DOSStub.getSize())

    println(PEHeader().getSize())

}