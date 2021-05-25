# CH编译器 测试用例报告
## 测试用例1
用例：`src\test\testcase\1.ch5`  

```
import printf "msvcrt.dll"
fun printf a string

main{
    "你好，世界！" printf
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.597ms|2.597ms|10465KB|
|10次|1.347ms|1.728ms|12558KB|
|100次|0.966ms|1.508ms|11040KB|
> 运行结果测试  

测试用例运行输出：  

```
你好，世界！
```
测试通过  
测试完成！  
---
## 测试用例2
用例：`src\test\testcase\app.ch5`  

```
import printf "msvcrt.dll"
import getChar "msvcrt.dll/_getch"

fun printf a string
fun printf a string,b int
fun printf a string,b bool
fun printf a string,b string
fun getChar

fun 打印整数 整数 int {"%d",整数 printf}
fun pause{"请按任意键继续. . .\n" printf;.getChar}
fun 打印字符串 字符串 string{"%s",字符串 printf}
fun 打印换号{"\n" printf}

main {

    val 一个字符串 string
    一个字符串 = "你好啊！\n"
    一个字符串 = "你很好啊！\n"
    一个字符串 打印字符串
    666666 打印整数
    .打印换号
    "666" printf
    "程序结束！\n" 打印字符串

}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.401ms|2.401ms|10465KB|
|10次|1.928ms|3.016ms|12558KB|
|100次|1.774ms|6.668ms|10988KB|
> 运行结果测试  

测试用例运行输出：  

```
你很好啊！
666666
666程序结束！

```
测试通过  
测试完成！  
---
## 测试用例3
用例：`src\test\testcase\bool.ch5`  

```
import printf "msvcrt.dll"
fun printf a string
fun println str string{
    str printf
    "\n"printf
}
init{
    "第一次加载这个文件时运行" println
}
main{
    val a = 100 / 2 + 10
    if a == 50
        "a==50是对的" println
    else if a == 60
        "a==60是对的！" println
    else
        "都不是" println

    "运行结束！" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.330ms|2.330ms|10465KB|
|10次|1.422ms|2.385ms|10988KB|
|100次|1.038ms|1.546ms|10517KB|
> 运行结果测试  

测试用例运行输出：  

```
第一次加载这个文件时运行
a==60是对的！
运行结束！

```
测试通过  
测试完成！  
---
## 测试用例4
用例：`src\test\testcase\helloworld.ch5`  

```
import printf "msvcrt.dll"
fun printf a string

main{
    "Hello,world!" printf
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|0.978ms|0.978ms|10465KB|
|10次|0.732ms|0.925ms|10465KB|
|100次|0.649ms|1.387ms|10569KB|
> 运行结果测试  

测试用例运行输出：  

```
Hello,world!
```
测试通过  
测试完成！  
---
## 测试用例5
用例：`src\test\testcase\int.ch5`  

```
import printf "msvcrt.dll"
fun printf a string
fun printf num1 int,num2 int{

}
main{
    "你好，世界！" printf
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|0.794ms|0.794ms|10465KB|
|10次|0.683ms|0.730ms|10465KB|
|100次|0.648ms|0.938ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
你好，世界！
```
测试通过  
测试完成！  
---
