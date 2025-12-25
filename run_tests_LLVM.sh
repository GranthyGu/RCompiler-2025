#!/bin/bash

BASE="rcompiler2025_testcases/semantic-2"
BUILTIN="builtin/builtin.o"
TOTAL_TESTS=50
PASS_COUNT=0
trim_trailing_blank_lines() {
    awk 'NF { last=NR } { lines[NR]=$0 } END { for (i=1; i<=last; i++) print lines[i] }' "$1"
}

ulimit -s 65532 2>/dev/null || true

echo "=================================================="
echo "               🚀 Running LLVM Test               "
echo "=================================================="

for i in $(seq 1 $TOTAL_TESTS); do
    DIR="$BASE/comprehensive$i"
    LLFILE="$DIR/comprehensive$i.ll"
    OBJFILE="$DIR/in.o"
    OUTFILE="$DIR/a.out"
    RESULTFILE="$DIR/result.txt"
    EXPECTED="$DIR/comprehensive$i.out"
    INPUTFILE="$DIR/comprehensive$i.in"

    if [ ! -f "$LLFILE" ]; then
        echo "⚠️  [$i/$TOTAL_TESTS] LL File not found: $LLFILE"
        continue
    fi

    clang -c "$LLFILE" -o "$OBJFILE"
    if [ $? -ne 0 ]; then
        echo "❌ [$i/$TOTAL_TESTS] Clang failed"
        continue
    fi

    clang "$OBJFILE" "$BUILTIN" -o "$OUTFILE"
    if [ $? -ne 0 ]; then
        echo "❌ [$i/$TOTAL_TESTS] Linking failed"
        continue
    fi

    if [ -f "$INPUTFILE" ]; then
        "$OUTFILE" < "$INPUTFILE" > "$RESULTFILE" 2>&1
    else
        "$OUTFILE" > "$RESULTFILE" 2>&1
    fi

    CLEAN_RESULT="$(trim_trailing_blank_lines "$RESULTFILE")"

    if [ -f "$EXPECTED" ]; then
        if diff -q <(printf "%s\n" "$CLEAN_RESULT") <(trim_trailing_blank_lines "$EXPECTED") > /dev/null; then
            echo "✅ [$i/$TOTAL_TESTS] comprehensive$i: PASS"
            ((PASS_COUNT++))
        else
            echo "❌ [$i/$TOTAL_TESTS] comprehensive$i: FAIL (Output mismatch)"
        fi
    else
        echo "⚠️  [$i/$TOTAL_TESTS] comprehensive$i: FAIL (Expected output missing)"
    fi

done

echo ""
echo "=================================================="
echo "                测试结果汇总                      "
echo "=================================================="

if [ "$PASS_COUNT" -eq "$TOTAL_TESTS" ]; then
    echo "🎉 太棒了！全部通过！"
    echo "🏆 最终成绩: $PASS_COUNT / $TOTAL_TESTS (100%)"
else
    echo "📊 测试完成。"
    echo "✅ 成功: $PASS_COUNT"
    echo "❌ 失败: $((TOTAL_TESTS - PASS_COUNT))"
    echo "📉 通过率: $PASS_COUNT / $TOTAL_TESTS"
fi
echo "=================================================="