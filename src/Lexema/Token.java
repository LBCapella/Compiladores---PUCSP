package Lexema;

public class Token {
    final TokenType type;
    final String lexema;
    final Object valor;

    public Token(TokenType type, String lexema, Object valor) {
        this.type = type;
        this.lexema = lexema;
        this.valor = valor;
    }

    public String toString() {
        return type + " " + lexema;
    }
}
