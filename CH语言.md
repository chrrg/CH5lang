# 介绍
本文件是mac上写的CH5语言语法草案

## 遵循法则

1. 常用的用法越简单越好

   语言层面会有一些非常频繁使用的语法，这些语法设计的要尽量简单而又明了。

2. 每一处都要符合直觉

   对于学过其它语言的用户来说，语法使用上要与自己的经验相符合。

3. 语法统一，不能特殊化

   不存在类或对象的性质和其它类或对象的性质不同，即处理方式相同。

4. 语法需要少敲键盘

   少敲一个键就省下敲一个键的时间，语法需要设计得能省即省，shift键能不按就不按。

5. 不允许语法冲突

   在同一行语句中不允许出现有歧义的语法，避免二义性。

6. 兼容性强

   对于未来可能加入的语法，这里可以提供语法不报错的书写方式。



## 	需要支持的特性

1. 换行字符串
2. 







## 待定的需求







# 正文
## 基础回顾
### 判断

```go
if expr{

}else{

}
```
### 循环

```js
for a in item{
  
}
```



```js
while expr{

}
```

### 定义

```kotlin
var a=1000
val b="abc"
```



### 表达式

```go
"Hello World"+"!" print
```



---

# 草案

以下内容可能不正式，而且 可能被修改

## 2021

02.11

字面量类型问题



字符串字面量类型问题解决办法：

例如：



```kotlin
//方案1:继承类
//内部
class string{
  
}
class string.utf8{////相当于继承
  
}
class string.gbk{
  
}
fun print str string.unicode{
  
}
fun print str string.utf8{
  
}
fun print str string.gbk{
  
}

//用法
val a string.utf8="你好，世界！"//未指定范型的字符串类型


a print//走哪个分支？没有选定就是默认字符集吗


//方案2:范型
//内部：
val a string<utf8>="你好，世界！"//指定范型的字符串类型
//常用的东西 上面写法太复杂了
val a="你好，世界！"u
//这里遵循3规范，其它类型也支持这种写法。
val f=3f
val f=0x1234
//iteral字面量修饰符
//在作用域内可以针对字面量进行修饰
//这个可以像语法糖 后期可以做

```

在写程序的时候不应该关注字符串是用的什么编码方式。

在与系统交互时才需要真正确定编码。

所以在系统调用时或解析时才需要用到。

在程序内存存储时使用的字符串编码方式为？ todo

```kotlin
//按照编译器确定的编码方式进行存储
//如：
val a=""//自动 由编译器确定编码类型
val u="你好"u  //强制unicode存储
//语法糖 可以转为val u string<unicode>="你好"



```

想法：

```kotlin
//以下是可以后期实现的
//先实现val a int=100//这样的就行
val s number=100//由编译器确定类型
val d number<double>=100//小数类型
//语法糖 val d=100d
val i number<int>=100

```



预编译器：在编译的过程中执行这些代码以改变编译的结果（困难）

编译器调用解释器执行代码。

作用域问题：(重点)

```kotlin
class a{
  
}
static a{
  
}

```

第一次运行类的时候要加载这个类中可能要用到的所有类或对象

重载运算符继承问题依赖作用域

作用域简化为class和static的作用域

及依赖上层的作用域

```kotlin
//scope top
import b from b
class a{
	//scope 1
  class b{
    
  }
}
```

```kotlin
b.ch5
class b{
  
}

```




//windows
05.07
ast解析基本完成

依赖管理草案 todo

import github.com/chrrg/CH5/parser
现阶段应该做builtin的函数和类型？

05.08
字面量类型
内置类型？
字面量
整数
小数
字符串 常量字符串是1字符时既可以作为字符也可以作为字符串

builtin类型拟：
int、uint 按位长 32位、64位
double 按位长 32位、64位
char、uchar 字符 8位
byte、word、dword 基本类型
string 字符串 引用基本类型 存储地址 按位长 32位、64位
特殊类型 指向一个类的地址的类型
基本类型都是值复制

main.ch5
特殊文件
import 目录时查找是否有该文件
即省略文件名

05.09

05.15
1 add 2
.with{
   
}

05.18



init{
   1 + 1 print
}

加载时应该先运行类/对象初始的代码
然后加载init块代码
如果是入口文件应该调用main函数

如果没有main函数说明不能作为入口文件


2021.05.25
第一个hello world

EAX        累加器(Accumulator), 用于乘、除、输入/输出等操作
EBX        基地址寄存器(Base Register), 作为存储器指针来使用
ECX        计数寄存器(Count Register), 在循环和字符串操作时，要用它来控制循环次数；在位操作中，当移多位时，要用CL来指明移位的位数

EDX        数据寄存器(Data Register), 在进行乘、除运算时，它可作为默认的操作数参与运算，也可用于存放I/O的端口地址
EDI         目的变址寄存器（Destination Index)
ESI         源变址寄存器（Source Index）
用于存放存储单元在段内的偏移量，用它们可实现多种存储器操作数的寻址方式，为以不同的地址形式访问存储单元提供方便

EBP        基址指针寄存器（Base Pointer）
ESP        堆栈指针寄存器（Stack Pointer）
用于存放堆栈内存储单元的偏移量，用它们可实现多种存储器操作数的寻址方式，为以不同的地址形式访问存储单元提供方便

链接：https://www.jianshu.com/p/134678afa146

