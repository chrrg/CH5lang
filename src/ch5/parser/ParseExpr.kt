package ch5.parser

import ch5.ast.*
import ch5.build.*
import ch5.op_assign

object todo : Exception("暂未实现的功能!")

class ParseExpr(val exprs: ASTExpression?, val namespace: NameSpace) {
    val app = namespace.app
    val rootCode = CodeBox()
    var type: DataType? = null

    init {
        parse(exprs)
    }

    private fun parseType(expr: ASTExpression?): DataType {
        if (expr is ASTNodeInt) {
            return DataType("int")
        } else if (expr is ASTNodeDouble) {
            TODO()
        } else if (expr is ASTNodeString) {
            return DataType("string")
        } else if (expr is ASTBinary) {
            if (expr.op.operator is op_assign) {
                //todo 获取左边的变量定义的类型
                val dataType = parseType(expr.right)//赋值表达式的类型
                if (getVariable(expr.left).type != dataType) throw Exception("类型不匹配")

                return parseType(expr.right)
            } else {
                TODO()
            }
        } else {
            TODO()
        }
    }

    fun getVariable(expr: ASTExpression): DefVar {
        //a.b.c
        if (expr is ASTNodeWord) {
            return namespace.getVariable(expr.value.value)
        } else {
            TODO()
        }
    }

    private fun parse(expr: ASTExpression?): CodeBox {
        val code = CodeBox()
        if (expr == null) return code
        when (expr) {
            is ASTNodeInt -> {
                mov(EAX, expr.value.number).addTo(code)
            }
            is ASTNodeDouble -> {
                throw Exception("?")
            }
            is ASTNodeString -> {
                mov(EAX, AddrSection(app.codeSection.add(GBKByteArray(expr.value.value)), app.codeSection)).addTo(code)
            }
            is ASTBinary -> {
                if (expr.op.operator is op_assign) {
                    parse(expr.left)
                    val vars = getVariable(expr.left)//获取左边的变量
                    mov(Addr(), EAX).addTo(code)//todo
                } else {
                    throw Exception("不支持的操作符:" + expr.op.operator.word)
                }
            }
            else -> {
                throw Exception("?")
            }
        }
        return code
    }

    fun addTo(codeBox: CodeBox) {
        codeBox.add(rootCode)
    }
}