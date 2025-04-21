package Lexema;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CBR {
    public static void main(String[] args) {
        // Exemplo de código fonte para testar o scanner.
        String source = "programa { int num << x; escreva(num);}";
        Scanner scanner = new Scanner(source);
        ArrayList<Token> tokens = scanner.analiseTokens();
        System.out.println("Mensagem escrita:\n"+source+"\n_______________\nléxico:\n");
        for (int i = 0; i < tokens.size(); i++){
            Token t = tokens.get(i);
            System.out.printf("[@%d,%d-%d='%s',<%s>,%d]\n", i+1, t.getPos_inicial(),
            t.getPos_final(), t.getLexema(), t.getType().toString(), t.getLinha());
        }
    }
}
