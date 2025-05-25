package Lexema;

import java.util.HashMap;
import java.util.Map;

public class PalavrasReservadas {

    public static final Map<String, TokenType> pReservadas;
    static {
        pReservadas = new HashMap<>();
        pReservadas.put("programa",  TokenType.PROGRAMA);
        pReservadas.put("e",    TokenType.LOGICO_E);
        pReservadas.put("senao",   TokenType.SENAO);
        pReservadas.put("falso",  TokenType.FALSO);
        pReservadas.put("para",    TokenType.PARA);
        pReservadas.put("se",     TokenType.SE);
        pReservadas.put("ou",     TokenType.LOGICO_OU);
        pReservadas.put("nao",     TokenType.NAO);
        pReservadas.put("escreva",  TokenType.ESCREVA);
        pReservadas.put("leia",  TokenType.LEIA);
        pReservadas.put("return", TokenType.RETURN);
        pReservadas.put("verdadeiro",   TokenType.VERDADEIRO);
        pReservadas.put("int",    TokenType.INTEIRO);
        pReservadas.put("real", TokenType.REAL);
        pReservadas.put("enquanto",  TokenType.ENQUANTO);
        pReservadas.put("faca",  TokenType.FACA_ENQUANTO);
        pReservadas.put("carac",  TokenType.CARACTER_TYPE);
        pReservadas.put("bool",  TokenType.BOOL);
        pReservadas.put("texto",  TokenType.TEXTO);
        pReservadas.put("ate",  TokenType.ATE);
    }
}
