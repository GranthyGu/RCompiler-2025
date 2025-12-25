package rcompiler2025.src;

import java.util.*;

abstract class IRInstructions {
    void Print(java.io.PrintStream out) {}
}

enum BinaryOp {
    add, sub, mul, sdiv, srem, urem, udiv, shl, ashr, and, or, xor,
}
enum Condition {
    eq, ne, ugt, uge, ult, ule, sgt, sge, slt, sle
}
class BinaryInstruction extends IRInstructions {
    public String result;
    public BinaryOp operator;
    public String operand1;
    public String operand2;
    public String type;
    BinaryInstruction(String result, BinaryOp Operator, String operand1, String operand2, String Type) {
        this.result = result;
        this.operator = Operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.type = Type;
    }
    void Print(java.io.PrintStream out) {
        out.printf("%s = ", result);
        out.print(operator);
        out.printf(" %s ", type);
        out.printf("%s, %s\n", operand1, operand2);
    }
}
class Br extends IRInstructions {
    public boolean has_condition;
    public String condition;
    public String lable1;
    public String lable2;
    Br(boolean has_condition, String condition, String lable1, String lable2) {
        this.has_condition = has_condition;
        this.condition = condition;
        this.lable1 = lable1;
        this.lable2 = lable2;
    }
    void Print(java.io.PrintStream out) {
        if (has_condition) {
            out.printf("br i1 %s, label %%%s, label %%%s\n", condition, lable1, lable2);
        } else {
            out.printf("br label %%%s\n", lable1);
        }
    }
}
class Ret extends IRInstructions {
    public String type;
    public String value;
    public boolean isVoid;
    Ret(String type, String value, boolean isVoid) {
        this.type = type;
        this.value = value;
        this.isVoid = isVoid;
    }
    void Print(java.io.PrintStream out) {
        if (isVoid) {
            out.println("ret void");
        } else {
            out.printf("ret %s %s\n", type, value);
        }
    }
}
class Alloca extends IRInstructions {
    public String result;
    public String type;
    Alloca(String result, String type) {
        this.result = result;
        this.type = type;
    }
    void Print(java.io.PrintStream out) {
        out.printf("%s = alloca %s\n", result, type);
    }
}
class Load extends IRInstructions {
    public String result;
    public String type;
    public String pointer;
    Load(String result, String type, String pointer) {
        this.result = result;
        this.type = type;
        this.pointer = pointer;
    }
    void Print(java.io.PrintStream out) {
        out.printf("%s = load %s, ptr %s\n", result, type, pointer);
    }
}
class Store extends IRInstructions {
    public String value;
    public String type;
    public String pointer;
    Store(String value, String type, String pointer) {
        this.value = value;
        this.type = type;
        this.pointer = pointer;
    }
    void Print(java.io.PrintStream out) {
        out.printf("store %s %s, ptr %s\n", type, value, pointer);
    }
}
class Getelementptr extends IRInstructions {
    public String result;
    public String type;
    public String ptrval;
    public List<String> types;
    public List<String> indices;
    public Integer size;
    Getelementptr(String result, String type, String ptrval, List<String> types, List<String> indices, Integer size) {
        this.result = result;
        this.type = type;
        this.ptrval = ptrval;
        this.types = types;
        this.indices = indices;
        this.size = size;
    }
    void Print(java.io.PrintStream out) {
        out.printf("%s = getelementptr %s, ptr %s", result, type, ptrval);
        for (int i = 0; i < size; i++) {
            out.printf(", %s %s", types.get(i), indices.get(i));
        }
        out.println();
    }
}
class Icmp extends IRInstructions {
    public String result;
    public Condition cond;
    public String type;
    public String operand1;
    public String operand2;
    Icmp(String result, Condition cond, String type, String operand1, String operand2) {
        this.result = result;
        this.cond = cond;
        this.type = type;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
    void Print(java.io.PrintStream out) {
        out.printf("%s = icmp ", result);
        out.print(cond);
        out.printf(" %s %s, %s\n", type, operand1, operand2);
    }
}
class Call extends IRInstructions {
    public String result;
    public String resultType;
    public String functionName;
    public List<String> types;
    public List<String> values;
    public Integer size;
    Call(String result, String resultType, String functionName, List<String> types, List<String> values, Integer size) {
        this.result = result;
        this.resultType = resultType;
        this.functionName = functionName;
        this.types = types;
        this.values = values;
        this.size = size;
    }
    void Print(java.io.PrintStream out) {
        if (result == null) {
            out.printf("call %s %s(", resultType, functionName);
        } else {
            out.printf("%s = call %s %s(", result, resultType, functionName);
        }
        for (int i = 0; i < size - 1; i++) {
            out.printf("%s %s, ", types.get(i), values.get(i));
        }
        if (size != 0) {
            out.printf("%s %s)\n", types.get(size - 1), values.get(size - 1));
        } else {
            out.println(")");
        }
    }
}
class Phi extends IRInstructions {
    public String result;
    public String type;
    public List<String> values;
    public List<String> lables;
    public Integer size;
    Phi(String result, String type, List<String> values, List<String> lables, Integer size) {
        this.result = result;
        this.type = type;
        this.values = values;
        this.lables = lables;
        this.size = size;
    }
    void Print(java.io.PrintStream out) {
        out.printf("%s = phi %s ", result, type);
        for (int i = 0; i < size; i++) {
            out.printf("[ %s, %s ]", values.get(i), lables.get(i));
            if (i != size - 1) {
                out.print(", ");
            }
        }
        out.println();
    }
}
class Select extends IRInstructions {
    public String result;
    public String condition;
    public String type1;
    public String type2;
    public String value1;
    public String value2;
    public Select(String result, String condition, String type1, String type2, String value1, String value2) {
        this.result = result;
        this.condition = condition;
        this.type1 = type1;
        this.type2 = type2;
        this.value1 = value1;
        this.value2 = value2;
    }
    public void Print(java.io.PrintStream out) {
        out.printf("%s = select i1 %s, %s %s, %s %s\n", result, condition, type1, value1, type2, value2);
    }
}
class StructType extends IRInstructions {
    public String type;
    public List<String> fields;
    public StructType(String type, List<String> fields) {
        this.type = type;
        this.fields = fields;
    }
    public void Print(java.io.PrintStream out) {
        out.print(type + " = type { ");
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            out.print(field);
            if (i != fields.size() - 1) {
                out.print(", ");
            }
        }
        out.println(" }\n");
    }
}
class Lable extends IRInstructions {
    public String name;
    public Lable(String name) {
        this.name = name;
    }
    public void Print(java.io.PrintStream out) {
        out.println(name + ":\n");
    }
}
class Header extends IRInstructions {
    public String name;
    public String returnType;
    public List<String> types;
    public List<String> names;
    public boolean isDeclare = false;
    public Header(String name, String returnType, List<String> types, List<String> names) {
        this.name = name;
        this.returnType = returnType;
        this.types = types;
        this.names = names;
    }
    public void Print(java.io.PrintStream out) {
        if (isDeclare) {
            out.print("declare " + returnType + " " + name + "(");
            for (int i = 0; i < types.size(); i++) {
                out.print(types.get(i));
                if (i != types.size() - 1) {
                    out.print(", ");
                }
            }
            out.println(")\n");
        } else {
            out.print("define ");
            if (returnType != null) {
                out.print(returnType + " " + name + "(");
            } else {
                out.print("void" + " " + name + "(");
            }
            for (int i = 0; i < types.size(); i++) {
                out.print(types.get(i) + " " + names.get(i));
                if (i != types.size() - 1) {
                    out.print(", ");
                }
            }
            out.println(") {");
        }
    }
}
class Constant extends IRInstructions {
    public String name;
    public String type;
    public String value;
    public Constant(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
    public void Print(java.io.PrintStream out) {
        out.printf("%s = constant %s %s\n", name, type, value);
    }
}

class IRGenerator extends IRInstructions {
    private FunctionItem currentFunction;
    private Map<String, Integer> variableNumMap;
    private Map<String, String> variableTypeMap;
    private List<IRInstructions> instructions;
    private Scope root;
    public ImplItem currentImpl;
    private Map<Integer, String> tempIndexMap;
    private Semantics semantics;
    private IR llvm;
    public IRGenerator(FunctionItem fun, Map<String, Integer> variableNumMap, Map<String, String> variableTypeMap,
                    ImplItem currentImpl, Scope root, Semantics semantics, Map<Integer, String> tempIndexMap, IR llvm) {
        this.root = root;
        this.currentFunction = fun;
        this.variableNumMap = variableNumMap;
        this.llvm = llvm;
        this.variableTypeMap = variableTypeMap;
        this.instructions = new ArrayList<>();
        this.currentImpl = currentImpl;
        this.tempIndexMap = tempIndexMap;
        this.semantics = semantics;
    }
    private String typeToString(Type type, Scope scope) {
        if (type instanceof TypePath) {
            TypePath typePath = (TypePath)type;
            String name = typePath.name;
            if (name.equals("i32")) {
                return "i32";
            } else if (name.equals("isize") || name.equals("usize") || name.equals("u32")) {
                return "i32";
            } else if (name.equals("bool")) {
                return "i1";
            } else if (name.equals("char")) {
                return "i8";
            } else if (name.equals("114514")) {
                return "i32";
            } else {
                return "%struct." + name;
            }
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            Expression exp = arrayType.exp;
            Integer value = getValueInteger(exp, scope);
            String str = typeToString(arrayType.type, scope);
            return "[" + value + " x " + str + "]";
        } else if (type instanceof ReferenceType) {
            return "ptr";
        } else {
            return null;
        }
    }
    private Integer getValueInteger(Expression exp, Scope scope) {
        if (exp == null) {
            return null;
        }
        if (exp instanceof BinaryExpression) {
            Integer temp = getValueInteger(((BinaryExpression)exp).left, scope);
            Integer temp_ = getValueInteger(((BinaryExpression)exp).right, scope);
            BinaryExpression binary = (BinaryExpression)exp;
            if (binary.operator.equals("+")) {
                return temp + temp_;
            } else if (binary.operator.equals("-")) {
                return temp - temp_;
            } else if (binary.operator.equals("*")) {
                return temp * temp_;
            } else if (binary.operator.equals("/")) {
                return temp / temp_;
            } else if (binary.operator.equals("%")) {
                return temp % temp_;
            }
        } else if (exp instanceof UnaryExpression) {
            Integer temp = getValueInteger(((UnaryExpression)exp).operand, scope);
            UnaryExpression unary = (UnaryExpression)exp;
            if (unary.operator.equals("-")) {
                return -temp;
            }
        } else if (exp instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression)exp;
            Integer temp = (Integer)literal.value;
            return temp;
        } else if (exp instanceof IdentifierExpression) {
            IdentifierExpression id = (IdentifierExpression)exp;
            String name = id.name;
            for (Map.Entry<String, Item> entry : scope.valueMap.entrySet()) {
                if (entry.getValue() instanceof ConstItem && entry.getKey().equals(name)) {
                    ConstItem constitem = (ConstItem)(entry.getValue());
                    return getValueInteger(constitem.exp, scope);
                }
            }
            if (scope.parent != null) {
                return getValueInteger(exp, scope.parent);
            }
        } else if (exp instanceof GroupedExpression) {
            return getValueInteger(((GroupedExpression)exp).inner, scope);
        }
        return null;
    }
    private List<IRInstructions> getLetStatements(Statement sta, Scope scope) {
        List<IRInstructions> instructions = new ArrayList<>();
        Integer scopeNum = scope.scopeIndex;
        LetStatement let = (LetStatement)sta;
        Pattern pattern = let.pattern;
        Type type = let.type;
        if (pattern instanceof ReferencePattern) {
            pattern = ((ReferencePattern)(pattern)).subPattern;
        }
        List<IRInstructions> exprInstructions = getExpressions(let.initializer, let.sta.scope, scope, null, null);
        if (exprInstructions != null) {instructions.addAll(exprInstructions);}
        String variableName = targetVariableName(exprInstructions, let.initializer, scope);
        String name = '%' + ((IdentifierPattern)pattern).name + '_' + scopeNum;
        String typeStr = typeToString(type, scope);
        if (variableNumMap.containsKey(name)) {
            name = name + '_' + scopeNum;
        }
        instructions.add(new Alloca(name, typeStr));
        variableTypeMap.put(name, typeStr);
        variableNumMap.put(name, 0);
        instructions.add(new Store(variableName, typeStr, name));
        return instructions;
    }
    private String targetVariableName(List<IRInstructions> instructions, Expression exp, Scope scope) {
        if (exp instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression)exp;
            if (binary.operator.equals("as")) {
                if (instructions.get(instructions.size() - 1) instanceof Select) {
                    Select select = (Select)instructions.get(instructions.size() - 1);
                    return select.result;
                }
                return targetVariableName(instructions, binary.left, scope);
            }
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof BinaryInstruction) {
                BinaryInstruction bi = (BinaryInstruction)ins;
                return bi.result;
            } else if (ins instanceof Icmp) {
                Icmp icmp = (Icmp)ins;
                return icmp.result;
            } else {
                System.err.println("Unmatch BinaryExpression ERROR!");
                return null;
            }
        } else if (exp instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression)exp;
            if (unary.operator.equals("!")) {
                IRInstructions ins = instructions.get(instructions.size() - 1);
                if (ins instanceof BinaryInstruction) {
                    BinaryInstruction bi = (BinaryInstruction)ins;
                    return bi.result;
                } else {
                    System.out.println("Unmatch !x ERROR!");
                    return null;
                }
            } else if (unary.operator.equals("-")) {
                IRInstructions ins = instructions.get(instructions.size() - 1);
                if (ins instanceof BinaryInstruction) {
                    BinaryInstruction bi = (BinaryInstruction)ins;
                    return bi.result;
                } else {
                    System.out.println("Unmatch -x ERROR!");
                    return null;
                }
            } else if (unary.operator.equals("*")) {
                IRInstructions ins = instructions.get(instructions.size() - 1);
                if (ins instanceof Load) {
                    Load load = (Load)ins;
                    return load.result;
                } else {
                    System.out.println("Unmatch *x ERROR!");
                    return null;
                }
            } else if (unary.operator.equals("&")) {
                if (unary.operand instanceof IdentifierExpression) {
                    IdentifierExpression id = (IdentifierExpression)(unary.operand);
                    String name = id.name;
                    Integer index = scope.scopeIndex;
                    String targetName = null;
                    if (name.equals("self")) {
                        String name_ = typeToString(currentImpl.type, scope) + '_' + currentFunction.scope.scopeIndex;
                        return name_;
                    }
                    while (true) {
                        String variableName = '%' + name + "_" + index;
                        if (variableNumMap.containsKey(variableName + '_' + index)) {
                            targetName = variableName + '_' + index;
                            break;
                        } else if (variableNumMap.containsKey(variableName)) {
                            targetName = variableName;
                            break;
                        } else {
                            scope = scope.parent;
                            if (scope == null) {
                                System.err.println("&Undefined variable: " + name + ' ' + currentFunction.name);
                                return null;
                            }
                            index = scope.scopeIndex;
                        }
                    }
                    return targetName;
                } else if (unary.operand instanceof UnaryExpression) {
                    UnaryExpression innerUnary = (UnaryExpression)unary.operand;
                    if (innerUnary.operator.equals("*")) {
                        return targetVariableName(instructions, innerUnary.operand, scope);
                    } else {
                        System.out.println("Undefined &(*x) Unary Expression!");
                        return null;
                    }
                } else if (unary.operand instanceof ArrIndexExpression || unary.operand instanceof FieldExpression) {
                    IRInstructions ins = instructions.get(instructions.size() - 1);
                    if (ins instanceof Getelementptr) {
                        Getelementptr gep = (Getelementptr)ins;
                        return gep.result;
                    } else {
                        System.out.println("Unmatch Identifier ERROR!");
                        return null;
                    }
                } else {
                    System.out.println("Undefined &x Unary Expression!");
                    return null;
                }
            }
        } else if (exp instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression)exp;
            if (literal.literal_type == TokenType.BOOL_LITERAL) {
                String Bool = (String)(literal.value);
                if (Bool.equals("true")) {
                    return "1";
                } else {
                    return "0";
                }
            } else if (literal.literal_type == TokenType.CHAR_LITERAL) {
                char Char = (char)(literal.value);
                int value = (int)Char;
                return ((Integer)value).toString();
            } else if (literal.literal_type == TokenType.INTERGER_LITERAL) {
                if (literal.value instanceof Integer) {
                    Integer value = (Integer)(literal.value);
                    return value.toString();
                } else {
                    Long value = (Long)(literal.value);
                    return value.toString();
                }
            }
        } else if (exp instanceof IdentifierExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof Load) {
                Load load = (Load)ins;
                return load.result;
            } else {
                System.out.println("Unmatch Identifier ERROR!");
                return null;
            }
        } else if (exp instanceof ArrIndexExpression || exp instanceof FieldExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof Load) {
                Load load = (Load)ins;
                return load.result;
            } else {
                System.out.println("Unmatch Arr or Field ERROR!");
                return null;
            }
        } else if (exp instanceof CallFuncExpression || exp instanceof CallMethodExpression) {
            Type type = semantics.getType(scope, scope, exp);
            if (type instanceof ArrayType) {
                IRInstructions ins = instructions.get(instructions.size() - 1);
                if (ins instanceof Load) {
                    Load call = (Load)ins;
                    return call.result;
                } else {
                    System.out.println("Unmatch CallFunc or CallMethod ERROR!!");
                    return null;
                }
            }
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof Call) {
                Call call = (Call)ins;
                return call.result;
            } else {
                System.out.println("Unmatch CallFunc or CallMethod ERROR!");
                return null;
            }
        } else if (exp instanceof BlockExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof Load) {
                Load load = (Load)ins;
                return load.result;
            } else {
                System.out.println("Unmatch BlockExpression ERROR!");
                System.out.println(ins);
                return null;
            }
        } else if (exp instanceof IfExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof Load) {
                Load load = (Load)ins;
                return load.result;
            } else {
                System.out.println("Unmatch IfExpression ERROR!");
                return null;
            }
        } else if (exp instanceof StructExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof Load) {
                Load load = (Load)ins;
                return load.result;
            } else {
                System.out.println("Unmatch StructExpression ERROR!");
                return null;
            }
        } else if (exp instanceof ArrayExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 1);
            if (ins instanceof Load) {
                Load load = (Load)ins;
                return load.result;
            } else {
                System.out.println("Unmatch ArrayExpression ERROR!");
                return null;
            }
        } else if (exp instanceof GroupedExpression) {
            return targetVariableName(instructions, ((GroupedExpression)exp).inner, scope);
        }
        return null;
    }
    private String targetVariableNameLeft(List<IRInstructions> instructions, Expression exp, Scope scope) {
        if (exp instanceof IdentifierExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 1);
            Load load = (Load)ins;
            if (load.type.equals("ptr")) {
                return load.result;
            } else {
                return load.pointer;
            }
        } else if (exp instanceof ArrIndexExpression || exp instanceof FieldExpression) {
            IRInstructions ins = instructions.get(instructions.size() - 2);
            if (ins instanceof Getelementptr) {
                Getelementptr get = (Getelementptr)ins;
                return get.result;
            } else {
                System.err.println("HAS ERROR!");
                return null;
            }
        } else if (exp instanceof UnaryExpression) {
            UnaryExpression unaryExpression = (UnaryExpression)exp;
            if (unaryExpression.operator.equals("*")) {
                instructions.remove(instructions.size() - 1);
                return targetVariableName(instructions, unaryExpression.operand, scope);
            } else {
                System.err.println("Unmatch UnaryExpression ERROR!");
                return null;
            }
        } else {
            System.err.println("Unmatch Expression ERROR!");
            return null;
        }
    }
    private String getType(String variableName) {
        if (variableName.startsWith("%ttemp.")) {
            String numPart = variableName.substring(7);
            Integer x = Integer.parseInt(numPart);
            return tempIndexMap.get(x);
        } else if (variableName.charAt(0) - '0' >= 0 && variableName.charAt(0) - '0' <= 9) {
            return "i32";
        }else {
            int dotIndex = variableName.lastIndexOf('.');
            if (dotIndex != -1) {
                String beforeDot = variableName.substring(0, dotIndex);
                return variableTypeMap.get(beforeDot);
            } else {
                return variableTypeMap.get(variableName);
            }
        }
    }
    private List<IRInstructions> getExpressions(Expression exp, Scope subScope, Scope scope, String startLable, String endLable) {
        if (exp instanceof BinaryExpression) {
            return getBinaryExpression(exp, scope);
        } else if (exp instanceof UnaryExpression) {
            return getUnaryExpression(exp, scope);
        } else if (exp instanceof LiteralExpression) {
            return null;
        } else if (exp instanceof IdentifierExpression) {
            List<IRInstructions> instructions = new ArrayList<>();
            IRInstructions ins = getIdentifierExpression(exp, scope);
            instructions.add(ins);
            return instructions;
        } else if (exp instanceof ArrIndexExpression || exp instanceof FieldExpression) {
            return getArrIndexOrFieldExpression(exp, scope);
        } else if (exp instanceof CallFuncExpression ) {
            return getCallFuncExpression(exp, scope);
        } else if (exp instanceof CallMethodExpression) {
            return getCallMethodExpression(exp, scope);
        } else if (exp instanceof BlockExpression) {
            return getBlockExpression(exp, subScope, startLable, endLable);
        } else if (exp instanceof LoopExpression) {
            return getLoopExpression(exp, subScope, scope);
        } else if (exp instanceof WhileExpression) {
            return getWhileExpression(exp, subScope, scope);
        } else if (exp instanceof IfExpression) {
            IfExpression ifExpression = (IfExpression)exp;
            boolean flag = false;
            if (ifExpression.then_branch.exp != null && ifExpression.else_branch != null) {
                flag = true;
            }
            return getIfExpression(flag, exp, ifExpression.thisSta.scope, scope, startLable, endLable, null, null);
        } else if (exp instanceof StructExpression) {
            return getStructExpression(exp, scope);
        } else if (exp instanceof ArrayExpression) {
            return getArrayExpression(exp, scope);
        } else if (exp instanceof GroupedExpression) {
            return getGroupedExpression(exp, subScope, scope);
        } else if (exp instanceof ReturnExpression) {
            return getReturnExpression(exp, subScope, scope);
        }
        System.out.println("Unmatch Expression!" + exp.getClass().getName());
        return null;
    }
    private List<IRInstructions> getConditionInstructions(Expression exp, Scope subScope, Scope scope, String trueLable, String falseLable) {
        List<IRInstructions> instructions = new ArrayList<>();
        if (exp instanceof GroupedExpression) {
            return getConditionInstructions(((GroupedExpression) exp).inner, subScope, scope, trueLable, falseLable);
        }
        if (exp instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) exp;
            if (binary.operator.equals("&&")) {
                String checkRightLabel = "and_rhs_" + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "lable");
                instructions.addAll(getConditionInstructions(binary.left, subScope, scope, checkRightLabel, falseLable));
                instructions.add(new Lable(checkRightLabel));
                instructions.addAll(getConditionInstructions(binary.right, subScope, scope, trueLable, falseLable));
                return instructions;
            } else if (binary.operator.equals("||")) {
                String checkRightLabel = "or_rhs_" + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "lable");
                instructions.addAll(getConditionInstructions(binary.left, subScope, scope, trueLable, checkRightLabel));
                instructions.add(new Lable(checkRightLabel));
                instructions.addAll(getConditionInstructions(binary.right, subScope, scope, trueLable, falseLable));
                return instructions;
            }
        }
        if (exp instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) exp;
            if (unary.operator.equals("!")) {
                return getConditionInstructions(unary.operand, subScope, scope, falseLable, trueLable);
            }
        }
        List<IRInstructions> evalIns = getExpressions(exp, subScope, scope, null, null);
        if (evalIns != null) {
            instructions.addAll(evalIns);
        }
        String condVar = targetVariableName(instructions, exp, scope);
        instructions.add(new Br(true, condVar, trueLable, falseLable));
        return instructions;
    }
    private List<IRInstructions> getBinaryExpression(Expression exp, Scope scope) {
        BinaryExpression binary = (BinaryExpression)exp;
        List<IRInstructions> instructions = new ArrayList<>();
        if (binary.operator.equals("=")) {
            Expression left = binary.left;
            Expression right = binary.right;
            List<IRInstructions> list1 = getExpressions(left, binary.sta.scope, scope, null, null);
            List<IRInstructions> list2 = getExpressions(right, binary.sta.scope, scope, null, null);
            String left_ = targetVariableNameLeft(list1, left, scope);
            String right_ = targetVariableName(list2, right, scope);
            if (list1 != null) {instructions.addAll(list1);}
            if (list2 != null) {instructions.addAll(list2);}
            String type = typeToString(semantics.getType(scope, scope, right), scope);
            instructions.add(new Store(right_, type, left_));
            return instructions;
        } else if (binary.operator.equals("+") || binary.operator.equals("-") || binary.operator.equals("/") ||
                   binary.operator.equals("*") || binary.operator.equals("%") || binary.operator.equals("&") ||
                   binary.operator.equals("|") || binary.operator.equals("^") || binary.operator.equals("<<") ||
                   binary.operator.equals(">>")) {
            Expression left = binary.left;
            Expression right = binary.right;
            List<IRInstructions> list1 = getExpressions(left, scope, scope, null, null);
            List<IRInstructions> list2 = getExpressions(right, scope, scope, null, null);
            String left_ = targetVariableName(list1, left, scope);
            String right_ = targetVariableName(list2, right, scope);
            if (list1 != null) {instructions.addAll(list1);}
            if (list2 != null) {instructions.addAll(list2);}
            String newTemp = "%ttemp." + tempIndexMap.size();
            String type = null;
            Type leftType = semantics.getType(scope, scope, left);
            Type rightType = semantics.getType(scope, scope, right);
            if (leftType instanceof TypePath && ((TypePath)leftType).name.equals("114514")) {
                type = typeToString(semantics.getType(scope, scope, right), scope);
            } else if (rightType instanceof TypePath && ((TypePath)rightType).name.equals("114514")) {
                type = typeToString(semantics.getType(scope, scope, left), scope);
            } else {
                type = typeToString(semantics.getType(scope, scope, left), scope);
            }
            tempIndexMap.put(tempIndexMap.size(), type);
            Type type_ = semantics.getType(scope, scope, left);
            Type type__ = semantics.getType(scope, scope, right);
            boolean flag = false;
            if (type_ instanceof TypePath) {
                TypePath typePath = (TypePath) type_;
                if (typePath.name.equals("u32") || typePath.name.equals("usize")) {
                    flag = true;
                }
            }
            if (type__ instanceof TypePath) {
                TypePath typePath = (TypePath) type__;
                if (typePath.name.equals("u32") || typePath.name.equals("usize")) {
                    flag = true;
                }
            }
            switch (binary.operator) {
                case "+":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.add, left_, right_, type));
                    break;
                case "-":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.sub, left_, right_, type));
                    break;
                case "*":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.mul, left_, right_, type));
                    break;
                case "/":
                    if (flag) {
                        instructions.add(new BinaryInstruction(newTemp, BinaryOp.udiv, left_, right_, type));
                    } else {
                        instructions.add(new BinaryInstruction(newTemp, BinaryOp.sdiv, left_, right_, type));
                    }
                    break;
                case "%":
                    if (flag) {
                        instructions.add(new BinaryInstruction(newTemp, BinaryOp.urem, left_, right_, type));
                    } else {
                        instructions.add(new BinaryInstruction(newTemp, BinaryOp.srem, left_, right_, type));
                    }
                    break;
                case "&":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.and, left_, right_, type));
                    break;
                case "|":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.or, left_, right_, type));
                    break;
                case "^":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.xor, left_, right_, type));
                    break;
                case "<<":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.shl, left_, right_, type));
                    break;
                case ">>":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.ashr, left_, right_, type));
                    break;
                default:
                    break;
            }
            return instructions;
        } else if (binary.operator.equals("==") || binary.operator.equals("!=") || binary.operator.equals("<") ||
                   binary.operator.equals(">") || binary.operator.equals("<=") || binary.operator.equals(">=") ||
                   binary.operator.equals("||") || binary.operator.equals("&&")) {
            Expression left = binary.left;
            Expression right = binary.right;
            List<IRInstructions> list1 = getExpressions(left, scope, scope, null, null);
            List<IRInstructions> list2 = getExpressions(right, scope, scope, null, null);
            String left_ = targetVariableName(list1, left, scope);
            String right_ = targetVariableName(list2, right, scope);
            if (list1 != null) {instructions.addAll(list1);}
            if (list2 != null) {instructions.addAll(list2);}
            String newTemp = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), "i1");
            Type type_ = semantics.getType(scope, scope, right);
            boolean flag = false;
            if (type_ instanceof TypePath) {
                TypePath typePath = (TypePath) type_;
                if (typePath.name.equals("u32") || typePath.name.equals("usize")) {
                    flag = true;
                }
            }
            String type = null;
            Type leftType = semantics.getType(scope, scope, left);
            Type rightType = semantics.getType(scope, scope, right);
            if (leftType instanceof TypePath && ((TypePath)leftType).name.equals("114514")) {
                type = typeToString(semantics.getType(scope, scope, right), scope);
            } else if (rightType instanceof TypePath && ((TypePath)rightType).name.equals("114514")) {
                type = typeToString(semantics.getType(scope, scope, left), scope);
            } else {
                type = typeToString(semantics.getType(scope, scope, left), scope);
            }
            switch (binary.operator) {
                case "==":
                    instructions.add(new Icmp(newTemp, Condition.eq, type, left_, right_));
                    break;
                case "!=":
                    instructions.add(new Icmp(newTemp, Condition.ne, type, left_, right_));
                    break;
                case "<":
                    if (flag) {
                        instructions.add(new Icmp(newTemp, Condition.ult, type, left_, right_));
                    } else {
                        instructions.add(new Icmp(newTemp, Condition.slt, type, left_, right_));
                    }
                    break;
                case ">":
                    if (flag) {
                        instructions.add(new Icmp(newTemp, Condition.ugt, type, left_, right_));
                    } else {
                        instructions.add(new Icmp(newTemp, Condition.sgt, type, left_, right_));
                    }
                    break;
                case "<=":
                    if (flag) {
                        instructions.add(new Icmp(newTemp, Condition.ule, type, left_, right_));
                    } else {
                        instructions.add(new Icmp(newTemp, Condition.sle, type, left_, right_));
                    }
                    break;
                case ">=":
                    if (flag) {
                        instructions.add(new Icmp(newTemp, Condition.uge, type, left_, right_));
                    } else {
                        instructions.add(new Icmp(newTemp, Condition.sge, type, left_, right_));
                    }
                    break;
                case "||":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.or, left_, right_, type));
                    break;
                case "&&":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.and, left_, right_, type));
                    break;
                default:
                    break;
            }
            return instructions;
        } else if (binary.operator.equals("+=") || binary.operator.equals("-=") || binary.operator.equals("*=") ||
                   binary.operator.equals("/=") || binary.operator.equals("%=") || binary.operator.equals("&=") || 
                   binary.operator.equals("|=") || binary.operator.equals("^=") || binary.operator.equals("<<=") || 
                   binary.operator.equals(">>=") ) {
            String operator = binary.operator.substring(0, binary.operator.length() - 1);
            BinaryExpression bi1 = new BinaryExpression(binary.left, operator, binary.right, null);
            BinaryExpression bi2 = new BinaryExpression(binary.left, "=", bi1, null);
            bi1.sta = bi2.sta = binary.sta;
            return getBinaryExpression(bi2, scope);
        } else if (binary.operator.equals("as")) {
            List<IRInstructions> ins = getExpressions(binary.left, scope, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetVariable = targetVariableName(instructions, binary.left, scope);
            String typeName = ((TypePath)(binary.type)).name;
            if (typeName.equals("bool")) {
                String newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "i1");
                instructions.add(new Icmp(newTemp, Condition.ne, "i32", targetVariable, "0"));
                return instructions;
            } else if (typeName.equals("i32") || typeName.equals("u32") || typeName.equals("isize") || typeName.equals("usize")) {
                String typeStr = typeToString(semantics.getType(scope, scope, binary.left), scope);
                if (typeStr.equals("i1")) {
                    String newTemp = "%ttemp." + tempIndexMap.size();
                    tempIndexMap.put(tempIndexMap.size(), "i32");
                    instructions.add(new Select(newTemp, targetVariable, "i32", "i32", "1", "0"));
                    return instructions;
                } else {
                    return getExpressions(binary.left, scope, scope, null, null);
                }
            } else {
                return getExpressions(binary.left, scope, scope, null, null);
            }
        } else {
            System.err.println("Undefined binary operator: " + binary.operator);
            return null;
        }
    }
    private List<IRInstructions> getUnaryExpression(Expression exp, Scope scope) {
        UnaryExpression unary = (UnaryExpression)exp;
        List<IRInstructions> instructions = new ArrayList<>();
        if (unary.operator.equals("!")) {
            List<IRInstructions> instructions_ = getExpressions(unary.operand, null, scope, null, null);
            if (instructions_ != null) {instructions.addAll(instructions_);}
            String targetVariable = targetVariableName(instructions, unary.operand, scope);
            String typeName = getType(targetVariable);
            if (typeName.equals("i1")) {
                String newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "i1");
                BinaryInstruction ins = new BinaryInstruction(newTemp, BinaryOp.xor, targetVariable, "true", "i1");
                instructions.add(ins);
                return instructions;
            } else if (typeName.equals("i32")) {
                String newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "i32");
                BinaryInstruction ins = new BinaryInstruction(newTemp, BinaryOp.xor, targetVariable, "-1", "i32");
                instructions.add(ins);
                return instructions;
            } else if (typeName.equals("i64")) {
                String newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "i64");
                BinaryInstruction ins = new BinaryInstruction(newTemp, BinaryOp.xor, targetVariable, "-1", "i64");
                instructions.add(ins);
                return instructions;
            } else {
                System.out.println("Undefined !x Unary Expression!");
                return null;
            }
        } else if (unary.operator.equals("&")) {
            if (unary.operand instanceof IdentifierExpression) {
                return null;
            } else if (unary.operand instanceof UnaryExpression) {
                UnaryExpression innerUnary = (UnaryExpression)unary.operand;
                if (innerUnary.operator.equals("*")) {
                    return getExpressions(innerUnary.operand, null, scope, null, null);
                } else {
                    System.out.println("Undefined &(*x) Unary Expression!");
                    return null;
                }
            } else if (unary.operand instanceof ArrIndexExpression || unary.operand instanceof FieldExpression) {
                List<IRInstructions> ins = getArrIndexOrFieldExpression(unary.operand, scope);
                ins.remove(ins.size() - 1);
                if (ins != null) {instructions.addAll(ins);}
                return instructions;
            } else {
                System.out.println("Undefined &x Unary Expression!");
                return null;
            }
        } else if (unary.operator.equals("-")) {
            List<IRInstructions> instructions_ = getExpressions(unary.operand, null, scope, null, null);
            if (instructions_ != null) {instructions.addAll(instructions_);}
            String targetVariable = targetVariableName(instructions, unary.operand, scope);
            String typeName = getType(targetVariable);
            if (typeName.equals("i32")) {
                String newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "i32");
                BinaryInstruction ins = new BinaryInstruction(newTemp, BinaryOp.sub, "0", targetVariable, "i32");
                instructions.add(ins);
                return instructions;
            } else if (typeName.equals("i64")) {
                String newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "i64");
                BinaryInstruction ins = new BinaryInstruction(newTemp, BinaryOp.sub, "0", targetVariable, "i64");
                instructions.add(ins);
                return instructions;
            } else {
                System.out.println("Undefined -x Unary Expression!");
                return null;
            }
        } else if (unary.operator.equals("*")) {
            List<IRInstructions> instructions_ = getExpressions(unary.operand, null, scope, null, null);
            if (instructions_ != null) {instructions.addAll(instructions_);}
            String targetVariable = targetVariableName(instructions, unary.operand, scope);
            Type type = semantics.getType(scope, scope, exp);
            String typeStr = typeToString(type, scope);
            String newTemp = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), typeStr);
            Load ins = new Load(newTemp, typeStr, targetVariable);
            instructions.add(ins);
            return instructions;
        } else {
            System.out.println("Undefined *x Unary Expression!");
            return null;
        }
    }
    private IRInstructions getIdentifierExpression(Expression exp, Scope scope) {
        IdentifierExpression id = (IdentifierExpression)exp;
        String name = id.name;
        Integer index = scope.scopeIndex;
        String targetName = null;
        String typeName = null;
        if (variableTypeMap.containsKey('@' + name)) {
            String targetName_ = '%' + name + '.' + variableNumMap.get('@' + name);
            variableNumMap.put('@' + name, variableNumMap.get('@' + name) + 1);
            typeName = variableTypeMap.get('@' + name);
            return new Load(targetName_, typeName, '@' + name);
        } else if (variableTypeMap.containsKey('%' + name)) {
            String targetName_ = '%' + name + '.' + variableNumMap.get('%' + name);
            variableNumMap.put('%' + name, variableNumMap.get('%' + name) + 1);
            typeName = variableTypeMap.get('%' + name);
            return new Load(targetName_, typeName, '%' + name);
        } else if (name.equals("self")) {
            String name_ = typeToString(currentImpl.type, scope) + '_' + currentFunction.scope.scopeIndex;
            String resultName = name_ + "." + variableNumMap.get(name_);
            variableNumMap.put(name_, variableNumMap.get(name_) + 1);
            typeName = variableTypeMap.get(name_);
            return new Load(resultName, typeName, name_);
        }
        while (true) {
            String variableName = '%' + name + "_" + index;
            if (variableNumMap.containsKey(variableName + '_' + index)) {
                targetName = variableName + '_' + index;
                typeName = variableTypeMap.get(variableName + '_' + index);
                break;
            } else if (variableNumMap.containsKey(variableName)) {
                targetName = variableName;
                typeName = variableTypeMap.get(targetName);
                break;
            } else {
                scope = scope.parent;
                if (scope == null) {
                    System.err.println("Undefined variable: " + name + currentFunction.name);
                    return null;
                }
                index = scope.scopeIndex;
            }
        }
        String resultName = targetName + "." + variableNumMap.get(targetName);
        variableNumMap.put(targetName, variableNumMap.get(targetName) + 1);
        return new Load(resultName, typeName, targetName);
    }
    private Integer getIndexofFieldExpression(Expression exp, String str, Scope scope) {
        Type type = semantics.getType(scope, scope, exp);
        if (type instanceof ReferenceType) {
            type = ((ReferenceType)type).type;
        }
        if (type instanceof TypePath) {
            String type_name = ((TypePath)type).name;
            Scope temp = scope;
            StructItem struct_ = null;
            boolean flag = false;
            while (temp != null) {
                for (Map.Entry<String, Item> entry : temp.typeMap.entrySet()) {
                    if (entry.getValue() instanceof StructItem && entry.getKey().equals(type_name)) {
                        struct_ = (StructItem)(entry.getValue());
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
                temp = temp.parent;
            }
            Integer index = 0;
            for (Parameter par : struct_.fields) {
                if (par.name.equals(str)) {
                    return index;
                } else {
                    index++;
                }
            }
        }
        return 0;
    }
    private Expression generateIndex(Expression exp, List<String> types, List<String> nums, Scope scope, List<IRInstructions> instructions) {
        if (exp instanceof FieldExpression) {
            FieldExpression field = (FieldExpression)exp;
            Expression exp_ = generateIndex(field.object, types, nums, scope, instructions);
            types.add("i32");
            nums.add(getIndexofFieldExpression(field.object, field.member, scope).toString());
            return exp_;
        } else if (exp instanceof ArrIndexExpression) {
            ArrIndexExpression arrIndex = (ArrIndexExpression)exp;
            Expression exp_ = generateIndex(arrIndex.object, types, nums, scope, instructions);
            List<IRInstructions> ins = getExpressions(arrIndex.index, scope, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String name = targetVariableName(ins, arrIndex.index, scope);
            types.add("i32");
            nums.add(name);
            return exp_;
        } else {
            return exp;
        }
    }
    private List<IRInstructions> getArrIndexOrFieldExpression(Expression exp, Scope scope) {
        List<IRInstructions> instructions = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> nums = new ArrayList<>();
        Expression exp_ = generateIndex(exp, types, nums, scope, instructions);
        types.add(0, "i32");
        nums.add(0, "0");
        List<IRInstructions> instructions_ = getExpressions(exp_, null, scope, null, null);
        if (instructions_ != null) {instructions.addAll(instructions_);}
        Type type_ = semantics.getType(scope, scope, exp_);
        if (type_ instanceof ReferenceType) {
            ReferenceType ref = (ReferenceType)type_;
            type_ = ref.type;
        }
        String type = typeToString(type_, scope);
        String newTemp = "%ttemp." + tempIndexMap.size();
        tempIndexMap.put(tempIndexMap.size(), "ptr");
        String targetVariable = targetVariableNameLeft(instructions_, exp_, scope);
        instructions.add(new Getelementptr(newTemp, type, targetVariable, types, nums, types.size()));
        String newTemp_ = "%ttemp." + tempIndexMap.size();
        String newType = typeToString(semantics.getType(scope, scope, exp), scope);
        tempIndexMap.put(tempIndexMap.size(), newType);
        instructions.add(new Load(newTemp_, newType, newTemp));
        return instructions;
    }
    private List<IRInstructions> getCallFuncExpression(Expression exp, Scope scope) {
        CallFuncExpression call = (CallFuncExpression)exp;
        String name = null;
        if (call.call_ instanceof PathExpression) {
            name = ((PathExpression)call.call_).subPath;
            String structName = ((PathExpression)call.call_).Path;
            Scope root = semantics.root;
            FunctionItem func = null;
            List<String> types = new ArrayList<>();
            List<String> values = new ArrayList<>();
            List<IRInstructions> instructions = new ArrayList<>();
            for (Scope child : root.children) {
                if (child.typeStruct != null && ((TypePath)(child.typeStruct)).name.equals(structName)) {
                    for (Map.Entry<String, Item> entry : child.valueMap.entrySet()) {
                        if (entry.getValue() instanceof FunctionItem && entry.getKey().equals(name)) {
                            func = (FunctionItem)(entry.getValue());
                        }
                    }
                }
            }
            String newTemp = "%ttemp." + tempIndexMap.size();
            if (func.return_type != null && func.return_type instanceof ArrayType) {
                tempIndexMap.put(tempIndexMap.size(), typeToString(func.return_type, scope));
                instructions.add(new Alloca(newTemp, typeToString(func.return_type, scope)));
                variableNumMap.put(newTemp, 0);
                variableTypeMap.put(newTemp, typeToString(func.return_type, scope));
                types.add("ptr");
                values.add(newTemp);
            }
            for (int i = 0; i < func.parameters.size(); i++) {
                Type type = func.parameters.get(i).type;
                if (type instanceof ArrayType) {
                    types.add("ptr");
                    List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
                    if (ins != null) {instructions.addAll(ins);}
                    String targetName = targetVariableName(ins, call.arguments.get(i), scope);
                    String newTemp_ = "%ttemp." + tempIndexMap.size();
                    tempIndexMap.put(tempIndexMap.size(), typeToString(type, scope));
                    variableNumMap.put(newTemp_, 0);
                    variableTypeMap.put(newTemp_, typeToString(type, scope));
                    instructions.add(new Alloca(newTemp_, typeToString(type, scope)));
                    instructions.add(new Store(targetName, typeToString(type, scope), newTemp_));
                    values.add(newTemp_);
                    continue;
                }
                types.add(typeToString(type, scope));
                List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
                if (ins != null) {instructions.addAll(ins);}
                String targetName = targetVariableName(ins, call.arguments.get(i), scope);
                values.add(targetName);
            }
            Type returnType = func.return_type;
            if (returnType != null && !(returnType instanceof ArrayType)) {
                String typeString = typeToString(returnType, scope);
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeString);
                instructions.add(new Call(newTemp_, typeString, "@struct." + structName + '.' + func.name, types, values, types.size()));
                return instructions;
            } else {
                String typeString = "void";
                instructions.add(new Call(null, typeString, "@struct." + structName + '.' + func.name, types, values, types.size()));
                if (returnType instanceof ArrayType) {
                    String newTemp_ = "%ttemp." + tempIndexMap.size();
                    tempIndexMap.put(tempIndexMap.size(), typeToString(returnType, scope));
                    instructions.add(new Load(newTemp_, typeToString(returnType, scope), newTemp));
                }
                return instructions;
            }
        }
        name = ((IdentifierExpression)call.call_).name;
        if (name.equals("exit")) {
            List<IRInstructions> instructions = new ArrayList<>();
            List<IRInstructions> ins = getExpressions(call.arguments.get(0), null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, call.arguments.get(0), scope);
            instructions.add(new Call(null, "void", "@exit", List.of("i32"), List.of(targetName), 1));
            return instructions;
        } else if (name.equals("printlnInt")) {
            List<IRInstructions> instructions = new ArrayList<>();
            List<IRInstructions> ins = getExpressions(call.arguments.get(0), null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, call.arguments.get(0), scope);
            instructions.add(new Call(null, "void", "@printlnInt", List.of("i32"), List.of(targetName), 1));
            return instructions;
        } else if (name.equals("printInt")) {
            List<IRInstructions> instructions = new ArrayList<>();
            List<IRInstructions> ins = getExpressions(call.arguments.get(0), null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, call.arguments.get(0), scope);
            instructions.add(new Call(null, "void", "@printInt", List.of("i32"), List.of(targetName), 1));
            return instructions;
        } else if (name.equals("getInt")) {
            List<IRInstructions> instructions = new ArrayList<>();
            String newTemp = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), "i32");
            instructions.add(new Call(newTemp, "i32", "@getInt", new ArrayList<>(), new ArrayList<>(), 0));
            return instructions;
        }
        FunctionItem func = null;
        List<String> types = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<IRInstructions> instructions = new ArrayList<>();
        Scope temp = scope;
        while (temp != null) {
            for (Map.Entry<String, Item> entry : temp.valueMap.entrySet()) {
                if (entry.getValue() instanceof FunctionItem && entry.getKey().equals(name)) {
                    func = (FunctionItem)(entry.getValue());
                    break;
                }
            }
            if (func != null) break;
            temp = temp.parent;
        }
        String newTemp = "%ttemp." + tempIndexMap.size();
        if (func.return_type != null && func.return_type instanceof ArrayType) {
            tempIndexMap.put(tempIndexMap.size(), typeToString(func.return_type, scope));
            instructions.add(new Alloca(newTemp, typeToString(func.return_type, scope)));
            variableNumMap.put(newTemp, 0);
            variableTypeMap.put(newTemp, typeToString(func.return_type, scope));
            types.add("ptr");
            values.add(newTemp);
        }
        for (int i = 0; i < func.parameters.size(); i++) {
            Type type = func.parameters.get(i).type;
            if (type instanceof ArrayType) {
                types.add("ptr");
                List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
                if (ins != null) {instructions.addAll(ins);}
                String targetName = targetVariableName(ins, call.arguments.get(i), scope);
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeToString(type, scope));
                variableNumMap.put(newTemp_, 0);
                variableTypeMap.put(newTemp_, typeToString(type, scope));
                instructions.add(new Alloca(newTemp_, typeToString(type, scope)));
                instructions.add(new Store(targetName, typeToString(type, scope), newTemp_));
                values.add(newTemp_);
                continue;
            }
            types.add(typeToString(type, scope));
            List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, call.arguments.get(i), scope);
            values.add(targetName);
        }
        Type returnType = func.return_type;
        if (returnType != null && !(returnType instanceof ArrayType)) {
            String typeString = typeToString(returnType, scope);
            String newTemp_ = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), typeString);
            instructions.add(new Call(newTemp_, typeString, '@' + name, types, values, types.size()));
            return instructions;
        } else {
            String typeString = "void";
            instructions.add(new Call(null, typeString, '@' + name, types, values, types.size()));
            if (returnType instanceof ArrayType) {
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeToString(returnType, scope));
                instructions.add(new Load(newTemp_, typeToString(returnType, scope), newTemp));
            }
            return instructions;
        }
    }
    private List<IRInstructions> getCallMethodExpression(Expression exp, Scope scope) {
        CallMethodExpression call = (CallMethodExpression)exp;
        List<IRInstructions> instructions = new ArrayList<>();
        // Have risk for self.func
        Type type_ = semantics.getType(scope, scope, call.call_);
        if (type_ instanceof ReferenceType) {
            ReferenceType ref = (ReferenceType)type_;
            type_ = ref.type;
        }
        String typeStr = typeToString(type_, scope);
        String methodName = '@' + typeStr.substring(1) + '.' + call.method_name;
        FunctionItem func = null;
        boolean find = false;
        String structName = typeStr.substring(8);
        for (Scope child : root.children) {
            if (child.type == Kind.IMPL && ((TypePath)(child.typeStruct)).name.equals(structName)) {
                for (Map.Entry<String, Item> entry : child.valueMap.entrySet()) {
                    if (entry.getValue() instanceof FunctionItem && entry.getKey().equals(call.method_name)) {
                        func = (FunctionItem)(entry.getValue());
                        find = true;
                        break;
                    }
                }
                if (find) {
                    break;
                }
            }
        }
        List<String> types = new ArrayList<>();
        List<String> values = new ArrayList<>();
        String newTemp = "%ttemp." + tempIndexMap.size();
        if (func.return_type != null && func.return_type instanceof ArrayType) {
            tempIndexMap.put(tempIndexMap.size(), typeToString(func.return_type, scope));
            instructions.add(new Alloca(newTemp, typeToString(func.return_type, scope)));
            variableNumMap.put(newTemp, 0);
            variableTypeMap.put(newTemp, typeToString(func.return_type, scope));
            types.add("ptr");
            values.add(newTemp);
        }
        if (func.parameters.size() != 0 && func.parameters.get(0).isSelf) {
            if (func.parameters.get(0).isReference) {
                Type type = semantics.getType(scope, scope, call.call_);
                if (type instanceof ReferenceType) {
                    List<IRInstructions> ins = getExpressions(call.call_, null, scope, null, null);
                    if (ins != null) {instructions.addAll(ins);}
                    String targetName = targetVariableName(ins, call.call_, scope);
                    types.add("ptr");
                    values.add(targetName);
                } else {
                    List<IRInstructions> ins = getExpressions(new UnaryExpression("&", call.call_, true), null, scope, null, null);
                    if (ins != null) {instructions.addAll(ins);}
                    String targetName = targetVariableName(ins, new UnaryExpression("&", call.call_, true), scope);
                    types.add("ptr");
                    values.add(targetName);
                }
            } else {
                List<IRInstructions> ins = getExpressions(call.call_, null, scope, null, null);
                if (ins != null) {instructions.addAll(ins);}
                String targetName = targetVariableName(ins, call.call_, scope);
                types.add(typeStr);
                values.add(targetName);
            }
        } else {
            List<IRInstructions> ins = getExpressions(call.call_, null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
        }
        boolean flag = func.parameters.size() != 0 && func.parameters.get(0).isSelf;
        int index = flag ? 1 : 0;
        for (int i = 0; i < func.parameters.size(); i++) {
            if (func.parameters.get(i).isSelf) {
                continue;
            }
            Type type = func.parameters.get(i).type;
            if (type instanceof ArrayType) {
                types.add("ptr");
                List<IRInstructions> ins = getExpressions(call.arguments.get(i - index), null, scope, null, null);
                if (ins != null) {instructions.addAll(ins);}
                String targetName = targetVariableName(ins, call.arguments.get(i - index), scope);
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeToString(type, scope));
                variableNumMap.put(newTemp_, 0);
                variableTypeMap.put(newTemp_, typeToString(type, scope));
                instructions.add(new Alloca(newTemp_, typeToString(type, scope)));
                instructions.add(new Store(targetName, typeToString(type, scope), newTemp_));
                values.add(newTemp_);
                continue;
            }
            types.add(typeToString(type, scope));
            List<IRInstructions> ins = getExpressions(call.arguments.get(i - index), null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, call.arguments.get(i - index), scope);
            values.add(targetName);
        }
        Type returnType = func.return_type;
        if (returnType != null && !(returnType instanceof ArrayType)) {
            String typeString = typeToString(returnType, scope);
            String newTemp_ = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), typeString);
            instructions.add(new Call(newTemp_, typeString, methodName, types, values, types.size()));
        } else {
            String typeString = "void";
            instructions.add(new Call(null, typeString, methodName, types, values, types.size()));
            if (returnType instanceof ArrayType) {
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeToString(returnType, scope));
                instructions.add(new Load(newTemp_, typeToString(returnType, scope), newTemp));
            }
        }
        return instructions;
    }
    private List<IRInstructions> getReturnExpression(Expression exp, Scope subScope, Scope scope) {
        List<IRInstructions> instructions = new ArrayList<>();
        ReturnExpression ret = (ReturnExpression)exp;
        Type type_ = null;
        if (ret.value != null) {
            Scope temp = scope;
            while (temp != null) {
                if (temp.type == Kind.FUNCTION) {
                    type_ = temp.returnType;
                    break;
                } else {
                    temp = temp.parent;
                }
            }
        }
        if (ret.value == null || type_ instanceof ArrayType) {
            if (type_ instanceof ArrayType) {
                List<IRInstructions> ins = getExpressions(ret.value, subScope, scope, null, null);
                if (ins != null) {instructions.addAll(ins);}
                String targetName = targetVariableName(ins, ret.value, scope);
                String name = '%' + currentFunction.name + "_arr_" + currentFunction.scope.scopeIndex;
                instructions.add(new Store(targetName, typeToString(currentFunction.return_type, scope), name));
            }
            instructions.add(new Ret("void", null, true));
            return instructions;
        } else {
            List<IRInstructions> ins = getExpressions(ret.value, subScope, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, ret.value, scope);
            String typeStr = typeToString(type_, scope);
            instructions.add(new Ret(typeStr, targetName, false));
            return instructions;
        }
    }
    private List<IRInstructions> getStructExpression(Expression exp, Scope scope) {
        StructExpression struct = (StructExpression)exp;
        List<IRInstructions> instructions = new ArrayList<>();
        String name = struct.name;
        String typeString = "%struct." + name;
        String newTemp = "%ttemp." + tempIndexMap.size();
        tempIndexMap.put(tempIndexMap.size(), "ptr");
        instructions.add(new Alloca(newTemp, typeString));
        StructItem structItem = semantics.findStruct(name, scope);
        for (int i = 0; i < struct.fields.size(); i++) {
            Parameter field = structItem.fields.get(i);
            Expression valueExp = struct.fields.get(i).exp;
            List<IRInstructions> ins = getExpressions(valueExp, null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, valueExp, scope);
            String fieldType = typeToString(field.type, scope);
            String gepTemp = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), "ptr");
            instructions.add(new Getelementptr(gepTemp, typeString, newTemp, 
                                              Arrays.asList("i32", "i32"), 
                                              Arrays.asList("0", ((Integer)i).toString()), 2));
            instructions.add(new Store(targetName, fieldType, gepTemp));
        }
        String gepTemp_ = "%ttemp." + tempIndexMap.size();
        tempIndexMap.put(tempIndexMap.size(), typeString);
        instructions.add(new Load(gepTemp_, typeString, newTemp));
        return instructions;
    }
    private List<IRInstructions> getArrayExpression(Expression exp, Scope scope) {
        ArrayExpression array = (ArrayExpression)exp;
        List<IRInstructions> instructions = new ArrayList<>();
        Type type = semantics.getType(scope, scope, exp);
        String typeStr = typeToString(type, scope);
        String newTemp = "%ttemp." + tempIndexMap.size();
        tempIndexMap.put(tempIndexMap.size(), "ptr");
        instructions.add(new Alloca(newTemp, typeStr));
        String eleType = typeToString(semantics.getType(scope, scope, array.elements.get(0)), scope);
        if (!array.flag) {
            for (int i = 0; i < array.elements.size(); i++) {
                Expression valueExpression = array.elements.get(i);
                List<IRInstructions> ins = getExpressions(valueExpression, null, scope, null, null);
                if (ins != null) {instructions.addAll(ins);}
                String targetName = targetVariableName(ins, valueExpression, scope);
                String gepTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "ptr");
                instructions.add(new Getelementptr(gepTemp, typeStr, newTemp, 
                                                  Arrays.asList("i32", "i32"),  
                                                  Arrays.asList("0", ((Integer)i).toString()), 2));
                instructions.add(new Store(targetName, eleType, gepTemp));
            }
            String gepTemp_ = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), typeStr);
            instructions.add(new Load(gepTemp_, typeStr, newTemp));
            return instructions;
        } else {
            Expression valueExpression = array.elements.get(0);
            List<IRInstructions> ins = getExpressions(valueExpression, null, scope, null, null);
            if (ins != null) {instructions.addAll(ins);}
            String targetName = targetVariableName(ins, valueExpression, scope);
            Integer value = getValueInteger(array.elements.get(1), scope);
            for (int i = 0; i < value; i++) {
                String gepTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "ptr");
                instructions.add(new Getelementptr(gepTemp, typeStr, newTemp, 
                                                  Arrays.asList("i32", "i32"), 
                                                  Arrays.asList("0", ((Integer)i).toString()), 2));
                instructions.add(new Store(targetName, eleType, gepTemp));
            }
            String gepTemp_ = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), typeStr);
            instructions.add(new Load(gepTemp_, typeStr, newTemp));
            return instructions;
        }
    }
    private List<IRInstructions> getGroupedExpression(Expression exp, Scope subScope, Scope scope) {
        return getExpressions(((GroupedExpression)exp).inner, subScope, scope, null, null);
    }
    private List<IRInstructions> getBlockExpression(Expression exp, Scope scope, String startLable, String endLable) {
        BlockExpression block = (BlockExpression)exp;
        List<IRInstructions> instructions = new ArrayList<>();
        for (Statement sta : block.statements) {
            if (sta instanceof ExpressionStatement) {
                Expression exp_ = ((ExpressionStatement)sta).expression;
                if (exp_ instanceof BreakExpression) {
                    List<IRInstructions> ins = getBreakExpression(exp_, scope, endLable);
                    if (ins != null) {instructions.addAll(ins);}
                } else if (exp_ instanceof ContinueExpression) {
                    List<IRInstructions> ins = getContinueExpression(exp_, scope, startLable);
                    if (ins != null) {instructions.addAll(ins);}
                } else {
                    List<IRInstructions> ins = getExpressions(exp_, sta.scope, scope, startLable, endLable);
                    if (ins != null) {instructions.addAll(ins);}
                }
            } else if (sta instanceof LetStatement) {
                List<IRInstructions> ins = getLetStatements(sta, scope);
                if (ins != null) {instructions.addAll(ins);}
            } else if (sta instanceof ConstItem) {
                ConstItem constItem = (ConstItem)sta;
                Integer num = getValueInteger(constItem.exp, root);
                variableNumMap.put('%' + constItem.name, 0);
                variableTypeMap.put('%' + constItem.name, typeToString(constItem.type, root));
                instructions.add(new Alloca('%' + constItem.name, typeToString(constItem.type, root)));
                instructions.add(new Store(num.toString(), typeToString(constItem.type, root), '%' + constItem.name));
            } else if (sta instanceof StructItem) {
                StructItem structItem = (StructItem)sta;
                List<String> typeList = new ArrayList<>();
                for (Parameter field : structItem.fields) {
                    typeList.add(typeToString(field.type, scope));
                }
                StructType struct_ = new StructType("%struct." + structItem.name, typeList);
                llvm.instructions.add(0, struct_);
            } else if (sta instanceof FunctionItem) {
                IRGenerator ge = new IRGenerator((FunctionItem)sta, variableNumMap, variableTypeMap, null, semantics.root, semantics, tempIndexMap, llvm);
                ge.build();
                llvm.instructions.add(ge);
            }
        }
        // Handle the tail expression!
        Expression tailExp = block.exp;
        if (tailExp == null) {
            return instructions;
        } else {
            List<IRInstructions> ins = new ArrayList<>();
            if (tailExp instanceof IfExpression) {
                IfExpression ifExp = (IfExpression)tailExp;
                List<IRInstructions> ins_ = getExpressions(tailExp, ifExp.thisSta.scope, scope, null, null);
                if (ins_ != null) {ins.addAll(ins_);}
            } else if (tailExp instanceof ReturnExpression) {
                List<IRInstructions> ins_ = getExpressions(tailExp, scope, scope, null, null);
                if (ins_ != null) {instructions.addAll(ins_);}
                return instructions;
            } else {
                List<IRInstructions> ins_ = getExpressions(tailExp, scope, scope, null, null);
                if (ins_ != null) {ins.addAll(ins_);}
            }
            String targetName = targetVariableName(ins, tailExp, scope);
            Type type = semantics.getType(scope, scope, tailExp);
            String typeStr = typeToString(type, scope);
            if (scope.type == Kind.FUNCTION && !(tailExp instanceof ReturnExpression)) {
                if (scope.returnType instanceof ArrayType) {
                    if (ins != null) {instructions.addAll(ins);}
                    instructions.add(new Store(targetName, typeStr, '%' + currentFunction.name + "_arr_" + currentFunction.scope.scopeIndex));
                    instructions.add(new Ret("void", null, true));
                    return instructions;
                }
                if (ins != null) {instructions.addAll(ins);}
                instructions.add(new Ret(typeStr, targetName, false));
                return instructions;
            }
            String newTemp = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), "ptr");
            ins.add(0, new Alloca(newTemp, typeStr));
            ins.add(new Store(targetName, typeStr, newTemp));
            String newTemp_ = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), typeStr);
            ins.add(new Load(newTemp_, typeStr, newTemp));
            if (ins != null) {instructions.addAll(ins);}
        }
        return instructions;
    }
    // Needless to handle the tail expression!
    private List<IRInstructions> getBreakExpression(Expression exp, Scope scope, String lable) {
        Br br = new Br(false, null, lable, null);
        List<IRInstructions> instructions = new ArrayList<>();
        instructions.add(br);
        return instructions;
    }
    private List<IRInstructions> getContinueExpression(Expression exp, Scope scope, String lable) {
        Br br = new Br(false, null, lable, null);
        List<IRInstructions> instructions = new ArrayList<>();
        instructions.add(br);
        return instructions;
    }
    private List<IRInstructions> getLoopExpression(Expression exp, Scope subScope, Scope scope) {
        Integer num = subScope.scopeIndex;
        String loopLable = "loop_" + num;
        String startLable = loopLable + "_start";
        String endLable = loopLable + "_end";
        List<IRInstructions> instructions = new ArrayList<>();
        instructions.add(new Lable(startLable));
        LoopExpression loop = (LoopExpression)exp;
        List<IRInstructions> bodyIns = getBlockExpression(loop.value, subScope, startLable, endLable);
        if (bodyIns != null) {instructions.addAll(bodyIns);}
        instructions.add(new Br(false, null, startLable, null));
        instructions.add(new Lable(endLable));
        return instructions;
    }
    private List<IRInstructions> getWhileExpression(Expression exp, Scope subScope, Scope scope) {
        Integer num = subScope.scopeIndex;
        String whileLable = "while_" + num;
        String condLable = whileLable + "_cond";
        String bodyLable = whileLable + "_body";
        String endLable = whileLable + "_end";
        List<IRInstructions> instructions = new ArrayList<>();
        instructions.add(new Br(false, null, condLable, null));
        instructions.add(new Lable(condLable));
        WhileExpression whileExp = (WhileExpression)exp;
        List<IRInstructions> condIns = getConditionInstructions(whileExp.condition, null, scope, bodyLable, endLable);
        if (condIns != null) { instructions.addAll(condIns); }
        instructions.add(new Lable(bodyLable));
        List<IRInstructions> bodyIns = getBlockExpression(whileExp.body, subScope, condLable, endLable);
        if (bodyIns != null) {instructions.addAll(bodyIns);}
        instructions.add(new Br(false, null, condLable, null));
        instructions.add(new Lable(endLable));
        for (int i = 0; i < instructions.size(); i++) {
            IRInstructions ins = instructions.get(i);
            if (ins instanceof Alloca) {
                instructions.remove(i);
                instructions.add(0, ins);
            }
        }
        return instructions;
    }
    private List<IRInstructions> getIfExpression(boolean flag, Expression exp, Scope subScope, Scope scope, String startLable, String endLable, String endIfLable, String newTemp_) {
        IfExpression ifExp = (IfExpression)exp;
        Integer num = ifExp.thisSta.scope.scopeIndex;
        String thenLable = "then_" + num;
        List<IRInstructions> instructions = new ArrayList<>();
        String newTemp = newTemp_;
        if (endIfLable == null) {
            if (flag) {
                Type type = semantics.getType(ifExp.thisSta.scope, ifExp.thisSta.scope, ifExp);
                String typeString = typeToString(type, scope);
                newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeString);
                variableTypeMap.put(newTemp, typeString);
                variableNumMap.put(newTemp, 0);
                instructions.add(new Alloca(newTemp, typeString));
            }
            endIfLable = "end_if_" + num;
        }
        String elseLable = "else_" + num;
        String targetFalseLable = (ifExp.else_branch == null) ? endIfLable : elseLable;
        List<IRInstructions> condIns = getConditionInstructions(ifExp.condition, null, scope, thenLable, targetFalseLable);
        if (condIns != null) { instructions.addAll(condIns); }
        instructions.add(new Lable(thenLable));
        List<IRInstructions> thenIns = getBlockExpression(ifExp.then_branch, subScope, startLable, endLable);
        if (thenIns != null) {instructions.addAll(thenIns);}
        if (flag) {
            String variable = targetVariableName(thenIns, ifExp.then_branch, subScope);
            int lastDot = newTemp.lastIndexOf('.');
            Integer index = Integer.parseInt(newTemp.substring(lastDot + 1));
            instructions.add(new Store(variable, tempIndexMap.get(index), newTemp));
        }
        instructions.add(new Br(false, null, endIfLable, null));
        if (ifExp.else_branch != null) {
            instructions.add(new Lable(elseLable));
            if (ifExp.else_branch instanceof IfExpression) {
                List<IRInstructions> elseIns = getIfExpression(flag, ifExp.else_branch, ifExp.sta.scope, scope, startLable, endLable, endIfLable, newTemp);
                if (elseIns != null) {instructions.addAll(elseIns);}
            } else if (ifExp.else_branch instanceof BlockExpression) {
                List<IRInstructions> elseIns = getBlockExpression(ifExp.else_branch, ifExp.sta.scope, startLable, endLable);
                if (elseIns != null) {instructions.addAll(elseIns);}
                if (flag) {
                    String variable = targetVariableName(elseIns, ifExp.else_branch, ifExp.sta.scope);
                    int lastDot = newTemp.lastIndexOf('.');
                    Integer index = Integer.parseInt(newTemp.substring(lastDot + 1));
                    instructions.add(new Store(variable, tempIndexMap.get(index), newTemp));
                }
                instructions.add(new Br(false, null, endIfLable, null));
                instructions.add(new Lable(endIfLable));
            }
        } else {
            instructions.add(new Lable(endIfLable));
        }
        if (flag) {
            int lastDot = newTemp.lastIndexOf('.');
            Integer index = Integer.parseInt(newTemp.substring(lastDot + 1));
            String newTemp__ = "%ttemp." + tempIndexMap.size();
            tempIndexMap.put(tempIndexMap.size(), tempIndexMap.get(index));
            instructions.add(new Load(newTemp__, tempIndexMap.get(index), newTemp));
        }
        return instructions;
    }
    public void build() {
        // System.out.println(currentFunction.name);
        if (currentImpl == null) {
            List<String> types = new ArrayList<>();
            List<String> names = new ArrayList<>();
            List<IRInstructions> declares = new ArrayList<>();
            if (currentFunction.return_type instanceof ArrayType) {
                types.add("ptr");
                String nameArr = '%' + currentFunction.name + "_arr_" + currentFunction.scope.scopeIndex;
                names.add(nameArr);
            }
            for (Parameter param : currentFunction.parameters) {
                if (param.type instanceof ArrayType) {
                    types.add("ptr");
                    Integer num = currentFunction.scope.scopeIndex;
                    Pattern pat = param.pattern;
                    if (pat instanceof ReferencePattern) {
                        ReferencePattern ref = (ReferencePattern)pat;
                        pat = ref.subPattern;
                    }
                    String name = '%' + ((IdentifierPattern)pat).name + '_' + num;
                    variableNumMap.put(name, 0);
                    variableTypeMap.put(name, typeToString(param.type, root));
                    names.add(name);
                } else if (param.type instanceof ReferenceType) {
                    Integer num = currentFunction.scope.scopeIndex;
                    Pattern pat = param.pattern;
                    if (pat instanceof ReferencePattern) {
                        ReferencePattern ref = (ReferencePattern)pat;
                        pat = ref.subPattern;
                    }
                    String name = "%temp." + ((IdentifierPattern)pat).name + '_' + num;
                    types.add("ptr");
                    names.add(name);
                    declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num, "ptr"));
                    declares.add(new Store(name, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num));
                    variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num, 0);
                    variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num, "ptr");
                } else {
                    Integer num = currentFunction.scope.scopeIndex;
                    Pattern pat = param.pattern;
                    if (pat instanceof ReferencePattern) {
                        ReferencePattern ref = (ReferencePattern)pat;
                        pat = ref.subPattern;
                    }
                    String name = "%temp." + ((IdentifierPattern)pat).name + '_' + num;
                    types.add(typeToString(param.type, root));
                    names.add(name);
                    declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num, typeToString(param.type, root)));
                    declares.add(new Store(name, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num));
                    variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num, 0);
                    variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num, typeToString(param.type, root));
                }
            }
            String returnType = "void";
            if (currentFunction.return_type != null && !(currentFunction.return_type instanceof ArrayType)) {
                returnType = typeToString(currentFunction.return_type, root);
            }
            Header header = new Header('@' + currentFunction.name, returnType, types, names);
            instructions.add(header);
            if (declares != null) {instructions.addAll(declares);}
            List<IRInstructions> retIns = getBlockExpression(currentFunction.body, currentFunction.scope, null, null);
            if (retIns != null) {instructions.addAll(retIns);}
        } else {
            List<String> types = new ArrayList<>();
            List<String> names = new ArrayList<>();
            List<IRInstructions> declares = new ArrayList<>();
            List<Parameter> implParams = currentFunction.parameters;
            Header header = null;
            String implType = typeToString(currentImpl.type, root);
            String returnType = typeToString(currentFunction.return_type, root);
            if (currentFunction.return_type instanceof TypePath) {
                String typeName = ((TypePath)(currentFunction.return_type)).name;
                if (typeName.equals("Self")) {
                    returnType = implType;
                }
            } else if (currentFunction.return_type instanceof ArrayType) {
                returnType = "void";
                types.add("ptr");
                String nameArr = '%' + currentFunction.name + "_arr_" + currentFunction.scope.scopeIndex;
                names.add(nameArr);
            }
            if (implParams.size() == 0) {
                header = new Header('@' + implType.substring(1) + '.' + currentFunction.name,
                                    returnType, types, names);
            } else {
                Parameter firstParam = implParams.get(0);
                if (firstParam.isSelf) {
                    if (firstParam.isReference) {
                        types.add("ptr");
                        Integer num = currentFunction.scope.scopeIndex;
                        String name = implType + '_' + num;
                        variableNumMap.put(name, 0);
                        variableTypeMap.put(name, implType);
                        names.add(name);
                        boolean flag = true;
                        for (Parameter param : currentFunction.parameters) {
                            if (flag) {
                                flag = false;
                                continue;
                            }
                            if (param.type instanceof ArrayType) {
                                types.add("ptr");
                                Integer num_ = currentFunction.scope.scopeIndex;
                                Pattern pat = param.pattern;
                                if (pat instanceof ReferencePattern) {
                                    ReferencePattern ref = (ReferencePattern)pat;
                                    pat = ref.subPattern;
                                }
                                String name_ = '%' + ((IdentifierPattern)pat).name + '_' + num_;
                                variableNumMap.put(name_, 0);
                                variableTypeMap.put(name_, typeToString(param.type, root));
                                names.add(name_);
                            } else if (param.type instanceof ReferenceType) {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                Pattern pat = param.pattern;
                                if (pat instanceof ReferencePattern) {
                                    ReferencePattern ref = (ReferencePattern)pat;
                                    pat = ref.subPattern;
                                }
                                String name_ = "%temp." + ((IdentifierPattern)pat).name + '_' + num_;
                                types.add("ptr");
                                names.add(name_);
                                declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num_, "ptr"));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num_));
                                variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, 0);
                                variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, "ptr");
                            } else {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                Pattern pat = param.pattern;
                                if (pat instanceof ReferencePattern) {
                                    ReferencePattern ref = (ReferencePattern)pat;
                                    pat = ref.subPattern;
                                }
                                String name_ = "%temp." + ((IdentifierPattern)pat).name + '_' + num_;
                                types.add(typeToString(param.type, root));
                                names.add(name_);
                                declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num_, typeToString(param.type, root)));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num_));
                                variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, 0);
                                variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, typeToString(param.type, root));
                            }
                        }
                        header = new Header('@' + implType.substring(1) + '.' + currentFunction.name, 
                                                returnType, types, names);
                    } else {
                        types.add(implType);
                        Integer num = currentFunction.scope.scopeIndex;
                        String name = "%temp." + implType.substring(1) + '_' + num;
                        names.add(name);
                        declares.add(new Alloca(implType + '_' + num, implType));
                        declares.add(new Store(name, implType, implType + '_' + num));
                        variableNumMap.put(implType + '_' + num, 0);
                        variableTypeMap.put(implType + '_' + num, implType);
                        boolean flag = true;
                        for (Parameter param : currentFunction.parameters) {
                            if (flag) {
                                flag = false;
                                continue;
                            }
                            if (param.type instanceof ArrayType) {
                                types.add("ptr");
                                Integer num_ = currentFunction.scope.scopeIndex;
                                Pattern pat = param.pattern;
                                if (pat instanceof ReferencePattern) {
                                    ReferencePattern ref = (ReferencePattern)pat;
                                    pat = ref.subPattern;
                                }
                                String name_ = '%' + ((IdentifierPattern)pat).name + '_' + num_;
                                variableNumMap.put(name_, 0);
                                variableTypeMap.put(name_, typeToString(param.type, root));
                                names.add(name_);
                            } else if (param.type instanceof ReferenceType) {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                Pattern pat = param.pattern;
                                if (pat instanceof ReferencePattern) {
                                    ReferencePattern ref = (ReferencePattern)pat;
                                    pat = ref.subPattern;
                                }
                                String name_ = "%temp." + ((IdentifierPattern)pat).name + '_' + num_;
                                types.add("ptr");
                                names.add(name_);
                                declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num_, "ptr"));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num_));
                                variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, 0);
                                variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, "ptr");
                            } else {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                Pattern pat = param.pattern;
                                if (pat instanceof ReferencePattern) {
                                    ReferencePattern ref = (ReferencePattern)pat;
                                    pat = ref.subPattern;
                                }
                                String name_ = "%temp." + ((IdentifierPattern)pat).name + '_' + num_;
                                types.add(typeToString(param.type, root));
                                names.add(name_);
                                declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num_, typeToString(param.type, root)));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num_));
                                variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, 0);
                                variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, typeToString(param.type, root));
                            }
                        }
                        header = new Header('@' + implType.substring(1) + '.' + currentFunction.name, 
                                                returnType, types, names);
                    }
                } else {
                    for (Parameter param : currentFunction.parameters) {
                        if (param.type instanceof ArrayType) {
                            types.add("ptr");
                            Integer num_ = currentFunction.scope.scopeIndex;
                            Pattern pat = param.pattern;
                            if (pat instanceof ReferencePattern) {
                                ReferencePattern ref = (ReferencePattern)pat;
                                pat = ref.subPattern;
                            }
                            String name_ = '%' + ((IdentifierPattern)pat).name + '_' + num_;
                            variableNumMap.put(name_, 0);
                            variableTypeMap.put(name_, typeToString(param.type, root));
                            names.add(name_);
                        } else if (param.type instanceof ReferenceType) {
                            Integer num_ = currentFunction.scope.scopeIndex;
                            Pattern pat = param.pattern;
                            if (pat instanceof ReferencePattern) {
                                ReferencePattern ref = (ReferencePattern)pat;
                                pat = ref.subPattern;
                            }
                            String name_ = "%temp." + ((IdentifierPattern)pat).name + '_' + num_;
                            types.add("ptr");
                            names.add(name_);
                            declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num_, "ptr"));
                            declares.add(new Store(name_, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num_));
                            variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, 0);
                            variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, "ptr");
                        } else {
                            Integer num_ = currentFunction.scope.scopeIndex;
                            Pattern pat = param.pattern;
                            if (pat instanceof ReferencePattern) {
                                ReferencePattern ref = (ReferencePattern)pat;
                                pat = ref.subPattern;
                            }
                            String name_ = "%temp." + ((IdentifierPattern)pat).name + '_' + num_;
                            types.add(typeToString(param.type, root));
                            names.add(name_);
                            declares.add(new Alloca("%" + ((IdentifierPattern)pat).name + '_' + num_, typeToString(param.type, root)));
                            declares.add(new Store(name_, typeToString(param.type, root), "%" + ((IdentifierPattern)pat).name + '_' + num_));
                            variableNumMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, 0);
                            variableTypeMap.put("%" + ((IdentifierPattern)pat).name + '_' + num_, typeToString(param.type, root));
                        }
                    }
                    header = new Header('@' + implType.substring(1) + '.' + currentFunction.name, 
                                            returnType, types, names);
                }
            }
            instructions.add(header);
            if (declares != null) {instructions.addAll(declares);}
            List<IRInstructions> retIns = getBlockExpression(currentFunction.body, currentFunction.scope, null, null);
            if (retIns != null) {instructions.addAll(retIns);}
        }
        if (currentFunction.return_type == null || currentFunction.return_type instanceof ArrayType) {
            instructions.add(new Ret(null, null, true));
        } else if (!(instructions.get(instructions.size() - 1) instanceof Ret)) {
            if (currentFunction.return_type instanceof ReferenceType) {
                instructions.add(new Ret("ptr", null, false));
            } else if (typeToString(currentFunction.return_type, root).equals("i32")) {
                instructions.add(new Ret("i32", "0", false));
            } else if (typeToString(currentFunction.return_type, root).equals("i1")) {
                instructions.add(new Ret("i1", "true", false));
            } else {
                instructions.add(new Ret(typeToString(currentFunction.return_type, root), null, false));
            }
        }
    }
    void Print(java.io.PrintStream out) {
        for (IRInstructions ins : instructions) {
            ins.Print(out);
        }
        out.println("}");
    }
}

