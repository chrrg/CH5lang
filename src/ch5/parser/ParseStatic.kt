package ch5.parser

import ch5.ast.ASTContainer
import ch5.ast.ASTOuterFun
import ch5.ast.ASTOuterVar
import ch5.build.BuildStruct
import ch5.build.CodeBox
import ch5.build.DwordSection
import ch5.build.Fun

class DefFunParam(val name: String) {
    var type: DataType? = null
}

class DefFun(private val ast: ASTOuterFun, private val obj: StaticObject) {
    val name = ast.name.getName()
    val param = arrayListOf<DefFunParam>()
    val func = Fun(obj.app)
    val expr = func.code

    fun use(): DefFun {
        obj.useInit()// 解析所在对象的init函数 && 解析var的表达式
        ParseExpr(obj).parse(ast.exprbody).addTo(expr)// 解析当前函数
        return this
    }
}

class DefVar(val ast: ASTOuterVar, val obj: StaticObject) {
    val name = ast.names[0].name.value
    val expr = CodeBox(obj.app.codeSection)
    var type: DataType? = null
    var offset = 0

    init {
        assert(ast.names.size == 1)

    }
}

class DefType(val name: String) {

}

//静态对象
class StaticObject(val ast: ASTContainer, app: BuildStruct) : NameSpace(app) {
    val staticEntry = DwordSection(0)
    var size = 0//对象所占空间

    init {
        //预编译
        for (i in ast.container) {
            //todo import
            if (i is ASTOuterVar) {
                defVarList.add(DefVar(i, this))
            } else if (i is ASTOuterFun) {
                val defFun = DefFun(i, this)
                code.add(defFun.func)
                defFunList.add(defFun)
                //todo 预编译需要类型
            } else {
                throw Exception("??")
            }
        }
    }

    //使用了init函数,那么解析var表达式和init函数体
    fun useInit() {
        use()
        for (i in defVarList) {
            i.expr.add(ParseExpr(this).parse(i.ast.expr))
        }
        getFun("init")?.use()//如果有init就初始化init函数
        //计算对象的大小 就是所有变量的大小

        //todo 预编译完成需要进行变量的类型推导
        defVarList.forEach {
            it.type = ParseExpr(this).parseType(it.ast.expr)
        }
        defVarList.forEach {
            size += 4
        }
    }

    //todo 函数参数
    fun getFun(funName: String, param: Array<DataType> = arrayOf()): DefFun? {
        return defFunList.find { it.name == funName }
    }
}

