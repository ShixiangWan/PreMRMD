Note:
1. Here are jar files, including "MRMD.jar" and "PreMRMD.jar"; "main.arff" and "remain.arff" are sample files.
2. The function of jar file is the same as source. If train file is "main.arff", test file is "remain.arff", initial value of feature selection is 10, characteristic multiple is 2(no need to change), the command is(no ".arff"): java -jar PreMRMD.jar main remain 10 2

说明：
1. 本目录下放的是MRMD的jar包版本，含MRMD.jar和PreMRMD.jar文件；main.arff和remain.arff是两个示例文件。
2. 功能与源码版本一致，假设训练集为main.arff，测试集为remain.arff，特征选择初始值为10，每次寻找的特征倍数为2（一般不需要更改），那么命令为（不需要输入文件名arff后缀）：
java -jar PreMRMD.jar main.arff remain.arff 10 2
提示：当只有一个原始序列文件时，可通过命令生成随机训练与测试文件：java -jar PreMRMD.jar example.arff