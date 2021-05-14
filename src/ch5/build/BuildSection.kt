package ch5.build

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.FileOutputStream

open class BuildSection : Section {
    private var before: BuildSection? = null
    private val list = arrayListOf<Section>()
    private var after: BuildSection? = null
    fun getBefore(): BuildSection {
        if (before == null) before = BuildSection()
        return before!!
    }

    fun getAfter(): BuildSection {
        if (after == null) after = BuildSection()
        return after!!
    }

    fun add(section: Section) {
        list.add(section)
    }

    /**
     * 将自己输出到输出流中
     */
    private fun outputStream(bw: DataOutputStream) {
        before?.outputStream(bw)
        for (i in list) bw.write(i.getByteArray())
        after?.outputStream(bw)
    }

    /**
     * 将自己输出到文件中
     */
    fun outputFile(file: String) {
        val bw = DataOutputStream(FileOutputStream(file))
        outputStream(bw)
        bw.close()
    }

    override fun getByteArray(): ByteArray {
        val bw = ByteArrayOutputStream()
        before?.let {
            bw.write(it.getByteArray())
        }
        for (i in list) bw.write(i.getByteArray())
        after?.let {
            bw.write(it.getByteArray())
        }
        return bw.toByteArray()
    }

    override fun getSize(): Int {
        var size = 0
        size += before?.getSize() ?: 0
        for (i in list) size += i.getSize()
        size += after?.getSize() ?: 0
        return size
    }
}