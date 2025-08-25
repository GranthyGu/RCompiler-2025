package rcompiler2025;

import java.io.*;
import java.util.*;

/**
 * 编译器主程序
 * 协调Lexer和Parser的工作流程
 */
public class Main {
    
    public static void main(String[] args) {
        // 默认输入文件名
        String inputFileName = "in.txt";
        
        // 如果命令行提供了文件名，使用命令行参数
        if (args.length > 0) {
            inputFileName = args[0];
        }
        
        System.out.println("Rust编译器启动...");
        System.out.println("正在读取文件: " + inputFileName);
        
        try {
            // 步骤1: 词法分析
            System.out.println("\n=== 词法分析阶段 ===");
            Lexer lexer = new Lexer(inputFileName);
            lexer.Tokenize();
            
            // 检查词法分析错误
            if (!lexer.ErrorList.isEmpty()) {
                System.err.println("词法分析发现错误:");
                for (Token errorToken : lexer.ErrorList) {
                    printTokenError(errorToken);
                }
                return; // 有词法错误时停止编译
            }
            
            System.out.println("词法分析成功! 生成了 " + lexer.TokenList.size() + " 个Token");
            
            // 可选：打印所有Token（调试用）
            if (shouldPrintTokens()) {
                printAllTokens(lexer.TokenList);
            }
            
            // 步骤2: 语法分析
            System.out.println("\n=== 语法分析阶段 ===");
            Parser parser = new Parser(lexer.TokenList);
            boolean parseSuccess = parser.Parse();
            
            // 检查语法分析结果
            if (!parseSuccess || parser.has_error) {
                System.err.println("语法分析失败!");
                System.err.println("在处理第 " + (parser.current_index + 1) + " 个Token时发生错误");
                if (parser.current_index < lexer.TokenList.size()) {
                    Token errorToken = lexer.TokenList.get(parser.current_index);
                    System.err.println("错误位置: 行" + errorToken.pos_begin.line + 
                                     ", 列" + errorToken.pos_begin.column + 
                                     ", Token值: '" + errorToken.value + "'");
                }
                return;
            }
            
            System.out.println("语法分析成功!");
            
            // 步骤3: 输出结果
            System.out.println("\n=== 编译结果 ===");
            if (parser.program != null && parser.program.nodes != null) {
                System.out.println("成功解析了 " + parser.program.nodes.size() + " 个顶级项目");
                printASTSummary(parser.program);
            } else {
                System.out.println("程序为空或解析结果异常");
            }
            
            System.out.println("\n编译完成!");
            
        } catch (Exception e) {
            System.err.println("编译过程中发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 打印Token错误信息
     */
    private static void printTokenError(Token errorToken) {
        System.err.println("  错误类型: " + errorToken.error_type);
        System.err.println("  位置: 行" + errorToken.pos_begin.line + 
                          ", 列" + errorToken.pos_begin.column);
        System.err.println("  Token值: '" + errorToken.value + "'");
        System.err.println("  Token类型: " + errorToken.token_type);
        System.err.println();
    }
    
    /**
     * 打印所有Token（调试用）
     */
    private static void printAllTokens(List<Token> tokens) {
        System.out.println("\n--- Token列表 ---");
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            System.out.printf("[%3d] %-20s %-15s '%s'%n", 
                i, 
                token.token_type, 
                "(" + token.pos_begin.line + "," + token.pos_begin.column + ")",
                token.value
            );
            
            // 每10个Token换行一次，避免输出过长
            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }
        System.out.println("--- Token列表结束 ---\n");
    }
    
    /**
     * 打印AST摘要信息
     */
    private static void printASTSummary(Crate program) {
        System.out.println("AST结构摘要:");
        
        Map<String, Integer> itemCounts = new HashMap<>();
        
        for (Item item : program.nodes) {
            String itemType = item.getClass().getSimpleName();
            itemCounts.put(itemType, itemCounts.getOrDefault(itemType, 0) + 1);
            
            // 打印具体项目信息
            if (item instanceof FunctionItem) {
                FunctionItem func = (FunctionItem) item;
                System.out.println("  函数: " + func.name + 
                                 " (参数: " + func.parameters.size() + ")");
            } else if (item instanceof StructItem) {
                StructItem struct = (StructItem) item;
                System.out.println("  结构体: " + struct.name + 
                                 " (字段: " + (struct.fields != null ? struct.fields.size() : 0) + ")");
            } else if (item instanceof EnumItem) {
                EnumItem enumItem = (EnumItem) item;
                System.out.println("  枚举: " + enumItem.name + 
                                 " (变体: " + enumItem.variants.size() + ")");
            } else if (item instanceof ConstItem) {
                ConstItem constItem = (ConstItem) item;
                System.out.println("  常量: " + constItem.name);
            } else if (item instanceof TraitItem) {
                TraitItem trait = (TraitItem) item;
                System.out.println("  特质: " + trait.name + 
                                 " (关联项: " + trait.AssociatedItems.size() + ")");
            } else if (item instanceof ImplItem) {
                ImplItem impl = (ImplItem) item;
                System.out.println("  实现块: " + (impl.name != null ? impl.name : "匿名") + 
                                 " (项目: " + impl.AssociatedItems.size() + ")");
            }
        }
        
        System.out.println("\n项目统计:");
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
    
    /**
     * 判断是否应该打印Token列表（可以通过系统属性控制）
     * 使用方式: java -Dprint.tokens=true Main
     */
    private static boolean shouldPrintTokens() {
        return "true".equals(System.getProperty("print.tokens", "false"));
    }
}