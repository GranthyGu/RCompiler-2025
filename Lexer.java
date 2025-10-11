package rcompiler2025;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

enum TokenType {
    // Identifiers and literals
    IDENTIFIER, INTERGER_LITERAL, FLOAT_LITERAL, CHAR_LITERAL, STRING_LITERAL, BOOL_LITERAL,
    // Keywords
    STRICT_KEYWORD, RESERVED_KEYWORD,
    // Operators
    OPERATOR,
    // Delimiters
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE,
    RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET,
    // Comments
    COMMENT,
    // Whitespaces
    WHITESPACE,
}

enum ErrorType {
    ERROR,
    OTHER_ERROR,
}

/**
 * The class Position.
 * @param line:   the number of line of the file.
 * @param column: the number of column of the row.
 * @param offset: the number of offset of the file.
 */
class Position {
    public Integer line;
    public Integer column;
    public Integer offset;
}

/**
 * The class Token.
 * @param token_type:    the type of the token.
 * @param value:         the value of the string.
 * @param real_value:    the real value of the number, to decimal type.
 *                       if the token is not the number, do nothing.
 * @param pos_begin:     the position of the beginning of the token.
 * @param pos_end:       the position of the end of the token.
 * @param suffix:        the suffix of the string(if have), else, return null.
 */
class Token {
    public TokenType token_type;
    public String value;
    public Object real_value;
    public Position pos_begin = new Position();
    public Position pos_end = new Position();
    public String suffix;
    public boolean is_error = false;
    public ErrorType error_type;
}

class Index {
    public Integer index = 0;
}

/**
 * The class Lexer.
 * TODO
 */
public class Lexer {
    static final private List<String> StrictKeywordList = new ArrayList<>();
    static final private List<String> ReservedKeywordList = new ArrayList<>();
    static final private List<Character> operators = new ArrayList<>();
    static final private List<Integer> whitespaces = new ArrayList<>();
    private String SrcCode;
    public List<Token> TokenList;
    public List<Token> ErrorList;
    private Index cur_index = new Index();
    private Index cur_row = new Index();
    private Index cur_column = new Index();
    

