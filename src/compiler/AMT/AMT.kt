package compiler.AMT

import compiler.AST.ast_container

object AMT{
    fun parse(ast:ast_container): amt_application {
        val app=amt_application()
        app.main=amt_container(app,ast)
        return app
    }
    fun parse(app:amt_application,ast:ast_container):amt_container{
        return amt_container(app,ast)
    }
}