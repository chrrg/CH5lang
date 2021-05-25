# CH编译器 测试用例报告
## 测试用例1

```
import printf "msvcrt.dll"
fun printf a string

main{
    val a="你好，世界！"
    a printf
}
```
>编译性能测试

|测试次数|平均编译速度 ms|编译最长耗时|平均内存消耗|
| ------ | ------ | ------ | ------ |
|1次|3.000ms|3ms|10465KB|
|10次|1.500ms|3ms|13604KB|
|100次|1.130ms|3ms|12349KB|