    /**
     * The function Lexer(String file_name), the construct function of Lexer.
     * @param file_name: the name of the rust program, ready to compile.
     * @operation initialize the lists of keywords.
     */
    public Lexer(String file_name) {
        try {
            byte[] content = Files.readAllBytes(Paths.get(file_name));
            SrcCode = new String(content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] strict_keywords = {"as", "break", "const", "continue", "crate", "else",
                                    "enum", "false", "fn", "for", "if", "impl", 
                                    "in", "let", "loop", "match", "mod", "move", "mut", 
                                    "ref", "return", "self", "Self", "static", "struct",
                                    "super", "trait", "true", "unsafe", "use", "where", "while", 
                                    "dyn"};
        String[] reserved_keywords = {"abstract", "become", "box", "do", "final", "macro", 
                                    "override", "priv", "typeof", "unsized", "virtual", 
                                    "yield", "try", "gen"};
        Character[] operators_ = {'+', '-', '*', '/', '%', '^', '!', '&', '|', '<', '>', '=', 
                                '@', '_', '.', ',', ';', ':', '#', '$', '?', '~'};
        Integer[] white_space = {0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x0020, 0x0085, 0x200E, 0x200F, 0x2028, 0x2029};
        for(String s : strict_keywords) {StrictKeywordList.add(s);}
        for(String s : reserved_keywords) {ReservedKeywordList.add(s);}
        for(Character ch : operators_) {operators.add(ch);}
        for(Integer i : white_space) {whitespaces.add(i);}
        TokenList = new ArrayList<>();
        ErrorList = new ArrayList<>();
    }

    private void NextIndex(Index index) {
        if (SrcCode.charAt(index.index) == '\n') {
            cur_row.index++;
            cur_column.index = 0;
        } else {
            cur_column.index++;
        }
        index.index++;
        return;
    }
    private String GetIdentifier(Index index) {
        String temp = "";
        if (Character.isUnicodeIdentifierStart(SrcCode.charAt(index.index)) || SrcCode.charAt(index.index) == '_') {
            temp += SrcCode.charAt(index.index);
            NextIndex(index);
            if (SrcCode.charAt(index.index) == '#' && SrcCode.charAt(index.index - 1) == 'r') {
                temp += '#';
                NextIndex(index);
            }
            while (index.index < SrcCode.length() && Character.isUnicodeIdentifierPart(SrcCode.charAt(index.index))) {
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
            }
        } else {
            while (index.index < SrcCode.length() && Character.isUnicodeIdentifierPart(SrcCode.charAt(index.index))) {
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
            }
        }
        return temp;
    }
    private Token ScanNumber(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        res.token_type = TokenType.INTERGER_LITERAL;
        String temp = "";
        temp += SrcCode.charAt(index.index);
        boolean is_dec = false;    // To judge decimal.
        if (SrcCode.charAt(index.index) == '0') {
            NextIndex(index);
            Character next_char = SrcCode.charAt(index.index);
            boolean has_digit = false;      // has_digit = true until the first element != '_'.
            if (next_char == 'b') {
                temp += next_char;
                NextIndex(index);
                while (index.index < SrcCode.length() && ((SrcCode.charAt(index.index) - '0' >= 0 &&
                    SrcCode.charAt(index.index) - '0' <= 1) || SrcCode.charAt(index.index) == '_')) {
                    if (SrcCode.charAt(index.index) != '_') {
                        temp += SrcCode.charAt(index.index);
                    }
                    if (SrcCode.charAt(index.index) - '0' >= 0 && SrcCode.charAt(index.index) - '0' <= 1) {
                        has_digit = true;
                    }
                    NextIndex(index);
                }
            } else if (next_char == 'o') {
                temp += next_char;
                NextIndex(index);
                while (index.index < SrcCode.length() && ((SrcCode.charAt(index.index) - '0' >= 0 &&
                    SrcCode.charAt(index.index) - '0' <= 7) || SrcCode.charAt(index.index) == '_')) {
                    if (SrcCode.charAt(index.index) != '_') {
                        temp += SrcCode.charAt(index.index);
                    }
                    if (SrcCode.charAt(index.index) - '0' >= 0 && SrcCode.charAt(index.index) - '0' <= 7) {
                        has_digit = true;
                    }
                    NextIndex(index);
                }
            } else if (next_char == 'x') {
                temp += next_char;
                NextIndex(index);
                while (index.index < SrcCode.length() && ((SrcCode.charAt(index.index) - '0' >= 0 &&
                    SrcCode.charAt(index.index) - '0' <= 9) || (SrcCode.charAt(index.index) - 'A' >= 0 &&
                    SrcCode.charAt(index.index) - 'A' <= 5) || (SrcCode.charAt(index.index) - 'a' >= 0 &&
                    SrcCode.charAt(index.index) - 'a' <= 5) || SrcCode.charAt(index.index) == '_')) {
                    if (SrcCode.charAt(index.index) != '_') {
                        temp += SrcCode.charAt(index.index);
                    }
                    if (SrcCode.charAt(index.index) != '_') {
                        has_digit = true;
                    }
                    NextIndex(index);
                }
            } else if ((next_char - '0' >= 0 && next_char - '0' <= 9) || next_char == '_') {
                temp += next_char;
                NextIndex(index);
                while (index.index < SrcCode.length() && ((SrcCode.charAt(index.index) - '0' >= 0 &&
                    SrcCode.charAt(index.index) - '0' <= 9) || SrcCode.charAt(index.index) == '_')) {
                    if (SrcCode.charAt(index.index) != '_') {
                        temp += SrcCode.charAt(index.index);
                    }
                    NextIndex(index);
                }
                is_dec = true;
            } else {
                is_dec = true;   // Means that it's 0 only.
            }
            if (!has_digit && !is_dec) {
                res.is_error = true;
                res.error_type = ErrorType.ERROR;   // NO DIGIT
            }
        } else if (SrcCode.charAt(index.index) - '0' >= 1 && SrcCode.charAt(index.index) - '0' <= 9) {
            NextIndex(index);
            while (index.index < SrcCode.length() && ((SrcCode.charAt(index.index) - '0' >= 0 &&
                SrcCode.charAt(index.index) - '0' <= 9) || SrcCode.charAt(index.index) == '_')) {
                if (SrcCode.charAt(index.index) != '_') {
                    temp += SrcCode.charAt(index.index);
                }
                NextIndex(index);
            }
            is_dec = true;
        } else {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;   // INVALID_NUMBER
        }
        String Suffix = GetIdentifier(index);
        res.value = temp;
        res.suffix = Suffix;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        // Scan the suffix.
        if (!Suffix.equals("u8") && !Suffix.equals("i8") &&
            !Suffix.equals("u16") && !Suffix.equals("i16") &&
            !Suffix.equals("u32") && !Suffix.equals("i32") &&
            !Suffix.equals("u64") && !Suffix.equals("i64") &&
            !Suffix.equals("u128") && !Suffix.equals("i128") &&
            !Suffix.equals("usize") && !Suffix.equals("isize") &&
            !Suffix.equals("")) {
            
            res.is_error = true;
            res.error_type = ErrorType.ERROR; // INVALID_SUFFIX
        }
        if (res.is_error) {
            return res;
        }        
        // Get the true value by the suffix's type.
        switch (Suffix) {
            case "":
                res.real_value = Integer.parseInt(temp);
                break;
            case "u8":
            case "i8":
                res.real_value = Byte.parseByte(temp);
                break;
            case "u16":
            case "i16":
                res.real_value = Short.parseShort(temp);
                break;
            case "u32":
            case "i32":
                res.real_value = Integer.parseInt(temp);
                break;
            case "u64":
            case "i64":
                res.real_value = Long.parseLong(temp);
                break;
            case "u128":
            case "i128":
                res.real_value = new java.math.BigInteger(temp);
                break;
            case "usize":
            case "isize":
                res.real_value = Long.parseLong(temp);
                break;
            default:
                throw new IllegalArgumentException("Unsupported suffix: " + Suffix);
        }
        return res;
    }
    private Token ScanOperators(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        res.token_type = TokenType.OPERATOR;
        String temp = "";
        switch (SrcCode.charAt(index.index)) {
            case '+':
            case '*':
            case '/':
            case '%':
            case '^':
            case '!':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '=') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '-':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '=' || SrcCode.charAt(index.index) == '>') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '&':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '=' || SrcCode.charAt(index.index) == '&') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '|':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '=' || SrcCode.charAt(index.index) == '|') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '<':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '=' || SrcCode.charAt(index.index) == '<' || SrcCode.charAt(index.index) == '-') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        if (operators.contains(SrcCode.charAt(index.index)) && SrcCode.charAt(index.index - 1) == '<') {
                            if (SrcCode.charAt(index.index) == '=') {
                                temp += SrcCode.charAt(index.index);
                                NextIndex(index);
                            } else {
                                temp += SrcCode.charAt(index.index);
                                NextIndex(index);
                                res.is_error = true;
                                res.error_type = ErrorType.ERROR;
                            }
                        }
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '>':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '=' || SrcCode.charAt(index.index) == '>') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        if (operators.contains(SrcCode.charAt(index.index)) && SrcCode.charAt(index.index - 1) == '>') {
                            if (SrcCode.charAt(index.index) == '=') {
                                temp += SrcCode.charAt(index.index);
                                NextIndex(index);
                            } else {
                                temp += SrcCode.charAt(index.index);
                                NextIndex(index);
                                res.is_error = true;
                                res.error_type = ErrorType.ERROR;
                            }
                        }
                    } else if (SrcCode.charAt(index.index) != ';') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index)) && SrcCode.charAt(index.index) != ';') {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '=':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '=' || SrcCode.charAt(index.index) == '>') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '.':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == '.') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        if (operators.contains(SrcCode.charAt(index.index))) {
                            if (SrcCode.charAt(index.index) == '=' || SrcCode.charAt(index.index) == '.') {
                                temp += SrcCode.charAt(index.index);
                                NextIndex(index);
                            } else {
                                temp += SrcCode.charAt(index.index);
                                NextIndex(index);
                                res.is_error = true;
                                res.error_type = ErrorType.ERROR;
                            }
                        }
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case ':':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                if (operators.contains(SrcCode.charAt(index.index))) {
                    if (SrcCode.charAt(index.index) == ':') {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                    } else {
                        temp += SrcCode.charAt(index.index);
                        NextIndex(index);
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                    }
                }
                while (operators.contains(SrcCode.charAt(index.index))) {
                    temp += SrcCode.charAt(index.index);
                    NextIndex(index);
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;   // WRONG_OPERATOR
                }
                break;
            case '#':
            case '$':
            case '~':
            case '?':
            case ';':
            case ',':
            case '@':
            case '_':
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
                break;
            default:
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
                break;
        }
        res.value = temp;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanDelimiters(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        String temp = "";
        switch (SrcCode.charAt(index.index)) {
            case '(':
                temp += SrcCode.charAt(index.index);
                res.token_type = TokenType.LEFT_PAREN;
                NextIndex(index);
                break;
            case ')':
                temp += SrcCode.charAt(index.index);
                res.token_type = TokenType.RIGHT_PAREN;
                NextIndex(index);
                break;
            case '[':
                temp += SrcCode.charAt(index.index);
                res.token_type = TokenType.LEFT_BRACKET;
                NextIndex(index);
                break;
            case ']':
                temp += SrcCode.charAt(index.index);
                res.token_type = TokenType.RIGHT_BRACKET;
                NextIndex(index);
                break;
            case '{':
                temp += SrcCode.charAt(index.index);
                res.token_type = TokenType.LEFT_BRACE;
                NextIndex(index);
                break;
            case '}':
                temp += SrcCode.charAt(index.index);
                res.token_type = TokenType.RIGHT_BRACE;
                NextIndex(index);
                break;
            default:
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
                break;
        }
        res.value = temp;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanChar(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        String temp = "";
        res.token_type = TokenType.CHAR_LITERAL;
        char ch1 = SrcCode.charAt(index.index);
        NextIndex(index);
        if (ch1 != '\'') {
            res.error_type = ErrorType.ERROR;
            res.is_error = true;
        }
        char ch2 = SrcCode.charAt(index.index);
        NextIndex(index);
        temp += ch1;
        temp += ch2;
        if (ch2 == '\'' || ch2 == '\n' || ch2 == '\r' || ch2 == '\t') {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;
        } else if (ch2 == '\\') {
            if (index.index >= SrcCode.length()) {
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
            }
            char ch3 = SrcCode.charAt(index.index);
            NextIndex(index);
            switch (ch3) {
                case '\'':
                    res.real_value = '\'';
                    temp += ch3;
                    break;
                case '\"':
                    res.real_value = '\"';
                    temp += ch3;
                    break;
                case 'n':
                    res.real_value = '\n';
                    temp += ch3;
                    break;
                case 'r':
                    res.real_value = '\r';
                    temp += ch3;
                    break;
                case 't':
                    res.real_value = '\t';
                    temp += ch3;
                    break;
                case '\\':
                    res.real_value = '\\';
                    temp += ch3;
                    break;
                case '0':
                    res.real_value = '\0';
                    temp += ch3;
                    break;
                case 'x':
                    if (index.index + 1 >= SrcCode.length()) {
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;
                    }
                    char hex1 = SrcCode.charAt(index.index);
                    char hex2 = SrcCode.charAt(index.index + 1);
                    if (!(hex1 >= '0' && hex1 <= '9') || !((hex2 >= '0' && hex2 <= '9') || (hex2 >= 'a' && hex2 <= 'f') || (hex2 >= 'A' && hex2 <= 'F'))) {
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;
                        if (hex1 == '\'') {
                            temp += hex1;
                            NextIndex(index);
                            res.value = temp;
                            res.pos_end.column = cur_column.index;
                            res.pos_end.line = cur_row.index;
                            res.pos_end.offset = index.index;
                            return res;
                        } else if (hex2 == '\'') {
                            temp += hex1;
                            temp += hex2;
                            NextIndex(index);
                            NextIndex(index);
                            res.value = temp;
                            res.pos_end.column = cur_column.index;
                            res.pos_end.line = cur_row.index;
                            res.pos_end.offset = index.index;
                            return res;
                        }
                    }
                    temp += hex1;
                    temp += hex2;
                    String num = "";
                    num += hex1 + hex2;
                    int num_ = Integer.parseInt(num, 16);
                    res.real_value = (char)num_;
                    NextIndex(index);
                    NextIndex(index);
                    break;
                default:
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;
            }
        } else {
            res.real_value = (char)ch2;
        }
        if (index.index >= SrcCode.length() || SrcCode.charAt(index.index) != '\'') {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;
        } else {
            temp += SrcCode.charAt(index.index);
            NextIndex(index);
        }
        res.value = temp;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token Scan_String(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        String temp = "";
        String realValue = "";
        res.token_type = TokenType.STRING_LITERAL;
        char ch1 = SrcCode.charAt(index.index);
        NextIndex(index);
        if (ch1 != '\"') {
            res.error_type = ErrorType.ERROR;
            res.is_error = true;
            res.value = temp;
            res.pos_end.column = cur_column.index;
            res.pos_end.line = cur_row.index;
            res.pos_end.offset = index.index;
            return res;
        }
        temp += ch1;
        while (index.index < SrcCode.length()) {
            char ch = SrcCode.charAt(index.index);
            if (ch == '\"') {
                temp += ch;
                NextIndex(index);
                break;
            }
            if (ch == '\r') {
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
                temp += ch;
                NextIndex(index);
                continue;
            }
            if (ch == '\\') {
                temp += ch;
                NextIndex(index);
                if (index.index >= SrcCode.length()) {
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;
                    break;
                }
                char ch2 = SrcCode.charAt(index.index);
                temp += ch2;
                switch (ch2) {
                    case '\"':
                        realValue += '\"';
                        NextIndex(index);
                        break;
                    case '\'':
                        realValue += '\'';
                        NextIndex(index);
                        break;
                    case 'n':
                        realValue += '\n';
                        NextIndex(index);
                        break;
                    case 'r':
                        realValue += '\r';
                        NextIndex(index);
                        break;
                    case 't':
                        realValue += '\t';
                        NextIndex(index);
                        break;
                    case '\\':
                        realValue += '\\';
                        NextIndex(index);
                        break;
                    case '0':
                        realValue += '\0';
                        NextIndex(index);
                        break;
                    case 'x':
                        NextIndex(index);
                        if (index.index + 1 >= SrcCode.length()) {
                            res.is_error = true;
                            res.error_type = ErrorType.ERROR;
                            break;
                        }
                        char hex1 = SrcCode.charAt(index.index);
                        char hex2 = SrcCode.charAt(index.index + 1);
                        temp += hex1;
                        temp += hex2;
                        if (!(hex1 >= '0' && hex1 <= '9') || !((hex2 >= '0' && hex2 <= '9') ||
                            (hex2 >= 'a' && hex2 <= 'f') || (hex2 >= 'A' && hex2 <= 'F'))) {
                            res.is_error = true;
                            res.error_type = ErrorType.ERROR;
                        } else {
                            String hexStr = "" + hex1 + hex2;
                            int value = Integer.parseInt(hexStr, 16);
                            if (value > 0x7F) {
                                res.is_error = true;
                                res.error_type = ErrorType.ERROR;
                            } else {
                                realValue += (char)value;
                            }
                        }
                        NextIndex(index);
                        NextIndex(index);
                        break;
                    case '\n':
                        NextIndex(index);
                        temp += ch2;
                        break;
                    default:
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;
                        realValue += ch2;
                        NextIndex(index);
                        break;
                }
            } else {
                temp += ch;
                realValue += ch;
                NextIndex(index);
            }
        }
        if (index.index >= SrcCode.length() || !temp.endsWith("\"")) {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;
        }
        res.value = temp;
        res.real_value = realValue;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanRawString(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        res.token_type = TokenType.STRING_LITERAL;
        String temp = "";
        temp += SrcCode.charAt(index.index);
        NextIndex(index);
        int num = 0;
        while (index.index < SrcCode.length() && SrcCode.charAt(index.index) == '#') {
            temp += '#';
            NextIndex(index);
            num++;
        }
        if (index.index >= SrcCode.length() || SrcCode.charAt(index.index) != '\"') {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;
            res.value = temp;
            res.pos_end.column = cur_column.index;
            res.pos_end.line = cur_row.index;
            res.pos_end.offset = index.index;
            return res;
        }
        temp += '\"';
        NextIndex(index);
        String content = "";
        while (index.index < SrcCode.length()) {
            char ch = SrcCode.charAt(index.index);
            if (ch == '\r') {
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
                temp += ch;
                content += ch;
                NextIndex(index);
                continue;
            }
            if (ch == '\"') {
                boolean isEnd = true;
                int checkIndex = index.index + 1;
                for (int i = 0; i < num; i++) {
                    if (checkIndex >= SrcCode.length() || SrcCode.charAt(checkIndex) != '#') {
                        isEnd = false;
                        break;
                    }
                    checkIndex++;
                }
                if (isEnd) {
                    temp += '\"';
                    NextIndex(index);
                    for (int i = 0; i < num; i++) {
                        temp += '#';
                        NextIndex(index);
                    }
                    break;
                }
            }
            temp += ch;
            content += ch;
            NextIndex(index);
        }
        res.value = temp;
        res.real_value = content;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanCString(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        res.token_type = TokenType.STRING_LITERAL;
        String temp = "";
        String realValue = "";
        temp += SrcCode.charAt(index.index);
        NextIndex(index);
        if (index.index >= SrcCode.length() || SrcCode.charAt(index.index) != '\"') {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;
            res.value = temp;
            res.pos_end.column = cur_column.index;
            res.pos_end.line = cur_row.index;
            res.pos_end.offset = index.index;
            return res;
        }
        temp += '\"';
        NextIndex(index);
        while (index.index < SrcCode.length()) {
            char ch = SrcCode.charAt(index.index);
            if (ch == '\"') {
                temp += ch;
                NextIndex(index);
                break;
            }
            if (ch == '\r' || ch == '\0') {
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
                temp += ch;
                NextIndex(index);
                continue;
            }
            if (ch == '\\') {
                temp += ch;
                NextIndex(index);
                if (index.index >= SrcCode.length()) {
                    res.is_error = true;
                    res.error_type = ErrorType.ERROR;
                    break;
                }
                char ch2 = SrcCode.charAt(index.index);
                temp += ch2;
                switch (ch2) {
                    case '\"': 
                        realValue += '\"';
                        NextIndex(index);
                        break;
                    case '\\':
                        realValue += (char)0x5C;
                        NextIndex(index);
                        break;
                    case 'n':
                        realValue += (char)0x0A;
                        NextIndex(index);
                        break;
                    case 'r':
                        realValue += (char)0x0D;
                        NextIndex(index);
                        break;
                    case 't':
                        realValue += (char)0x09;
                        NextIndex(index);
                        break;
                    case 'x':
                        NextIndex(index);
                        if (index.index + 1 >= SrcCode.length()) {
                            res.is_error = true;
                            res.error_type = ErrorType.ERROR;
                            break;
                        }
                        char hex1 = SrcCode.charAt(index.index);
                        char hex2 = SrcCode.charAt(index.index + 1);
                        temp += hex1;
                        temp += hex2;
                        if (!(hex1 >= '0' && hex1 <= '9') || !((hex2 >= '0' && hex2 <= '9') ||
                            (hex2 >= 'a' && hex2 <= 'f') || (hex2 >= 'A' && hex2 <= 'F'))) {
                            res.is_error = true;
                            res.error_type = ErrorType.ERROR;
                        } else {
                            String hexStr = "" + hex1 + hex2;
                            int value = Integer.parseInt(hexStr, 16);
                            realValue += (char)value;
                        }
                        NextIndex(index);
                        NextIndex(index);
                        break;
                    case '\n':
                        NextIndex(index);
                        break;
                    default:
                        res.is_error = true;
                        res.error_type = ErrorType.ERROR;
                        realValue += ch2;
                        NextIndex(index);
                        break;
                }
            } else {
                realValue += ch;
                temp += ch;
                NextIndex(index);
            }
        }
        if (index.index >= SrcCode.length() || !temp.endsWith("\"")) {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;
        }
        realValue += '\0';
        res.value = temp;
        res.real_value = realValue;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanRawCString(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        res.token_type = TokenType.STRING_LITERAL;
        String temp = "";
        temp += SrcCode.charAt(index.index);
        NextIndex(index);
        temp += SrcCode.charAt(index.index);
        NextIndex(index);
        int hashCount = 0;
        while (index.index < SrcCode.length() && SrcCode.charAt(index.index) == '#' && hashCount < 256) {
            temp += '#';
            NextIndex(index);
            hashCount++;
        }
        if (index.index >= SrcCode.length() || SrcCode.charAt(index.index) != '\"') {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;
            res.value = temp;
            res.pos_end.column = cur_column.index;
            res.pos_end.line = cur_row.index;
            res.pos_end.offset = index.index;
            return res;
        }
        temp += '\"';
        NextIndex(index);
        String content = "";
        while (index.index < SrcCode.length()) {
            char ch = SrcCode.charAt(index.index);
            if (ch == '\0' || ch == '\r') {
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
                temp += ch;
                content += ch;
                NextIndex(index);
                continue;
            }
            if (ch == '\"') {
                boolean isEnd = true;
                int checkIndex = index.index + 1;
                for (int i = 0; i < hashCount; i++) {
                    if (checkIndex >= SrcCode.length() || SrcCode.charAt(checkIndex) != '#') {
                        isEnd = false;
                        break;
                    }
                    checkIndex++;
                }
                if (isEnd) {
                    temp += '\"';
                    NextIndex(index);
                    for (int i = 0; i < hashCount; i++) {
                        temp += '#';
                        NextIndex(index);
                    }
                    break;
                }
            }
            content += ch;
            temp += ch;
            NextIndex(index);
        }
        content += '\0';
        res.value = temp;
        res.real_value = content;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanString(Index index) {
        Token res = new Token();
        char firstChar = SrcCode.charAt(index.index);
        if (firstChar == 'c') {
            if (index.index + 1 < SrcCode.length()) {
                char secondChar = SrcCode.charAt(index.index + 1);
                if (secondChar == '\"') {
                    return ScanCString(index);
                } else if (secondChar == 'r') {
                    return ScanRawCString(index);
                }
            }
        } else if (firstChar == 'r') {
            return ScanRawString(index);
        } else if (firstChar == '\"') {
            return Scan_String(index);
        }
        res.is_error = true;
        res.error_type = ErrorType.ERROR;
        return res;
    }
    private Token ScanComment(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        String temp = "";
        res.token_type = TokenType.COMMENT;
        char ch1 = SrcCode.charAt(index.index);
        NextIndex(index);
        char ch2 = SrcCode.charAt(index.index);
        NextIndex(index);
        if (ch1 == '/' && ch2 == '/') {
            temp += ch1 + ch2;
            while (index.index < SrcCode.length() && SrcCode.charAt(index.index) != '\n') {
                temp += SrcCode.charAt(index.index);
                NextIndex(index);
            }
        } else if (ch1 == '/' && ch2 == '*') {
            temp += ch1 + ch2;
            boolean flag1 = false;
            boolean flag2 = false;
            while (index.index < SrcCode.length() && !flag2) {
                temp += SrcCode.charAt(index.index);
                if (SrcCode.charAt(index.index) == '*') {
                    flag1 = true;
                } else if (flag1 && SrcCode.charAt(index.index) == '/') {
                    flag2 = true;
                } else if (SrcCode.charAt(index.index) != '*') {
                    flag1 = false;
                }
                NextIndex(index);
            }
            if (!flag2) {
                res.is_error = true;
                res.error_type = ErrorType.ERROR;
            }
        } else {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;   // INVALID COMMENT
        }
        res.value = temp;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanWhitespace(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        String temp = "";
        res.token_type = TokenType.WHITESPACE;
        int val = SrcCode.codePointAt(index.index);
        if (whitespaces.contains(val)) {
            temp += SrcCode.charAt(index.index);
            NextIndex(index);
        } else {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;   // INVALID_WHITESPACE
        }
        res.value = temp;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    private Token ScanIdentifierOrKeyword(Index index) {
        Token res = new Token();
        res.pos_begin.column = cur_column.index;
        res.pos_begin.line = cur_row.index;
        res.pos_begin.offset = cur_index.index;
        String str = GetIdentifier(index);
        if (!Character.isUnicodeIdentifierStart(str.charAt(0)) && str.charAt(0) != '_') {
            res.is_error = true;
            res.error_type = ErrorType.ERROR;   // INVALID IDENTIFIER
        }
        if (StrictKeywordList.contains(str)) {
            res.token_type = TokenType.STRICT_KEYWORD;
            if (str.equals("true") || str.equals("false")) {
                res.token_type = TokenType.BOOL_LITERAL;
            }
        } else if (ReservedKeywordList.contains(str)) {
            res.token_type = TokenType.RESERVED_KEYWORD;
        } else {
            res.token_type = TokenType.IDENTIFIER;
        }
        if (str.equals("r#crate") || str.equals("r#self") || str.equals("r#super") || 
            str.equals("r#Self") || str.equals("r#_")) {
            res.error_type = ErrorType.ERROR;    // INVALID IDENTIFIER
            res.is_error = true;
        }
        res.value = str;
        res.pos_end.column = cur_column.index;
        res.pos_end.line = cur_row.index;
        res.pos_end.offset = index.index;
        return res;
    }
    public void Tokenize() {
        while (cur_index.index < SrcCode.length()) {
            Token res = new Token();
            char ch = SrcCode.charAt(cur_index.index);
            if (ch >= '0' && ch <= '9') {
                res = ScanNumber(cur_index);
            } else if (operators.contains(ch) && ch != '/' && ch != '_') {
                res = ScanOperators(cur_index);
            } else if (ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}') {
                res = ScanDelimiters(cur_index);
            } else if (ch == '\'') {
                res = ScanChar(cur_index);
            } else if (whitespaces.contains((int)ch)) {
                res = ScanWhitespace(cur_index);
            } else if (ch == '/') {
                if (cur_index.index + 1 < SrcCode.length() && SrcCode.charAt(cur_index.index + 1) != '/' && SrcCode.charAt(cur_index.index + 1) != '*') {
                    res = ScanOperators(cur_index);
                } else {
                    res = ScanComment(cur_index);
                }
            } else if (ch == '_') {
                if (Character.isUnicodeIdentifierPart(SrcCode.charAt(cur_index.index + 1))) {
                    res = ScanIdentifierOrKeyword(cur_index);
                } else {
                    res = ScanOperators(cur_index);
                }
            } else if (ch == 'r') {
                boolean isRawString = false;
                if (cur_index.index + 1 < SrcCode.length()) {
                    char nextChar = SrcCode.charAt(cur_index.index + 1);
                    if (nextChar == '\"') {
                        isRawString = true;
                    } else if (nextChar == '#') {
                        int tempIndex = cur_index.index + 2;
                        while (tempIndex < SrcCode.length() && SrcCode.charAt(tempIndex) == '#') {
                            tempIndex++;
                        }
                        if (tempIndex < SrcCode.length() && SrcCode.charAt(tempIndex) == '\"') {
                            isRawString = true;
                        }
                    }
                }
                if (isRawString) {
                    res = ScanString(cur_index);
                } else {
                    res = ScanIdentifierOrKeyword(cur_index);
                }
                
            } else if (ch == 'c') {
                boolean isCString = false;
                if (cur_index.index + 1 < SrcCode.length()) {
                    char nextChar = SrcCode.charAt(cur_index.index + 1);
                    if (nextChar == '\"') {
                        isCString = true;
                    } else if (nextChar == 'r' && cur_index.index + 2 < SrcCode.length()) {
                        char thirdChar = SrcCode.charAt(cur_index.index + 2);
                        if (thirdChar == '\"') {
                            isCString = true;
                        } else if (thirdChar == '#') {
                            int tempIndex = cur_index.index + 3;
                            while (tempIndex < SrcCode.length() && SrcCode.charAt(tempIndex) == '#') {
                                tempIndex++;
                            }
                            if (tempIndex < SrcCode.length() && SrcCode.charAt(tempIndex) == '\"') {
                                isCString = true;
                            }
                        }
                    }
                }
                if (isCString) {
                    res = ScanString(cur_index);
                } else {
                    res = ScanIdentifierOrKeyword(cur_index);
                }
            } else if (ch == '\"') {
                res = ScanString(cur_index);
            } else {
                res = ScanIdentifierOrKeyword(cur_index);
            }
            TokenList.add(res);
            if (res.is_error) {
                ErrorList.add(res);
                break;
            }
        }
    }
}