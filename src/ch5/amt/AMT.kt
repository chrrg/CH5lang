package ch5.amt

import ch5.ast.ASTContainer

//import ch5.AST.ast_container
object AMT {
    fun parse(ast: ASTContainer): AmtApplication {
        val app = AmtApplication()
        //解析一个静态对象
        //解析过程 写解析提升部分 后解析代码部分
        val entry = AmtStatic(ast)
        app.entryStatic = entry
        val entryFun = entry.funPool.find { it.name == "main" } ?: throw Exception("未找到main方法！")
        app.codePool.add(AmtCall(entryFun))
        return app
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