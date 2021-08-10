# 语法介绍

语法的解析是从关键字开始的，关键字分为2大类：
语法1
语法2

语法1为外层关键字，有：outer
```
import
class
static
fun
var
val
```

语法2是函数内关键字，有：inner
```
if
for
var
val
break
continue
fun
```

其中部分关键字重复说明函数内外都可以使用该关键字

一个文件是一个static名字是文件名

一行语法syntax

可以是expression 表达式 如：a++

也可以是语句statement 如：for{}

语句是没有返回值的 表达式有返回值

## 一期实现的功能有：  
- [x] 语法树解析
- [x] 实现基本语法 if for var val 等基本语法解析
- [x] 操作符的支持
- [x] 基本类型
- [x] 语义分析
- [x] 编译成x86指令集


## 二期完成：
- [ ] 类的支持
- [ ] 编译优化
- [ ] h5界面的演示
- [ ] 匿名函数
- [ ] 自动和手动GC
- [ ] 依赖管理


## 三期：
- [ ] ide
- [ ] 跨平台编译 linux、mac
- [ ] 编译时运行


# 基本类型

重要类型：
## 元组 任何语句都可以是元组：
如果是只有一个元素，那就不是元组
a=1
a是int类型
a=1,


```
1,2,3
a=1,2,3
a[0]

```


# 函数定义
``` 
fun foo{
    
}
```
# 匿名函数
``` 
a = fun{

}
a = fun a int,b int{

}
```

# 导入、依赖
import a "github.com/chrrg/CH5/parse"

/pkg/github.com/chrrg/CH5/
- parse.ch5
- test.ch5

import parse as ss,test as test2,test "github.com/chrrg/CH5"
import * "github.com/chrrg/CH5"

import "github.com/chrrg/CH5/parse"





