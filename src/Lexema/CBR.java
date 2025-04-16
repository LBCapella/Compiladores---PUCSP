package Lexema;

import java.util.List;

public class CBR {
    public static void main(String[] args) {
        // Exemplo de código fonte para testar o scanner.
        String source = "$";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.analiseTokens();
        System.out.println("Mensagem escrita:\n"+source+"\n_______________\nléxico:\n");
        for (Token token : tokens) {
            System.out.println(token);
        } 
    }
}
