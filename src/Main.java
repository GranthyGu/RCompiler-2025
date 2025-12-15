package rcompiler2025.src;

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        try {
            // 1. 读取 STDIN 到 String
            String sourceCode = "";
            // 使用 try-with-resources 自动关闭 Scanner
            // 指定 UTF-8 防止中文注释乱码
            try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
                // "\\A" 是正则表达式，代表输入的开头
                // useDelimiter("\\A") 会迫使 Scanner 读到流的末尾，即读取全部内容
                if (scanner.hasNext()) {
                    sourceCode = scanner.useDelimiter("\\A").next();
                }
            }

            // 2. 直接将字符串传给 Lexer
            // 这里假设你的 Lexer 有一个接受 String 内容的构造函数
            Lexer lexer = new Lexer(sourceCode);
            lexer.Tokenize();
            
            // --- 下面是标准的错误检查和后续流程 (保持不变) ---
            
            if (!lexer.ErrorList.isEmpty()) {
                System.exit(1); 
            }
            
            Parser parser = new Parser(lexer.TokenList);
            boolean parseSuccess = parser.Parse();
            
            if (!parseSuccess || parser.has_error) {
                System.exit(1);
            }
            
            Semantics sem = new Semantics(parser);
            if (!sem.semanticCheck()) {
                System.exit(1);
            }

            // LLVMProgram llvmProgram = new LLVMProgram(parser, sem);
            // llvmProgram.build();
            
            // // 输出 LLVM IR 到标准输出
            // llvmProgram.printAll(System.out);
            
            System.exit(0);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}