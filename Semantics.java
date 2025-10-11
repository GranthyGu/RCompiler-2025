package rcompiler2025;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.sql.Struct;
import java.util.*;
import java.util.function.Function;

enum Kind {
    CRATE,
    BLOCK,
    IFEXP, LOOPEXP, WHILEEXP,
    FUNCTION, IMPL
}

class Scope {
    public Kind type;
    public Scope parent;
    public List<Scope> children = new ArrayList<>();
    public Map<String, Item> typeMap = new HashMap<>();     // Trait, Struct, Enum
    public Map<String, Item> valueMap = new HashMap<>();    // Function, Const
    public List<Statement> statements = new ArrayList<>();
    public Expression returnExpression = null;
    public Type returnType = null;      // Only for Function
    public boolean isMut = false;       // Only for the first parameter of Function.
    public boolean isReference = false; // Only for the first parameter of Function.
    public boolean isSelf = false;      // Only for the first parameter of Function.
    public String traitName = null;     // Only for impl use.
    public Type typeStruct = null;      // Only for impl use.
    public TraitItem traitItem = null;  // Only for impl use.
    public Scope(Kind type, Scope parent) {
        this.type = type;
        this.parent = parent;
        this.returnExpression = null;
        if (parent != null) {
            parent.children.add(this);
        }
    }
}

