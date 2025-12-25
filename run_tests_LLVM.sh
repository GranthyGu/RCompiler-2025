BASE="rcompiler2025_testcases/semantic-2"
BUILTIN="builtin/builtin.o"

trim_trailing_blank_lines() {
    awk 'NF { last=NR } { lines[NR]=$0 } END { for (i=1; i<=last; i++) print lines[i] }' "$1"
}
ulimit -s unlimited
for i in $(seq 1 50); do
    DIR="$BASE/comprehensive$i"
    LLFILE="$DIR/comprehensive$i.ll"
    OBJFILE="$DIR/in.o"
    OUTFILE="$DIR/a.out"
    RESULTFILE="$DIR/result.txt"
    EXPECTED="$DIR/comprehensive$i.out"
    INPUTFILE="$DIR/comprehensive$i.in"

    echo "=============================="
    echo "Processing comprehensive$i ..."
    echo "LL File: $LLFILE"

    clang -c "$LLFILE" -o "$OBJFILE"
    if [ $? -ne 0 ]; then
        echo "❌ clang failed for comprehensive$i"
        continue
    fi

    clang "$OBJFILE" "$BUILTIN" -o "$OUTFILE"
    if [ $? -ne 0 ]; then
        echo "❌ Linking failed for comprehensive$i"
        continue
    fi

    if [ -f "$INPUTFILE" ]; then
        "$OUTFILE" < "$INPUTFILE" > "$RESULTFILE" 2>&1
    else
        "$OUTFILE" > "$RESULTFILE" 2>&1
    fi

    echo "Done: output saved to $RESULTFILE"

    CLEAN_RESULT="$(trim_trailing_blank_lines "$RESULTFILE")"

    if [ -z "$CLEAN_RESULT" ]; then
        echo "❌ ERROR: output is empty after trimming trailing blank lines (comprehensive$i)"
        continue
    fi

    if [ -f "$EXPECTED" ]; then
        if diff -q \
            <(printf "%s\n" "$CLEAN_RESULT") \
            <(trim_trailing_blank_lines "$EXPECTED") \
            > /dev/null; then
            echo "✅ WONDERFUL!!!! ✨ (Test $i matched expected output)"
        else
            echo "❌ Output mismatch for comprehensive$i"
        fi
    else
        echo "⚠️  Expected output file not found: $EXPECTED"
    fi

done