package ch5.build

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.lang.Exception

open class BuildSection : Section, Iterable<Section> {
    private var before: BuildSection? = null
    private val list = arrayListOf<Section>()
    private var after: BuildSection? = null
    private var parent: BuildSection? = null
    fun getParent(): BuildSection {
        return parent!!
    }

    fun getBefore(): BuildSection {
        if (before == null) before = BuildSection()
        return before!!
    }

    fun getAfter(): BuildSection {
        if (after == null) after = BuildSection()
        return after!!
    }

    /**
     * 获取一个子section的偏移值
     */
    fun offset(section: Section): Int {
        var size = 0
        for (i in list) {
            if (i == section) return size
            size += i.getSize()
        }
        throw Exception("?")
    }
    fun<T:Section> add(section: T): T {
        if (list.contains(section))throw Exception("?")
        list.add(section)
        if (section is BuildSection) {
            if (section.parent != null) throw Exception("?")
            section.parent = this
        }
        return section
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

    operator fun get(index: Int) = list[index]
    override fun iterator() = list.iterator()
    fun doFix() {
        before?.doFix()
        for (i in list) if (i is FixableSection) i.doFix() else if (i is BuildSection) i.doFix()
        after?.doFix()
    }
}