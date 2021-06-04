# CH编译器 测试用例报告
## 测试用例1
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
|1次|4.383ms|4.383ms|10465KB|
|10次|2.630ms|3.656ms|13604KB|
|100次|1.623ms|3.412ms|12087KB|
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
## 测试用例2
用例：`src\test\testcase\basic.ch5`  

```
import printf "msvcrt.dll"
import ansiToInt "msvcrt.dll/atoi"
import exit "KERNEl32.dll/ExitProcess"

fun printf a string,b string
fun printf a string,b int
fun ansiToInt a string:int
fun exit code int

fun print num int{
    "%d",num printf
}
fun print str string{
    "%s",str printf
}
fun println str string{
    "%s\n",str printf
}
fun println num int{
    "%d\n",num printf
}
fun println bool bool{
    if bool
        "True" println
    else
        "False" println
}


init{
    "初始化时运行" println
}
main{
    //单行注释
    if 100<200 {
        "判断成功！" println
    }else{
        "判断失败！" println
    }
    /*
    多行注释
    */
    "-123" ansiToInt + 100 println
    0 exit
    "这里不会运行" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|3.771ms|3.771ms|10465KB|
|10次|2.043ms|3.002ms|11511KB|
|100次|1.474ms|2.211ms|10622KB|
> 运行结果测试  

测试用例运行输出：  

```
初始化时运行
判断成功！
-23

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
|1次|2.233ms|2.233ms|10465KB|
|10次|2.125ms|2.400ms|10465KB|
|100次|1.721ms|2.269ms|10569KB|
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
用例：`src\test\testcase\daffodil_number.ch5`  

```
import printf "msvcrt.dll"
fun printf a string, b string
fun printf a string, b int
fun println str string = "%s\n", str printf
fun println num int = "%d\n", num printf
fun pow3 num int = num * num * num
main {
    "开始计算三位数的水仙花数" println
    for i = 100 .. 1000
        if (i / 100 % 10 pow3) + (i / 10 % 10 pow3) + (i % 10 pow3) == i
            i println
    "完成" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|1.665ms|1.665ms|10465KB|
|10次|1.695ms|2.711ms|10465KB|
|100次|1.259ms|2.667ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
开始计算三位数的水仙花数
153
370
371
407
完成

```
测试通过  
测试完成！  
---
## 测试用例5
用例：`src\test\testcase\daffodil_number_pro.ch5`  

```
import printf "msvcrt.dll"
import GetTickCount "kernel32.dll"
fun GetTickCount:int
fun printf a string,b string
fun printf a string,b int
fun print num int="%d",num printf
fun print str string="%s",str printf
fun println str string="%s\n",str printf
fun println num int="%d\n",num printf

fun pow num int,p int:int{
    var result = 1
    for i = 0 .. p
        result = result * num
    = result
}
main{
    val time1=.GetTickCount
    for level = 3...7{
        "开始计算" print
        level print
        "位数的水仙花数" println
        for i = 10,level-1 pow .. (10,level pow) {
            var sum = 0
            for j = 0 .. level
                sum += i / (10,j pow) % 10,level pow
            if sum == i
                i println
        }
    }
    val time2=.GetTickCount
    "计算完成，耗时：" print
    time2-time1 print
    "ms完成" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.999ms|2.999ms|10465KB|
|10次|2.715ms|4.195ms|10465KB|
|100次|2.329ms|17.722ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
开始计算3位数的水仙花数
153
370
371
407
开始计算4位数的水仙花数
1634
8208
9474
开始计算5位数的水仙花数
54748
92727
93084
开始计算6位数的水仙花数
548834
开始计算7位数的水仙花数
1741725
4210818
9800817
9926315
计算完成，耗时：2578ms完成

```
测试不通过  

期待结果：  

```
开始计算3位数的水仙花数
153
370
371
407
开始计算4位数的水仙花数
1634
8208
9474
开始计算5位数的水仙花数
54748
92727
93084
开始计算6位数的水仙花数
548834
开始计算7位数的水仙花数
1741725
4210818
9800817
9926315
计算完成，耗时：2391ms完成

```
测试完成！  
---
## 测试用例6
用例：`src\test\testcase\fibonacci.ch5`  

```
import GetTickCount "kernel32.dll"
import printf "msvcrt.dll"
fun printf a string, b string
fun printf a string, b int
fun print a int="%d",a printf
fun println str string = "%s\n", str printf
fun println num int = "%d\n", num printf
fun GetTickCount:int
fun fibonacci num int:int {
    = if num <= 1
        1
    else
        num - 1 fibonacci + (num - 2 fibonacci)
}
main{
    "开始计算30个斐波那契数列：" println
    var time = .GetTickCount
    for i = 0 .. 30
        i fibonacci println
    var time2 = .GetTickCount
    "总耗时：" println
    time2 - time print
    "ms" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|1.498ms|1.498ms|10465KB|
|10次|1.436ms|1.577ms|10465KB|
|100次|1.282ms|1.496ms|10517KB|
> 运行结果测试  

测试用例运行输出：  

```
开始计算30个斐波那契数列：
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
总耗时：
31ms

