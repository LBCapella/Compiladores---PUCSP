package compiler.main;

import compiler.lexical.*;
import compiler.syntax.ASTNode;
import compiler.syntax.Parser;
import java.util.List;

public class CBR {
    public static void main(String[] args) {
        // Para testar com o exemplo completo, descomente a linha abaixo:
        String codigoFonte = ExemploCompleto.getCodigoExemplo();
        //String codigoFonte = "programa() { int x; x << 10; escreva(x); }";
        try {
            Lexer lexer = new Lexer(codigoFonte);
            List<Token> tokens = lexer.analisarTokens();
            System.out.println("Tokens:");
            imprimirTokens(tokens);
            System.out.println("\nÁrvore Sintática (AST):");
            Parser parser = new Parser(tokens);
            ASTNode arvore = parser.analisar();
            imprimirAST(arvore, 0);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void imprimirTokens(List<Token> tokens) {
        int i = 0;
        for (Token t : tokens) {
            System.out.printf("  [@%d, %d-%d='%s', <%s>, linha %d]%n",
                i++, t.getPos_inicial(), t.getPos_final(), t.getLexema(), t.getType(), t.getLinha());
        }
    }

    private static void imprimirAST(ASTNode no, int nivel) {
        for (int i = 0; i < nivel; i++) System.out.print("  ");
        System.out.println(no.getTipo() + (no.getValor() != null ? ": " + no.getValor() : ""));
        for (ASTNode filho : no.getFilhos()) {
            imprimirAST(filho, nivel + 1);
        }
    }
}
