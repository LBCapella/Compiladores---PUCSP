package Cbr;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um nó na árvore sintática
 */
public class No {
    private String valor;
    private List<No> filhos;

    public No(String valor) {
        this.valor = valor;
        this.filhos = new ArrayList<>();
    }

    public String getValor() {
        return valor;
    }

    public List<No> getFilhos() {
        return filhos;
    }

    public No acrescentarFilho(String valor) {
        No filho = new No(valor);
        filhos.add(filho);
        return filho;
    }

    public void acrescentarFilho(No filho) {
        filhos.add(filho);
    }

    public void imprimirArvore() {
        imprimirArvore(this, 0);
    }

    private void imprimirArvore(No no, int nivel) {
        StringBuilder espacos = new StringBuilder();
        for (int i = 0; i < nivel; i++) {
            espacos.append("  ");
        }

        System.out.println(espacos + no.valor);

        for (No filho : no.filhos) {
            imprimirArvore(filho, nivel + 1);
        }
    }
} 