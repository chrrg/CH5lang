package ch5.build

import java.io.File

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

/**
 * Build
 * 构建器 完成最终的构建
 * @constructor Create empty Build
 */
object Build {
    fun build(app: BuildStruct, output: File) {
        val root = BuildSection()
        val header = AlignSection(0x200)

        root.add(header)
        header.add(DOSHeader)
        header.add(DOSStub)
        val peHeader = header.add(PEHeader())
        if (header.getRawSize() != 0x178) throw Exception("文件大小验证失败！")
        //SectionTable
        val sectionTable = BuildSection()//段表

        val dataSectionHeader =
            sectionTable.add(SectionTableItem("data", (CH_INITIALIZED_DATA + CH_MEM_READ + CH_MEM_WRITE).toInt()))
        val codeSectionHeader = sectionTable.add(SectionTableItem("code", CH_CODE + CH_MEM_READ + CH_MEM_EXECUTE))
        val idataSectionHeader =
            sectionTable.add(SectionTableItem("idata", (CH_INITIALIZED_DATA + CH_MEM_READ + CH_MEM_WRITE).toInt()))
//    sectionTable.add(SectionTableItem("edata", CH_INITIALIZED_DATA + CH_MEM_READ))
//    sectionTable.add(SectionTableItem("rsrc", CH_INITIALIZED_DATA + CH_MEM_READ))
//    sectionTable.add(SectionTableItem("reloc", CH_MEM_DISCARDABLE + CH_INITIALIZED_DATA))

        header.add(sectionTable)

        val importManager = app.importManager
//        for (i in app.importDllPool) importManager.use(i.path, i.name)

        //内置dll调用
//        val exitProcess = importManager.use("KERNEL32.DLL", "ExitProcess")
//        val getProcessHeap = importManager.use("KERNEL32.DLL", "GetProcessHeap")
//        val heapAlloc = importManager.use("KERNEL32.DLL", "HeapAlloc")

//    val messageBoxA = importManager.use("USER32.DLL", "MessageBoxA")
        val dataSection = app.dataSection
        val codeSection = app.codeSection
        val idataSection = IdataSection(importManager)

        dataSectionHeader.setSectionBody(dataSection)
        codeSectionHeader.setSectionBody(codeSection)
        idataSectionHeader.setSectionBody(idataSection)

        val sectionBody = BuildSection()//段体

        sectionBody.add(dataSection)
        sectionBody.add(codeSection)
        sectionBody.add(idataSection)

        root.add(sectionBody)
//        Push(0).addTo(codeSection)
//        Invoke(exitProcess).addTo(codeSection)

        var sizeOfAllSectionsBefore = 0x1000
        var sizeOfAllSectionsBeforeRaw = header.getSize()
        peHeader.fix(sizeOfAllSectionsBeforeRaw, "SizeOfHeaders")
        var numberOfSections = 0
        for (i in sectionTable.filterIsInstance<SectionTableItem>()) {
            numberOfSections++
            when (i.name) {
                "code" -> peHeader.fix(sizeOfAllSectionsBefore, "AddressOfEntryPoint")
                "idata" -> {
                    peHeader.fix(sizeOfAllSectionsBefore, "ImportTable.Entry")
                    peHeader.fix(i.getSectionBody().getRawSize(), "ImportTable.Size")
                }
            }
            i.fix(i.getSectionBody().getRawSize(), "VirtualSize")
            i.fix(sizeOfAllSectionsBefore, "VirtualAddress")
            i.fix(sizeOfAllSectionsBeforeRaw, "PointerToRawData")
            val physicalSize = i.getSectionBody().getSize()
            i.fix(physicalSize, "SizeOfRawData")//PhysicalSize
            sizeOfAllSectionsBefore += i.getSectionBody().getSize(0x1000)
            sizeOfAllSectionsBeforeRaw += physicalSize
//            if (physicalSize % 0x1000 == 0) sizeOfAllSectionsBefore -= 0x1000
        }
        peHeader.fix(numberOfSections, "NumberOfSections")
        peHeader.fix(
            sizeOfAllSectionsBefore,
//            0x1000 + dataSection.getSize(0x1000) + codeSection.getSize(0x1000) + idataSection.getSize(0x1000),
            "SizeOfImage"
        )
        root.doFix(app)
//        println("输出大小：" + root.getSize())
        root.outputFile(output)
    }
}