package ch5.build

fun push(value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0x68)
    result.dword(value)
    return result
}

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
        } ?: run {
            result.byte(0x35)
        }
        result.dword(addr.value)
    }
    return result
}

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
        result.fix(0, "addr", fun(_: Int): Int {
            return 0x400000 + virtualAddressOf(addr.parentSection) + addr.parentSection.offset(addr.section)
        })
    }
    return result
}

fun mov(register: Win32Register, value: Int): CodeItem {
    val result = CodeItem()
    result.byte(0xB8 + register.value)
    result.dword(value)
    return result
}

fun mov(register: Win32Register, register2: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x8B)
    result.byte(0xC0 + register.value * 8 + register2.value)
    return result
}

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
            result.fix(0, "addr", fun(_: Int): Int {
                return 0x400000 + virtualAddressOf(addr.parentSection) + addr.parentSection.offsetDeep(addr.section)
            })
        }
    }
    return result
}

class Invoke(ili: ImportLibraryItem) : CodeItem() {
    init {
        word(0x15ff)
        dword(0, "invoke")
        fix(0, "invoke", fun(value: Int): Int {
            return 0x400000 + virtualAddressOf(ili.importManager!!.idataSection!!) + ili.offset
        })
    }
}

class Call(fn: Fun) : CodeItem() {
    init {
        byte(0xE8)
        dword(0, "call")
        fix(0, "call", fun(_: Int): Int {
            val offset = getCodeSection().offsetDeep(fn)//获取要调用的函数的偏移值
            return offset - getCodeSection().offsetDeep(this) - 5//获取当前代码的偏移值
        })

    }
}

open class Win32Register(val value: Int)
object EAX : Win32Register(0)
object ECX : Win32Register(1)//this
object EDX : Win32Register(2)
object EBX : Win32Register(3)
object ESP : Win32Register(4)//堆栈的最顶端
object EBP : Win32Register(5)
object ESI : Win32Register(6)
object EDI : Win32Register(7)

//EIP：指向CPU下一步即将执行的指令
fun push(register: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x50 + register.value)
    return result
}

fun pop(register: Win32Register): CodeItem {
    val result = CodeItem()
    result.byte(0x58 + register.value)
    return result
}

fun jz(code: CodeBox): CodeItem {
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x84)
    result.dword(0, "jz")
    result.fix(0, "jz", fun(_): Int {
        return code.getSize()
    })
    return result
}

fun jnz(code: CodeBox): CodeItem {
    val result = CodeItem()
    result.byte(0x0F)
    result.byte(0x85)
    result.dword(0, "jnz")
    result.fix(0, "jnz", fun(_): Int {
        return code.getSize()
    })
    return result
}