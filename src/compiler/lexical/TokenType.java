package compiler.lexical;

public enum TokenType {
    PROGRAMA,

    // Tokens de um único caractere.
    PONTO_VIRGULA,APOSTROFE,VIRGULA,

    // Literais.
    IDENTIFICADOR, TEXTO, NUMERICO, VARIAVEL,

    // Palavras reservadas.
    SENAO, FALSO, PARA, ATE, SE,
    ESCREVA, RETURN,REAL, VERDADEIRO, INTEIRO, ENQUANTO, FACA_ENQUANTO,CARACTER,
    BOOL, LEIA,

    //Aritméticos e símbolos
    ADICAO, SUBTRACAO, DIVISAO, MULTIPLICACAO, RESTO,PARENTESES_D,PARENTESES_E,
    DELIMITADOR_D,DELIMITADOR_E,

    //Lógicos
    LOGICO_OU, LOGICO_E, LOGICO_NAO, NAO,

    //Relacionais
    IGUAL,MAIOR_QUE, MENOR_QUE,DIFERENTE,RECEBE,MAIOR_IGUAL,MENOR_IGUAL,

    //COMENTARIO
    COMENTARIO,

    EOF
}
