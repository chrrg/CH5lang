package ch5.build

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.FileOutputStream

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
        //深度搜索
        throw Exception("?")
    }

    /**
     * 深度获取子section的偏移
     * 不存在则抛异常
     */
    fun offsetDeep(section: Section): Int {
        val a = _offsetDeep(section)
        if (a == -1) throw java.lang.Exception("offset不存在!")
        return a
    }

    fun _offsetDeep(section: Section): Int {
        var size = 0
        for (i in list) {
            if (i == section) return size
            if (i is BuildSection) {
                val result = i._offsetDeep(section)
                if (result != -1) return size + result
            }
            size += i.getSize()
        }
        //深度搜索
        return -1
    }


    fun <T : Section> add(section: T): T {
        if (list.contains(section)) throw Exception("?")
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
    fun doFix(buildStruct: BuildStruct) {
        before?.doFix(buildStruct)
        for (i in list) if (i is FixableSection) i.doFix(buildStruct) else if (i is BuildSection) i.doFix(buildStruct)
        after?.doFix(buildStruct)
    }
}