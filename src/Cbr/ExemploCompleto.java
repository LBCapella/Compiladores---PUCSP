package Cbr;

public class ExemploCompleto {
    public static String getCodigoExemplo() {
        return "programa () {\n" +
               "    int contador << 0;\n" +
               "    int limite << 10;\n" +
               "    texto mensagem << \"Contador:\";\n" +
               "    \n" +
               "    se (contador < limite) {\n" +
               "        escreva(mensagem);\n" +
               "        escreva(contador);\n" +
               "    } senao {\n" +
               "        escreva(\"Limite atingido\");\n" +
               "    }\n" +
               "    \n" +
               "    enquanto (contador < limite) {\n" +
               "        contador << contador + 1;\n" +
               "        escreva(contador);\n" +
               "    }\n" +
               "    \n" +
               "     int i << 0;"+
               "    \n" +
               "     int n << 5;"+
               "    \n" +
               "    para (i ate 5) {\n" +
               "        escreva(i);\n" +
               "    }\n" +
               "    \n" +
               "    bool flag << verdadeiro;\n" +
               "    se (flag) {\n" +
               "        escreva(\"Flag é verdadeiro\");\n" +
               "    }\n" +
               "    \n" +
               "    leia(contador);\n" +
               "    escreva(\"Novo valor do contador:\");\n" +
               "    escreva(contador);\n" +
               "    escreva('E');\n" +
               "    carac eita;\n" +
               "}";
    }
} 