public class Semantics {
    public List<Item> items;
    public Scope root = new Scope(Kind.CRATE, null);
    public boolean has_error = false;
    private List<String> primitiveTypes = Arrays.asList("i32", "u32", "isize", "usize", "bool", "str", "String", "char");
    private List<TraitItem> traitItems = new ArrayList<>();
    public Semantics(Parser parser) {
        items = parser.program.nodes;
    }
    private boolean isBlockExpression(Statement sta) {
        if (!(sta instanceof ExpressionStatement)) {
            has_error = true;
            return false;
        }
        Expression exp = ((ExpressionStatement)sta).expression;
        if ((exp instanceof BlockExpression) || (exp instanceof LoopExpression)
            || (exp instanceof IfExpression) || (exp instanceof WhileExpression)) {
            return true;
        } else {
            return false;
        }
    }
    private boolean scanScope(Statement item, Scope parent) {
        Scope scope = new Scope(null, parent);
        if (item instanceof FunctionItem) {
            scope.type = Kind.FUNCTION;
            FunctionItem funcItem = (FunctionItem)item;
            BlockExpression block = funcItem.body;
            List<Statement> statements = block.statements;
            scope.returnExpression = block.exp;
            scope.returnType = funcItem.return_type;
            Parameter first = funcItem.parameters.get(0);
            boolean flag = false;
            if (first.isSelf) {
                flag = true;
                scope.isSelf = first.isSelf;
                scope.isMut = first.isMut;
                scope.isReference = first.isReference;
            }
            for (Parameter par : funcItem.parameters) {
                if (flag) {
                    flag = false;
                    continue;
                }
                LetStatement let = new LetStatement(par.pattern, par.type, null);
                scope.statements.add(let);
            }
            for (Statement sta : statements) {
                if (sta instanceof FunctionItem) {
                    FunctionItem sta_ = (FunctionItem)sta;
                    String name = sta_.name;
                    scope.valueMap.put(name, sta_);
                    if (!scanScope(sta_, scope)) {
                        return false;
                    }
                } else if (sta instanceof StructItem) {
                    StructItem sta_ = (StructItem)sta;
                    String name = sta_.name;
                    scope.typeMap.put(name, sta_);
                } else if (sta instanceof EnumItem) {
                    EnumItem sta_ = (EnumItem)sta;
                    String name = sta_.name;
                    scope.typeMap.put(name, sta_);
                } else if (sta instanceof ConstItem) {
                    ConstItem sta_ = (ConstItem)sta;
                    String name = sta_.name;
                    scope.typeMap.put(name, sta_);
                } else if (sta instanceof TraitItem) {
                    TraitItem sta_ = (TraitItem)sta;
                    String name = sta_.name;
                    scope.typeMap.put(name, sta_);
                } else if (sta instanceof ImplItem) {
                    ImplItem sta_ = (ImplItem)sta;
                    if (!scanScope(sta_, scope)) {
                        return false;
                    }
                } else if (sta instanceof ExpressionStatement && isBlockExpression(sta)) {
                    scope.statements.add(sta);
                    if (!scanScope(sta, scope)) {
                        has_error = true;
                        return false;
                    }
                } else {
                    if (sta instanceof ExpressionStatement) {
                        Expression exp = ((ExpressionStatement)sta).expression;
                        if (exp instanceof BreakExpression || exp instanceof ContinueExpression) {
                            has_error = true;
                            return false;
                        }
                    } else if (sta instanceof LetStatement) {
                        LetStatement let = (LetStatement)sta;
                        if (isBlockExpression(new ExpressionStatement(let.initializer))) {
                            if (!scanScope(new ExpressionStatement(let.initializer), scope)) {
                                has_error = true;
                                return false;
                            }
                        }
                    }
                    scope.statements.add(sta);
                }
            }
        } else if (item instanceof ImplItem) {
            scope.type = Kind.IMPL;
            ImplItem implItem = (ImplItem)item;
            List<Item> items = implItem.AssociatedItems;
            scope.typeStruct = implItem.type;
            scope.traitName = implItem.name;
            for(Item item_ : items) {
                if (item_ instanceof FunctionItem) {
                    FunctionItem sta_ = (FunctionItem)item_;
                    String name = sta_.name;
                    scope.valueMap.put(name, sta_);
                    if (!scanScope(sta_, scope)) {
                        has_error = true;
                        return false;
                    }
                } else if (item_ instanceof ConstItem) {
                    ConstItem sta_ = (ConstItem)item_;
                    String name = sta_.name;
                    scope.valueMap.put(name, sta_);
                } else if (item_ instanceof StructItem || item_ instanceof EnumItem || item_ instanceof TraitItem || item_ instanceof ImplItem) {
                    has_error = true;
                    return false;
                }
            }
        } else if (item instanceof ExpressionStatement && isBlockExpression(item)) {
            Expression exp = ((ExpressionStatement)item).expression;
            if (exp instanceof IfExpression) {
                scope.type = Kind.IFEXP;
                IfExpression exp_ = (IfExpression)exp;
                scope.returnExpression = exp_.then_branch.exp;
                List<Statement> statements = exp_.then_branch.statements;
                for (Statement sta : statements) {
                    if (sta instanceof FunctionItem) {
                        FunctionItem sta_ = (FunctionItem)sta;
                        String name = sta_.name;
                        scope.valueMap.put(name, sta_);
                        if (!scanScope(sta_, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else if (sta instanceof StructItem) {
                        StructItem sta_ = (StructItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof EnumItem) {
                        EnumItem sta_ = (EnumItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ConstItem) {
                        ConstItem sta_ = (ConstItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ExpressionStatement && isBlockExpression(sta)) {
                        scope.statements.add(sta);
                        if (!scanScope(sta, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else {
                        if (sta instanceof ExpressionStatement) {
                            Expression _exp_ = ((ExpressionStatement)sta).expression;
                            if (_exp_ instanceof BreakExpression || _exp_ instanceof ContinueExpression) {
                                has_error = true;
                                return false;
                            }
                        } else if (sta instanceof LetStatement) {
                            LetStatement let = (LetStatement)sta;
                            if (isBlockExpression(new ExpressionStatement(let.initializer))) {
                                if (!scanScope(new ExpressionStatement(let.initializer), scope)) {
                                    has_error = true;
                                    return false;
                                }
                            }
                        }
                        scope.statements.add(sta);
                    }
                }
                if (exp_.else_branch instanceof IfExpression || exp_.else_branch instanceof BlockExpression) {
                    ExpressionStatement sta = new ExpressionStatement(exp_.else_branch);
                    return scanScope(sta, parent);
                } else if (!exp_.equals(null)) {
                    has_error = true;
                    return false;
                }
                return true;
            } else if (exp instanceof LoopExpression) {
                scope.type = Kind.LOOPEXP;
                LoopExpression exp_ = (LoopExpression)exp;
                scope.returnExpression = exp_.value.exp;
                List<Statement> statements = exp_.value.statements;
                for (Statement sta : statements) {
                    if (sta instanceof FunctionItem) {
                        FunctionItem sta_ = (FunctionItem)sta;
                        String name = sta_.name;
                        scope.valueMap.put(name, sta_);
                        if (!scanScope(sta_, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else if (sta instanceof StructItem) {
                        StructItem sta_ = (StructItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof EnumItem) {
                        EnumItem sta_ = (EnumItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ConstItem) {
                        ConstItem sta_ = (ConstItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ExpressionStatement && isBlockExpression(sta)) {
                        scope.statements.add(sta);
                        if (!scanScope(sta, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else {
                        if (sta instanceof LetStatement) {
                            LetStatement let = (LetStatement)sta;
                            if (isBlockExpression(new ExpressionStatement(let.initializer))) {
                                if (!scanScope(new ExpressionStatement(let.initializer), scope)) {
                                    has_error = true;
                                    return false;
                                }
                            }
                        }
                        scope.statements.add(sta);
                    }
                }
                return true;
            } else if (exp instanceof WhileExpression) {
                scope.type = Kind.WHILEEXP;
                WhileExpression exp_ = (WhileExpression)exp;
                scope.returnExpression = exp_.body.exp;
                List<Statement> statements = exp_.body.statements;
                for (Statement sta : statements) {
                    if (sta instanceof FunctionItem) {
                        FunctionItem sta_ = (FunctionItem)sta;
                        String name = sta_.name;
                        scope.valueMap.put(name, sta_);
                        if (!scanScope(sta_, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else if (sta instanceof StructItem) {
                        StructItem sta_ = (StructItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof EnumItem) {
                        EnumItem sta_ = (EnumItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ConstItem) {
                        ConstItem sta_ = (ConstItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ExpressionStatement && isBlockExpression(sta)) {
                        scope.statements.add(sta);
                        if (!scanScope(sta, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else {
                        if (sta instanceof LetStatement) {
                            LetStatement let = (LetStatement)sta;
                            if (isBlockExpression(new ExpressionStatement(let.initializer))) {
                                if (!scanScope(new ExpressionStatement(let.initializer), scope)) {
                                    has_error = true;
                                    return false;
                                }
                            }
                        }
                        scope.statements.add(sta);
                    }
                }
                return true;
            } else if (exp instanceof BlockExpression) {
                scope.type = Kind.BLOCK;
                BlockExpression exp_ = (BlockExpression)exp;
                scope.returnExpression = exp_.exp;
                List<Statement> statements = exp_.statements;
                for (Statement sta : statements) {
                    if (sta instanceof FunctionItem) {
                        FunctionItem sta_ = (FunctionItem)sta;
                        String name = sta_.name;
                        scope.valueMap.put(name, sta_);
                        if (!scanScope(sta_, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else if (sta instanceof StructItem) {
                        StructItem sta_ = (StructItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof EnumItem) {
                        EnumItem sta_ = (EnumItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ConstItem) {
                        ConstItem sta_ = (ConstItem)sta;
                        String name = sta_.name;
                        scope.typeMap.put(name, sta_);
                    } else if (sta instanceof ExpressionStatement && isBlockExpression(sta)) {
                        scope.statements.add(sta);
                        if (!scanScope(sta, scope)) {
                            has_error = true;
                            return false;
                        }
                    } else {
                        if (sta instanceof ExpressionStatement) {
                            Expression _exp_ = ((ExpressionStatement)sta).expression;
                            if (_exp_ instanceof BreakExpression || _exp_ instanceof ContinueExpression) {
                                has_error = true;
                                return false;
                            }
                        } else if (sta instanceof LetStatement) {
                            LetStatement let = (LetStatement)sta;
                            if (isBlockExpression(new ExpressionStatement(let.initializer))) {
                                if (!scanScope(new ExpressionStatement(let.initializer), scope)) {
                                    has_error = true;
                                    return false;
                                }
                            }
                        }
                        scope.statements.add(sta);
                    }
                }
                return true;
            }
        }
        return true;
    }
    private boolean buildScope() {
        for(Item item_ : items) {
            if (has_error) {
                return false;
            }
            if (item_ instanceof FunctionItem) {
                FunctionItem sta_ = (FunctionItem)item_;
                String name = sta_.name;
                root.valueMap.put(name, sta_);
                scanScope(sta_, root);
            } else if (item_ instanceof StructItem) {
                StructItem sta_ = (StructItem)item_;
                String name = sta_.name;
                root.typeMap.put(name, sta_);
            } else if (item_ instanceof EnumItem) {
                EnumItem sta_ = (EnumItem)item_;
                String name = sta_.name;
                root.typeMap.put(name, sta_);
            } else if (item_ instanceof ConstItem) {
                ConstItem sta_ = (ConstItem)item_;
                String name = sta_.name;
                root.valueMap.put(name, sta_);
            } else if (item_ instanceof TraitItem) {
                TraitItem sta_ = (TraitItem)item_;
                String name = sta_.name;
                root.typeMap.put(name, sta_);
                traitItems.add(sta_);
            } else if (item_ instanceof ImplItem) {
                ImplItem sta_ = (ImplItem)item_;
                scanScope(sta_, root);
            }
        }
        return true;
    }
    private boolean hasType(Scope scope, Type type) {
        if (scope == null) {
            has_error = true;
            return false;
        }
        if (type instanceof UnitType) {
            return true;
        } else if (type instanceof ArrayType) {
            expressionValueCheck(((ArrayType)type).exp, scope);
            if (has_error) {
                return false;
            }
            return hasType(scope, ((ArrayType)type).type);
        } else if (type instanceof ReferenceType) {
            return hasType(scope, ((ReferenceType)type).type);
        } else {
            String name = ((TypePath)type).name;
            if (name.equals("self") || name.equals("Self")) {
                has_error = true;
                return false;
            } else if (primitiveTypes.contains(name)) {
                return true;
            } else {
                for (Map.Entry<String, Item> entry : scope.typeMap.entrySet()) {
                    if (entry.getValue() instanceof TraitItem) {
                        continue;
                    } else if (entry.getKey().equals(name)) {
                        return true;
                    }
                }
                return hasType(scope.parent, type);
            }
        }
    }
    private boolean hasType_(Scope scope, Type type) {
        if (scope == null) {
            has_error = true;
            return false;
        }
        if (type instanceof UnitType) {
            return true;
        } else if (type instanceof ArrayType) {
            return hasType(scope, ((ArrayType)type).type);
        } else if (type instanceof ReferenceType) {
            return hasType(scope, ((ReferenceType)type).type);
        } else {
            String name = ((TypePath)type).name;
            if (name.equals("self")) {
                has_error = true;
                return false;
            } else if (primitiveTypes.contains(name) || name.equals("Self")) {
                return true;
            } else {
                for (Map.Entry<String, Item> entry : scope.typeMap.entrySet()) {
                    if (entry.getValue() instanceof TraitItem) {
                        continue;
                    } else if (entry.getKey().equals(name)) {
                        return true;
                    }
                }
                return hasType(scope.parent, type);
            }
        }
    }
    private boolean checkStruct(Scope scope) {
        for (Scope child : scope.children) {
            if (!checkStruct(child)) {
                has_error = true;
                return false;
            }
        }
        Map<String, Item> map = scope.typeMap;
        Map<String, StructItem> structMap = new HashMap<>();
        for (Map.Entry<String, Item> entry : map.entrySet()) {
            if (entry.getValue() instanceof StructItem) {
                structMap.put(entry.getKey(), (StructItem)entry.getValue());
            }
        }
        for (Map.Entry<String, StructItem> entry : structMap.entrySet()) {
            StructItem struct_ = entry.getValue();
            List<Parameter> parameters = struct_.fields;
            for (Parameter par : parameters) {
                if (!hasType(scope, par.type)) {
                    has_error = true;
                    return false;
                }
            }
        }
        return true;
    }
    private TraitItem getTraitItem(List<TraitItem> list, String name) {
        TraitItem result = null;
        for (TraitItem trait : list) {
            if (trait.name.equals(name)) {
                if (result == null) {
                    result = trait;
                } else {
                    has_error = true;
                    return null;
                }
            }
            for (TraitItem other : list) {
                if (other != trait && other.name.equals(trait.name)) {
                    has_error = true;
                    return null;
                }
            }
        }
        if (result == null) {
            has_error = true;
        }
        return result;
    }
    private boolean checkImpl(Scope scope) {
        for (Scope child : scope.children) {
            if (!checkImpl(child)) {
                return false;
            }
        }
        if (scope.typeStruct != null) {
            String name = ((TypePath)scope.typeStruct).name;
            Scope parent = scope.parent;
            if (scope.traitName != null) {
                TraitItem trait = getTraitItem(traitItems, scope.traitName);
                if (trait == null) {
                    return false;
                }
                List<String> funcs = getFunctions(trait);
                List<String> funcs_IMPL = new ArrayList<>();
                for (Map.Entry<String, Item> entry : scope.valueMap.entrySet()) {
                    if (entry.getValue() instanceof FunctionItem) {
                        funcs_IMPL.add(entry.getKey());
                    }
                }
                for (String f : funcs) {
                    if (!funcs_IMPL.contains(f)) {
                        has_error = true;
                        return false;
                    }
                }
                scope.traitItem = trait;
            }
            if (!checkImplType(scope)) {
                has_error = true;
                return false;
            }
            if (parent.type != Kind.FUNCTION && parent.type != Kind.CRATE) {
                has_error = true;
                return false;
            }
            boolean flag = false;
            for (Map.Entry<String, Item> entry : parent.typeMap.entrySet()) {
                if (entry.getValue() instanceof StructItem && entry.getKey().equals(name)) {
                    StructItem struct = (StructItem)entry.getValue();
                    struct.impls.add(scope);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                has_error = true;
            }
            return flag;
        }
        return true;
    }
    private boolean checkTrait(Scope scope) {
        for (Scope child : scope.children) {
            if (!checkTrait(child)) {
                return false;
            }
        }
        if (scope != root) {
            for (Map.Entry<String, Item> entry : scope.typeMap.entrySet()) {
                if (entry.getValue() instanceof TraitItem) {
                    has_error = true;
                    return false;
                }
            }
        } else {
            for (Map.Entry<String, Item> entry : scope.typeMap.entrySet()) {
                if (entry.getValue() instanceof TraitItem) {
                    if (!checkTraitType((TraitItem)entry.getValue())) {
                        return false;
                    }
                    traitItems.add((TraitItem)(entry.getValue()));
                }
            }
        }
        return true;
    }
    private List<String> getFunctions(TraitItem trait) {
        List<Item> list = trait.AssociatedItems;
        List<String> funcs = new ArrayList<>();
        for (Item item : list) {
            if (item instanceof FunctionItem) {
                FunctionItem func = (FunctionItem)item;
                if (func.body == null) {
                    funcs.add(func.name);
                }
            }
        }
        return funcs;
    }
    private boolean checkImplType(Scope scope) {
        List<Item> list = new ArrayList<>();
        for (Map.Entry<String, Item> entry : scope.valueMap.entrySet()) {
            list.add(entry.getValue());
        }
        for (Item temp : list) {
            if (temp instanceof FunctionItem) {
                FunctionItem fun = (FunctionItem)temp;
                Type type = fun.return_type;
                if (!hasType_(scope, type)) {
                    return false;
                }
                List<Parameter> parameters = fun.parameters;
                boolean flag = parameters.get(0).isSelf;
                List<String> names = new ArrayList<>();
                for (Parameter par : parameters) {
                    if (flag) {
                        flag = false;
                        continue;
                    }
                    if (names.contains(par.name)) {
                        has_error = true;
                        return false;
                    }
                    names.add(par.name);
                    if (!hasType_(scope, par.type)) {
                        has_error = true;
                        return false;
                    }
                }
            } else if (temp instanceof ConstItem) {
                if (!hasType(scope, ((ConstItem)temp).type)) {
                    return false;
                }
            } else {
                has_error = true;
                return false;
            }
        }
        return true;
    }
    private boolean checkTraitType(TraitItem item) {
        List<Item> list = item.AssociatedItems;
        for (Item temp : list) {
            if (temp instanceof FunctionItem) {
                FunctionItem fun = (FunctionItem)temp;
                Type type = fun.return_type;
                if (!hasType_(root, type)) {
                    return false;
                }
                List<Parameter> parameters = fun.parameters;
                boolean flag = parameters.get(0).isSelf;
                List<String> names = new ArrayList<>();
                for (Parameter par : parameters) {
                    if (flag) {
                        flag = false;
                        continue;
                    }
                    if (names.contains(par.name)) {
                        has_error = true;
                        return false;
                    }
                    names.add(par.name);
                    if (!hasType_(root, par.type)) {
                        has_error = true;
                        return false;
                    }
                }
            } else if (temp instanceof ConstItem) {
                if (!hasType(root, ((ConstItem)temp).type)) {
                    return false;
                }
            } else {
                has_error = true;
                return false;
            }
        }
        return true;
    }
    private Integer expressionValueCheck(Expression exp, Scope scope) {
        if (exp instanceof BinaryExpression) {
            Integer temp = expressionValueCheck(((BinaryExpression)exp).left, scope);
            Integer temp_ = expressionValueCheck(((BinaryExpression)exp).right, scope);
            if (temp == null || temp_ == null) {
                has_error = true;
                return null;
            }
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
            } else {
                has_error = true;
                return null;
            }
        } else if (exp instanceof UnaryExpression) {
            Integer temp = expressionValueCheck(((UnaryExpression)exp).operand, scope);
            if (temp == null) {
                has_error = true;
                return null;
            }
            BinaryExpression binary = (BinaryExpression)exp;
            if (binary.operator.equals("-")) {
                return -temp;
            } else {
                has_error = true;
                return null;
            }
        } else if (exp instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression)exp;
            if (literal.literal_type != TokenType.INTERGER_LITERAL) {
                has_error = true;
                return null;
            }
            Integer temp = (Integer)literal.value;
            return temp;
        } else if (exp instanceof IdentifierExpression) {
            IdentifierExpression id = (IdentifierExpression)exp;
            String name = id.name;
            for (Map.Entry<String, Item> entry : scope.valueMap.entrySet()) {
                if (entry.getValue() instanceof ConstItem && entry.getKey().equals(name)) {
                    ConstItem constitem = (ConstItem)(entry.getValue());
                    if (!(constitem.type instanceof TypePath)) {
                        has_error = true;
                        return null;
                    }
                    TypePath typepath = (TypePath)(constitem.type);
                    String pathname = typepath.name;
                    if (!(pathname.equals("i32") || pathname.equals("u32") || pathname.equals("isize") || pathname.equals("usize"))) {
                        has_error = true;
                        return null;
                    }
                    return expressionValueCheck(constitem.exp, scope);
                }
            }
            if (scope.parent != null) {
                return expressionValueCheck(exp, scope.parent);
            } else {
                has_error = true;
                return null;
            }
        } else if (exp instanceof GroupedExpression) {
            return expressionValueCheck(((GroupedExpression)exp).inner, scope);
        } else {
            has_error = true;
            return null;
        }
    }
    private boolean sameType(Type type1, Type type2, Scope scope) {
        if (type1 instanceof TypePath && type2 instanceof TypePath) {
            TypePath type1_ = (TypePath)type1;
            TypePath type2_ = (TypePath)type2;
            return type1_.name.equals(type2_.name);
        } else if (type1 instanceof ReferenceType && type2 instanceof ReferenceType) {
            ReferenceType type1_ = (ReferenceType)type1;
            ReferenceType type2_ = (ReferenceType)type2;
            return (type1_.isMut == type2_.isMut) && (sameType(type1_.type, type2_.type, scope));
        } else if (type1 instanceof ArrayType && type2 instanceof ArrayType) {
            ArrayType type1_ = (ArrayType)type1;
            ArrayType type2_ = (ArrayType)type2;
            Integer i1 = expressionValueCheck(type1_.exp, scope);
            Integer i2 = expressionValueCheck(type2_.exp, scope);
            return sameType(type1_.type, type2_.type, scope) && (i1 == i2);  // HAVE SOME RISK!
        } else if (type1 instanceof UnitType && type2 instanceof UnitType) {
            return true;
        } else {
            return false;
        }
    }
    private boolean expressionTypeCheck(Expression exp, Type type, Scope scope){
        if (exp == null || type == null) {
            return true;
        }
        if (exp instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression)exp;
            if (binary.operator.equals("==") || binary.operator.equals("!=")) {
                // TODO CHECK TYPE
                if (type instanceof TypePath) {
                    String t = ((TypePath)type).name;
                    if (!t.equals("bool")) {
                        has_error = true;
                        return false;
                    }
                } else {
                    has_error = true;
                    return false;
                }
                return true;
            }
            boolean flag = expressionTypeCheck(binary.left, type, scope) && expressionTypeCheck(binary.right, type, scope);
            if (!flag) {
                has_error = true;
            }
            return flag;
        } else if (exp instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression)exp;
            boolean flag = expressionTypeCheck(unary.operand, type, scope);
            if (!flag) {
                has_error = true;
            }
            return flag;
        } else if (exp instanceof LiteralExpression) {
            LiteralExpression literal = (LiteralExpression)exp;
            if (!(type instanceof TypePath)) {
                has_error = true;
                return false;
            }
            String name = ((TypePath)type).name;
            TokenType token_type = literal.literal_type;
            if (name.equals("i32") || name.equals("u32") || name.equals("isize") || name.equals("usize")) {
                if (token_type != TokenType.INTERGER_LITERAL) {
                    has_error = true;
                    return false;
                }
            } else if (name.equals("char")) {
                if (token_type != TokenType.CHAR_LITERAL) {
                    has_error = true;
                    return false;
                }
            } else if (name.equals("str") || name.equals("String")) {
                if (token_type != TokenType.STRING_LITERAL) {
                    has_error = true;
                    return false;
                }
            } else if (name.equals("bool")) {
                if (token_type != TokenType.BOOL_LITERAL) {
                    has_error = true;
                    return false;
                }
            } else {
                has_error = true;
                return false;
            }
            return true;
        } else if (exp instanceof IdentifierExpression) {
            IdentifierExpression iden = (IdentifierExpression)exp;
            String name = iden.name;
            for (Statement sta : scope.statements) {
                if (sta instanceof LetStatement) {
                    LetStatement let = (LetStatement)sta;
                    Pattern pattern = let.pattern;
                    if (pattern instanceof ReferencePattern) {
                        ReferencePattern ref = (ReferencePattern)pattern;
                        pattern = ref.subPattern;
                    }
                    IdentifierPattern id = (IdentifierPattern)pattern;
                    if (id.name.equals(name)) {
                        Type type_ = let.type;
                        if (!sameType(type_, type, scope)) {
                            has_error = true;
                            return false;
                        }
                        return true;
                    }
                }
            }
            // CHECK CONSTS
            for (Map.Entry<String, Item> entry : scope.valueMap.entrySet()) {
                if (entry.getValue() instanceof ConstItem && entry.getKey().equals(name)) {
                    ConstItem const_ = (ConstItem)(entry.getValue());
                    if (!sameType(const_.type, type, scope)) {
                        has_error = true;
                        return false;
                    }
                    return true;
                }
            }
            if (scope.parent == null) {
                has_error = true;
                return false;
            } else {
                return expressionTypeCheck(exp, type, scope.parent);
            }
        } else if (exp instanceof CallFuncExpression) {
            CallFuncExpression call = (CallFuncExpression)exp;
            // TODO
        } else if (exp instanceof CallMethodExpression) {
            CallMethodExpression method = (CallMethodExpression)exp;
            // TODO
        } else if (exp instanceof ArrIndexExpression) {
            ArrIndexExpression arr = (ArrIndexExpression)exp;
            Boolean flag = expressionTypeCheck(arr.object, new ArrayType(type, null), scope);
            // TODO: CHECK the index
            return flag;
        } else if (exp instanceof FieldExpression) {
            FieldExpression field = (FieldExpression)exp;
            // TODO
        } else if (exp instanceof BlockExpression) {
            BlockExpression block = (BlockExpression)exp;
            return expressionTypeCheck(block.exp, type, scope);
            // RISKY!! NEED TO CHECK!!! RETURN EXPRESSION
        } else if (exp instanceof BreakExpression) {
            BreakExpression break_ = (BreakExpression)exp;
            return expressionTypeCheck(break_.break_expression, type, scope);
        } else if (exp instanceof ReturnExpression) {
            ReturnExpression return_ = (ReturnExpression)exp;
            return expressionTypeCheck(return_.value, type, scope);
        } else if (exp instanceof LoopExpression) {
            LoopExpression loop = (LoopExpression)exp;
            return expressionTypeCheck(loop.value, type, scope);
        } else if (exp instanceof WhileExpression) {
            WhileExpression while_ = (WhileExpression)exp;
            TypePath boolean_ = new TypePath("bool");
            return expressionTypeCheck(while_.condition, boolean_, scope) && expressionTypeCheck(while_.body, type, scope);
        } else if (exp instanceof StructExpression) {
            StructExpression struct_ = (StructExpression)exp;
            String name = struct_.name;
            List<StructExprField> list = struct_.fields;
            for (Map.Entry<String, Item> entry : scope.typeMap.entrySet()) {
                if (entry.getValue() instanceof StructItem && entry.getKey().equals(name)) {
                    StructItem structitem = (StructItem)(entry.getValue());
                    if (structitem.fields.size() != list.size()) {
                        has_error = true;
                        return false;
                    }
                    for (int i = 0; i < structitem.fields.size(); i++) {
                        Parameter par = structitem.fields.get(i);
                        StructExprField field = list.get(i);
                        if (!field.name.equals(par.name)) {
                            has_error = true;
                            return false;
                        }
                        expressionTypeCheck(field.exp, par.type, scope);
                    }
                }
            }
            if (scope.parent != null) {
                return expressionTypeCheck(exp, type, scope.parent);
            } else {
                has_error = true;
                return false;
            }
        } else if (exp instanceof IfExpression) {
            IfExpression if_ = (IfExpression)exp;
            TypePath boolean_ = new TypePath("bool");
            boolean flag = expressionTypeCheck(if_.condition, boolean_, scope) && expressionTypeCheck(if_.then_branch, type, scope)
                           && expressionTypeCheck(if_.then_branch, type, scope);
            return flag;
        } else if (exp instanceof ArrayExpression) {
            ArrayExpression array = (ArrayExpression)exp;
            if (!(type instanceof ArrayType)) {
                has_error = true;
                return false;
            }
            ArrayType type_ = (ArrayType)type;
            for (Expression expression : array.elements) {
                if (!expressionTypeCheck(expression, type_.type, scope)) {
                    has_error = true;
                    return false;
                }
            }
            if (array.elements.size() != expressionValueCheck(type_.exp, scope)) {
                has_error = true;
                return false;
            }
        } else if (exp instanceof GroupedExpression) {
            GroupedExpression group = (GroupedExpression)exp;
            return expressionTypeCheck(group.inner, type, scope);
        } else if (exp instanceof UnderscoreExpression) {
            has_error = true;
            return false;
        }
        return true;
    }
    private boolean isMutVariable(Expression exp, Scope scope) {
        if (exp instanceof IdentifierExpression) {
            IdentifierExpression id = (IdentifierExpression)exp;
            String name = id.name;
            for (Statement sta : scope.statements) {
                if (sta instanceof LetStatement) {
                    Pattern pattern = ((LetStatement)sta).pattern;
                    while (pattern instanceof ReferencePattern) {
                        ReferencePattern pattern_ = (ReferencePattern)pattern;
                        pattern = pattern_.subPattern;
                    }
                    IdentifierPattern pattern__ = (IdentifierPattern)pattern;
                    if (pattern__.name.equals(name)) {
                        return pattern__.isMut;
                    }
                }
            }
            if (scope.parent == null) {
                has_error = true;
                return false;
            }
            return isMutVariable(exp, scope.parent);
        } else if (exp instanceof ArrIndexExpression) {
            ArrIndexExpression arr = (ArrIndexExpression)exp;
            return isMutVariable(arr.object, scope);
        } else if (exp instanceof FieldExpression) {
            FieldExpression field = (FieldExpression)exp;
            return isMutVariable(field.object, scope);
        } else if (exp instanceof GroupedExpression) {
            GroupedExpression group = (GroupedExpression)exp;
            return isMutVariable(group.inner, scope);
        } else {
            has_error = true;
            return false;
        }
    }
    private void checkStatements(Scope scope) {
        List<Statement> statements = scope.statements;
        for (Statement sta : statements) {
            if (sta instanceof LetStatement) {
                LetStatement let = (LetStatement)sta;
                if (!hasType(scope, let.type) || !expressionTypeCheck(let.initializer, let.type, scope)) {
                    has_error = true;
                    return;
                }
            } else if (sta instanceof ExpressionStatement) {
                ExpressionStatement exp = (ExpressionStatement)sta;
                Expression exp_ = exp.expression;
                if (exp_ instanceof BinaryExpression && (((BinaryExpression)exp_).operator.equals("=") ||
                    ((BinaryExpression)exp_).operator.equals("+=") || ((BinaryExpression)exp_).operator.equals("-=") || 
                    ((BinaryExpression)exp_).operator.equals("*=") || ((BinaryExpression)exp_).operator.equals("/=") || 
                    ((BinaryExpression)exp_).operator.equals("%="))) {
                    if (!isMutVariable(((BinaryExpression)exp_).left, scope)) {
                        has_error = true;
                        return;
                    }
                }
            }
        }
        for (Scope child : scope.children) {
            checkStatements(child);
        }
    }
    private boolean typeExistCheck(Scope scope) {
        for (Scope child : scope.children) {
            if (!typeExistCheck(child)) {
                has_error = true;
                return false;
            }
        }
        for (Map.Entry<String, Item> entry : scope.valueMap.entrySet()) {
            if (entry.getValue() instanceof ConstItem) {
                ConstItem constitem = (ConstItem)(entry.getValue());
                if (!hasType(scope, constitem.type)) {
                    has_error = true;
                    return false;
                }
            } else {
                FunctionItem fun = (FunctionItem)(entry.getValue());
                if (!hasType(scope, fun.return_type)) {
                    has_error = true;
                    return false;
                }
                if (!expressionTypeCheck(fun.body, fun.return_type, scope)) {
                    has_error = true;
                    return false;
                }
                for (Parameter par : fun.parameters) {
                    Type type = par.type;
                    if (!hasType(scope, type)) {
                        has_error = true;
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private boolean firstCheckScope() {
        buildScope();
        // 1. Traverse the scope tree and check the type of values in structs.
        // 2. Check if the position is legal for Trait and Impl.
        // 3. Check the types of parameters of the functions in Trait and Impl.
        if (has_error || !typeExistCheck(root) || !checkStruct(root) || !checkTrait(root) || !checkImpl(root)) {
            return false;
        }
        checkStatements(root);
        return true;
    }
}
