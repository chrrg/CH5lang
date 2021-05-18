package ch5.amt

import ch5.ast.ASTContainer
import ch5.ast.ASTOuterFun
import ch5.ast.ASTOuterVar

//import ch5.AST.ast_container
object AMT {
    fun parse(ast: ASTContainer): AmtApplication {
        val app = AmtApplication()
        //解析一个静态对象
        app.staticList.add(parseStatic(ast))

        return app
    }

    private fun parseStatic(ast: ASTContainer): AmtStatic {
        val static = AmtStatic()

        for (i in ast.container) {
            //在这里碰到的语句需要在外层全部解析完成后进行解析，即：

            if (i is ASTOuterVar) {
                assert(i.names.size == 1)
                static.amtVariablePool.add(AmtVariable(i.names[0].name.value,i.const,0))

            }else if(i is ASTOuterFun){
                val func=AmtFun()
                static.amtFunPool.add(func)
                static.amtDefineFunPool.add(AmtDefineFun(i.name.getName(),func))
            }
            println(i)
        }
        return static
    }
//    fun parse(ast: ast_container) {
//        val app= amt_application()
//        app.main= amt_container(app,ast)
//        return app
//    }
//    fun parse(app: amt_application, ast:ast_container): amt_container {
//        return amt_container(app,ast)
//    }
}