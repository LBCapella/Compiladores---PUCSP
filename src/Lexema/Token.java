package Lexema;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;

    public Token(TokenType type, String lexeme, Object literal) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
    }

    @Override
    public String toString() {
        return type + " " + lexeme;
    }
}
