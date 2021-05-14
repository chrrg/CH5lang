package ch5.build

const val CH_CODE = 0x20
const val CH_INITIALIZED_DATA = 0x40
const val CH_UNINITIALIZED_DATA = 0x80
const val CH_MEM_DISCARDABLE = 0x2000000
const val CH_MEM_NOT_CHACHED = 0x4000000
const val CH_MEM_NOT_PAGED = 0x8000000
const val CH_MEM_SHARED = 0x10000000
const val CH_MEM_EXECUTE = 0x20000000
const val CH_MEM_READ = 0x40000000
const val CH_MEM_WRITE = 0x80000000

fun main() {
    val root = BuildSection()
    root.add(DOSHeader)
    root.add(DOSStub)
    val peHeader = PEHeader()
    root.add(peHeader)
    if (root.getSize() != 0x178) throw Exception("文件大小验证失败！")
    //SectionTable
    val sectionTable = BuildSection()//段表

    sectionTable.add(SectionTableItem("data", (CH_INITIALIZED_DATA + CH_MEM_READ + CH_MEM_WRITE).toInt()))
    sectionTable.add(SectionTableItem("code", CH_CODE + CH_MEM_READ + CH_MEM_EXECUTE))
    sectionTable.add(SectionTableItem("idata", (CH_INITIALIZED_DATA + CH_MEM_READ + CH_MEM_WRITE).toInt()))
    sectionTable.add(SectionTableItem("edata", CH_INITIALIZED_DATA + CH_MEM_READ))
    sectionTable.add(SectionTableItem("rsrc", CH_INITIALIZED_DATA + CH_MEM_READ))
    sectionTable.add(SectionTableItem("reloc", CH_MEM_DISCARDABLE + CH_INITIALIZED_DATA))

    root.add(sectionTable)

    //SectionBody
    val sectionBody = BuildSection()//段体

    val dataSection = DataSection()
    val codeSection = CodeSection()
    val importSection = ImportSection(ImportManager())
    sectionBody.add(dataSection)
    sectionBody.add(codeSection)
    sectionBody.add(importSection)

    root.add(sectionBody)


    root.outputFile("1.txt")
}

