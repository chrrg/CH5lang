package ch5.parser

import ch5.Tokenizer
import ch5.ast.AST
import ch5.ast.ASTImport
import ch5.ast.ASTOuterFun
import ch5.ast.ASTOuterVar
import ch5.build.CodeBox
import ch5.build.Fun
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

object VoidType : PrimitiveType("void") {
    override fun getSize() = 0
}

/**
 * Reference type
 * 引用类型
 * @constructor Create empty Reference type
 */
class ReferenceType() : DataType() {
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
    var offset=0
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
    var type: DataType? = null//返回类型
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
//        println(ast.toString())
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
                    TODO()
                }
                is ASTOuterVar -> {
                    assert(i.names.size == 1)
                    val variable = DefVariable()
                    variable.ast = i
                    variable.name = i.names[0].name.value
                    variable.type = IntType//todo 类型 ast好像没有类型？
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
                        function.param.add(DefFunParam(it.name, IntType))
                    }
                    function.type = IntType//todo i.type
                    funList.add(function)
                }
                else -> {
                    throw Exception("无法解析的类型" + i::class.simpleName)
                }
            }
        }


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
class ParseProgram(val app: Application, val entryFile: String) {
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
        val entryObject = parseFile(File(entryFile))//预编译 如果遇到import会优先预编译import的部分
        entryObject.funList.find { it.name == "main" }?.let {
            it.use()
            app.entry = it.func
        } ?: run {
            throw java.lang.Exception("未找到main方法！")
        }
        app.list.add(entryObject)
    }
}