public class IR {
    public List<IRInstructions> instructions;
    private List<Item> program;
    private Scope root;
    private Semantics semantics;
    private Map<String, Integer> variableNumMap;
    private Map<String, String> variableTypeMap;
    private Map<Integer, String> tempIndexMap;
    private String typeToString(Type type, Scope scope) {
        if (type instanceof TypePath) {
            TypePath typePath = (TypePath)type;
            String name = typePath.name;
            if (name.equals("i32") || name.equals("u32")) {
                return "i32";
            } else if (name.equals("isize") || name.equals("usize")) {
                return "i32";
            } else if (name.equals("bool")) {
                return "i1";
            } else if (name.equals("char")) {
                return "i8";
            } else if (name.equals("114514")) {
                return "i32";
            } else {
                return "%struct." + name;
            }
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            Expression exp = arrayType.exp;
            Integer value = getValueInteger(exp, scope);
            String str = typeToString(arrayType.type, scope);
            return "[" + value + " x " + str + "]";
        } else if (type instanceof ReferenceType) {
            return "ptr";
        } else {
            return null;
        }
    }
    private Integer getValueInteger(Expression exp, Scope scope) {
        if (exp == null) {
            return null;
        }
        if (exp instanceof BinaryExpression) {
            Integer temp = getValueInteger(((BinaryExpression)exp).left, scope);
            Integer temp_ = getValueInteger(((BinaryExpression)exp).right, scope);
            BinaryExpression binary = (BinaryExpression)exp;
            if (binary.operator.equals("+")) {
                return temp + temp_;
            } else if (binary.operator.equals("-")) {
                return temp - temp_;
            } else if (binary.operator.equals("*")) {
                return temp * temp_;
            } else if (binary.operator.equals("/")) {
                return temp / temp_;
            } else if (binary.operator.equals("%")) {
                return temp % temp_;
            }
        } else if (exp instanceof UnaryExpression) {
            Integer temp = getValueInteger(((UnaryExpression)exp).operand, scope);
            UnaryExpression unary = (UnaryExpression)exp;
            if (unary.operator.equals("-")) {
                return -temp;
            }
        } else if (exp instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression)exp;
            Integer temp = (Integer)literal.value;
            return temp;
        } else if (exp instanceof IdentifierExpression) {
            IdentifierExpression id = (IdentifierExpression)exp;
            String name = id.name;
            for (Map.Entry<String, Item> entry : scope.valueMap.entrySet()) {
                if (entry.getValue() instanceof ConstItem && entry.getKey().equals(name)) {
                    ConstItem constitem = (ConstItem)(entry.getValue());
                    return getValueInteger(constitem.exp, scope);
                }
            }
            if (scope.parent != null) {
                return getValueInteger(exp, scope.parent);
            }
        } else if (exp instanceof GroupedExpression) {
            return getValueInteger(((GroupedExpression)exp).inner, scope);
        }
        return null;
    }
    public IR(Parser parser, Semantics semantics) {
        this.root = semantics.root;
        program = parser.program.nodes;
        this.instructions = new ArrayList<>();
        this.semantics = semantics;
        this.variableNumMap = new HashMap<>();
        this.variableTypeMap = new HashMap<>();
        this.tempIndexMap = new HashMap<>();
        for (Item item : program) {
            if (item instanceof ConstItem) {
                ConstItem constItem = (ConstItem) item;
                Integer num = getValueInteger(constItem.exp, root);
                variableNumMap.put('@' + constItem.name, 0);
                variableTypeMap.put('@' + constItem.name, typeToString(constItem.type, root));
                Constant con = new Constant('@' + constItem.name, typeToString(constItem.type, root), num.toString());
                instructions.add(con);
            } else if (item instanceof StructItem) {
                StructItem structItem = (StructItem) item;
                List<String> typeList = new ArrayList<>();
                for (Parameter field : structItem.fields) {
                    typeList.add(typeToString(field.type, root));
                }
                instructions.add(new StructType("%struct." + structItem.name, typeList));
            } else if (item instanceof ImplItem) {
                ImplItem impl = (ImplItem) item;
                String type = ((TypePath)(impl.type)).name;
                for (Item item_ : impl.AssociatedItems) {
                    if (item_ instanceof ConstItem) {
                        ConstItem constItem = (ConstItem) item_;
                        String newName = constItem.name + '_' + type;
                        Integer num = getValueInteger(constItem.exp, root);
                        variableNumMap.put('@' + newName, 0);
                        variableTypeMap.put('@' + newName, typeToString(constItem.type, root));
                        Constant con = new Constant('@' + newName, typeToString(constItem.type, root), num.toString());
                        instructions.add(con);
                    }
                }
            }
        }
    }
    public void build() {
        List<String> typeList = new ArrayList<>();
        typeList.add("i32");
        Header declareExit = new Header("@exit", "void", typeList, null);
        declareExit.isDeclare = true;
        this.instructions.add(declareExit);
        Header declarePrintln = new Header("@printlnInt", "void", typeList, null);
        declarePrintln.isDeclare = true;
        this.instructions.add(declarePrintln);
        Header declareGetInt = new Header("@getInt", "i32", new ArrayList<>(), null);
        declareGetInt.isDeclare = true;
        this.instructions.add(declareGetInt);
        Header declarePrint = new Header("@printInt", "void", typeList, null);
        declarePrint.isDeclare = true;
        this.instructions.add(declarePrint);
        for (Item item : program) {
            if (item instanceof ImplItem) {
                ImplItem impl = (ImplItem) item;
                for (Item item_ : impl.AssociatedItems) {
                    if (item_ instanceof FunctionItem) {
                        FunctionItem fun = (FunctionItem) item_;
                        IRGenerator irGenerator = new IRGenerator(fun, variableNumMap, variableTypeMap, impl, root, semantics, tempIndexMap, this);
                        irGenerator.build();
                        this.instructions.add(irGenerator);
                    }
                }
            } else if (item instanceof FunctionItem) {
                FunctionItem fun = (FunctionItem) item;
                IRGenerator irGenerator = new IRGenerator(fun, variableNumMap, variableTypeMap, null, root, semantics, tempIndexMap, this);
                irGenerator.build();
                this.instructions.add(irGenerator);
            }
        }
    }
    public void printAll(java.io.PrintStream out) {
        for (IRInstructions ins : instructions) {
            ins.Print(out);
        }
    }
}
