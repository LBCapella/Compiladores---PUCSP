package compiler.lexical;

import java.util.ArrayList;

public class Lexer {
    private final String cadeia;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int inicio = 0;
    private int atual = 0;
    private int linha = 1;

    public Lexer(String cadeia) {
        this.cadeia = cadeia;
    }

    public ArrayList<Token> analisarTokens() {
        tokens.clear();
        while (!finalDaCadeia()) {
            inicio = atual;
            scanearToken();
        }

        tokens.add(new Token(TokenType.EOF, "eof", inicio, atual-1, linha));
        return tokens;
    }

    private void scanearToken() {
        char c = avancar();
        switch (c) {
            case '-': addToken(TokenType.SUBTRACAO); break;
            case '+': addToken(TokenType.ADICAO); break;
            case '/': addToken(TokenType.DIVISAO); break;
            case ';': addToken(TokenType.PONTO_VIRGULA); break;
            case '.': addToken(TokenType.MULTIPLICACAO); break;
            case '=': addToken(TokenType.IGUAL); break;
            case '(': addToken(TokenType.PARENTESES_E); break;
            case ')': addToken(TokenType.PARENTESES_D); break;
            case '{': addToken(TokenType.DELIMITADOR_E); break;
            case '}': addToken(TokenType.DELIMITADOR_D); break;
            case '%': addToken(TokenType.RESTO); break;
            case '\'': caracter();break;
            case '*': comentario();break;
            case '"': texto(); break;

            case '<':
                if (proxIgual('<')) {
                    addToken(TokenType.RECEBE);
                }
                else if (proxIgual('=')) {
                    addToken(TokenType.MENOR_IGUAL);
                }
                else 
                    addToken(TokenType.MENOR_QUE);
                break;
            case '>':
                addToken(proxIgual('=') ? TokenType.MAIOR_IGUAL : TokenType.MAIOR_QUE);
                break;

            case ' ':
                break;
            case '\r':
                break;
            case '\t':
                break;
            case '\n':
                linha++;
                break;

            default:
                if (ehDigito(c)) {
                    numero();
                } else if (ehAlphanumerico(c)) {
                    identificador();
                } else {
                    System.err.println("Caractere inesperado: " + c);
                }
                break;
        }
    }

    private boolean finalDaCadeia() {
        return atual >= cadeia.length();
    }

    private char avancar() {
        char c = cadeia.charAt(atual);
        atual++;
        return c;
    }

    private void addToken(TokenType type) {
        String text = cadeia.substring(inicio, atual);
        int pos_inicial = inicio;
        int pos_final = atual-1;
        tokens.add(new Token(type, text, pos_inicial, pos_final, linha));
    }

    private boolean proxIgual(char proximo) {
        if (finalDaCadeia()) return false;
        if (cadeia.charAt(atual) != proximo) return false;

        atual++;
        return true;
    }

    private char verProximo() {
        if (finalDaCadeia()) return '\0';
        return cadeia.charAt(atual);
    }

    private void texto() {
        while (verProximo() != '"' && !finalDaCadeia()) {
            avancar();
        }

        avancar();

        addToken(TokenType.TEXTO);
    }

    private void caracter() {
        while (verProximo() != '\'' && !finalDaCadeia()) {
            avancar();
        }

        avancar();

        addToken(TokenType.CARACTER);
    }

    private void comentario() {
        while (verProximo() != '\n' && !finalDaCadeia()) {
            avancar();
        }


        addToken(TokenType.COMENTARIO);
    }

    private boolean ehDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private void numero() {
        while (ehDigito(verProximo())) avancar();

        if (verProximo() == ',' && ehDigito(verProximoNext())) {
            avancar();

            while (ehDigito(verProximo())) avancar();
        }

        addToken(TokenType.NUMERICO);
    }

    private char verProximoNext() {
        if (atual + 1 >= cadeia.length()) return '\0';
        return cadeia.charAt(atual + 1);
    }

    private boolean ehAlphanumerico(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean ehAlphanumericoNumeric(char c) {
        return ehAlphanumerico(c) || ehDigito(c);
    }

    private void identificador() {
        while (ehAlphanumericoNumeric(verProximo())) avancar();

        String text = cadeia.substring(inicio, atual);
        TokenType type = PalavrasReservadas.pReservadas.get(text);
        if (type == null) type = TokenType.IDENTIFICADOR;
        addToken(type);
    }
}
