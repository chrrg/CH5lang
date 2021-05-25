package ch5.build

/**
 * Push
 * 向栈中插入4个固定的字节。
 * @param value
 * @return
 */
fun push(value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0x68)
    result.dword(value)
    return result
}

fun leave(): CodeItem {
    val result = CodeItem()
    result.byte(0xC9)
    return result
}

fun ret(): CodeItem {
    val result = CodeItem()
    result.byte(0xC3)
    return result
}

/**
 * Push
 * 向段中插入参数所指定的地址的值。
 * @param addr
 * @return
 */
fun push(addr: Addr): CodeItem {
    val result = CodeItem()
    result.byte(0xFF)
    if (addr.value == 0) {
        addr.register?.let {
            result.byte(0x30 + it.value)
        } ?: run {
            throw Exception("?")
        }
    } else {
        addr.register?.let {
            result.byte(0xB0 + it.value)
            if (it is ESP) {
                result.byte(0x20 + it.value)
            }
        } ?: run {
            result.byte(0x35)
        }
        result.dword(addr.value)
    }
    return result
}

/**
 * Mov
 * 将寄存器的值复制到地址所指向的空间
 * @param addr
 * @param register
 * @return
 */
fun mov(addr: Addr, register: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x89)
    addr.register?.let {
        result.byte(0x80 + it.value + register.value * 8)
    } ?: run {
        result.byte(0x05 + register.value * 8)
    }
    result.dword(addr.value, "addr")
    if (addr is AddrSection) {
        result.fix(0, "addr", fun(_: Int, _): Int {
            return 0x400000 + virtualAddressOf(addr.parentSection) + addr.parentSection.offset(addr.section)
        })
    }
    return result
}

/**
 * Mov
 * 将固定值复制到计算器。
 * @param register
 * @param value
 * @return
 */
fun mov(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0xB8 + register.value)
    result.dword(value)
    return result
}

/**
 * Mov
 * 将第二个寄存器的值复制到第一个寄存器中。
 * @param register
 * @param register2
 * @return
 */
fun mov(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x8B)
    result.byte(0xC0 + register.value * 8 + register2.value)
    return result
}

/**
 * Mov
 * 将第2个参数地址所指向的值复制到第1个参数寄存器中
 * @param register
 * @param addr
 * @return
 */
fun mov(register: Win32Register, addr: Addr): CodeItem {
    val result = CodeItem()
    if (addr.value == 0 && addr !is AddrSection) {
        result.byte(0x8B)
        result.byte(register.value * 8 + addr.register!!.value)
    } else {
        addr.register?.let {
//            mov eax,[eax+1] 8B 40 01
//            mov eax,[eax+0x111] 8B 80 11 01 00 00
            result.byte(0x8B)
            result.byte(0x80 + register.value * 8 + it.value)
            result.dword(addr.value, "addr")
        } ?: run {
//            mov eax,[0x111] A1 11 01 00 00
//            mov ecx,[0x111] 8B 0D 11 01 00 00
//            mov edx,[0x111] 8B 15 11 01 00 00
//            mov ebx,[0x111] 8B 1D 11 01 00 00
            if (register == EAX) {
                result.byte(0xA1)
                result.dword(addr.value, "addr")
            } else {
                result.byte(0x8B)
                result.byte(0x05 + register.value * 8)
                result.dword(addr.value, "addr")
            }
        }
        if (addr is AddrSection) {
            result.fix(0, "addr", fun(_: Int, _): Int {
                return 0x400000 + virtualAddressOf(addr.parentSection) + addr.parentSection.offsetDeep(addr.section)
            })
        }
    }
    return result
}

fun lea(register: Win32Register, addr: Addr): CodeItem {
    val result = CodeItem()
    result.byte(0x8D)
    addr.register?.let {
        result.byte(0x80 + register.value * 8 + it.value)
    } ?: run {
        result.byte(0x05 + register.value * 8)
    }
    result.dword(addr.value, "addr")
    if (addr is AddrSection) {
        result.fix(0, "addr", fun(_: Int, _): Int {
            return 0x400000 + virtualAddressOf(addr.parentSection) + addr.parentSection.offsetDeep(addr.section)
        })
    }
    return result
}

/**
 * Invoke
 * 调用系统动态链接库函数。
 * @constructor
 *
 * @param ili
 */
class Invoke(ili: ImportLibraryItem) : CodeItem() {
    init {
        word(0x15ff)
        dword(0, "invoke")
        fix(0, "invoke", fun(value: Int, _): Int {
            return 0x400000 + virtualAddressOf(ili.importManager!!.idataSection!!) + ili.offset
        })
    }
}

/**
 * Call
 * 调用当前程序的函数。
 * @constructor
 *
 * @param fn
 */
class Call(fn: Fun) : CodeItem() {
    init {
        byte(0xE8)
        dword(0, "call")
        fix(0, "call", fun(_: Int, buildStruct): Int {
            val offset = buildStruct.codeSection.offset(fn)//获取要调用的函数的偏移值
            return offset - buildStruct.codeSection.offset(this) - 5//获取当前代码的偏移值
        })

    }
}

/**
 * Win32register
 * 所有Win32寄存器的父类
 * @property value
 * @constructor Create empty Win32register
 */
open class Win32Register(val value: Int)
object EAX : Win32Register(0)
object ECX : Win32Register(1)//this
object EDX : Win32Register(2)
object EBX : Win32Register(3)
object ESP : Win32Register(4)//堆栈的最顶端
object EBP : Win32Register(5)
object ESI : Win32Register(6)
object EDI : Win32Register(7)
open class Win8Register(val value: Int)
object AL : Win8Register(0)
object CL : Win8Register(1)
object DL : Win8Register(2)
object BL : Win8Register(3)
object AH : Win8Register(4)
object CH : Win8Register(5)
object DH : Win8Register(6)
object BH : Win8Register(7)


