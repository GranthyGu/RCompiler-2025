package rcompiler2025.src;

import java.util.*;

abstract class IRInstructions {}

enum BinaryOp {
    add, sub, mul, sdiv, srem, shl, ashr, and, or, xor,
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
    void Print() {
        System.out.printf("%s = ", result);
        System.out.print(operator);
        System.out.printf(" %s ", type);
        System.out.printf("%s, %s\n", operand1, operand2);
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
    void Print() {
        if (has_condition) {
            System.out.printf("br i1 %s, label %s, label %s\n", condition, lable1, lable2);
        } else {
            System.out.printf("br label %s\n", lable1);
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
    void Print() {
        if (isVoid) {
            System.out.println("ret void");
        } else {
            System.out.printf("ret %s %s\n", type, value);
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
    void Print() {
        System.out.printf("%s = alloca %s\n", result, type);
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
    void Print() {
        System.out.printf("%s = load %s, ptr %s\n", result, type, pointer);
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
    void Print() {
        System.out.printf("store %s %s, ptr %s\n", value, type, pointer);
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
    void Print() {
        System.out.printf("%s = getelementptr %s, ptr %s", result, type, ptrval);
        for (int i = 0; i < size; i++) {
            System.out.printf(", %s %s", types.get(i), indices.get(i));
        }
        System.out.println();
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
    void Print() {
        System.out.printf("%s = icmp ", result);
        System.out.print(cond);
        System.out.printf(" %s %s, %s\n", type, operand1, operand2);
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
    void Print() {
        System.out.printf("%s = call %s @%s(", result, resultType, functionName);
        for (int i = 0; i < size - 1; i++) {
            System.out.printf("%s %s, ", types.get(i), values.get(i));
        }
        System.out.printf("%s %s)\n", types.get(size - 1), values.get(size - 1));
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
    void Print() {
        System.out.printf("%s = phi %s ", result, type);
        for (int i = 0; i < size; i++) {
            System.out.printf("[ %s, %s ]", values.get(i), lables.get(i));
            if (i != size - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
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
    public void Print() {
        System.out.printf("%s = select %s, %s %s, %s %s\n", result, condition, type1, value1, type2, value2);
    }
}
class StructType extends IRInstructions {
    public String type;
    public List<String> fields;
    public StructType(String type, List<String> fields) {
        this.type = type;
        this.fields = fields;
    }
    public void Print() {
        System.out.print(type + " = type { ");
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            System.out.print(field);
            if (i != fields.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(" }\n");
    }
}
class Lable extends IRInstructions {
    public String name;
    public Lable(String name) {
        this.name = name;
    }
    public void Print() {
        System.out.println(name + ":\n");
    }
}
class Header extends IRInstructions {
    public String name;
    public String returnType;
    public List<String> types;
    public List<String> names;
    public Header(String name, String returnType, List<String> types, List<String> names) {
        this.name = name;
        this.returnType = returnType;
        this.types = types;
        this.names = names;
    }
    public void Print() {
        System.out.print("define " + returnType + " " + name + "(");
        for (int i = 0; i < types.size(); i++) {
            System.out.print(types.get(i) + " " + names.get(i));
            if (i != types.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(") {");
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
    public void Print() {
        System.out.printf("%s = constant %s %s\n", name, type, value);
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
    public IRGenerator(FunctionItem fun, Map<String, Integer> variableNumMap, Map<String, String> variableTypeMap,
                    ImplItem currentImpl, Scope root, Integer index, Semantics semantics) {
        this.root = root;
        this.currentFunction = fun;
        this.variableNumMap = new HashMap<>(variableNumMap);
        this.variableTypeMap = new HashMap<>(variableTypeMap);
        this.instructions = new ArrayList<>();
        this.currentImpl = currentImpl;
        this.tempIndexMap = new HashMap<>();
        this.semantics = semantics;
    }
    private String typeToString(Type type, Scope scope) {
        if (type instanceof TypePath) {
            TypePath typePath = (TypePath)type;
            String name = typePath.name;
            if (name.equals("i32") || name.equals("u32")) {
                return "i32";
            } else if (name.equals("isize") || name.equals("usize")) {
                return "i64";
            } else if (name.equals("bool")) {
                return "i1";
            } else if (name.equals("char")) {
                return "i8";
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
        Integer scopeNum = sta.scope.scopeIndex;
        LetStatement let = (LetStatement)sta;
        Pattern pattern = let.pattern;
        Type type = let.type;
        if (pattern instanceof ReferencePattern) {
            pattern = ((ReferencePattern)(pattern)).subPattern;
        }
        String name = '%' + ((IdentifierPattern)pattern).name + '_' + scopeNum;
        String typeStr = typeToString(type, scope);
        instructions.add(new Alloca(name, typeStr));
        variableTypeMap.put(name, typeStr);
        variableNumMap.put(name, 0);
        List<IRInstructions> exprInstructions = getExpressions(let.initializer, let.sta.scope, scope, null, null);
        instructions.addAll(exprInstructions);
        String variableName = targetVariableName(exprInstructions, let.initializer, let.sta.scope);
        instructions.add(new Store(variableName, typeStr, name));
        return instructions;
    }
    private String targetVariableName(List<IRInstructions> instructions, Expression exp, Scope scope) {
        if (exp instanceof BinaryExpression) {
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
                    while (true) {
                        String variableName = '%' + name + "_" + index;
                        if (variableNumMap.containsKey(variableName)) {
                            targetName = variableName;
                            break;
                        } else {
                            scope = scope.parent;
                            if (scope == null) {
                                System.out.println("Didn't find this variable!");
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
                Integer value = (Integer)(literal.value);
                return value.toString();
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
                    System.out.println("Unmatch CallFunc or CallMethod ERROR!");
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
        System.out.println("Unmatch Expression!");
        return null;
    }
    private String targetVariableNameLeft(List<IRInstructions> instructions, Expression exp, Scope scope) {
        if (exp instanceof IdentifierExpression) {
            String name = targetVariableName(instructions, exp, scope);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (c == '.') break;
                sb.append(c);
            }
            return sb.toString();
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
                return targetVariableName(getExpressions(unaryExpression.operand, scope, scope, null, null), unaryExpression.operand, scope);
            } else {
                System.err.println("Unmatch UnaryExpression ERROR!");
                return null;
            }
        } else {
            System.out.println(exp);
            System.err.println("Unmatch Expression ERROR!");
            return null;
        }
    }
    private String getType(String variableName) {
        if (variableName.startsWith("%ttemp.")) {
            String numPart = variableName.substring(7);
            Integer x = Integer.parseInt(numPart);
            return tempIndexMap.get(x);
        } else {
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
            return getBlockExpression(exp, scope, startLable, endLable);
        } else if (exp instanceof LoopExpression) {
            return getLoopExpression(exp, subScope, scope);
        } else if (exp instanceof WhileExpression) {
            return getWhileExpression(exp, subScope, scope);
        } else if (exp instanceof IfExpression) {
            return getIfExpression(true, exp, subScope, scope, startLable, endLable, null);
        } else if (exp instanceof StructExpression) {
            return getStructExpression(exp, scope);
        } else if (exp instanceof ArrayExpression) {
            return getArrayExpression(exp, scope);
        } else if (exp instanceof GroupedExpression) {
            return getGroupedExpression(exp, subScope, scope);
        } else if (exp instanceof ReturnExpression) {
            return getReturnExpression(exp, subScope, scope);
        }
        System.out.println("Unmatch Expression!");
        return null;
    }
    private List<IRInstructions> getBinaryExpression(Expression exp, Scope scope) {
        BinaryExpression binary = (BinaryExpression)exp;
        List<IRInstructions> instructions = new ArrayList<>();
        if (binary.operator.equals("=")) {
            Expression left = binary.left;
            Expression right = binary.right;
            List<IRInstructions> list1 = getExpressions(left, scope, scope, null, null);
            List<IRInstructions> list2 = getExpressions(right, scope, scope, null, null);
            String left_ = targetVariableNameLeft(list1, left, scope);
            String right_ = targetVariableName(list2, right, scope);
            instructions.addAll(list1);
            instructions.addAll(list2);
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
            instructions.addAll(list1);
            instructions.addAll(list2);
            String newTemp = "%ttemp." + tempIndexMap.size();
            String type = typeToString(semantics.getType(scope, scope, right), scope);
            tempIndexMap.put(tempIndexMap.size(), type);
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
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.sdiv, left_, right_, type));
                    break;
                case "%":
                    instructions.add(new BinaryInstruction(newTemp, BinaryOp.srem, left_, right_, type));
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
            instructions.addAll(list1);
            instructions.addAll(list2);
            String newTemp = "%ttemp." + tempIndexMap.size();
            Type type_ = semantics.getType(scope, scope, right);
            boolean flag = false;
            if (type_ instanceof TypePath) {
                TypePath typePath = (TypePath) type_;
                if (typePath.name.equals("u32") || typePath.name.equals("usize")) {
                    flag = true;
                }
            }
            String type = typeToString(semantics.getType(scope, scope, right), scope);
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
            return getBinaryExpression(new BinaryExpression(binary.left, "=", new BinaryExpression(binary.left, operator, binary.right, null), null), scope);
        } else if (binary.operator.equals("as")) {
            // TODO: as operator.
            return null;
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
            instructions.addAll(instructions_);
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
                instructions.addAll(ins);
                return instructions;
            } else {
                System.out.println("Undefined &x Unary Expression!");
                return null;
            }
        } else if (unary.operator.equals("-")) {
            List<IRInstructions> instructions_ = getExpressions(unary.operand, null, scope, null, null);
            instructions.addAll(instructions_);
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
            instructions.addAll(instructions_);
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
        if (variableNumMap.containsKey('@' + name)) {
            String targetName_ = '@' + name + '.' + variableNumMap.get('@' + name);
            variableNumMap.put('@' + name, variableNumMap.get('@' + name) + 1);
            typeName = variableTypeMap.get('@' + name);
            return new Load(targetName_, typeName, '@' + name);
        } else if (name.equals("self")) {
            Header header = (Header)(instructions.get(0));
            String name_ = header.names.get(0);
            if (name_.equals('%' + currentFunction.name + "_arr_" + currentFunction.scope.scopeIndex)) {
                name_ = header.names.get(1);
            }
            String resultName = name_ + "." + variableNumMap.get(name_);
            variableNumMap.put(name_, variableNumMap.get(name_) + 1);
            typeName = variableTypeMap.get(name_);
            return new Load(resultName, typeName, name_);
        }
        while (true) {
            String variableName = '%' + name + "_" + index;
            if (variableNumMap.containsKey(variableName)) {
                targetName = variableName;
                typeName = variableTypeMap.get(targetName);
                break;
            } else {
                scope = scope.parent;
                if (scope == null) {
                    System.err.println("Undefined variable: " + name);
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
    private Expression generateIndex(Expression exp, List<String> types, List<String> nums, Scope scope) {
        if (exp instanceof FieldExpression) {
            FieldExpression field = (FieldExpression)exp;
            Expression exp_ = generateIndex(field.object, types, nums, scope);
            types.add("i32");
            nums.add(getIndexofFieldExpression(exp, field.member, scope).toString());
            return exp_;
        } else if (exp instanceof ArrIndexExpression) {
            ArrIndexExpression arrIndex = (ArrIndexExpression)exp;
            Expression exp_ = generateIndex(arrIndex.object, types, nums, scope);
            types.add("i64");
            nums.add(getValueInteger(arrIndex.index, scope).toString());
            return exp_;
        } else {
            return exp;
        }
    }
    private List<IRInstructions> getArrIndexOrFieldExpression(Expression exp, Scope scope) {
        List<IRInstructions> instructions = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> nums = new ArrayList<>();
        Expression exp_ = generateIndex(exp, types, nums, scope);
        List<IRInstructions> instructions_ = getExpressions(exp_, null, scope, null, null);
        instructions.addAll(instructions_);
        String type = typeToString(semantics.getType(scope, scope, exp_), scope);
        String newTemp = "%ttemp." + tempIndexMap.size();
        tempIndexMap.put(tempIndexMap.size(), "ptr");
        String targetVariable = targetVariableNameLeft(instructions_, exp_, scope);
        instructions.add(new Getelementptr(newTemp, type, targetVariable, types, nums, types.size()));
        String newTemp_ = "%ttemp." + tempIndexMap.size();
        String newType = typeToString(semantics.getType(scope, scope, exp), scope);
        tempIndexMap.put(tempIndexMap.size(), newType);
        instructions.add(new Load(newTemp_, type, newTemp));
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
            if (func.return_type instanceof ArrayType) {
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
                    instructions.addAll(ins);
                    String targetName = targetVariableName(ins, call.arguments.get(i), scope);
                    String newTemp_ = "%ttemp." + tempIndexMap.size();
                    tempIndexMap.put(tempIndexMap.size(), typeToString(type, scope));
                    variableNumMap.put(newTemp_, 0);
                    variableTypeMap.put(newTemp_, typeToString(type, scope));
                    instructions.add(new Alloca(newTemp_, typeToString(type, scope)));
                    instructions.add(new Store(targetName, newTemp_, typeToString(type, scope)));
                    values.add(newTemp_);
                    continue;
                }
                types.add(typeToString(type, scope));
                List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
                instructions.addAll(ins);
                String targetName = targetVariableName(ins, call.arguments.get(i), scope);
                values.add(targetName);
            }
            Type returnType = func.return_type;
            if (returnType != null && !(returnType instanceof ArrayType)) {
                String typeString = typeToString(returnType, scope);
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeString);
                instructions.add(new Call(newTemp_, typeString, '@' + structName + '.' + func.name, types, values, types.size()));
                return instructions;
            } else {
                String typeString = "void";
                instructions.add(new Call(null, typeString, '@' + structName + '.' + func.name, types, values, types.size()));
                if (returnType instanceof ArrayType) {
                    String newTemp_ = "%ttemp." + tempIndexMap.size();
                    tempIndexMap.put(tempIndexMap.size(), typeToString(returnType, scope));
                    instructions.add(new Load(newTemp_, typeToString(returnType, scope), newTemp));
                }
                return instructions;
            }
        }
        name = ((IdentifierExpression)call.call_).name;
        Scope root = semantics.root;
        FunctionItem func = null;
        List<String> types = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<IRInstructions> instructions = new ArrayList<>();
        for (Map.Entry<String, Item> entry : root.valueMap.entrySet()) {
            if (entry.getValue() instanceof FunctionItem && entry.getKey().equals(name)) {
                func = (FunctionItem)(entry.getValue());
            }
        }
        String newTemp = "%ttemp." + tempIndexMap.size();
        if (func.return_type instanceof ArrayType) {
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
                instructions.addAll(ins);
                String targetName = targetVariableName(ins, call.arguments.get(i), scope);
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeToString(type, scope));
                variableNumMap.put(newTemp_, 0);
                variableTypeMap.put(newTemp_, typeToString(type, scope));
                instructions.add(new Alloca(newTemp_, typeToString(type, scope)));
                instructions.add(new Store(targetName, newTemp_, typeToString(type, scope)));
                values.add(newTemp_);
                continue;
            }
            types.add(typeToString(type, scope));
            List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
            instructions.addAll(ins);
            String targetName = targetVariableName(ins, call.arguments.get(i), scope);
            values.add(targetName);
        }
        Type returnType = func.return_type;
        if (returnType == null && !(returnType instanceof ArrayType)) {
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
        Type type_ = semantics.getType(scope, scope, call.call_);
        String typeStr = typeToString(type_, scope);
        String methodName = '@' + typeStr + '.' + call.method_name;
        FunctionItem func = null;
        boolean find = false;
        for (Scope child : root.children) {
            if (child.typeStruct != null && ((TypePath)(child.typeStruct)).name.equals(typeStr)) {
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
        if (func.return_type instanceof ArrayType) {
            tempIndexMap.put(tempIndexMap.size(), typeToString(func.return_type, scope));
            instructions.add(new Alloca(newTemp, typeToString(func.return_type, scope)));
            variableNumMap.put(newTemp, 0);
            variableTypeMap.put(newTemp, typeToString(func.return_type, scope));
            types.add("ptr");
            values.add(newTemp);
        }
        if (func.parameters.get(0).isSelf) {
            if (func.parameters.get(0).isReference) {
                List<IRInstructions> ins = getExpressions(new UnaryExpression("&", call.call_, true), null, scope, null, null);
                instructions.addAll(ins);
                String targetName = targetVariableName(ins, new UnaryExpression("&", call.call_, true), scope);
                types.add("ptr");
                values.add(targetName);
            } else {
                List<IRInstructions> ins = getExpressions(call.call_, null, scope, null, null);
                instructions.addAll(ins);
                String targetName = targetVariableName(ins, call.call_, scope);
                types.add(typeStr);
                values.add(targetName);
            }
        } else {
            List<IRInstructions> ins = getExpressions(call.call_, null, scope, null, null);
            instructions.addAll(ins);
        }
        for (int i = 0; i < func.parameters.size(); i++) {
            Type type = func.parameters.get(i).type;
            if (type instanceof ArrayType) {
                types.add("ptr");
                List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
                instructions.addAll(ins);
                String targetName = targetVariableName(ins, call.arguments.get(i), scope);
                String newTemp_ = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeToString(type, scope));
                variableNumMap.put(newTemp_, 0);
                variableTypeMap.put(newTemp_, typeToString(type, scope));
                instructions.add(new Alloca(newTemp_, typeToString(type, scope)));
                instructions.add(new Store(targetName, newTemp_, typeToString(type, scope)));
                values.add(newTemp_);
                continue;
            }
            types.add(typeToString(type, scope));
            List<IRInstructions> ins = getExpressions(call.arguments.get(i), null, scope, null, null);
            instructions.addAll(ins);
            String targetName = targetVariableName(ins, call.arguments.get(i), scope);
            values.add(targetName);
        }
        Type returnType = func.return_type;
        if (returnType != null) {
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
        Type type_ = semantics.getType(subScope, scope, ret.value);
        if (ret.value == null || type_ instanceof ArrayType) {
            if (type_ instanceof ArrayType) {
                List<IRInstructions> ins = getExpressions(ret.value, subScope, scope, null, null);
                instructions.addAll(ins);
                String targetName = targetVariableName(ins, ret.value, scope);
                String name = '%' + currentFunction.name + "_arr_" + currentFunction.scope.scopeIndex;
                instructions.add(new Store(targetName, typeToString(currentFunction.return_type, scope), name));
            }
            instructions.add(new Ret("void", null, true));
            return instructions;
        } else {
            List<IRInstructions> ins = getExpressions(ret.value, subScope, scope, null, null);
            instructions.addAll(ins);
            String targetName = targetVariableName(ins, ret.value, scope);
            Type type = semantics.getType(scope, scope, ret.value);
            String typeStr = typeToString(type, scope);
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
            instructions.addAll(ins);
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
                instructions.addAll(ins);
                String targetName = targetVariableName(ins, valueExpression, scope);
                String gepTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "ptr");
                instructions.add(new Getelementptr(gepTemp, typeStr, newTemp, 
                                                  Arrays.asList("i32", "i64"), 
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
            instructions.addAll(ins);
            String targetName = targetVariableName(ins, valueExpression, scope);
            Integer value = getValueInteger(array.elements.get(1), scope);
            for (int i = 0; i < value; i++) {
                String gepTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), "ptr");
                instructions.add(new Getelementptr(gepTemp, typeStr, newTemp, 
                                                  Arrays.asList("i32", "i64"), 
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
                    instructions.addAll(ins);
                } else if (exp_ instanceof ContinueExpression) {
                    List<IRInstructions> ins = getContinueExpression(exp_, scope, startLable);
                    instructions.addAll(ins);
                } else {
                    List<IRInstructions> ins = getExpressions(exp_, sta.scope, scope, startLable, endLable);
                    instructions.addAll(ins);
                }
            } else if (sta instanceof LetStatement) {
                List<IRInstructions> ins = getLetStatements(sta, scope);
                instructions.addAll(ins);
            } else if (sta instanceof ConstItem) {
                ConstItem constItem = (ConstItem)sta;
                Integer num = getValueInteger(constItem.exp, root);
                variableNumMap.put('@' + constItem.name, 0);
                variableTypeMap.put('@' + constItem.name, typeToString(constItem.type, root));
                Constant con = new Constant('@' + constItem.name, typeToString(constItem.type, root), num.toString());
                instructions.add(con);
            } else if (sta instanceof StructItem) {
                StructItem structItem = (StructItem)sta;
                List<String> typeList = new ArrayList<>();
                for (Parameter field : structItem.fields) {
                    typeList.add(typeToString(field.type, scope));
                }
                instructions.add(new StructType("%struct." + structItem.name, typeList));
            }
        }
        // Handle the tail expression!
        Expression tailExp = block.exp;
        if (tailExp == null) {
            return instructions;
        } else {
            List<IRInstructions> ins = getExpressions(tailExp, scope, scope, null, null);
            String targetName = targetVariableName(ins, tailExp, scope);
            Type type = semantics.getType(scope, scope, tailExp);
            String typeStr = typeToString(type, scope);
            if (scope.type == Kind.FUNCTION) {
                instructions.addAll(ins);
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
            instructions.addAll(ins);
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
        instructions.addAll(bodyIns);
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
        List<IRInstructions> condIns = getExpressions(whileExp.condition, null, scope, null, null);
        instructions.addAll(condIns);
        String condVar = targetVariableName(condIns, whileExp.condition, scope);
        instructions.add(new Br(true, condVar, bodyLable, endLable));
        instructions.add(new Lable(bodyLable));
        List<IRInstructions> bodyIns = getBlockExpression(whileExp.body, subScope, condLable, endLable);
        instructions.addAll(bodyIns);
        instructions.add(new Br(false, null, condLable, null));
        instructions.add(new Lable(endLable));
        return instructions;
    }
    private List<IRInstructions> getIfExpression(boolean flag, Expression exp, Scope subScope, Scope scope, String startLable, String endLable, String endIfLable) {
        IfExpression ifExp = (IfExpression)exp;
        Integer num = subScope.scopeIndex;
        String thenLable = "then_" + num;
        List<IRInstructions> instructions = new ArrayList<>();
        if (endIfLable == null) {
            if (flag) {
                Type type = semantics.getType(scope, scope, ifExp);
                String typeString = typeToString(type, scope);
                String newTemp = "%ttemp." + tempIndexMap.size();
                tempIndexMap.put(tempIndexMap.size(), typeString);
                variableTypeMap.put(newTemp, typeString);
                variableNumMap.put(newTemp, 0);
                instructions.add(new Alloca(newTemp, typeString));
            }
            endIfLable = "end_if_" + num;
        }
        String elseLable = "else_" + num;
        List<IRInstructions> condIns = getExpressions(ifExp.condition, null, scope, null, null);
        instructions.addAll(condIns);
        String condVar = targetVariableName(condIns, ifExp.condition, scope);
        if (ifExp.else_branch == null) {
            instructions.add(new Br(true, condVar, thenLable, endIfLable));
            instructions.add(new Lable(thenLable));
            List<IRInstructions> thenIns = getBlockExpression(ifExp.then_branch, subScope, startLable, endLable);
            instructions.addAll(thenIns);
            instructions.add(new Br(false, null, endIfLable, null));
            instructions.add(new Lable(endIfLable));
            return instructions;
        } else {
            instructions.add(new Br(true, condVar, thenLable, elseLable));
            instructions.add(new Lable(thenLable));
            List<IRInstructions> thenIns = getBlockExpression(ifExp.then_branch, subScope, startLable, endLable);
            instructions.addAll(thenIns);
            if (flag) {
                String variable = targetVariableName(thenIns, ifExp.then_branch, subScope);
                String name = "%ttemp." + (tempIndexMap.size() - 1);
                instructions.add(new Store(variable, variableTypeMap.get(name), name));
            }
            instructions.add(new Br(false, null, endIfLable, null));
            instructions.add(new Lable(elseLable));
            if (ifExp.else_branch instanceof IfExpression) {
                List<IRInstructions> elseIns = getIfExpression(flag, ifExp.else_branch, ifExp.sta.scope, scope, startLable, endLable, endIfLable);
                instructions.addAll(elseIns);
            } else if (ifExp.else_branch instanceof BlockExpression) {
                List<IRInstructions> elseIns = getBlockExpression(ifExp.else_branch, ifExp.sta.scope, startLable, endLable);
                instructions.addAll(elseIns);
                if (flag) {
                    String variable = targetVariableName(elseIns, ifExp.else_branch, ifExp.sta.scope);
                    String name = "%ttemp." + (tempIndexMap.size() - 1);
                    instructions.add(new Store(variable, variableTypeMap.get(name), name));
                }
                instructions.add(new Br(false, null, endIfLable, null));
                instructions.add(new Lable(endIfLable));
                if (flag) {
                    String name = "%ttemp." + (tempIndexMap.size() - 1);
                    String newTemp = "%ttemp." + tempIndexMap.size();
                    tempIndexMap.put(tempIndexMap.size(), variableTypeMap.get(name));
                    instructions.add(new Load(newTemp, variableTypeMap.get(name), name));
                }
            }
            return instructions;
        }
    }
    public void build() {
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
                    String name = '%' + param.name + '_' + num;
                    variableNumMap.put(name, 0);
                    variableTypeMap.put(name, typeToString(param.type, root));
                    names.add(name);
                } else if (param.type instanceof ReferenceType) {
                    Integer num = currentFunction.scope.scopeIndex;
                    String name = "%temp." + param.name + '_' + num;
                    types.add("ptr");
                    names.add(name);
                    declares.add(new Alloca("%" + param.name + '_' + num, "ptr"));
                    declares.add(new Store(name, typeToString(param.type, root), "%" + param.name + '_' + num));
                    variableNumMap.put("%" + param.name + '_' + num, 0);
                    variableTypeMap.put("%" + param.name + '_' + num, "ptr");
                } else {
                    Integer num = currentFunction.scope.scopeIndex;
                    String name = "%temp." + param.name + '_' + num;
                    types.add(typeToString(param.type, root));
                    names.add(name);
                    declares.add(new Alloca("%" + param.name + '_' + num, typeToString(param.type, root)));
                    declares.add(new Store(name, typeToString(param.type, root), "%" + param.name + '_' + num));
                    variableNumMap.put("%" + param.name + '_' + num, 0);
                    variableTypeMap.put("%" + param.name + '_' + num, typeToString(param.type, root));
                }
            }
            String returnType = "void";
            if (currentFunction.return_type != null && !(currentFunction.return_type instanceof ArrayType)) {
                returnType = typeToString(currentFunction.return_type, root);
            }
            Header header = new Header('@' + currentFunction.name, returnType, types, names);
            instructions.add(header);
            instructions.addAll(declares);
            instructions.addAll(getBlockExpression(currentFunction.body, currentFunction.scope, null, null));
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
                header = new Header('@' + implType + '.' + currentFunction.name,
                                    returnType, types, names);
            } else {
                Parameter firstParam = implParams.get(0);
                if (firstParam.isSelf) {
                    if (firstParam.isReference) {
                        types.add("ptr");
                        Integer num = currentFunction.scope.scopeIndex;
                        String name = '%' + implType + '_' + num;
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
                                String name_ = '%' + param.name + '_' + num_;
                                variableNumMap.put(name_, 0);
                                variableTypeMap.put(name_, typeToString(param.type, root));
                                names.add(name_);
                            } else if (param.type instanceof ReferenceType) {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                String name_ = "%temp." + param.name + '_' + num_;
                                types.add("ptr");
                                names.add(name_);
                                declares.add(new Alloca("%" + param.name + '_' + num_, "ptr"));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + param.name + '_' + num_));
                                variableNumMap.put("%" + param.name + '_' + num_, 0);
                                variableTypeMap.put("%" + param.name + '_' + num_, "ptr");
                            } else {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                String name_ = "%temp." + param.name + '_' + num_;
                                types.add(typeToString(param.type, root));
                                names.add(name_);
                                declares.add(new Alloca("%" + param.name + '_' + num_, typeToString(param.type, root)));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + param.name + '_' + num_));
                                variableNumMap.put("%" + param.name + '_' + num_, 0);
                                variableTypeMap.put("%" + param.name + '_' + num_, typeToString(param.type, root));
                            }
                        }
                        header = new Header('@' + implType + '.' + currentFunction.name, 
                                                returnType, types, names);
                    } else {
                        types.add(implType);
                        Integer num = currentFunction.scope.scopeIndex;
                        String name = "%temp." + implType + '_' + num;
                        names.add(name);
                        declares.add(new Alloca("%" + implType + '_' + num, implType));
                        declares.add(new Store(name, implType, "%" + implType + '_' + num));
                        variableNumMap.put("%" + implType + '_' + num, 0);
                        variableTypeMap.put("%" + implType + '_' + num, implType);
                        boolean flag = true;
                        for (Parameter param : currentFunction.parameters) {
                            if (flag) {
                                flag = false;
                                continue;
                            }
                            if (param.type instanceof ArrayType) {
                                types.add("ptr");
                                Integer num_ = currentFunction.scope.scopeIndex;
                                String name_ = '%' + param.name + '_' + num_;
                                variableNumMap.put(name_, 0);
                                variableTypeMap.put(name_, typeToString(param.type, root));
                                names.add(name_);
                            } else if (param.type instanceof ReferenceType) {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                String name_ = "%temp." + param.name + '_' + num_;
                                types.add("ptr");
                                names.add(name_);
                                declares.add(new Alloca("%" + param.name + '_' + num_, "ptr"));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + param.name + '_' + num_));
                                variableNumMap.put("%" + param.name + '_' + num_, 0);
                                variableTypeMap.put("%" + param.name + '_' + num_, "ptr");
                            } else {
                                Integer num_ = currentFunction.scope.scopeIndex;
                                String name_ = "%temp." + param.name + '_' + num_;
                                types.add(typeToString(param.type, root));
                                names.add(name_);
                                declares.add(new Alloca("%" + param.name + '_' + num_, typeToString(param.type, root)));
                                declares.add(new Store(name_, typeToString(param.type, root), "%" + param.name + '_' + num_));
                                variableNumMap.put("%" + param.name + '_' + num_, 0);
                                variableTypeMap.put("%" + param.name + '_' + num_, typeToString(param.type, root));
                            }
                        }
                        header = new Header('@' + implType + '.' + currentFunction.name, 
                                                returnType, types, names);
                    }
                } else {
                    for (Parameter param : currentFunction.parameters) {
                        if (param.type instanceof ArrayType) {
                            types.add("ptr");
                            Integer num_ = currentFunction.scope.scopeIndex;
                            String name_ = '%' + param.name + '_' + num_;
                            variableNumMap.put(name_, 0);
                            variableTypeMap.put(name_, typeToString(param.type, root));
                            names.add(name_);
                        } else if (param.type instanceof ReferenceType) {
                            Integer num_ = currentFunction.scope.scopeIndex;
                            String name_ = "%temp." + param.name + '_' + num_;
                            types.add("ptr");
                            names.add(name_);
                            declares.add(new Alloca("%" + param.name + '_' + num_, "ptr"));
                            declares.add(new Store(name_, typeToString(param.type, root), "%" + param.name + '_' + num_));
                            variableNumMap.put("%" + param.name + '_' + num_, 0);
                            variableTypeMap.put("%" + param.name + '_' + num_, "ptr");
                        } else {
                            Integer num_ = currentFunction.scope.scopeIndex;
                            String name_ = "%temp." + param.name + '_' + num_;
                            types.add(typeToString(param.type, root));
                            names.add(name_);
                            declares.add(new Alloca("%" + param.name + '_' + num_, typeToString(param.type, root)));
                            declares.add(new Store(name_, typeToString(param.type, root), "%" + param.name + '_' + num_));
                            variableNumMap.put("%" + param.name + '_' + num_, 0);
                            variableTypeMap.put("%" + param.name + '_' + num_, typeToString(param.type, root));
                        }
                    }
                    header = new Header('@' + implType + '.' + currentFunction.name, 
                                            returnType, types, names);
                }
            }
            instructions.add(header);
            instructions.addAll(declares);
            instructions.addAll(getBlockExpression(currentFunction.body, currentFunction.scope, null, null));
        }
    }
}

public class LLVMProgram {
    private List<IRInstructions> instructions;
    private List<Item> program;
    private Scope root;
    private Integer tempIndex = 0;
    private Semantics semantics;
    private Map<String, Integer> variableNumMap;
    private Map<String, String> variableTypeMap;
    private String typeToString(Type type, Scope scope) {
        if (type instanceof TypePath) {
            TypePath typePath = (TypePath)type;
            String name = typePath.name;
            if (name.equals("i32") || name.equals("u32")) {
                return "i32";
            } else if (name.equals("isize") || name.equals("usize")) {
                return "i64";
            } else if (name.equals("bool")) {
                return "i1";
            } else if (name.equals("char")) {
                return "i8";
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
    public LLVMProgram(Parser parser, Scope root, Semantics semantics) {
        this.root = root;
        program = parser.program.nodes;
        this.instructions = new ArrayList<>();
        this.semantics = semantics;
        this.variableNumMap = new HashMap<>();
        this.variableTypeMap = new HashMap<>();
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
        for (Item item : program) {
            if (item instanceof ImplItem) {
                ImplItem impl = (ImplItem) item;
                for (Item item_ : impl.AssociatedItems) {
                    if (item_ instanceof FunctionItem) {
                        FunctionItem fun = (FunctionItem) item_;
                        IRGenerator irGenerator = new IRGenerator(fun, variableNumMap, variableTypeMap, impl, root, tempIndex, semantics);
                        irGenerator.build();
                        this.instructions.add(irGenerator);
                    }
                }
            } else if (item instanceof FunctionItem) {
                FunctionItem fun = (FunctionItem) item;
                IRGenerator irGenerator = new IRGenerator(fun, variableNumMap, variableTypeMap, null, root, tempIndex, semantics);
                irGenerator.build();
                this.instructions.add(irGenerator);
            }
        }
    }
}
