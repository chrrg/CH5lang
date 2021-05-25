package ch5.parser

import ch5.ast.*
import ch5.build.CodeBox
import ch5.build.Fun
import ch5.build.ImportLibraryItem
import ch5.token.Tokenizer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

/**
 * Data type
 * 数据类型描述类 数组是一种类
 * @constructor Create empty Data type
 */
abstract class DataType {
    abstract fun getSize(): Int

    override operator fun equals(other: Any?): Boolean {
        if (super.equals(other)) return true
        if (this is ReferenceType && other is ReferenceType) {
            if (ref != other.ref) return false
            if (general.size != other.general.size) return false
            for (i in 0 until this.general.size) {
                if (general[i] != other.general[i]) return false
            }
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}

/**
 * Primitive type
 * 原始类型
 * @property name
 * @constructor Create empty Primitive type
 */
abstract class PrimitiveType(val name: String) : DataType()
object BoolType : PrimitiveType("bool") {
    override fun getSize() = 1
}

object IntType : PrimitiveType("int") {
    override fun getSize() = 4
}

object StringType : PrimitiveType("string") {
    override fun getSize() = 4
}

object VoidType : PrimitiveType("void") {
    override fun getSize() = 0
}

/**
 * Reference type
 * 引用类型
 * @constructor Create empty Reference type
 */
open class ReferenceType() : DataType() {
    var ref: MyClass? = null//类
    var general = arrayListOf<DataType>()//泛型列表
    override fun getSize() = 4

}

/**
 * Def local variable
 * 定义在函数内的变量
 * @constructor Create empty Def local variable
 */
class DefLocalVariable {
    var name = ""//变量名
    var type: DataType? = null
    var isConst = false
    var offset = 0
}

/**
 * Def variable
 * 定义的变量
 * @constructor Create empty Def variable
 */
class DefVariable {
    var name = ""//变量名
    var type: DataType? = null
    var isConst = false
    var initCode: CodeBox? = null
    var ast: ASTOuterVar? = null
    var space: Space? = null
    fun use() {
        space?.use()
    }
}

class DefImport(var alias: String, var ili: ImportLibraryItem) {
    var func: DefFunction? = null
}

/**
 * Def fun param
 * 定义的函数参数
 * @property name
 * @property type
 * @constructor Create empty Def fun param
 */
class DefFunParam(val name: String, val type: DataType)

/**
 * Def function
 * 定义的函数
 * @constructor Create empty Def function
 */
class DefFunction {
    var name = ""//函数名
    val func = Fun()
    val param = arrayListOf<DefFunParam>()
    var ast: ASTOuterFun? = null
    var type: DataType = VoidType//返回类型
    var space: MyClass? = null
    fun use() {
        space?.use()
    }
}

/**
 * Pre static
 * 预编译的static
 * @constructor
 *
 * @param app
 */
open class PreStatic(app: Application) : MyStatic(app) {

}

/**
 * Pre file
 * 预编译的文件对象
 * @property file
 * @constructor
 *
 * @param app
 */
class PreFile(app: Application, val file: File) : PreStatic(app) {


    init {
        val tokenizer = Tokenizer(getFileCode(file))//并没有进行分词
        val ast = AST(tokenizer).parse()
//        println(ch5.ast.toString())
        //文件转语法树结束

        //预编译命名空间树
        //原则：有import先预编译import
        //有定义变量，解析变量名 链接类型
        //有定义函数，解析函数名 链接参数和返回值类型
        //todo 将ast转为MyStatic 并写入代码
        //todo 将MyStatic的main函数赋值到app.entry上面
        for (i in ast.container) {
            when (i) {
                is ASTImport -> {
                    val from = i.from
                    from?.let {
                        val name = it.split("/")
                        if (name.size <= 2 && name[0].toUpperCase().endsWith(".DLL")) {
                            //说明是导入动态链接库
                            val ili: ImportLibraryItem
                            if (name.size == 1) {
                                //import printf "msvcrt.dll"
                                ili = app.buildStruct.importManager.use(it, i.name)
                            } else {
                                //import print "msvcrt.dll/printf"
                                ili = app.buildStruct.importManager.use(name[0], name[1])
                            }
                            importList.add(DefImport(i.name, ili))
                        }
                    } ?: kotlin.run {
                        TODO()
                    }
                }
                is ASTOuterVar -> {
                    if(i.names.size != 1) TODO()
                    val variable = DefVariable()
                    variable.ast = i
                    variable.name = i.names[0].name.value
                    i.names[0].type?.let {
                        variable.type = parseDataType(it)
                    }
                    if (variable.type == null && i.expr == null) {
                        throw Exception("变量" + variable.name + "必须指定类型！")
                    }
                    variable.space = this
                    varList.add(variable)
                    if (i.const) variable.isConst = true
                }
                is ASTOuterFun -> {
                    val function = DefFunction()
                    function.ast = i
                    function.name = i.name.getName()
                    function.space = this
                    i.param.forEach {
                        if (it is ASTFunParam) {
                            val type = parseDataType(it.type)
                            function.param.add(DefFunParam(it.name, type))
                        } else TODO()
                    }
                    i.type?.let {
                        function.type=parseDataType(it)
                    }
                    var paramSize = 0
                    function.param.forEach {
                        paramSize += it.type.getSize()
                    }
//                    function.func.setParamSize(paramSize)todo
                    i.type?.let {
                        function.type = parseDataType(it)
                    }
                    funList.add(function)
                }
                else -> {
                    throw Exception("无法解析的类型" + i::class.simpleName)
                }
            }
        }
//        预解析完成


    }


    /**
     * Get file code
     * 获取文件的源代码
     * @param file
     * @return
     */
    private fun getFileCode(file: File): String {
        val input = BufferedInputStream(FileInputStream(file))
        var len: Int
        val temp = ByteArray(1024)
        val sb = StringBuilder()
        while (input.read(temp).also { len = it } != -1) sb.append(String(temp, 0, len))
        return sb.toString()
    }
}

/**
 * Parse program
 * 解析程序
 * @property app
 * @property entryFile
 * @constructor Create empty Parse program
 */
class ParseProgram(val app: Application, val entryFile: File) {
    private val fileList = ArrayList<PreFile>()

    //todo 输入ast 输出MyStatic


    /**
     * Parse file
     * 解析文件
     * @param file
     * @return
     */
    private fun parseFile(file: File): PreFile {
        //文件转语法树
        fileList.find { it.file == file }?.let {
            return it
        } ?: run {
            val preFile = PreFile(app, file)
            fileList.add(preFile)
            return preFile
        }
    }

    init {
        val entryObject = parseFile(entryFile)//预编译 如果遇到import会优先预编译import的部分
        entryObject.funList.find { it.name == "main" }?.let {
            it.use()
            app.entry = it.func
        } ?: run {
            throw Exception("未找到main方法！")
        }
        app.list.add(entryObject)
    }
}

