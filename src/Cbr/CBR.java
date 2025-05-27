package Cbr;

import java.util.ArrayList;

public class CBR {
    public static void main(String[] args) {
        // Código fonte para testar o analisador
        String source = ExemploCompleto.getCodigoExemplo();
        
        // Análise léxica
        Lexer scanner = new Lexer(source);
        ArrayList<Token> tokens = scanner.analisarTokens();
        
        System.out.println("Código fonte:\n" + source + "\n_______________\nAnálise Léxica:\n");
        for (int i = 0; i < tokens.size(); i++){
            Token t = tokens.get(i);
            System.out.printf("[@%d,%d-%d='%s',<%s>,%d]\n", i+1, t.getPos_inicial(),
            t.getPos_final(), t.getLexema(), t.getType().toString(), t.getLinha());
        }
        
        // Análise sintática
        System.out.println("\n_______________\nAnálise Sintática:\n");
        AnalisadorSintatico analisador = new AnalisadorSintatico(tokens);
        Arvore arvore = analisador.analisar();
        
        // Imprime a árvore sintática
        System.out.println("\n_______________\nÁrvore Sintática:\n");
        arvore.imprimir();
    }
}
