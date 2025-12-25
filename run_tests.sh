#!/bin/bash

cd "$(dirname "$0")"

echo "========================================"
echo "   Start to compile the Java program    "
echo "========================================"

mkdir -p out
find src -name "*.java" -print0 | xargs -0 javac -d out

if [ $? -ne 0 ]; then
    echo "❌ Compile ERROR!"
    exit 1
fi

echo "✅ Compile Successful!"

mkdir -p result

> result/semantic1_result.txt
> result/semantic2_result.txt

echo ""
echo "========================================"
echo "         Testing Semantic-1 ...         "
echo "========================================"

for test_dir in rcompiler2025_testcases/semantic-1/*/; do
    if [ -d "$test_dir" ]; then
        test_name=$(basename "$test_dir")
        rx_file="$test_dir/$test_name.rx"
        
        if [ -f "$rx_file" ]; then
            echo -n "Testing $test_name ... "

            java -cp out rcompiler2025.src.Main "$rx_file" > "result/$test_name.output" 2>&1
            
            if [ ! -f "result/$test_name.output" ]; then
                echo "❌ ERROR!"
                echo "false" >> result/semantic1_result.txt
                continue
            fi

            if tail -n 10 "result/$test_name.output" | tr -d '\r' | grep -F -x -q "You MADE it!!!"; then
                echo "true" >> result/semantic1_result.txt
                echo "✅"
            else
                echo "false" >> result/semantic1_result.txt
                echo "❌"
            fi
        fi
    fi
done

echo ""
echo "========================================"
echo "         Testing Semantic-2 ...         "
echo "========================================"

for test_dir in rcompiler2025_testcases/semantic-2/*/; do
    if [ -d "$test_dir" ]; then
        test_name=$(basename "$test_dir")
        rx_file="$test_dir/$test_name.rx"
        
        if [ -f "$rx_file" ]; then
            echo -n "Testing $test_name ... "

            java -cp out rcompiler2025.src.Main "$rx_file" > "result/$test_name.output" 2>&1
            
            if [ ! -f "result/$test_name.output" ]; then
                echo "❌ ERROR!"
                echo "false" >> result/semantic2_result.txt
                continue
            fi

            if tail -n 1 "result/$test_name.output" | tr -d '\r' | grep -F -x -q "You MADE it!!!"; then
                echo "true" >> result/semantic2_result.txt
                echo "✅"
            else
                echo "false" >> result/semantic2_result.txt
                echo "❌"
            fi
        fi
    fi
done

echo ""
echo "########################################"
echo "           Result of the test           "
echo "########################################"
echo ""

echo -n "Result of Semantic-1"
if [ -f "result/ans.txt" ]; then
    if diff -w -q result/semantic1_result.txt result/ans.txt > /dev/null; then
        echo "✅ TOTALLY CORRECT! (Corresponding to ans.txt)"
    else
        echo "❌ Did not match ans.txt!"
        echo "   -> You can run: diff result/semantic1_result.txt result/ans.txt"
    fi
else
    echo "⚠️"
fi

s2_total=$(grep -c "" result/semantic2_result.txt)
s2_passed=$(grep -c "true" result/semantic2_result.txt)

echo "Result of Semantic-2: Passed $s2_passed / $s2_total"

echo "########################################"