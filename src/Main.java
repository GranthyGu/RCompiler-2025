package rcompiler2025.src;

public class Main {
    
    public static void main(String[] args) {
        String inputFileName = "in.txt";
        if (args.length > 0) {
            inputFileName = args[0];
        }
        if (args.length > 0) {
            inputFileName = args[0];
        }
        try {
            Lexer lexer = new Lexer(inputFileName);
            lexer.Tokenize();
            if (!lexer.ErrorList.isEmpty()) {
                return;
            }
            Parser parser = new Parser(lexer.TokenList);
            parser.Parse();
            
            Semantics sem = new Semantics(parser);
            boolean flag = sem.semanticCheck();
            if (flag) {
                System.out.println("\nYou MADE it!!!");
            } else {
                System.out.println("\nFUCK YOU! YOU MADE SOME MISTAKES!!!");
            }

            IR llvmProgram = new IR(parser, sem);
            llvmProgram.build();
                        String outputFileName = "output.ll";
            if (inputFileName.contains(".")) {
                outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".ll";
            } else {
                outputFileName = inputFileName + ".ll";
            }
            try (java.io.PrintStream fileOut = new java.io.PrintStream(new java.io.FileOutputStream(outputFileName))) {
                llvmProgram.printAll(fileOut); 
            } catch (java.io.IOException e) {
                System.err.println("Failed: " + e.getMessage());
            }
        } catch (Exception e) {
            return;
        }
    }
}