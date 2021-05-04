import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun main() {
//    var results = ""
//    val a = ("FFFEF6C3FFFEF14BFFFEC2CFFFFEE0E3FFFED07BFFFED337FFFF7747FFFF6167FFFFA87FFFFEC58BFFFEC58BFFFEE39FFFFFA87FFFFEF14BFFFED07BFFFEF14BFFFEF6C3FFFED07BFFFF50FFFFFF6423FFFF8237FFFF7A03FFFF71CFFFFF6C57FFFF8237FFFF7747FFFF6F13FFFF7CBFFFFF8237FFFF7A03FFFF66DFFFFF7747FFFFA87FFFFECDBFFFFEBA9BFFFEEE8FFFFF6167FFFFA87FFFFF253FFFFF0F5FFFFF66DFFFFF66DFFFFEE65BFFFEFC3BFFFF2283FFFEBD57FFFECDBFFFFF302F")
//    for (i in Regex("(.{8})").findAll(a)) {
//        val a = i.value
//        var result: UInt = 0u
//        result += (Integer.parseInt(a.substring(0, 2), 16) shl 3 * 8).toUInt()
//        result += (Integer.parseInt(a.substring(2, 4), 16) shl 2 * 8).toUInt()
//        result += (Integer.parseInt(a.substring(4, 6), 16) shl 1 * 8).toUInt()
//        result += (Integer.parseInt(a.substring(6, 8), 16)).toUInt()
//        val char = (0u - result) / 700u
//        results += char.toInt().toChar()
//    }
//    println(results)
//
//
//    var results = ""
//    val a = "***"//base64后
//    for (i in Regex("(.{8})").findAll(a)) {
//        val a = i.value
//        var result: UInt = 0u
//        result += (Integer.parseInt(a.substring(0, 2), 16) shl 3 * 8).toUInt()
//        result += (Integer.parseInt(a.substring(2, 4), 16) shl 2 * 8).toUInt()
//        result += (Integer.parseInt(a.substring(4, 6), 16) shl 1 * 8).toUInt()
//        result += (Integer.parseInt(a.substring(6, 8), 16)).toUInt()
//        val char = (0u - result) / 700u
//        results += char.toInt().toChar()
//    }
//    println(results)

//
//
//    val bis: BufferedInputStream?
//
//    bis = BufferedInputStream(FileInputStream(bmp))
//
//    val len: Int = bis.available()
//    val b = ByteArray(len)
//
//    bis.read(b, 0, len)
//    var bis = File(bmp).readBytes()
//    println(bis)

//    val img = ImageIO.read(File(bmp))
//    val imageType = img.type
//
//    val w = img.getWidth()
//    val h = img.getHeight()
//    val str = "Iron".toCharArray() //73 114 111 110
    // 0 1 0 0 1 0 0 1    //  0 1 1 0 1 0 0 1
    // 0 1 1 1 0 0 1 0
    // 0 1 1 0 1 1 1 1
    // 0 1 1 0 1 1 1 0

    //Iron  73 114 111 110


//    var success = false
//    for (x in 0 until w) {
//        for (y in 0 until h) {
//
//            val x = w - x - 1
//            val y = h - y - 1
//
//
////            println("rgb:$r|$g|$b")
////            if (mat(b) || mat(g) || mat(r)) {
////                println("success")
////                println("$w|$h")
////            }
//
//        }
//    }


    val bmp = "/Users/cocao/Desktop/mistery_picture.bmp"
    val img = ImageIO.read(File(bmp))


    //965×543
    var max = 0
    for (i0 in 0..1) {
        for (i1 in 0..1) {
            for (i2 in 0..1) {
                for (i3 in 0..1) {
                    for (i4 in 0..1) {
                        for (i5 in 0..1) {
                            val sum = resolve(img, i0, i1, i2, i3, i4, i5)
                            max = Math.max(max, sum)
                            println("|$i0|$i1|$i2|$i3|$i4|$i5:$sum")
                        }
                    }
                }
            }
        }
    }
    println("max:" + max)
}

fun resolve(img: BufferedImage, i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): Int {
    val find = "sub"
    val match = arrayListOf<Int>()
//        arrayOf(0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0)//
    for (c in find.toCharArray()) {
        var str = Integer.toBinaryString(c.toInt())
        str = "0".repeat(8 - str.length) + str
        for (i in 0..7) {
            match.add(str[i].toInt() - 48)
        }
    }

    if (i0 == 0) match.reverse()
    var index = 0
    var max = 0
    var x = 0
    var y = 0
    val tui = fun() {
        if (x == 0) {
            y--
        } else {
            x--
        }
    }
    val mat = fun(a: Int): Boolean {
        if (a == match[index]) {
            index++
            max = Math.max(max, index)
            if (index >= match.size) {
//                for (i in 0 until index - 1) {
//                    tui()
//                }
                index = 0

                return true
            }
        } else {
//            for (i in 0 until index - 1) {
//                tui()
//            }
            index = 0
        }
        return false
    }
    val w = img.getWidth()
    val h = img.getHeight()
    var success = false
    val successArr = arrayListOf<Int>()

    fun solve(x: Int, y: Int) {

        var x = x
        var y = y
        if (i1 == 0) {
            x = w - x - 1
        }
        if (i2 == 0) {
            y = h - y - 1
        }
        val pixel = img.getRGB(x, y)
        var r = pixel and 0xff0000 shr 16 and 1
        var g = pixel and 0xff00 shr 8 and 1
        var b = pixel and 0xff and 1
        if (i5 == 0) {
            r = r xor 1
            g = g xor 1
            b = b xor 1
        }
        if (!success) {
            if (i4 == 0) {
                if (mat(b) || mat(g) || mat(r)) {
                    println("success")
                    success = true
//                throw Exception("success")
                }
            } else {
                if (mat(r) || mat(g) || mat(b)) {
                    println("success")
                    success = true

//                throw Exception("success")
                }
            }
        } else {
            if (successArr.size >= 400) {

                var text = ""
                for (i in 0 until successArr.size / 8) {
                    var char = 0
                    char += successArr[i * 8] shl 7
                    char += successArr[i * 8 + 1] shl 6
                    char += successArr[i * 8 + 2] shl 5
                    char += successArr[i * 8 + 3] shl 4
                    char += successArr[i * 8 + 4] shl 3
                    char += successArr[i * 8 + 5] shl 2
                    char += successArr[i * 8 + 6] shl 1
                    char += successArr[i * 8 + 7]
                    text += char.toChar()
                }
                println("result:::===>>$text")
                success = false
                successArr.clear()
                return
//                throw Exception("ok")
            }
            if (i4 == 0) {
                successArr.add(b)
                successArr.add(g)
                successArr.add(r)
            } else {
                successArr.add(r)
                successArr.add(g)
                successArr.add(b)
            }
        }
    }

    fun init() {
        success = false
        successArr.clear()
    }
    init()

    if (i3 == 0) {
        while (x < w) {
            while (y < h) {
                solve(x, y)
                y++
            }
            x++
            y = 0
        }
    } else {
        while (y < h) {
            while (x < w) {
                solve(x, y)
                x++
            }
            y++
            x = 0
        }
    }
    return max

//    if (!success) {
//
//    } else {
//        result.add(b)
//        result.add(g)
//        result.add(r)
//        if (result.size >= 800) {
//            break
//        }
//    }

}