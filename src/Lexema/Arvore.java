package Lexema;

/**
 * Classe que representa a árvore sintática
 */
public class Arvore {
    private No raiz;

    public Arvore(No raiz) {
        this.raiz = raiz;
    }

    public No getRaiz() {
        return raiz;
    }

    public void imprimir() {
        raiz.imprimirArvore();
    }
} 