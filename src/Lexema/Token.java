package Lexema;

public class Token {
    final TokenType type;
    final String lexema;
    final Object valor;
    final int pos_inicial;
    final int pos_final;
    final int linha;

    public Token(TokenType type, String lexema, Object valor, int pos_inicial, int pos_final, int linha) {
        this.type = type;
        this.lexema = lexema;
        this.valor = valor;
        this.pos_inicial = pos_inicial;
        this.pos_final = pos_final;
        this.linha = linha;
    }

    public TokenType getType()   { return type; }
    public String getLexema()    { return lexema; }
    public int getPos_inicial()        { return pos_inicial; }
    public int getPos_final()          { return pos_final; }
    public int getLinha()         { return linha; }

    public String toString() {
        return type + " " + lexema;
    }
}
