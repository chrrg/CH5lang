package compiler.Platform.win32

import compiler.AMT.amt_application
import compiler.Platform.Platform
import compiler.Platform.Section
import java.nio.charset.Charset


class Platform_win32(): Platform {
    companion object {
        const val CH_CODE:Int=0x20
        const val CH_INITIALIZED_DATA:Int= 0x40
        const val CH_UNINITIALIZED_DATA:Int= 0x80
        const val CH_MEM_DISCARDABLE:Int= 0x2000000
        const val CH_MEM_NOT_CHACHED:Int= 0x4000000
        const val CH_MEM_NOT_PAGED:Int= 0x8000000
        const val CH_MEM_SHARED:Int= 0x10000000
        const val CH_MEM_EXECUTE:Int= 0x20000000
        const val CH_MEM_READ:Int= 0x40000000
        const val CH_MEM_WRITE:Int= 0x80000000.toInt()
    }
    fun WriteImportSection(sections:Array<Section>,importApiList:ImportFunctionManager){//得到导入头
        //此时数据段代码段已写入
        val importSectionAddress=VirtualAddressOf(sections,2)
        val import=sections[2]
        val header=Section()
        import.addChild(header)
        for(i in importApiList.getIterator()){
            val image=Image_Import_Directory()
            header.addChild(Section(image))
            i.image=image
        }
        header.addChild(Section(Image_Import_Directory()))//留白
        for(i in importApiList.getIterator()){
            i.image!!.FirstThunk=importSectionAddress+import.getSize()
            for(i2 in i.getIterator()){
                i2.address=importSectionAddress+import.getSize()
                val entry=ImportFunctionEntry()
                i2.entry=entry
                header.addChild(Section(entry))
            }
            header.addChild(Section(ByteArray(4)))
//            header.addChild(Section(ImportFunctionEntry()))//留白
        }
        for(i in importApiList.getIterator()){
            i.image!!.Name=importSectionAddress+import.getSize()
            header.addChild(Section(ImportPathName(i.name.toByteArray())))
        }
        for(i in importApiList.getIterator()){
            for(i2 in i.getIterator()){
                i2.entry!!.entry=importSectionAddress+import.getSize()//+0x400000
                header.addChild(Section(ImportFunctionBody(i2.name)))
            }
        }
    }