/**
 * Push
 * 往Stack中插入寄存器的值。
 * @param register
 * @return
 *///EIP：指向CPU下一步即将执行的指令
fun push(register: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x50 + register.value)
    return result
}

/**
 * Pop
 * 从Stack中弹出4个字节的值到寄存器中。
 * @param register
 * @return
 */
fun pop(register: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x58 + register.value)
    return result
}

/**
 * Add
 *
 * @param register
 * @param register2
 * @return
 */
fun add(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x03)
    result.byte(0xC0 + register.value * 8 + register2.value)
    return result
}

fun add(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0x81)
    result.byte(0xC0 + register.value)
    result.dword(value)
    return result
}

fun sub(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x2B)
    result.byte(0xC0 + register.value * 8 + register2.value)
    return result
}

fun sub(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0x81)
    result.byte(0xE8 + register.value)
    result.dword(value)
    return result
}

fun cmp(addr: Addr, register: Win32Register): CodeItem {
    val result = CodeItem()
    addr.register?.let {
        result.byte(0x39)
        result.byte(0x80 + it.value + register.value * 8)
        result.dword(addr.value)
    } ?: run {
        result.byte(0x39)
        result.byte(0x05 + register.value * 8)
        result.dword(addr.value)
    }
    return result
}

fun cmp(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0x81)
    result.byte(0xF8 + register.value)
    result.dword(value)
    return result
}

fun cmp(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x3B)
    result.byte(0xC0 + register.value * 8 + register2.value)
    return result
}

fun mul(register: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0xF7)
    result.byte(0xE0 + register.value)
    return result
}

fun div(register: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0xF7)
    result.byte(0xF0 + register.value)
    return result
}

fun lahf(): CodeItem {
    val result = CodeItem()
    result.byte(0x9F)
    return result
}

fun shl(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0xC1)
    result.byte(0xE0 + register.value)
    result.byte(value)
    return result
}

fun shl(register: Win32Register, register2: Win8Register): CodeItem {
    val result = CodeItem()
    result.byte(0xD3)
    result.byte(0xE0 + register.value)
    assert(register2 == CL)
    return result
}

fun shr(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0xC1)
    result.byte(0xE8 + register.value)
    result.byte(value)
    return result
}

fun shr(register: Win32Register, register2: Win8Register): CodeItem {
    val result = CodeItem()
    result.byte(0xD3)
    result.byte(0xE8 + register.value)
    assert(register2 == CL)
    return result
}


fun and(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x21)
    result.byte(0xC0 + register.value + register2.value * 8)
    return result
}

fun and(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0x81)
    result.byte(0xE0 + register.value)
    result.dword(value)
    result.byte(value)
    return result
}

fun or(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x09)
    result.byte(0xC0 + register.value + register2.value * 8)
    return result
}

fun xor(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x33)
    result.byte(0xC0 + register.value * 8 + register2.value)
    return result
}

fun xor(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0x81)
    result.byte(0xF0 + register.value)
    result.dword(value)
    return result
}

fun jg(code: CodeBox): CodeBox {
    val codeBox = CodeBox()
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x8F)
    result.dword(0, fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(codeBox)
    code.addTo(codeBox)
    return codeBox
}

fun jg(code: CodeBox, code2: CodeBox): CodeBox {
    val codeBox = CodeBox()
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x8F)
    result.dword(0, fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(codeBox)
    val result2 = CodeItem()
    result2.byte(0xE9)
    result2.dword(0, fun(_, _): Int {
        return code2.getSize()
    })
    result2.addTo(code.getAfter())
    code.addTo(codeBox)
    code2.addTo(codeBox)
    return codeBox
}

fun jge(code: CodeBox): CodeBox {
    val codeBox = CodeBox()
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x8D)
    result.dword(0, fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(codeBox)
    code.addTo(codeBox)
    return codeBox
}

fun jge(code: CodeBox, code2: CodeBox): CodeBox {
    val codeBox = CodeBox()
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x8D)
    result.dword(0, fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(codeBox)
    val result2 = CodeItem()
    result2.byte(0xE9)//jmp
    result2.dword(0, fun(_, _): Int {
        return code2.getSize()
    })
    result2.addTo(code.getAfter())
    code.addTo(codeBox)
    code2.addTo(codeBox)
    return codeBox
}

fun jmp(code: CodeBox): CodeBox {
    val codeBox = CodeBox()
    val result = CodeItem()
    result.byte(0xE9)
    result.dword(0, fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(codeBox)
    code.addTo(codeBox)
    return codeBox
}

fun jl(code: CodeBox): CodeBox {
    val codeBox = CodeBox()
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x8C)
    result.dword(0, fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(codeBox)
    code.addTo(codeBox)
    return codeBox
}

fun jle(code: CodeBox): CodeBox {
    val codeBox = CodeBox()
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x8E)
    result.dword(0, fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(codeBox)
    code.addTo(codeBox)
    return codeBox
}

/**
 * Jz
 * 如果EAX值为0那么跳转。
 * @param code
 * @return
 */
fun jz(code: CodeBox): CodeBox {
    val box = CodeBox()
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x84)
    result.dword(0, "jz")
    result.fix(0, "jz", fun(_, _): Int {
        return code.getSize()
    })
    result.addTo(box)
    code.addTo(box)
    return box
}

/**
 * Jnz
 * 如果EAX值不为0，那么跳转。
 * @param code
 * @return
 */
fun jnz(code: CodeBox): CodeItem {
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x85)
    result.dword(0, "jnz")
    result.fix(0, "jnz", fun(_, _): Int {
        return code.getSize() - 1
    })
    return result
}