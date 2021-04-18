package compiler.Build
import compiler.AMT.amt_application
import compiler.Platform.Platform
import compiler.Platform.Section
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.OutputStream

class Build(var platform: Platform,var app:amt_application,var output:String){
    init{
        val sec=platform.build(app,output)
        output(FileOutputStream("output.exe"),sec)
    }
    fun output(file:FileOutputStream,sec: Section){
        sec.output(file)
    }
//    fun output(){
////        val o = ByteArrayOutputStream();
//
////        o.close();
//
//        val f = FileOutputStream("output.txt")
//        val a=ByteArray(20)
//        a.set(0,0x12)
//        a.set(0,0x34)
//        a.set(0,0x56)
//
//        f.write(a)
//
//    }

}