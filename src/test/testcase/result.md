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
> 编译性能测试结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.132ms|2.132ms|10465KB|
|10次|1.226ms|1.500ms|12558KB|
|100次|0.984ms|2.132ms|11040KB|
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
> 编译性能测试结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.715ms|2.715ms|10465KB|
|10次|1.408ms|1.744ms|12558KB|
|100次|1.048ms|1.517ms|11145KB|
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
用例：`src\test\testcase\helloworld.ch5`  

```
import printf "msvcrt.dll"
fun printf a string

main{
    "Hello,world!" printf
}
```
> 编译性能测试结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|0.793ms|0.793ms|10465KB|
|10次|0.768ms|0.852ms|10988KB|
|100次|0.630ms|0.851ms|10517KB|
> 运行结果测试  

测试用例运行输出：  

```
Hello,world!
```
测试通过  
测试完成！  
---
## 测试用例4
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
> 编译性能测试结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|0.670ms|0.670ms|10465KB|
|10次|0.710ms|0.854ms|10465KB|
|100次|0.627ms|0.940ms|10517KB|
> 运行结果测试  

测试用例运行输出：  

```
你好，世界！
```
测试通过  
测试完成！  
---
