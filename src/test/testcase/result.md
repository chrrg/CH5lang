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
|1次|2.193ms|2.193ms|15698KB|
|10次|1.092ms|1.702ms|11512KB|
|100次|0.971ms|2.273ms|11145KB|
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
|1次|1.926ms|1.926ms|10465KB|
|10次|1.570ms|2.199ms|12558KB|
|100次|1.188ms|2.661ms|11145KB|
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
fun println bool bool{
    if bool
        "True" println
    else
        "False" println
}
init{
    "第一次加载这个文件时运行" println
}
main{
    val a = 100 / 2 + 10
    if a == 50
        "a==50是对的"
    else if a == 60
        "a==60是对的！"
    else{
        "都不是"
    } println

    if true & false | true
        "成功"
    else{
        "失败"
    } println
    0<0 println
    0<=0 println
    1<1 println
    1<=1 println
    0>0 println
    0>=0 println
    1>1 println
    1>=1 println
    0<1 println
    0<=1 println
    1<0 println
    1<=0 println
    "运行结束！" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|3.677ms|3.677ms|10465KB|
|10次|2.269ms|3.011ms|10988KB|
|100次|1.971ms|3.207ms|10517KB|
> 运行结果测试  

测试用例运行输出：  

```
第一次加载这个文件时运行
a==60是对的！
成功
False
True
False
True
False
True
False
True
True
True
False
False
运行结束！

```
测试通过  
测试完成！  
---
## 测试用例4
用例：`src\test\testcase\fibonacci.ch5`  

```
import GetTickCount "kernel32.dll"
import printf "msvcrt.dll"

fun GetTickCount:int
fun printf a string
fun printf a string,a int
fun println num int{
    "%d\n",num printf
}
fun println str string{
    str printf
    "\n"printf
}
fun fibonacci num int:int{
    = if num <= 1
        1
    else
        (num - 1 fibonacci) + (num - 2 fibonacci)
}
main{
    "开始计算斐波那契数列：" println
    var time = .GetTickCount
    for i = 0...30 {
        i fibonacci println
    }
    var time2 = .GetTickCount
    "总耗时：" println
    time2 - time println
    "ms" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|1.922ms|1.922ms|10465KB|
|10次|1.678ms|1.911ms|10465KB|
|100次|1.352ms|3.411ms|10517KB|
> 运行结果测试  

测试用例运行输出：  

```
开始计算斐波那契数列：
1
1
2
3
5
8
13
21
34
55
89
144
233
377
610
987
1597
2584
4181
6765
10946
17711
28657
46368
75025
121393
196418
317811
514229
832040
1346269
总耗时：
47
ms

```
测试通过  
测试完成！  
---
## 测试用例5
用例：`src\test\testcase\for.ch5`  

```
import printf "msvcrt.dll"

fun printf a string,b string
fun printf a string,b int
fun println str string{
    "%s\n",str printf
}
fun println num int{
    "%d\n",num printf
}
fun print str string{
    "%s",str printf
}
fun print num int{
    "%d",num printf
}
main {
    "for 输出0 1 2 3 4" println
    var i = 0
    for {
        if i >= 5;break
        i++ println
    }
    "for-while输出 1 2 3 4 5"println
    i = 0
    for i++ < 5 {
        i println
    }
    "for-each输出1 2 3 4 8 9 10" println
    for i = 1 ... 10 {
        if i >= 5 & i < 8;continue
        i println
    }
    "跳出多层循环：" println
    for i = 1 ... 9{
        for j = 1 ... 9{
            if i == 5 & j == 5
                break 2
            i print
            " * " print
            j print
            " = " print
            i * j println
        }
    }

    i println
    "测试完成" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.890ms|2.890ms|10465KB|
|10次|2.914ms|4.937ms|10465KB|
|100次|1.962ms|3.071ms|10517KB|
> 运行结果测试  

测试用例运行输出：  

```
for 输出0 1 2 3 4
0
1
2
3
4
for-while输出 1 2 3 4 5
1
2
3
4
5
for-each输出1 2 3 4 8 9 10
1
2
3
4
8
9
10
跳出多层循环：
1 * 1 = 1
1 * 2 = 2
1 * 3 = 3
1 * 4 = 4
1 * 5 = 5
1 * 6 = 6
1 * 7 = 7
1 * 8 = 8
1 * 9 = 9
2 * 1 = 2
2 * 2 = 4
2 * 3 = 6
2 * 4 = 8
2 * 5 = 10
2 * 6 = 12
2 * 7 = 14
2 * 8 = 16
2 * 9 = 18
3 * 1 = 3
3 * 2 = 6
3 * 3 = 9
3 * 4 = 12
3 * 5 = 15
3 * 6 = 18
3 * 7 = 21
3 * 8 = 24
3 * 9 = 27
4 * 1 = 4
4 * 2 = 8
4 * 3 = 12
4 * 4 = 16
4 * 5 = 20
4 * 6 = 24
4 * 7 = 28
4 * 8 = 32
4 * 9 = 36
5 * 1 = 5
5 * 2 = 10
5 * 3 = 15
5 * 4 = 20
6
测试完成

```
测试完成！  
---
## 测试用例6
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
|1次|1.283ms|1.283ms|10465KB|
|10次|0.730ms|0.786ms|10465KB|
|100次|0.578ms|0.849ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
Hello,world!
```
测试通过  
测试完成！  
---
## 测试用例7
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
|1次|0.767ms|0.767ms|10465KB|
|10次|0.717ms|0.860ms|10465KB|
|100次|0.617ms|0.847ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
你好，世界！
```
测试通过  
测试完成！  
---
