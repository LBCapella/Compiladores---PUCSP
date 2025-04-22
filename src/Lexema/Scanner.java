package Lexema;

import java.util.ArrayList;

public class Scanner {
    private final String cadeia;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int inicio = 0;
    private int atual = 0;
    private int linha = 1;

    public Scanner(String cadeia) {
        this.cadeia = cadeia;
    }

    public ArrayList<Token> analiseTokens() {
        tokens.clear();
        while (!finalDaCadeia()) {
            inicio = atual;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "eof", null, inicio, atual-1, linha));
        return tokens;
    }

    private void scanToken() {
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
            case '"': fita(); break;

            case '<':
                if (match('<')) {
                    addToken(TokenType.RECEBE);
                }
                else if (match('=')) {
                    addToken(TokenType.MENOR_IGUAL);
                }
                else 
                    addToken(TokenType.MENOR_QUE);
                break;
            case '>':
                addToken(match('=') ? TokenType.MAIOR_IGUAL : TokenType.MAIOR_QUE);
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
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
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
        tokens.add(new Token(type, text, null, pos_inicial, pos_final, linha));
    }

    private boolean match(char proximo) {
        if (finalDaCadeia()) return false;
        if (cadeia.charAt(atual) != proximo) return false;

        atual++;
        return true;
    }

    private char verProximo() {
        if (finalDaCadeia()) return '\0';
        return cadeia.charAt(atual);
    }

    private void fita() {
        while (verProximo() != '"' && !finalDaCadeia()) {
            avancar();
        }

        avancar();

        addToken(TokenType.STRING);
    }

    private void caracter() {
        while (verProximo() != '\'' && !finalDaCadeia()) {
            avancar();
        }

        avancar();

        addToken(TokenType.CARACTER);
    }

    private void comentario() {
        while (verProximo() != '*' && !finalDaCadeia()) {
            avancar();
        }

        avancar();

        addToken(TokenType.COMENTARIO);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(verProximo())) avancar();

        if (verProximo() == ',' && isDigit(verProximoNext())) {
            avancar();

            while (isDigit(verProximo())) avancar();
        }

        addToken(TokenType.NUMERICO);
    }

    private char verProximoNext() {
        if (atual + 1 >= cadeia.length()) return '\0';
        return cadeia.charAt(atual + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void identificador() {
        while (isAlphaNumeric(verProximo())) avancar();

        String text = cadeia.substring(inicio, atual);
        TokenType type = PalavrasReservadas.pReservadas.get(text);
        if (type == null) type = TokenType.IDENTIFICADOR;
        addToken(type);
    }
}
