package Lexema;

import java.util.List;

public class Lox {
    public static void main(String[] args) {
        // Exemplo de código fonte para testar o scanner.
        String source = "inteiro x << 42;\n//escreva x"
                +"\ndasdawd";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        System.out.println("Mensagem escrita:\n"+source+"\n_______________\nléxico:\n");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}