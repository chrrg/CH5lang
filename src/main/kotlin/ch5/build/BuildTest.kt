package ch5.build

fun main() {
    val app = BuildStruct()
//    app.importManager.use()
    app.dataSection.add(GBKByteArray(""))

    val exitProcess = app.importManager.use("KERNEL32.DLL", "ExitProcess")


    val code = app.codeSection

//    val fun1 = Fun()
//    Push(0).addTo(fun1)
//    Invoke(exitProcess).addTo(fun1)
//    Push(0).addTo(fun1)
//    Invoke(exitProcess).addTo(fun1)

    push(0).addTo(code)
    Invoke(exitProcess).addTo(code)
//    fun1.addTo(code)



//    ----------------------------------------
//    Build.build(app, File()"test.exe")
}

