package rcompiler2025;

import java.util.*;

abstract class Expression {}
abstract class Statement {}
abstract class Item extends Statement{}
abstract class Type {}
abstract class Pattern {}

class BinaryExpression extends Expression {
    public Expression left;
    public String operator;
    public Expression right;
    public Type type;
    public BinaryExpression(Expression left, String operator, Expression right, Type type) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.type = type;
    }
}
class UnaryExpression extends Expression {
    public String operator;
    public Expression operand;
    public boolean isMut;
    public UnaryExpression(String operator, Expression operand, boolean ismut) {
        this.operator = operator;
        this.operand = operand;
        this.isMut = ismut;
    }
}
class LiteralExpression extends Expression {
    public Object value;
    public TokenType literal_type;
    public LiteralExpression(Object value, TokenType type) {
        this.value = value;
        this.literal_type = type;
    }
}
class IdentifierExpression extends Expression {
    public String name;
    public IdentifierExpression(String name) {
        this.name = name;
    }
}
class CallFuncExpression extends Expression {
    public Expression call_;
    public List<Expression> arguments;
    public CallFuncExpression(Expression call_, List<Expression> arguments) {
        this.call_ = call_;
        this.arguments = arguments;
    }
}
class CallMethodExpression extends Expression {
    public Expression call_;
    public String method_name;
    public List<Expression> arguments;
    public CallMethodExpression(Expression call_, String method, List<Expression> arguments) {
        this.call_ = call_;
        this.arguments = arguments;
        this.method_name = method;
    }
}
class ArrIndexExpression extends Expression {
    public Expression object;
    public Expression index;
    public ArrIndexExpression(Expression object, Expression index) {
        this.object = object;
        this.index = index;
    }
}
class FieldExpression extends Expression {
    public Expression object;
    public String member;
    public FieldExpression(Expression object, String member) {
        this.object = object;
        this.member = member;
    }
}
class BlockExpression extends Expression {
    public boolean isConst;
    public List<Statement> statements;
    public Expression exp;
    public BlockExpression(List<Statement> statements, Expression exp, boolean isConst) {
        this.statements = statements;
        this.exp = exp;
        this.isConst = isConst;
    }
}
class BreakExpression extends Expression {
    public Expression break_expression;
    public BreakExpression(Expression exp) {
        this.break_expression = exp;
    }
}
class ContinueExpression extends Expression {}
class UnderscoreExpression extends Expression {}
class ReturnExpression extends Expression {
    public Expression value;
    public ReturnExpression(Expression value) {
        this.value = value;
    }
}
class LoopExpression extends Expression {
    public BlockExpression value;
    public LoopExpression(BlockExpression value) {
        this.value = value;
    }
}
class WhileExpression extends Expression {
    public Expression condition;
    public BlockExpression body;
    public WhileExpression(Expression value, BlockExpression body_) {
        this.condition = value;
        this.body = body_;
    }
}
class Parameter {
    public boolean isMut;
    public boolean isReference;
    public boolean isSelf;
    public boolean ispublic;
    public String name;
    public Pattern pattern;
    public Type type;
    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
class StructExprField {
    public String name;
    public Expression exp;
    public StructExprField(String name, Expression exp) {
        this.name = name;
        this.exp = exp;
    }
}
class StructExpression extends Expression {
    public String name;
    public String subName;
    public List<StructExprField> fields;
    public StructExpression(String name, String subname, List<StructExprField> fields) {
        this.name = name;
        this.fields = fields;
        this.subName = subname;
    }
}
class IfExpression extends Expression {
    public Expression condition;
    public BlockExpression then_branch;
    public Expression else_branch;
    public IfExpression(Expression condition, BlockExpression then_branch, Expression else_branch) {
        this.condition = condition;
        this.then_branch = then_branch;
        this.else_branch = else_branch;
    }
}
class PathExpression extends Expression {
    public String Path;
    public String subPath;
    public PathExpression(String str1, String str2) {
        this.Path = str1;
        this.subPath = str2;
    }
}
class ArrayExpression extends Expression {
    public List<Expression> elements;
    public ArrayExpression(List<Expression> elements) {
        this.elements = elements;
    }
}
class GroupedExpression extends Expression {
    public Expression inner;
    public GroupedExpression(Expression inner) {
        this.inner = inner;
    }
}

class ExpressionStatement extends Statement {
    public Expression expression;
    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
}
class LetStatement extends Statement {
    public Pattern pattern;
    public Type type;
    public Expression initializer;
    public LetStatement(Pattern pt, Type type, Expression init) {
        this.pattern = pt;
        this.type= type;
        this.initializer = init;
    }
}

class FunctionItem extends Item {
    public String name;
    public List<Parameter> parameters;
    public Type return_type;
    public BlockExpression body;
    public boolean is_const;
    public FunctionItem(String name, List<Parameter> params, Type return_type, BlockExpression body, boolean is_const) {
        this.name = name;
        this.parameters = params;
        this.return_type = return_type;
        this.body = body;
        this.is_const = is_const;
    }
}
class StructItem extends Item {
    public String name;
    public List<Parameter> fields;
    public StructItem(String name, List<Parameter> fields) {
        this.name = name;
        this.fields = fields;
    }
}
class EnumItem extends Item {
    public String name;
    public List<String> variants;
    public EnumItem(String name, List<String> variants) {
        this.name = name;
        this.variants = variants;
    }
}
class ConstItem extends Item {
    public String name;
    public Type type;
    public Expression exp;
    public ConstItem(String name, Type type, Expression exp) {
        this.name = name;
        this.type = type;
        this.exp = exp;
    }
}
class TraitItem extends Item {
    public String name;
    public List<Item> AssociatedItems;
    public TraitItem(String name, List<Item> list) {
        this.name = name;
        this.AssociatedItems = list;
    }
}
class ImplItem extends Item {
    public String name;
    public Type type;
    public List<Item> AssociatedItems;
    public ImplItem(String name, Type type, List<Item> list) {
        this.name = name;
        this.type = type;
        this.AssociatedItems = list;
    }
}

class TypePath extends Type {
    public String name;
    public TypePath(String name) {
        this.name = name;
    }
}
class ReferenceType extends Type {
    public boolean isMut;
    public Type type;
    public ReferenceType(boolean ismut, Type tp) {
        this.isMut = ismut;
        this.type = tp;
    }
}
class ArrayType extends Type {
    public Type type;
    public Expression exp;
    public ArrayType(Type tp, Expression exp) {
        this.type = tp;
        this.exp = exp;
    }
}
class UnitType extends Type {}

class LiteralPattern extends Pattern {
    public boolean isNegative;
    public LiteralExpression exp;
    public LiteralPattern(boolean isneg, LiteralExpression exp) {
        this.isNegative = isneg;
        this.exp = exp;
    }
}
class IdentifierPattern extends Pattern {
    public boolean isRef;
    public boolean isMut;
    public String name;
    public Pattern subPattern;
    public IdentifierPattern(boolean isref, boolean ismut, String name, Pattern pat) {
        this.isMut = ismut;
        this.isRef = isref;
        this.name = name;
        this.subPattern = pat;
    }
}
class WildcardPattern extends Pattern {}
class ReferencePattern extends Pattern {
    public boolean isMut;
    public Pattern subPattern;
    public ReferencePattern(boolean isMut, Pattern sub) {
        this.isMut = isMut;
        this.subPattern = sub;
    }
}
class TupleStructPattern extends Pattern {
    public String path;
    public String subPath;
    public List<Pattern> patterns;
    public TupleStructPattern(String str1, String str2, List<Pattern> pat) {
        this.path = str1;
        this.subPath = str2;
        this.patterns = pat;
    }
}
class PathPattern extends Pattern {
    public String path;
    public String subPath;
    public PathPattern(String str1, String str2) {
        this.path = str1;
        this.subPath = str2;
    }
}

class Crate {
    public List<Item> nodes;
    public Crate(List<Item> nodes) {
        this.nodes = nodes;
    }
}

public class Parser {
    public List<Token> tokens;
    public int current_index;
    public boolean has_error = false;
    public Crate program;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current_index = 0;
        this.tokens = SkipWhitespace(tokens);
        this.has_error = false;
        this.program = new Crate(null);
    }
    private List<Token> SkipWhitespace(List<Token> tokens) {
        List<Token> temp = new ArrayList<>();
        for(Token tk : tokens) {
            if (tk.token_type == TokenType.WHITESPACE || tk.token_type == TokenType.COMMENT) {
                continue;
            } else {
                temp.add(tk);
            }
        }
        return temp;
    }
    private int GetPriority(String operator) {
        switch (operator) {
            case "=":
            case "+=":
            case "-=":
            case "*=":
            case "/=":
            case "%=":
            case "^=":
            case "&=":
            case "|=":
            case "<<=":
            case ">>=":
            case "as":
                return 1;
            case "||":
                return 2;
            case "&&":
                return 3;
            case "|":
                return 4;
            case "^":
                return 5;
            case "&":
                return 6;
            case "==":
            case "!=":
                return 7;
            case "<":
            case "<=":
            case ">":
            case ">=":
                return 8;
            case "<<":
            case ">>":
                return 9;
            case "..":
            case "..=":
            case "...":
                return 10;
            case "+":
            case "-":
                return 11;
            case "*":
            case "/":
            case "%":
                return 12;
            default:
                return 0;
        }
    }
    private BinaryExpression ParseBinaryExpression(Expression left, int priority) {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        String operator;
        if (tk.token_type == TokenType.OPERATOR) {
            operator = tk.value;
            current_index++;
        } else if (tk.value.equals("as")) {
            operator = tk.value;
            current_index++;
            Type tp = ParseType();
            return new BinaryExpression(left, operator, null, tp);
        } else {
            has_error = true;
            return null;
        }
        if (!(operator.equals("=") || operator.equals("+=") || operator.equals("-=") || 
            operator.equals("*=") || operator.equals("/=") || operator.equals("%=") ||
            operator.equals("^=") || operator.equals("&=") || operator.equals("|=") || 
            operator.equals("<<=") || operator.equals(">>="))) {
            priority++;
        }
        Expression right = ParseExpression_(priority);
        if (right == null || left == null) {
            has_error = true;
            return null;
        }
        return new BinaryExpression(left, operator, right, null);
    }
    private UnaryExpression ParseUnaryExpression() {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        String operator = "";
        boolean ismut = false;
        if (tk.token_type == TokenType.OPERATOR) {
            operator = tk.value;
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).value.equals("mut")) {
            ismut = true;
            current_index++;
        }
        Expression right = ParseExpression();
        return new UnaryExpression(operator, right, ismut);
    }
    private LiteralExpression ParseLiteralExpression() {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        Object ob = new Object();
        if (tk.token_type == TokenType.CHAR_LITERAL || tk.token_type == TokenType.STRING_LITERAL || tk.token_type == TokenType.INTERGER_LITERAL) {
            current_index++;
            ob = tk.real_value;
        } else if (tk.value.equals("true") || tk.value.equals("false")) {
            current_index++;
            ob = tk.value;
        } else {
            has_error = true;
            return null;
        }
        return new LiteralExpression(ob, tk.token_type);
    }
    private IdentifierExpression ParseIdentifierExpression() {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        String name = "";
        if (tk.token_type == TokenType.IDENTIFIER || tk.value.equals("self") || tk.value.equals("Self")) {
            name = tk.value;
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        return new IdentifierExpression(name);
    }
    private CallFuncExpression ParseCallFuncExpression(Expression left) {
        if (has_error) {
            return null;
        }
        List<Expression> list = new ArrayList<>();
        if (tokens.get(current_index).value.equals("(")) {
            current_index++;
            boolean flag = true;
            while (true) {
                Token tk = tokens.get(current_index);
                if (tk.value.equals(")")) {
                    current_index++;
                    break;
                } else if (tk.value.equals(",") && !flag) {
                    flag = true;
                    current_index++;
                    continue;
                } else if (flag) {
                    flag = false;
                    Expression new_exp = ParseExpression();
                    if (new_exp == null) {
                        has_error = true;
                        return null;
                    }
                    list.add(new_exp);
                } else {
                    has_error = true;
                    return null;
                }
            }
        } else {
            has_error = true;
            return null;
        }
        return new CallFuncExpression(left, list);
    }
    private CallMethodExpression ParseCallMethodExpression(Expression left) {
        if (has_error) {
            return null;
        }
        Token tk_ = tokens.get(current_index);
        if (tk_.value.equals(".")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Token id = tokens.get(current_index);
        String identifier = "";
        if (id.token_type == TokenType.IDENTIFIER || id.value.equals("self") || id.value.equals("Self")) {
            identifier = id.value;
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        List<Expression> list = new ArrayList<>();
        if (tokens.get(current_index).value.equals("(")) {
            current_index++;
            boolean flag = true;
            while (true) {
                Token tk = tokens.get(current_index);
                if (tk.value.equals(")")) {
                    current_index++;
                    break;
                } else if (tk.value.equals(",") && !flag) {
                    flag = true;
                    current_index++;
                    continue;
                } else if (flag) {
                    flag = false;
                    Expression new_exp = ParseExpression();
                    if (new_exp == null) {
                        has_error = true;
                        return null;
                    }
                    list.add(new_exp);
                } else {
                    has_error = true;
                    return null;
                }
            }
        } else {
            has_error = true;
            return null;
        }
        return new CallMethodExpression(left, identifier, list);
    }
    private ArrIndexExpression ParseArrIndexExpression(Expression left) {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("[")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Expression exp2 = ParseExpression();
        if (exp2 == null) {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).value.equals("]")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        return new ArrIndexExpression(left, exp2);
    }
    private FieldExpression ParseFieldExpression(Expression left) {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals(".")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Token tk = tokens.get(current_index);
        if (tk.token_type == TokenType.IDENTIFIER) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        return new FieldExpression(left, tk.value);
    }
    private BlockExpression ParseBlockExpression() {
        if (has_error) {
            return null;
        }
        boolean isconst = false;
        if (tokens.get(current_index).value.equals("const")) {
            isconst = true;
            current_index++;
        }
        if (tokens.get(current_index).value.equals("{")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        List<Statement> list = new ArrayList<>();
        while (true && !has_error) {
            // System.out.println(current_index);
            Token tk = tokens.get(current_index);
            if (tk.value.equals("}")) {
                current_index++;
                break;
            } else if (isStatement()) {
                Statement new_sta = ParseStatement();
                list.add(new_sta);
            } else {
                Expression exp = ParseExpression();
                if (tokens.get(current_index).value.equals("}")) {
                    current_index++;
                } else {
                    has_error = true;
                    return null;
                }
                return new BlockExpression(list, exp, isconst);
            }
        }
        return new BlockExpression(list, null, isconst);
    }
    private BreakExpression ParseBreakExpression() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("break")) {
            current_index++;
        } else {
            return null;
        }
        if (tokens.get(current_index).value.equals(";")) {
            return new BreakExpression(null);
        }
        Expression exp = ParseExpression();
        return new BreakExpression(exp);
    }
    private ReturnExpression ParseReturnExpression() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("return")) {
            current_index++;
        } else {
            return null;
        }
        if (tokens.get(current_index).value.equals(";")) {
            return new ReturnExpression(null);
        }
        Expression exp = ParseExpression();
        return new ReturnExpression(exp);
    }
    private LoopExpression ParseLoopExpression() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("loop")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        BlockExpression block = ParseBlockExpression();
        return new LoopExpression(block);
    }
    private WhileExpression ParseWhileExpression() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("while")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).value.equals("(")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Expression exp = ParseExpression();
        if (tokens.get(current_index).value.equals(")")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (exp instanceof StructExpression) {
            has_error = true;
            return null;
        }
        BlockExpression block = ParseBlockExpression();
        return new WhileExpression(exp, block);
    }
    private StructExpression ParseStructExpression() {
        if (has_error) {
            return null;
        }
        String path = "";
        String sub_path = "";
        if (tokens.get(current_index).token_type == TokenType.IDENTIFIER || tokens.get(current_index).value.equals("self") 
            || tokens.get(current_index).value.equals("Self")) {
            path = tokens.get(current_index).value;
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).value.equals("::")) {
            current_index++;
            if (tokens.get(current_index).token_type == TokenType.IDENTIFIER || tokens.get(current_index).value.equals("self") 
                || tokens.get(current_index).value.equals("Self")) {
                sub_path = tokens.get(current_index).value;
                current_index++;
            } else {
                has_error = true;
                return null;
            }
        }
        if (tokens.get(current_index).value.equals("{")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        List<StructExprField> list = new ArrayList<>();
        boolean flag = true;
        while (true && !has_error) {
            Token tk = tokens.get(current_index);
            if (tk.value.equals("}")) {
                current_index++;
                break;
            } else if (tk.value.equals(",") && !flag) {
                flag = true;
                current_index++;
                continue;
            } else if (flag) {
                flag = false;
                if (tk.token_type != TokenType.IDENTIFIER) {
                    has_error = true;
                    return null;
                }
                StructExprField temp = new StructExprField(null, null);
                temp.name = tk.value;
                current_index++;
                if (tokens.get(current_index).value.equals(":")) {
                    current_index++;
                    Expression exp = ParseExpression();
                    temp.exp = exp;
                } else if (tokens.get(current_index).value.equals(",") ||
                            tokens.get(current_index).value.equals("}")) {
                    continue;
                } else {
                    has_error = true;
                    return null;
                }
                list.add(temp);
            }
        }
        return new StructExpression(path, sub_path, list);
    }
    private IfExpression ParseIfExpression() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("if")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).value.equals("(")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Expression exp = ParseExpression();
        if (exp instanceof StructExpression) {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).value.equals(")")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        BlockExpression block = ParseBlockExpression();
        if (tokens.get(current_index).value.equals("else")) {
            current_index++;
            Expression exp_ = ParseExpression();
            if (!(exp_ instanceof BlockExpression) && !(exp_ instanceof IfExpression)) {
                has_error = true;
                return null;
            }
            return new IfExpression(exp, block, exp_);
        }
        return new IfExpression(exp, block, null);
    }
    private PathExpression ParsePathExpression() {
        if (has_error) {
            return null;
        }
        String path = "";
        String sub_path = "";
        if (tokens.get(current_index).token_type == TokenType.IDENTIFIER || tokens.get(current_index).value.equals("self") 
            || tokens.get(current_index).value.equals("Self")) {
            path = tokens.get(current_index).value;
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).value.equals("::")) {
            current_index++;
            if (tokens.get(current_index).token_type == TokenType.IDENTIFIER || tokens.get(current_index).value.equals("self") 
                || tokens.get(current_index).value.equals("Self")) {
                sub_path = tokens.get(current_index).value;
                current_index++;
            } else {
                has_error = true;
                return null;
            }
            return new PathExpression(path, sub_path);
        }
        return new PathExpression(path, null);
    }
    private ArrayExpression ParseArrayExpression() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("[")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        boolean flag = true;
        boolean flag_ = false;
        List<Expression> list = new ArrayList<>();
        while (true) {
            Token tk = tokens.get(current_index);
            if (tk.value.equals("]")) {
                current_index++;
                break;
            } else if ((tk.value.equals(",") || tk.value.equals(";")) && !flag) {
                if (tk.value.equals(";")) {
                    flag_ = true;
                }
                flag = true;
                current_index++;
                continue;
            } else if (flag) {
                flag = false;
                Expression new_exp = ParseExpression();
                list.add(new_exp);
            }
        }
        if (flag_ && (list.size() != 2 || flag)) {
            has_error = true;
            return null;
        }
        return new ArrayExpression(list);
    }
    private GroupedExpression ParseGroupedExpression() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("(")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Expression exp = ParseExpression();
        if (tokens.get(current_index).value.equals(")")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        return new GroupedExpression(exp);
    }
    private Expression ParseIdentifierBasedExpression() {
        if (has_error) {
            return null;
        }
        int index = current_index;
        if (current_index + 1 < tokens.size() && tokens.get(current_index + 1).value.equals("::")) {
            current_index += 2;
            if (current_index < tokens.size()) {
                current_index++;
                if (current_index < tokens.size() && tokens.get(current_index).value.equals("{")) {
                    current_index = index;
                    return ParseStructExpression();
                } else {
                    current_index = index;
                    return ParsePathExpression();
                }
            }
        } else if (current_index + 1 < tokens.size() && tokens.get(current_index + 1).value.equals("{")) {
            return ParseStructExpression();
        } else {
            current_index = index;
            return ParseIdentifierExpression();
        }
        return null;
    }
    private Expression ParseAtomExpression() {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        if (tk.value.equals("{")) {
            return ParseBlockExpression();
        } else if (tk.value.equals("break")) {
            return ParseBreakExpression();
        } else if (tk.value.equals("continue")) {
            current_index++;
            return new ContinueExpression();
        } else if (tk.value.equals("_")) {
            current_index++;
            return new UnderscoreExpression();
        } else if (tk.value.equals("return")) {
            return ParseReturnExpression();
        } else if (tk.value.equals("loop")) {
            return ParseLoopExpression();
        } else if (tk.value.equals("while")) {
            return ParseWhileExpression();
        } else if (tk.value.equals("if")) {
            return ParseIfExpression();
        } else if (tk.value.equals("[")) {
            return ParseArrayExpression();
        } else if (tk.value.equals("(")) {
            return ParseGroupedExpression();
        } else if (tk.value.equals("-") || tk.value.equals("!") || tk.value.equals("~") ||
                    tk.value.equals("&") || tk.value.equals("*") || tk.value.equals("@")) {
            return ParseUnaryExpression();
        } else if (tk.token_type == TokenType.CHAR_LITERAL || 
                    tk.token_type == TokenType.STRING_LITERAL || 
                    tk.token_type == TokenType.INTERGER_LITERAL ||
                    tk.token_type == TokenType.FLOAT_LITERAL || 
                    tk.value.equals("true") || tk.value.equals("false")) {
            return ParseLiteralExpression();
        } else if (tk.token_type == TokenType.IDENTIFIER ||
                    tk.value.equals("self") || tk.value.equals("Self")) {
            return ParseIdentifierBasedExpression();
        }
        return null;
    }
    private Expression ParseExpression_(int priority) {
        if (has_error) {
            return null;
        }
        Expression exp = ParseAtomExpression();
        if (exp == null) {
            return null;
        }
        while (current_index < tokens.size()) {
            Token tk = tokens.get(current_index);
            if (tk.value.equals("(")) {
                exp = ParseCallFuncExpression(exp);
                if (exp == null) return null;
            } else if (tk.value.equals("[")) {
                exp = ParseArrIndexExpression(exp);
                if (exp == null) return null;
            } else if (tk.value.equals(".")) {
                if (current_index + 2 < tokens.size() && tokens.get(current_index + 2).value.equals("(")) {
                    exp = ParseCallMethodExpression(exp);
                } else {
                    exp = ParseFieldExpression(exp);
                }
                if (exp == null) return null;
            } else if (tk.value.equals("+") || tk.value.equals("-") || tk.value.equals("*") || tk.value.equals("/") ||
                    tk.value.equals("%") || tk.value.equals("==") || tk.value.equals("!=") || tk.value.equals("<") ||
                    tk.value.equals(">") || tk.value.equals("<=") || tk.value.equals(">=") || tk.value.equals("&&") ||
                    tk.value.equals("||") || tk.value.equals("=") || tk.value.equals("+=") || tk.value.equals("-=") ||
                    tk.value.equals("*=") || tk.value.equals("/=") || tk.value.equals("as") ||
                    tk.value.equals("&") || tk.value.equals("|") || tk.value.equals("^") ||
                    tk.value.equals("<<") || tk.value.equals(">>") || tk.value.equals("..") ||
                    tk.value.equals("..=") || tk.value.equals("...") || tk.value.equals("%=") ||
                    tk.value.equals("^=") || tk.value.equals("&=") || tk.value.equals("|=") ||
                    tk.value.equals("<<=") || tk.value.equals(">>=") || tk.value.equals("as")) {
                int priority_ = GetPriority(tk.value);
                if (priority_ <= priority) {
                    break;
                } else {
                    exp = ParseBinaryExpression(exp, priority_);
                }
            } else {
                break;
            }
        }
        return exp;
    }
    private Expression ParseExpression() {
        return ParseExpression_(0);
    }
    private boolean isStatement() {
        if (tokens.get(current_index).value.equals("if") || tokens.get(current_index).value.equals("loop") 
            || tokens.get(current_index).value.equals("while") || tokens.get(current_index).value.equals("let")) {
            return true;
        }
        boolean flag = false;
        int index = current_index;
        while (index < tokens.size()) {
            if (tokens.get(index).value.equals("}")) {
                break;
            }
            if (tokens.get(index).value.equals(";")) {
                flag = true;
                break;
            }
            index++;
        }
        return flag;
    }
    private LetStatement ParseLetStatement() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("let")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Pattern pt = ParsePattern();
        Type tp = null;
        if (tokens.get(current_index).value.equals(":")) {
            current_index++;
            tp = ParseType();
        }
        if (tokens.get(current_index).value.equals("=")) {
            current_index++;
            Expression exp = ParseExpression();
            if (tokens.get(current_index).value.equals(";")) {
                current_index++;
            } else {
                has_error = true;
                return null;
            }
            return new LetStatement(pt, tp, exp);
        } else if (tokens.get(current_index).value.equals(";")) {
            current_index++;
            return new LetStatement(pt, tp, null);
        } else {
            has_error = true;
            return null;
        }
    }
    private ExpressionStatement ParseExpressionStatement() {
        if (has_error) {
            return null;
        }
        Expression exp = ParseExpression();
        if (tokens.get(current_index).value.equals(";")) {
            current_index++;
        } else if (!tokens.get(current_index - 1).value.equals("}")) {
            has_error = true;
            return null;
        }
        return new ExpressionStatement(exp);
    }
    private Statement ParseStatement() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("let")) {
            return ParseLetStatement();
        } else if (tokens.get(current_index).value.equals("fn") || 
                    tokens.get(current_index).value.equals("struct")||
                    tokens.get(current_index).value.equals("enum") ||
                    tokens.get(current_index).value.equals("const") || 
                    tokens.get(current_index).value.equals("trait") ||
                    tokens.get(current_index).value.equals("impl")) {
            return ParseItems();
        } else if (tokens.get(current_index).value.equals(";")) {
            current_index++;
            return null;
        } else {
            ExpressionStatement exp = ParseExpressionStatement();
            return exp;
        }
    }
    private Item ParseItems() {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        if (tk.value.equals("fn")) {
            return ParseFunctionItem();
        } else if (tk.value.equals("struct")) {
            return ParseStructItem();
        } else if (tk.value.equals("enum")) {
            return ParseEnumItem();
        } else if (tk.value.equals("const")) {
            if (current_index + 1 < tokens.size() && tokens.get(current_index + 1).value.equals("fn")) {
                return ParseFunctionItem();
            } else {
                return ParseConstItem();
            }
        } else if (tk.value.equals("trait")) {
            return ParseTraitItem();
        } else if (tk.value.equals("impl")) {
            return ParseImplItem();
        } else {
            has_error = true;
            return null;
        }
    }
    private Type ParseType() {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        if (tk.value.equals("&")) {
            current_index++;
            boolean flag = false;
            if (tokens.get(current_index).value.equals("mut")) {
                flag = true;
                current_index++;
            }
            Type tp = ParseType();
            return new ReferenceType(flag, tp);
        } else if (tk.value.equals("[")) {
            current_index++;
            Type tp = ParseType();
            if (tokens.get(current_index).value.equals(";")) {
                current_index++;
                Expression exp = ParseExpression();
                if (!tokens.get(current_index).value.equals("]")) {
                    has_error = true;
                    return null;
                } else {
                    current_index++;
                    return new ArrayType(tp, exp);
                }
            }
        } else if (tk.token_type == TokenType.IDENTIFIER || tk.value.equals("self") || tk.value.equals("Self")) {
            current_index++;
            return new TypePath(tk.value);
        } else if (tk.value.equals("(")) {
            current_index++;
            if (!tokens.get(current_index).value.equals(")")) {
                has_error = true;
                return null;
            } else {
                current_index++;
                return new UnitType();
            }
        } else {
            has_error = true;
            return null;
        }
        return null;
    }
    private Pattern ParsePattern() {
        if (has_error) {
            return null;
        }
        Token tk = tokens.get(current_index);
        if (tk.value.equals("_")) {
            current_index++;
            return new WildcardPattern();
        } else if (tk.value.equals("-")) {
            LiteralPattern temp = new LiteralPattern(true, null);
            current_index++;
            LiteralExpression exp = ParseLiteralExpression();
            temp.exp = exp;
            return temp;
        } else if (tk.token_type == TokenType.CHAR_LITERAL || tk.token_type == TokenType.FLOAT_LITERAL ||
                    tk.token_type == TokenType.INTERGER_LITERAL || tk.token_type == TokenType.STRING_LITERAL || 
                    tk.value.equals("true") || tk.value.equals("false")) {
            LiteralPattern temp = new LiteralPattern(false, null);
            LiteralExpression exp = ParseLiteralExpression();
            temp.exp = exp;
            return temp;
        } else if (tk.value.equals("&") || tk.value.equals("&&")) {
            current_index++;
            boolean isMut = false;
            if (tokens.get(current_index).value.equals("mut")) {
                isMut = true;
                current_index++;
            }
            Pattern sub = ParsePattern();
            return new ReferencePattern(isMut, sub);
        } else if (tk.token_type == TokenType.IDENTIFIER || tk.value.equals("self") || tk.value.equals("Self") || tk.token_type == TokenType.STRICT_KEYWORD) {
            current_index++;
            Token next_token = tokens.get(current_index);
            if (tk.value.equals("mut") || tk.value.equals("ref")) {
                boolean isMut = false;
                boolean isRef = false;
                if (tk.value.equals("mut")) {
                    isMut = true;
                } else if (tk.value.equals("ref")) {
                    isRef = true;
                    if (next_token.value.equals("mut")) {
                        current_index++;
                        isMut = true;
                    }
                }
                Token id = tokens.get(current_index);
                if (id.token_type != TokenType.IDENTIFIER) {
                    has_error = true;
                    return null;
                }
                String name = id.value;
                current_index++;
                Token At = tokens.get(current_index);
                if (At.value.equals("@")) {
                    current_index++;
                    Pattern pt = ParsePattern();
                    return new IdentifierPattern(isRef, isMut, name, pt);
                } else {
                    return new IdentifierPattern(isRef, isMut, name, null);
                }
            } else if (next_token.value.equals("::")) {
                current_index++;
                Token temp = tokens.get(current_index);
                String path = "";
                if (temp.token_type == TokenType.IDENTIFIER) {
                    path = temp.value;
                } else {
                    has_error = true;
                    return null;
                }
                current_index++;
                if (tokens.get(current_index).value.equals("(")) {
                    current_index++;
                    List<Pattern> list = new ArrayList<>();
                    boolean flag = true;
                    while (true) {
                        Token new_token = tokens.get(current_index);
                        if (new_token.value.equals(")")) {
                            current_index++;
                            break;
                        } else if (new_token.value.equals(",") && !flag) {
                            flag = true;
                            current_index++;
                            continue;
                        } else if (flag) {
                            flag = false;
                            list.add(ParsePattern());
                        } else {
                            has_error = true;
                            return null;
                        }
                    }
                    return new TupleStructPattern(tk.value, path, list);
                } else {
                    return new PathPattern(tk.value, path);
                }
            } else if (next_token.value.equals("(")) {
                current_index++;
                List<Pattern> list = new ArrayList<>();
                boolean flag = true;
                while (true) {
                    Token new_token = tokens.get(current_index);
                    if (new_token.value.equals(")")) {
                        current_index++;
                        break;
                    } else if (new_token.value.equals(",") && !flag) {
                        flag = true;
                        current_index++;
                        continue;
                    } else if (flag) {
                        flag = false;
                        list.add(ParsePattern());
                    } else {
                        has_error = true;
                        return null;
                    }
                }
                return new TupleStructPattern(tk.value, null, list);
            } else if (next_token.value.equals("@")) {
                current_index++;
                Pattern pt = ParsePattern();
                return new IdentifierPattern(false, false, tk.value, pt);
            } else {
                return new IdentifierPattern(false, false, tk.value, null);
            }
        }
        return null;
    }
    private List<Parameter> ParseParameter() {
        if (has_error) {
            return null;
        }
        List<Parameter> parameters = new ArrayList<>();
        if (tokens.get(current_index).value.equals("(")) {
            current_index++;
        } else {
            has_error = true;
            return parameters;
        }
        boolean flag = false;
        Token token1 = tokens.get(current_index);
        Token token2 = tokens.get(current_index + 1);
        Token token3 = tokens.get(current_index + 2);
        boolean has_self = false;
        if ((token1.value.equals("self") || token2.value.equals("self") || token3.value.equals("self"))
            && (!token1.value.equals("::") && !token2.value.equals("::") && !token3.value.equals("::"))) {
            has_self = true;
        }
        while (true) {
            Token tk = tokens.get(current_index);
            Parameter param = new Parameter(null, null);
            if (tk.value.equals(")")) {
                current_index++;
                break;
            } else if (tk.value.equals(",") && !flag) {
                flag = true;
                current_index++;
            } else if (tk.value.equals(",") && flag) {
                has_error = true;
                current_index++;
                return parameters;
            } else if (has_self) {
                flag = false;
                has_self = false;
                if (tk.value.equals("&")) {
                    current_index++;
                    param.isReference = true;
                    Token next = tokens.get(current_index);
                    if (next.value.equals("mut")) {
                        param.isMut = true;
                        current_index++;
                        if (tokens.get(current_index).value.equals("self")) {
                            current_index++;
                            param.isSelf = true;
                            parameters.add(param);
                        } else {
                            has_error = true;
                            return null;
                        }
                    } else if (next.value.equals("self")) {
                        current_index++;
                        param.isSelf = true;
                        parameters.add(param);
                    } else {
                        has_error = true;
                        return null;
                    }
                } else if (tk.value.equals("mut")) {
                    current_index++;
                    param.isMut = true;
                    Token next = tokens.get(current_index);
                    if (next.value.equals("self")) {
                        param.isSelf = true;
                        current_index++;
                        if (tokens.get(current_index).value.equals(":")) {
                            current_index++;
                            Type tp = ParseType();
                            param.type = tp;
                            parameters.add(param);
                        } else {
                            parameters.add(param);
                        }
                    } else {
                        has_error = true;
                        return null;
                    }
                } else if (tk.value.equals("self")) {
                    param.isSelf = true;
                    current_index++;
                    if (tokens.get(current_index).value.equals(":")) {
                        current_index++;
                        Type tp = ParseType();
                        param.type = tp;
                        parameters.add(param);
                    } else {
                        parameters.add(param);
                    }
                }
                has_self = false;
            } else {
                flag = false;
                Pattern pt = ParsePattern();
                param.pattern = pt;
                if (tokens.get(current_index).value.equals(":")) {
                    current_index++;
                    Type tp = ParseType();
                    param.type = tp;
                    parameters.add(param);
                } else {
                    has_error = true;
                    return null;
                }
            }
            if (has_error) {
                return null;
            }
        }
        return parameters;
    }
    private FunctionItem ParseFunctionItem() {
        if (has_error) {
            return null;
        }
        FunctionItem func = new FunctionItem(null, null, null, null, false);
        if (tokens.get(current_index).value.equals("fn")) {
            current_index++;
        } else if (tokens.get(current_index).value.equals("const")) {
            current_index += 2;
            func.is_const = true;
        } else {
            has_error = true;
            return null;
        }
        Token name = tokens.get(current_index);
        if (name.token_type != TokenType.IDENTIFIER) {
            has_error = true;
            return null;
        }
        func.name = name.value;
        current_index++;
        if (!tokens.get(current_index).value.equals("(")) {
            has_error = true;
            return null;
        }
        func.parameters = ParseParameter();
        Token arrow = tokens.get(current_index);
        if (arrow.value.equals("->")) {
            current_index++;
            Type tp = ParseType();
            func.return_type = tp;
        }
        if (!tokens.get(current_index).value.equals(";")) {
            BlockExpression block = ParseBlockExpression();
            func.body = block;
        } else {
            current_index++;
        }
        return func;
    }
    private StructItem ParseStructItem() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("struct")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).token_type != TokenType.IDENTIFIER) {
            has_error = true;
            return null;
        }
        String name = tokens.get(current_index).value;
        current_index++;
        if (tokens.get(current_index).value.equals(";")) {
            current_index++;
            return new StructItem(name, null);
        } else if (tokens.get(current_index).value.equals("{")) {
            current_index++;
            boolean flag = true;
            List<Parameter> list = new ArrayList<>();
            while (true) {
                Token new_token = tokens.get(current_index);
                if (new_token.value.equals("}")) {
                    current_index++;
                    break;
                } else if (new_token.value.equals(",") && !flag) {
                    flag = true;
                    current_index++;
                    continue;
                } else if (flag) {
                    flag = false;
                    if (new_token.token_type != TokenType.IDENTIFIER) {
                        has_error = true;
                        return null;
                    } else {
                        String id = new_token.value;
                        current_index++;
                        if (tokens.get(current_index).value.equals(":")) {
                            current_index++;
                            Type type = ParseType();
                            if (type == null) {
                                has_error = true;
                                return null;
                            }
                            Parameter temp = new Parameter(id, type);
                            list.add(temp);
                        } else {
                            has_error = true;
                            return null;
                        }
                    }
                } else {
                    has_error = true;
                    return null;
                }
            }
            if (list.size() == 0) {
                has_error = true;
                return null;
            }
            return new StructItem(name, list);
        } else {
            has_error = true;
            return null;
        }
    }
    private EnumItem ParseEnumItem() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("enum")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).token_type != TokenType.IDENTIFIER) {
            has_error = true;
            return null;
        }
        String name = tokens.get(current_index).value;
        current_index++;
        if (tokens.get(current_index).value.equals("{")) {
            current_index++;
            boolean flag = true;
            List<String> list = new ArrayList<>();
            while (true) {
                Token new_token = tokens.get(current_index);
                if (new_token.value.equals("}")) {
                    current_index++;
                    break;
                } else if (new_token.value.equals(",") && !flag) {
                    flag = true;
                    current_index++;
                    continue;
                } else if (flag) {
                    flag = false;
                    if (new_token.token_type != TokenType.IDENTIFIER) {
                        has_error = true;
                        return null;
                    } else {
                        String id = new_token.value;
                        current_index++;
                        list.add(id);
                    }
                } else {
                    has_error = true;
                    return null;
                }
            }
            if (list.size() == 0) {
                has_error = true;
                return null;
            }
            return new EnumItem(name, list);
        } else {
            has_error = true;
            return null;
        }
    }
    private ConstItem ParseConstItem() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("const")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).token_type != TokenType.IDENTIFIER) {
            has_error = true;
            return null;
        }
        String name = tokens.get(current_index).value;
        current_index++;
        if (tokens.get(current_index).value.equals(":")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        Type ty = ParseType();
        if (tokens.get(current_index).value.equals(";")) {
            current_index++;
            return new ConstItem(name, ty, null);
        } else if (tokens.get(current_index).value.equals("=")) {
            current_index++;
            Expression exp = ParseExpression();
            if (exp == null) {
                has_error = true;
                return null;
            }
            if (tokens.get(current_index).value.equals(";")) {
                current_index++;
            } else {
                has_error = true;
                return null;
            }
            return new ConstItem(name, ty, exp);
        } else {
            has_error = true;
            return null;
        }
    }
    private TraitItem ParseTraitItem() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("trait")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index).token_type != TokenType.IDENTIFIER) {
            has_error = true;
            return null;
        }
        String name = tokens.get(current_index).value;
        current_index++;
        if (tokens.get(current_index).value.equals("{")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        List<Item> list = new ArrayList<>();
        while (true) {
            if (tokens.get(current_index).value.equals("}")) {
                current_index++;
                break;
            }
            Item item = ParseItems();
            if (item == null) {
                has_error = true;
                return null;
            }
            list.add(item);
        }
        return new TraitItem(name, list);
    }
    private ImplItem ParseImplItem() {
        if (has_error) {
            return null;
        }
        if (tokens.get(current_index).value.equals("impl")) {
            current_index++;
        } else {
            has_error = true;
            return null;
        }
        if (tokens.get(current_index + 1).value.equals("for")) {
            String name = "";
            if (tokens.get(current_index).token_type == TokenType.IDENTIFIER) {
                name = tokens.get(current_index).value;
                current_index += 2;
            } else {
                has_error = true;
                return null;
            }
            Type tp = ParseType();
            if (tokens.get(current_index).value.equals("{")) {
                current_index++;
            } else {
                has_error = true;
                return null;
            }
            List<Item> list = new ArrayList<>();
            while (true) {
                if (tokens.get(current_index).value.equals("}")) {
                    current_index++;
                    break;
                }
                Item item = ParseItems();
                if (item == null) {
                    has_error = true;
                    return null;
                }
                list.add(item);
            }
            return new ImplItem(name, tp, list);
        } else {
            Type tp = ParseType();
            if (tp == null) {
                has_error = true;
                return null;
            }
            if (tokens.get(current_index).value.equals("{")) {
                current_index++;
            } else {
                has_error = true;
                return null;
            }
            List<Item> list = new ArrayList<>();
            while (true) {
                if (tokens.get(current_index).value.equals("}")) {
                    current_index++;
                    break;
                }
                Item item = ParseItems();
                if (item == null) {
                    has_error = true;
                    return null;
                }
                list.add(item);
            }
            return new ImplItem(null, tp, list);
        }
    }
    public boolean Parse() {
        List<Item> items = new ArrayList<>();
        while (current_index < tokens.size()) {
            Item item = ParseItems();
            if (has_error) {
                return false;
            }
            items.add(item);
        }
        program.nodes = items;
        return true;
    }
}