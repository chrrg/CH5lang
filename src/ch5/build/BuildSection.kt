package ch5.build

import java.io.DataOutputStream
import java.io.FileOutputStream
interface Section{
    fun getByteArray():ByteArray
}
class BuildSection{
    private val before=BuildSection()
    private val list= arrayListOf<Section>()
    private val after=BuildSection()

    /**
     * 将自己输出到输出流中
     */
    private fun outputStream(bw:DataOutputStream){
        before.outputStream(bw)
        for(i in list) bw.write(i.getByteArray())
        after.outputStream(bw)
    }

    /**
     * 将自己输出到文件中
     */
    fun outputFile(file:String){
        val bw = DataOutputStream(FileOutputStream(file))
        outputStream(bw)
        bw.close()
    }
}