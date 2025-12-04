#!/bin/bash

# 切换到脚本所在目录
cd "$(dirname "$0")"

echo "编译 Java 程序..."

# ---- 编译 src/ 下所有 .java 到 out/ ----
mkdir -p out
find src -name "*.java" -print0 | xargs -0 javac -d out

if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo "编译成功！"

# 创建结果目录
mkdir -p result
> result/total_result.txt

# 遍历 semantic-2 目录下的所有子目录
for test_dir in rcompiler2025_testcases/semantic-2/*/; do
    if [ -d "$test_dir" ]; then
        test_name=$(basename "$test_dir")
        echo "处理测试用例: $test_name"
        
        rx_file="$test_dir/$test_name.rx"
        if [ -f "$rx_file" ]; then
            echo "  运行测试: $rx_file"

            # 运行并保存输出（注意 result 路径是相对项目根目录）
            java -cp out rcompiler2025.src.Main "$rx_file" > "result/$test_name.output" 2>&1
            
            # 再检查文件是否真的存在
            if [ ! -f "result/$test_name.output" ]; then
                echo "  ❌ 输出文件未生成！可能程序崩溃。"
                echo "false" >> result/total_result.txt
                continue
            fi

            echo "  测试完成: result/$test_name.output"

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