```
测试不通过  

期待结果：  

```
开始计算30个斐波那契数列：
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
总耗时：
32ms

```
测试完成！  
---
## 测试用例7
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
    "for-while输出 1 2 3 4 5"println
    var i = 0
    for i++ < 5
        i println
    "for 输出0 1 2 3 4" println
    i = 0
    for {
        if i >= 5;break
        i++ println
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
    "for 循环的变量不会影响外层变量：" println
    i println
    "测试完成" println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|2.787ms|2.787ms|10465KB|
|10次|2.833ms|3.674ms|10465KB|
|100次|2.338ms|3.712ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
for-while输出 1 2 3 4 5
1
2
3
4
5
for 输出0 1 2 3 4
0
1
2
3
4
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
for 循环的变量不会影响外层变量：
5
测试完成

```
测试通过  
测试完成！  
---
## 测试用例8
用例：`src\test\testcase\fun.ch5`  

```
import printf "msvcrt.dll"
import ExitProcess "kernel32.dll"
fun ExitProcess code int
fun printf a string,b int
fun println num int="%d\n",num printf

fun 乘以2 num int = num * 2
fun 加5 num int = num + 5
fun 输出 num int = num println
fun 退出程序 = 0 ExitProcess

main{
    val 要处理的数据 = 5
    要处理的数据 输出
    要处理的数据 乘以2 加5 乘以2 输出
    .退出程序
    123 输出
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|1.140ms|1.140ms|10465KB|
|10次|1.175ms|1.251ms|10465KB|
|100次|1.013ms|1.339ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
5
30

```
测试完成！  
---
## 测试用例9
用例：`src\test\testcase\helloworld.ch5`  

```
import printf "msvcrt.dll"
fun printf a string

main {
    "Hello,world!" printf
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|0.893ms|0.893ms|10465KB|
|10次|0.923ms|0.965ms|10465KB|
|100次|0.764ms|0.976ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
Hello,world!
```
测试通过  
测试完成！  
---
## 测试用例10
用例：`src\test\testcase\if.ch5`  

```
import printf "msvcrt.dll"
fun printf a int
fun printf a string,b string
fun printf a string,b int
fun print str string="%s",str printf
fun println str string="%s\n",str printf
fun println num int="%d\n",num printf

fun main1{
    if 100 > 50 {
        "ok" println
    } else if 100 == 50 {
         "no1" println
    } else {
         "no2" println
    }
}
fun main2{
    (if 100 > 50
        "ok"
    else if 100 == 50
         "no1"
    else
         "no2") println
}
main{
    .main1
    .main2
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|1.220ms|1.220ms|10465KB|
|10次|1.239ms|1.547ms|10465KB|
|100次|0.994ms|1.243ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
ok
ok

```
测试通过  
测试完成！  
---
## 测试用例11
用例：`src\test\testcase\int.ch5`  

```
import printf "msvcrt.dll"
fun printf a string
fun printf a string,b int
fun println num int{
    "%d",num printf
}
main{
    var a int = 1
    var b = 2
    a + b * 3 + -4 println
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|1.024ms|1.024ms|10465KB|
|10次|1.006ms|1.108ms|10465KB|
|100次|0.860ms|1.405ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
3
```
测试通过  
测试完成！  
---
## 测试用例12
用例：`src\test\testcase\io.ch5`  

```
import printf "msvcrt.dll"
import scanf "msvcrt.dll"
import HeapAlloc "kernel32.dll"
import GetProcessHeap "kernel32.dll"
fun printf a int
fun printf a string,b string
fun printf a string,b int
fun scanf str string,addr int
fun println str string="%s\n",str printf
fun println num int="%d\n",num printf
fun GetProcessHeap:int
fun HeapAlloc hHeap int,dwFlags int,dwBytes int:int

main{
    val hHeap = .GetProcessHeap
    val addr=hHeap,8,255 HeapAlloc
    "请输入你的字符串！" println
//    "%s",addr scanf
    "你输入了：" println
    addr printf
}
```
> 编译结果  

编译成功！ 
> 编译性能测试  


|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|1.182ms|1.182ms|10465KB|
|10次|1.157ms|1.269ms|10465KB|
|100次|0.920ms|1.232ms|10465KB|
> 运行结果测试  

测试用例运行输出：  

```
请输入你的字符串！
你输入了：

```
测试通过  
测试完成！  
---
