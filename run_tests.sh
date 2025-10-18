#!/bin/bash

# 切换到脚本所在目录（防止从别的路径调用时出错）
cd "$(dirname "$0")"

echo "编译Java程序..."
# 这里的 -d . 表示在当前目录生成正确的 rcompiler2025/Main.class
javac -d . Main.java Lexer.java Parser.java Semantics.java
cd rcompiler2025

if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo "编译成功！"

# 创建结果目录
mkdir -p result
# 清空 total_result 文件
> result/total_result.txt

# 遍历 semantic-1 目录下的所有子目录
for test_dir in semantic-1/*/; do
    if [ -d "$test_dir" ]; then
        test_name=$(basename "$test_dir")
        echo "处理测试用例: $test_name"
        
        rx_file="$test_dir/$test_name.rx"
        if [ -f "$rx_file" ]; then
            echo "  运行测试: $rx_file"
            
            # 运行并保存输出
            java rcompiler2025.Main "$rx_file" > "result/$test_name.output" 2>&1
            
            if [ $? -eq 0 ]; then
                echo "  测试完成: result/$test_name.output"
            else
                echo "  测试执行出错: $test_name"
            fi

            # 检查输出最后一行是否是 “太对了！！！”
            last_line=$(tail -n 1 "result/$test_name.output" | tr -d '\r\n')

            if [ "$last_line" = "太对了！！！" ]; then
                echo "true" >> result/total_result.txt
                echo "  ✅ 结果正确 (太对了！！！)"
            else
                echo "false" >> result/total_result.txt
                echo "  ❌ 结果错误"
            fi
        else
            echo "  警告: 找不到.rx文件: $rx_file"
        fi
    fi
done

echo "所有测试完成！结果保存在 result 目录中。"
echo "汇总结果已写入 result/total_result.txt"