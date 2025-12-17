package rcompiler2025.src;

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        try {
            String sourceCode = "";
            try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
                if (scanner.hasNext()) {
                    sourceCode = scanner.useDelimiter("\\A").next();
                }
            }
            Lexer lexer = new Lexer(sourceCode);
            lexer.Tokenize();
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
            // llvmProgram.build();
            // llvmProgram.printAll(System.out);
            System.exit(0);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}