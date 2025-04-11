package Lexema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String cadeia;
    private final List<Token> tokens = new ArrayList<>();
    private int inicio = 0;
    private int atual = 0;

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("e",    TokenType.LOGICO_E);
        keywords.put("senao",   TokenType.SENAO);
        keywords.put("falso",  TokenType.FALSO);
        keywords.put("para",    TokenType.PARA);
        keywords.put("se",     TokenType.SE);
        keywords.put("ou",     TokenType.LOGICO_OU);
        keywords.put("escreva",  TokenType.ESCREVA);
        keywords.put("return", TokenType.RETURN);
        keywords.put("verdadeiro",   TokenType.VERDADEIRO);
        keywords.put("int",    TokenType.INTEIRO);
        keywords.put("real", TokenType.REAL);
        keywords.put("enquanto",  TokenType.ENQUANTO);
        keywords.put("faca",  TokenType.FACA_ENQUANTO);
    }

    public Scanner(String cadeia) {
        this.cadeia = cadeia;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            inicio = atual;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '-': addToken(TokenType.SUBTRACAO); break;
            case '+': addToken(TokenType.ADICAO); break;
            case ';': addToken(TokenType.PONTO_FINAL); break;
            case '*': addToken(TokenType.MULTIPLICACAO); break;
            case '=': addToken(TokenType.IGUAL); break;

            // Tratamento de tokens com dois caracteres.
            case '<':
                addToken(match('<') ? TokenType.RECEBE : TokenType.MENOR_QUE);
                break;
            case '>':
                addToken(match('=') ? TokenType.MAIOR_IGUAL : TokenType.MAIOR_QUE);
                break;

            case '/':
                if (match('/')) {
                    // Comentário de uma linha: descarta até o final da linha.
                    while (peek() != '\n' && !isAtEnd()) advance();
                    addToken(TokenType.COMENTARIO);
                } else {
                    addToken(TokenType.DIVISAO);
                }
                break;

            // Ignora espaços em branco, tabs e retornos de carro.
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    System.err.println("Caractere inesperado: " + c);
                }
                break;
        }
    }

    private boolean isAtEnd() {
        return atual >= cadeia.length();
    }

    private char advance() {
        char c = cadeia.charAt(atual);
        atual++;
        return c;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = cadeia.substring(inicio, atual);
        tokens.add(new Token(type, text, literal));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (cadeia.charAt(atual) != expected) return false;

        atual++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return cadeia.charAt(atual);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            advance();
        }

        // Consumir a aspa final.
        advance();

        // Pega o conteúdo da string sem as aspas.
        String value = cadeia.substring(inicio + 1, atual - 1);
        addToken(TokenType.STRING, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Se encontrar um ponto seguido de um dígito, trata de números com parte fracionária.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consome o ponto.
            advance();

            while (isDigit(peek())) advance();
        }

        String numberString = cadeia.substring(inicio, atual);
        addToken(TokenType.NUMERICO, Double.parseDouble(numberString));
    }

    private char peekNext() {
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

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = cadeia.substring(inicio, atual);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFICADOR;
        addToken(type);
    }
}
