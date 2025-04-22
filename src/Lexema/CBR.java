package Lexema;

import java.util.ArrayList;

public class CBR {
    public static void main(String[] args) {
        // Exemplo de código fonte para testar o scanner.
        String source = "programa { int num << x; escreva(num);}";
        Lexer scanner = new Lexer(source);
        ArrayList<Token> tokens = scanner.analiseTokens();
        System.out.println("Mensagem escrita:\n"+source+"\n_______________\nléxico:\n");
        for (int i = 0; i < tokens.size(); i++){
            Token t = tokens.get(i);
            System.out.printf("[@%d,%d-%d='%s',<%s>,%d]\n", i+1, t.getPos_inicial(),
            t.getPos_final(), t.getLexema(), t.getType().toString(), t.getLinha());
        }
    }
}
