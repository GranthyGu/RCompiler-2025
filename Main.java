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
            }Parser parser = new Parser(lexer.TokenList);
            // 步骤2: 语法分析
            System.out.println("\n=== 语法分析阶段 ===");
            printAllTokens(parser.tokens);
            boolean parseSuccess = parser.Parse();
            
            // 检查语法分析结果
            if (!parseSuccess || parser.has_error) {
                System.err.println("语法分析失败!");
                System.err.println("在处理第 " + (parser.current_index + 1) + " 个Token时发生错误");
                if (parser.current_index < lexer.TokenList.size()) {
                    Token errorToken = parser.tokens.get(parser.current_index);
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
     * 打印AST详细信息
     */
    private static void printASTSummary(Crate program) {
        System.out.println("AST详细结构:");
        System.out.println("=".repeat(60));
        
        Map<String, Integer> itemCounts = new HashMap<>();
        int itemIndex = 1;
        
        for (Item item : program.nodes) {
            String itemType = item.getClass().getSimpleName();
            itemCounts.put(itemType, itemCounts.getOrDefault(itemType, 0) + 1);
            
            System.out.println("\n[" + itemIndex + "] " + itemType);
            System.out.println("-".repeat(30));
            
            if (item instanceof FunctionItem) {
                printFunctionItem((FunctionItem) item);
            } else if (item instanceof StructItem) {
                printStructItem((StructItem) item);
            } else if (item instanceof EnumItem) {
                printEnumItem((EnumItem) item);
            } else if (item instanceof ConstItem) {
                printConstItem((ConstItem) item);
            } else if (item instanceof TraitItem) {
                printTraitItem((TraitItem) item);
            } else if (item instanceof ImplItem) {
                printImplItem((ImplItem) item);
            }
            
            itemIndex++;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("项目统计:");
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
    
    private static void printFunctionItem(FunctionItem func) {
        System.out.println("  函数名: " + func.name);
        System.out.println("  是否const: " + func.is_const);
        System.out.println("  参数数量: " + func.parameters.size());
        
        if (func.parameters.size() > 0) {
            System.out.println("  参数列表:");
            for (int i = 0; i < func.parameters.size(); i++) {
                Parameter param = func.parameters.get(i);
                System.out.println("    [" + i + "] " + printParameter(param));
            }
        }
        
        if (func.return_type != null) {
            System.out.println("  返回类型: " + printType(func.return_type));
        }
        
        if (func.body != null) {
            System.out.println("  函数体:");
            printBlockExpression(func.body, "    ");
        } else {
            System.out.println("  函数体: 无 (声明)");
        }
    }
    
    private static void printStructItem(StructItem struct) {
        System.out.println("  结构体名: " + struct.name);
        if (struct.fields != null && struct.fields.size() > 0) {
            System.out.println("  字段数量: " + struct.fields.size());
            System.out.println("  字段列表:");
            for (int i = 0; i < struct.fields.size(); i++) {
                Parameter field = struct.fields.get(i);
                System.out.println("    [" + i + "] " + printParameter(field));
            }
        } else {
            System.out.println("  字段: 无");
        }
    }
    
    private static void printEnumItem(EnumItem enumItem) {
        System.out.println("  枚举名: " + enumItem.name);
        System.out.println("  变体数量: " + enumItem.variants.size());
        if (enumItem.variants.size() > 0) {
            System.out.println("  变体列表:");
            for (int i = 0; i < enumItem.variants.size(); i++) {
                System.out.println("    [" + i + "] " + enumItem.variants.get(i));
            }
        }
    }
    
    private static void printConstItem(ConstItem constItem) {
        System.out.println("  常量名: " + constItem.name);
        System.out.println("  类型: " + printType(constItem.type));
        if (constItem.exp != null) {
            System.out.println("  初始化表达式: " + printExpression(constItem.exp));
        } else {
            System.out.println("  初始化表达式: 无");
        }
    }
    
    private static void printTraitItem(TraitItem trait) {
        System.out.println("  特质名: " + trait.name);
        System.out.println("  关联项数量: " + trait.AssociatedItems.size());
        if (trait.AssociatedItems.size() > 0) {
            System.out.println("  关联项:");
            for (int i = 0; i < trait.AssociatedItems.size(); i++) {
                Item item = trait.AssociatedItems.get(i);
                System.out.println("    [" + i + "] " + item.getClass().getSimpleName());
                printNestedItem(item, "      ");
            }
        }
    }
    
    private static void printImplItem(ImplItem impl) {
        if (impl.name != null) {
            System.out.println("  实现特质: " + impl.name);
            System.out.println("  目标类型: " + printType(impl.type));
        } else {
            System.out.println("  直接实现类型: " + printType(impl.type));
        }
        System.out.println("  实现项数量: " + impl.AssociatedItems.size());
        if (impl.AssociatedItems.size() > 0) {
            System.out.println("  实现项:");
            for (int i = 0; i < impl.AssociatedItems.size(); i++) {
                Item item = impl.AssociatedItems.get(i);
                System.out.println("    [" + i + "] " + item.getClass().getSimpleName());
                printNestedItem(item, "      ");
            }
        }
    }
    
    private static void printNestedItem(Item item, String indent) {
        if (item instanceof FunctionItem) {
            printNestedFunctionItem((FunctionItem) item, indent);
        } else if (item instanceof StructItem) {
            printNestedStructItem((StructItem) item, indent);
        } else if (item instanceof EnumItem) {
            printNestedEnumItem((EnumItem) item, indent);
        } else if (item instanceof ConstItem) {
            printNestedConstItem((ConstItem) item, indent);
        } else if (item instanceof TraitItem) {
            printNestedTraitItem((TraitItem) item, indent);
        } else if (item instanceof ImplItem) {
            printNestedImplItem((ImplItem) item, indent);
        }
    }
    
    private static void printNestedFunctionItem(FunctionItem func, String indent) {
        System.out.println(indent + "函数名: " + func.name);
        System.out.println(indent + "是否const: " + func.is_const);
        System.out.println(indent + "参数数量: " + func.parameters.size());
        
        if (func.parameters.size() > 0) {
            System.out.println(indent + "参数列表:");
            for (int i = 0; i < func.parameters.size(); i++) {
                Parameter param = func.parameters.get(i);
                System.out.println(indent + "  [" + i + "] " + printParameter(param));
            }
        }
        
        if (func.return_type != null) {
            System.out.println(indent + "返回类型: " + printType(func.return_type));
        }
        
        if (func.body != null) {
            System.out.println(indent + "函数体:");
            printBlockExpression(func.body, indent + "  ");
        } else {
            System.out.println(indent + "函数体: 无 (声明)");
        }
    }
    
    private static void printNestedStructItem(StructItem struct, String indent) {
        System.out.println(indent + "结构体名: " + struct.name);
        if (struct.fields != null && struct.fields.size() > 0) {
            System.out.println(indent + "字段数量: " + struct.fields.size());
            System.out.println(indent + "字段列表:");
            for (int i = 0; i < struct.fields.size(); i++) {
                Parameter field = struct.fields.get(i);
                System.out.println(indent + "  [" + i + "] " + printParameter(field));
            }
        } else {
            System.out.println(indent + "字段: 无");
        }
    }
    
    private static void printNestedEnumItem(EnumItem enumItem, String indent) {
        System.out.println(indent + "枚举名: " + enumItem.name);
        System.out.println(indent + "变体数量: " + enumItem.variants.size());
        if (enumItem.variants.size() > 0) {
            System.out.println(indent + "变体列表:");
            for (int i = 0; i < enumItem.variants.size(); i++) {
                System.out.println(indent + "  [" + i + "] " + enumItem.variants.get(i));
            }
        }
    }
    
    private static void printNestedConstItem(ConstItem constItem, String indent) {
        System.out.println(indent + "常量名: " + constItem.name);
        System.out.println(indent + "类型: " + printType(constItem.type));
        if (constItem.exp != null) {
            System.out.println(indent + "初始化表达式: " + printExpression(constItem.exp));
        } else {
            System.out.println(indent + "初始化表达式: 无");
        }
    }
    
    private static void printNestedTraitItem(TraitItem trait, String indent) {
        System.out.println(indent + "特质名: " + trait.name);
        System.out.println(indent + "关联项数量: " + trait.AssociatedItems.size());
        if (trait.AssociatedItems.size() > 0) {
            System.out.println(indent + "关联项:");
            for (int i = 0; i < trait.AssociatedItems.size(); i++) {
                Item item = trait.AssociatedItems.get(i);
                System.out.println(indent + "  [" + i + "] " + item.getClass().getSimpleName());
                printNestedItem(item, indent + "    ");
            }
        }
    }
    
    private static void printNestedImplItem(ImplItem impl, String indent) {
        if (impl.name != null) {
            System.out.println(indent + "实现特质: " + impl.name);
            System.out.println(indent + "目标类型: " + printType(impl.type));
        } else {
            System.out.println(indent + "直接实现类型: " + printType(impl.type));
        }
        System.out.println(indent + "实现项数量: " + impl.AssociatedItems.size());
        if (impl.AssociatedItems.size() > 0) {
            System.out.println(indent + "实现项:");
            for (int i = 0; i < impl.AssociatedItems.size(); i++) {
                Item item = impl.AssociatedItems.get(i);
                System.out.println(indent + "  [" + i + "] " + item.getClass().getSimpleName());
                printNestedItem(item, indent + "    ");
            }
        }
    }
    
    private static void printBlockExpression(BlockExpression block, String indent) {
        System.out.println(indent + "块类型: " + (block.isConst ? "const块" : "普通块"));
        System.out.println(indent + "语句数量: " + block.statements.size());
        
        if (block.statements.size() > 0) {
            System.out.println(indent + "语句列表:");
            for (int i = 0; i < block.statements.size(); i++) {
                Statement stmt = block.statements.get(i);
                System.out.println(indent + "  [" + i + "] " + stmt.getClass().getSimpleName());
                printDetailedStatement(stmt, indent + "      ");
            }
        }
        
        if (block.exp != null) {
            System.out.println(indent + "块表达式:");
            printDetailedExpression(block.exp, indent + "    ");
        } else {
            System.out.println(indent + "块表达式: 无");
        }
    }
    
    private static void printDetailedStatement(Statement stmt, String indent) {
        if (stmt instanceof ExpressionStatement) {
            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            System.out.println(indent + "表达式语句:");
            printDetailedExpression(exprStmt.expression, indent + "  ");
        } else if (stmt instanceof LetStatement) {
            LetStatement letStmt = (LetStatement) stmt;
            System.out.println(indent + "变量绑定:");
            System.out.println(indent + "  模式: " + printPattern(letStmt.pattern));
            System.out.println(indent + "  类型: " + printType(letStmt.type));
            if (letStmt.initializer != null) {
                System.out.println(indent + "  初始化表达式:");
                printDetailedExpression(letStmt.initializer, indent + "    ");
            } else {
                System.out.println(indent + "  初始化表达式: 无");
            }
        } else if (stmt instanceof FunctionItem) {
            System.out.println(indent + "嵌套函数:");
            printNestedFunctionItem((FunctionItem) stmt, indent + "  ");
        } else if (stmt instanceof StructItem) {
            System.out.println(indent + "嵌套结构体:");
            printNestedStructItem((StructItem) stmt, indent + "  ");
        } else if (stmt instanceof EnumItem) {
            System.out.println(indent + "嵌套枚举:");
            printNestedEnumItem((EnumItem) stmt, indent + "  ");
        } else if (stmt instanceof ConstItem) {
            System.out.println(indent + "嵌套常量:");
            printNestedConstItem((ConstItem) stmt, indent + "  ");
        } else if (stmt instanceof TraitItem) {
            System.out.println(indent + "嵌套特质:");
            printNestedTraitItem((TraitItem) stmt, indent + "  ");
        } else if (stmt instanceof ImplItem) {
            System.out.println(indent + "嵌套实现块:");
            printNestedImplItem((ImplItem) stmt, indent + "  ");
        }
    }
    
    private static void printDetailedExpression(Expression exp, String indent) {
        if (exp == null) {
            System.out.println(indent + "表达式: null");
            return;
        }
        
        String className = exp.getClass().getSimpleName();
        System.out.println(indent + "类型: " + className);
        
        if (exp instanceof LiteralExpression) {
            LiteralExpression lit = (LiteralExpression) exp;
            System.out.println(indent + "值: " + lit.value);
            System.out.println(indent + "字面量类型: " + lit.literal_type);
        } else if (exp instanceof IdentifierExpression) {
            IdentifierExpression id = (IdentifierExpression) exp;
            System.out.println(indent + "标识符: " + id.name);
        } else if (exp instanceof BinaryExpression) {
            BinaryExpression bin = (BinaryExpression) exp;
            System.out.println(indent + "操作符: " + bin.operator);
            System.out.println(indent + "左操作数:");
            printDetailedExpression(bin.left, indent + "  ");
            if (bin.right != null) {
                System.out.println(indent + "右操作数:");
                printDetailedExpression(bin.right, indent + "  ");
            }
            if (bin.type != null) {
                System.out.println(indent + "类型转换目标: " + printType(bin.type));
            }
        } else if (exp instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) exp;
            System.out.println(indent + "操作符: " + unary.operator);
            System.out.println(indent + "可变: " + unary.isMut);
            System.out.println(indent + "操作数:");
            printDetailedExpression(unary.operand, indent + "  ");
        } else if (exp instanceof CallFuncExpression) {
            CallFuncExpression call = (CallFuncExpression) exp;
            System.out.println(indent + "被调用函数:");
            printDetailedExpression(call.call_, indent + "  ");
            System.out.println(indent + "参数数量: " + call.arguments.size());
            if (call.arguments.size() > 0) {
                System.out.println(indent + "参数列表:");
                for (int i = 0; i < call.arguments.size(); i++) {
                    System.out.println(indent + "  [" + i + "]");
                    printDetailedExpression(call.arguments.get(i), indent + "    ");
                }
            }
        } else if (exp instanceof CallMethodExpression) {
            CallMethodExpression call = (CallMethodExpression) exp;
            System.out.println(indent + "调用对象:");
            printDetailedExpression(call.call_, indent + "  ");
            System.out.println(indent + "方法名: " + call.method_name);
            System.out.println(indent + "参数数量: " + call.arguments.size());
            if (call.arguments.size() > 0) {
                System.out.println(indent + "参数列表:");
                for (int i = 0; i < call.arguments.size(); i++) {
                    System.out.println(indent + "  [" + i + "]");
                    printDetailedExpression(call.arguments.get(i), indent + "    ");
                }
            }
        } else if (exp instanceof ArrIndexExpression) {
            ArrIndexExpression arr = (ArrIndexExpression) exp;
            System.out.println(indent + "数组对象:");
            printDetailedExpression(arr.object, indent + "  ");
            System.out.println(indent + "索引表达式:");
            printDetailedExpression(arr.index, indent + "  ");
        } else if (exp instanceof FieldExpression) {
            FieldExpression field = (FieldExpression) exp;
            System.out.println(indent + "字段名: " + field.member);
            System.out.println(indent + "对象:");
            printDetailedExpression(field.object, indent + "  ");
        } else if (exp instanceof BlockExpression) {
            BlockExpression block = (BlockExpression) exp;
            printBlockExpression(block, indent);
        } else if (exp instanceof StructExpression) {
            StructExpression struct = (StructExpression) exp;
            System.out.println(indent + "结构体名: " + struct.name);
            if (struct.subName != null) {
                System.out.println(indent + "子名: " + struct.subName);
            }
            System.out.println(indent + "字段数量: " + struct.fields.size());
            if (struct.fields.size() > 0) {
                System.out.println(indent + "字段初始化:");
                for (int i = 0; i < struct.fields.size(); i++) {
                    StructExprField field = struct.fields.get(i);
                    System.out.println(indent + "  [" + i + "] " + field.name + ":");
                    if (field.exp != null) {
                        printDetailedExpression(field.exp, indent + "    ");
                    } else {
                        System.out.println(indent + "    表达式: 无 (简写)");
                    }
                }
            }
        } else if (exp instanceof IfExpression) {
            IfExpression ifExpr = (IfExpression) exp;
            System.out.println(indent + "条件表达式:");
            printDetailedExpression(ifExpr.condition, indent + "  ");
            System.out.println(indent + "then分支:");
            printBlockExpression(ifExpr.then_branch, indent + "  ");
            if (ifExpr.else_branch != null) {
                System.out.println(indent + "else分支:");
                printDetailedExpression(ifExpr.else_branch, indent + "  ");
            }
        } else if (exp instanceof WhileExpression) {
            WhileExpression whileExpr = (WhileExpression) exp;
            System.out.println(indent + "条件表达式:");
            printDetailedExpression(whileExpr.condition, indent + "  ");
            System.out.println(indent + "循环体:");
            printBlockExpression(whileExpr.body, indent + "  ");
        } else if (exp instanceof LoopExpression) {
            LoopExpression loopExpr = (LoopExpression) exp;
            System.out.println(indent + "循环体:");
            printBlockExpression(loopExpr.value, indent + "  ");
        } else if (exp instanceof PathExpression) {
            PathExpression path = (PathExpression) exp;
            System.out.println(indent + "路径: " + path.Path);
            if (path.subPath != null) {
                System.out.println(indent + "子路径: " + path.subPath);
            }
        } else if (exp instanceof ArrayExpression) {
            ArrayExpression arr = (ArrayExpression) exp;
            System.out.println(indent + "元素数量: " + arr.elements.size());
            if (arr.elements.size() > 0) {
                System.out.println(indent + "元素列表:");
                for (int i = 0; i < arr.elements.size(); i++) {
                    System.out.println(indent + "  [" + i + "]");
                    printDetailedExpression(arr.elements.get(i), indent + "    ");
                }
            }
        } else if (exp instanceof GroupedExpression) {
            GroupedExpression grouped = (GroupedExpression) exp;
            System.out.println(indent + "内部表达式:");
            printDetailedExpression(grouped.inner, indent + "  ");
        } else if (exp instanceof BreakExpression) {
            BreakExpression breakExpr = (BreakExpression) exp;
            if (breakExpr.break_expression != null) {
                System.out.println(indent + "break值:");
                printDetailedExpression(breakExpr.break_expression, indent + "  ");
            } else {
                System.out.println(indent + "break值: 无");
            }
        } else if (exp instanceof ReturnExpression) {
            ReturnExpression retExpr = (ReturnExpression) exp;
            if (retExpr.value != null) {
                System.out.println(indent + "返回值:");
                printDetailedExpression(retExpr.value, indent + "  ");
            } else {
                System.out.println(indent + "返回值: 无");
            }
        } else if (exp instanceof ContinueExpression) {
            System.out.println(indent + "continue表达式");
        } else if (exp instanceof UnderscoreExpression) {
            System.out.println(indent + "下划线表达式");
        } else {
            System.out.println(indent + "未知表达式类型: " + className);
        }
    }
    

    
    private static String printExpression(Expression exp) {
        if (exp == null) return "null";
        
        String className = exp.getClass().getSimpleName();
        
        if (exp instanceof LiteralExpression) {
            LiteralExpression lit = (LiteralExpression) exp;
            return className + "(" + lit.value + ")";
        } else if (exp instanceof IdentifierExpression) {
            IdentifierExpression id = (IdentifierExpression) exp;
            return className + "(" + id.name + ")";
        } else if (exp instanceof BinaryExpression) {
            BinaryExpression bin = (BinaryExpression) exp;
            return className + "(" + bin.operator + ")";
        } else if (exp instanceof CallFuncExpression) {
            CallFuncExpression call = (CallFuncExpression) exp;
            return className + "(参数: " + call.arguments.size() + ")";
        } else if (exp instanceof BlockExpression) {
            BlockExpression block = (BlockExpression) exp;
            return className + "(语句: " + block.statements.size() + ")";
        } else if (exp instanceof StructExpression) {
            StructExpression struct = (StructExpression) exp;
            return className + "(" + struct.name + ", 字段: " + struct.fields.size() + ")";
        }
        
        return className;
    }
    
    private static String printType(Type type) {
        if (type == null) return "unknown";
        
        if (type instanceof TypePath) {
            return ((TypePath) type).name;
        } else if (type instanceof ReferenceType) {
            ReferenceType ref = (ReferenceType) type;
            return (ref.isMut ? "&mut " : "&") + printType(ref.type);
        } else if (type instanceof ArrayType) {
            ArrayType arr = (ArrayType) type;
            return "[" + printType(arr.type) + "]";
        } else if (type instanceof UnitType) {
            return "()";
        }
        
        return type.getClass().getSimpleName();
    }
    
    private static String printPattern(Pattern pattern) {
        if (pattern == null) return "unknown";
        
        if (pattern instanceof IdentifierPattern) {
            IdentifierPattern id = (IdentifierPattern) pattern;
            String result = "";
            if (id.isRef) result += "ref ";
            if (id.isMut) result += "mut ";
            result += id.name;
            return result;
        } else if (pattern instanceof WildcardPattern) {
            return "_";
        } else if (pattern instanceof LiteralPattern) {
            LiteralPattern lit = (LiteralPattern) pattern;
            return (lit.isNegative ? "-" : "") + "literal";
        }
        
        return pattern.getClass().getSimpleName();
    }
    
    private static String printParameter(Parameter param) {
        String result = "";
        if (param.isSelf) {
            if (param.isReference) result += "&";
            if (param.isMut) result += "mut ";
            result += "self";
        } else {
            if (param.pattern != null) {
                result += printPattern(param.pattern);
            } else if (param.name != null) {
                result += param.name;
            }
        }
        if (param.type != null) {
            result += ": " + printType(param.type);
        }
        return result;
    }
    
    /**
     * 判断是否应该打印Token列表（可以通过系统属性控制）
     * 使用方式: java -Dprint.tokens=true Main
     */
    private static boolean shouldPrintTokens() {
        return "true".equals(System.getProperty("print.tokens", "false"));
    }
}