    fun getSections():Array<Section>{
        val apiList=ImportFunctionManager()
        val ExitProcess=apiList.addFunc("KERNEL32.DLL","ExitProcess")
        val GetProcessHeap=apiList.addFunc("KERNEL32.DLL","GetProcessHeap")
        val HeapAlloc=apiList.addFunc("KERNEL32.DLL","HeapAlloc")
        val MessageBoxA=apiList.addFunc("User32.DLL","MessageBoxA")
        val printf=apiList.addFunc("msvcrt.DLL","printf")

        val watcher=Watcher()

        val section=arrayOf(Section(),Section(),Section(),Section(),Section(),Section())//data code idata edata rsrc reloc
//        section[0].addChild(Section("Hello World".toByteArray(Charset.forName("GBK"))))
//        section[0].addChild(Section(ByteArray(1)))//ascii=0 字符串终止符
//        section[0].addChild(Section("CH Compiler".toByteArray(Charset.forName("GBK"))))
//        section[0].addChild(Section(ByteArray(1)))//ascii=0 字符串终止符
        val data=section[0]
        val dataAddress=VirtualAddressOf(section,0)
        val hello=Data_ConstantString("你好世界！")

//        val format=Data_ConstantString("%s")
        data.addChild(Section(watcher.post(hello,0x400000+dataAddress+data.getSize())))
//        section[0].addChild(Section(watcher.post(format,0x400000+dataAddress+section[0].getSize())))
        //到这里data已经完全写入了 *************************************************************************
        val code=section[1]
        val codeAddress=VirtualAddressOf(section,1)
        val func1=Func()
        func1.body.addChild(Section(enter()))
//        func1.body.addChild(Section(watcher.desc(push(),hello)))
        func1.body.addChild(Section(mov_EaxEbp(8)))
        func1.body.addChild(Section(push_Eax()))
        func1.body.addChild(Section(watcher.desc(invoke(),printf)))
        func1.body.addChild(Section(leave()))
        func1.body.addChild(Section(ret(4)))

//        section[1].addChild(Section(watcher.descible(push(),hello)))
//        section[1].addChild(Section(watcher.descible(push(),format)))
//        section[1].addChild(Section(watcher.descible(invoke(),printf)))//invoke printf
        code.addChild(Section(watcher.desc(push(),hello)))
        code.addChild(Section(watcher.desc(call(code.getSize()),func1)))
        code.addChild(Section(watcher.desc(invoke(),ExitProcess)))//invoke ExitProcess
        code.addChild(Section(watcher.post(func1,code.getSize())))


//        section[1].addChild(Section(watcher.descible(invoke(),ExitProcess)))//invoke ExitProcess
//        section[1].addChild(Section(watcher.descible(invoke(),ExitProcess)))//invoke ExitProcess
//        section[1].addChild(Section(watcher.descible(invoke(),ExitProcess)))//invoke ExitProcess

        //必须data 和 code写入后才能写下面的
        WriteImportSection(section,apiList)//写入导入表
        for(i in apiList.getIterator()){
            for(i2 in i.getIterator()){
                watcher.post(i2,i2.address+0x400000)
            }
        }

        return section
    }
//    fun PhysicalSizeOf(num:Int,sub:Int=0): Int {
//        if(num==0)return 0
//        var result=0
//        for(i in 0..num+512-sub step 512)
//            result=i
//        return result
//    }
//    fun VirtualSizeOf(num:Int,sub:Int=0): Int {
//        if(num==0)return 0
//        for(i in 0x1000..0xFFFF000 step 0x1000)//268431360
//            if(i>num-sub)
//                return i
//        throw Exception("err")
//    }
    fun PhysicalSizeOf(num:Int): Int {
        if(num%512==0)return num
        return (num/512+1)*512
    }
    fun VirtualSizeOf(num:Int): Int {
        if(num%0x1000==0)return num
        if(num>0xFFFF000)throw Exception("err")
        return (num/0x1000+1)*0x1000
    }
    fun VirtualAddressOf(section:Array<Section>,index:Int): Int {//rva
        var addr=0x1000
        for(i in 0 until index){
            val size=section[i].getSize()
            if(size==0)continue//说明没写入
            addr+=((size/0x1000) +1)*0x1000
        }
        return addr
    }
    override fun build(app: amt_application, output: String): Section {
        val root=Section()
        root.addChild(Section(DOSHeader()))//0~0x40
        root.addChild(Section(DOSStub()))//0x41~0x80
        val peHeader=PEHeader()
        root.addChild(Section(peHeader))
//        if(root.getSize()!=0x178)throw Exception("文件长度验证不通过！")
        val sectionHeaders=arrayOf(SectionHeader(),SectionHeader(),SectionHeader(),SectionHeader(),SectionHeader(),SectionHeader())//段头
        sectionHeaders[0].setName(".data").setCharacteristics(CH_INITIALIZED_DATA + CH_MEM_READ + CH_MEM_WRITE)
        sectionHeaders[1].setName(".code").setCharacteristics(CH_CODE + CH_MEM_READ + CH_MEM_EXECUTE)
        sectionHeaders[2].setName(".idata").setCharacteristics(CH_INITIALIZED_DATA + CH_MEM_READ + CH_MEM_WRITE)
        sectionHeaders[3].setName(".edata").setCharacteristics(CH_INITIALIZED_DATA + CH_MEM_READ)
        sectionHeaders[4].setName(".rsrc").setCharacteristics(CH_INITIALIZED_DATA + CH_MEM_READ)
        sectionHeaders[5].setName(".reloc").setCharacteristics(CH_MEM_DISCARDABLE + CH_INITIALIZED_DATA)
        val SectionBody=getSections()//段体
        //获得段体后根据段体来生成段头
        //先写入段头再写入段体
        val SectionWrited=arrayOfNulls<Section?>(sectionHeaders.size)//写入的段头
        for(i in 0 until sectionHeaders.size)
            if(SectionBody[i].getSize()>0) {//段体有内容段头就应该写入
                val sec=Section(sectionHeaders[i])
                SectionWrited[i]=sec
                peHeader.NumberOfSections++
                root.addChild(sec)//写入段头
            }
        root.addChild(Section(ByteArray((512-root.getSize()%512)%512)))// padding 段头的文件对齐
        val SizeOfHeaders=root.getSize()
        peHeader.SizeOfHeaders=SizeOfHeaders
//        =SectionWrited.size.toShort()//段数量

        //至此段头已写入，开始修复段头
        var SizeOfAllSectionsBefore=0x1000
        var SizeOfAllSectionsBeforeRaw=SizeOfHeaders
        for(i in 0 until sectionHeaders.size) {
            if(SectionWrited[i]==null)continue//说明段体为空没有被写入

            val section=SectionBody[i]//段体
            val size = section.getSize()
            val sec=sectionHeaders[i]
            sec.VirtualSize = size
            when (i) {
                1 -> {//code
                    peHeader.AddressOfEntryPoint = SizeOfAllSectionsBefore
                }
                2 -> {//idata
                    peHeader.ImportTable_Address = SizeOfAllSectionsBefore
                    peHeader.ImportTable_Size=size
                }
            }
            val PhysicalSize = PhysicalSizeOf(size)
            sec.VirtualAddress = SizeOfAllSectionsBefore
            SizeOfAllSectionsBefore += VirtualSizeOf(section.getSize())
            sec.PointerToRawData = SizeOfAllSectionsBeforeRaw
            SizeOfAllSectionsBeforeRaw += PhysicalSize
            sec.SizeOfRawData = PhysicalSize
            if(PhysicalSize%0x1000==0)SizeOfAllSectionsBefore-=0x1000
        }
        peHeader.SizeOfImage=SizeOfAllSectionsBefore
        for(i in SectionBody){//段体对齐
            i.addChild(Section(ByteArray((512-i.getSize()%512)%512)))//// padding 文件对齐
        }

        val secbody=Section()
        for(i in SectionBody)secbody.addChild(i)
        root.addChild(secbody)
        println("生成完成，文件大小："+root.getSize())//
//        root.addChild(Section(ByteArray((512-root.getSize()%512)%512)))
        return root
    }




}