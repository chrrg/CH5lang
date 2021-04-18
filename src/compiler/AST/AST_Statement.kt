//package compiler.AST
//
//import compiler.Tokenizer.*
//
//class ast_statement(val ast:AST.ast_parser){
//    val tokens=ast.tokens
//    fun getExpr():ast_expr?{
//        return getExpr(16)
//    }
//    fun getExpr(priority:Int):ast_expr?{
//        val token = tokens.next()
//        token ?: return null
//        var result: ast_expr
//        when (token) {
//            is Token_Annotation -> {
//                return getExpr(priority)
//            }
//            is Token_Comment -> {
//                return getExpr(priority)
//            }
//            is Token_Crlf -> {
//                return getExpr(priority)
//            }
//            is Token_Int -> {
//                result = ast_nodeInt(token)
//            }
//            is Token_String -> {
//                result = ast_nodeString(token)
//            }
//            is Token_Double -> {
//                result = ast_nodeDouble(token)
//            }
//            is Token_Word -> {
//                result = ast_nodeWord(token)
//            }
//            is Token_Operator -> {//++i +5+6
//                when(token.operator){
//                    op_comma->throw Exception("err?")
//                    op_tinybracket1->{
//                        TODO("err")
////                        result=ast_tuple()
////                        result.exprs.add(getTuple())
////                        if(!getOperator(op_tinybracket2))throw Exception("err")
//                    }
//                    else->{
//                        val opType = ast.OperateType(token)
//                        when (opType) {
//                            2 -> {
//                                result = ast_binary(token, ast_expr(), getExpr(ast.getPriority(token))!!)
//                            }
//                            4, 5 -> {
//                                result = ast_unaryLeft(token, getExpr(ast.getPriority(token))!!)
//                            }
//                            else -> {
//                                throw Exception("未知的运算符类型：" + token.operator)
//                            }
//                        }
//                    }
//                }
//            }
//            else -> {
//                throw Exception("不支持的符号：" + token::class.simpleName)
//            }
//        }
//        while (true) {
//            val next = tokens.next()
//            next ?: return result
//            when (next) {
//                is Token_Operator -> {
//
//                    val myPriority=ast.getPriority(next)
//                    if (priority <= myPriority) {
//                        tokens.prev();return result
//                    }
//                    if(next.operator==op_comma){
//                        val tuple=ast_tuple()
//                        tuple.exprs.add(result)
//
//                        val exp=getExpr(myPriority)
//                        if(exp is ast_tuple){
//                            for(i in exp.exprs)tuple.exprs.add(i)
//                        }else{
//                            exp?.let{
//                                tuple.exprs.add(it)
//                            }
//                        }
//                        return tuple
//                    }
//                    val opType = ast.OperateType(next)
//                    when (opType) {//k++ i++
//                        4 -> {
//                            result = ast_unaryRight(next, result)
//                        }
//                        2 -> {
//                            result = ast_binary(next, result, getExpr(myPriority)!!)
//                        }
//                        else -> {
//                            throw Exception("不支持2元操作符")
//                        }
//                    }
//                }
//                is Token_Word->{
//                    //这里需要处理
//
//                }
//                else->{
//                    tokens.prev()
//                    return result
//                }
//            }
//        }
//
//    }
//}