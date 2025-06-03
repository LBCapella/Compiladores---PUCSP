package compiler.syntax;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    private String tipo;
    private String valor;
    private List<ASTNode> filhos = new ArrayList<>();

    public ASTNode(String tipo) {
        this.tipo = tipo;
    }

    public ASTNode(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public void adicionarFilho(ASTNode filho) {
        filhos.add(filho);
    }

    public String getTipo() {
        return tipo;
    }

    public Object getValor() {
        return valor;
    }

    public List<ASTNode> getFilhos() {
        return filhos;
    }
} 