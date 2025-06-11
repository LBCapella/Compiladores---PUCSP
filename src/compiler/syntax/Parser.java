package compiler.syntax;

import compiler.lexical.*;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int pos = 0;
    private Token tokenAtual;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.tokenAtual = tokens.get(pos);
    }

    private void consumir(TokenType token) {
        if (tokenAtual.getType() == token) {
            pos++;
            if (pos < tokens.size()) {
                tokenAtual = tokens.get(pos);
            }
        } 
        else {
            erro("Esperado: " + token + ", encontrado: " + tokenAtual.getType());
        }
    }

    private void erro(String mensagem) {
        throw new RuntimeException("Erro sintático na linha " + tokenAtual.getLinha() + ": " + mensagem);
    }

    public ASTNode analisar() {
        return programa();
    }

    // programa: 'programa' PARENTESES_E PARENTESES_D DELIMITADOR_E bloco DELIMITADOR_D;
    private ASTNode programa() {
        consumir(TokenType.PROGRAMA);
        consumir(TokenType.PARENTESES_E);
        consumir(TokenType.PARENTESES_D);
        consumir(TokenType.DELIMITADOR_E);
        ASTNode bloco = bloco();
        consumir(TokenType.DELIMITADOR_D);
        ASTNode noPrograma = new ASTNode("programa");
        noPrograma.adicionarFilho(bloco);
        return noPrograma;
    }

    // bloco: (declaracao | instrucao)*;
    private ASTNode bloco() {
        ASTNode noBloco = new ASTNode("bloco");
        while (ehDeclaracao() || ehInstrucao()) {
            if (ehDeclaracao()) {
                noBloco.adicionarFilho(declaracao());
            } 
            else {
                noBloco.adicionarFilho(instrucao());
            }
        }
        return noBloco;
    }

    // declaracao: tipo IDENTIFICADOR (RECEBE expressao)? PONTO_VIRGULA;
    private ASTNode declaracao() {
        ASTNode tipo = tipo();
        Token id = tokenAtual;
        consumir(TokenType.IDENTIFICADOR);
        ASTNode noDeclaracao = new ASTNode("declaracao");
        noDeclaracao.adicionarFilho(tipo);
        noDeclaracao.adicionarFilho(new ASTNode("identificador", id.getLexema()));
        if (tokenAtual.getType() == TokenType.RECEBE) {
            consumir(TokenType.RECEBE);
            noDeclaracao.adicionarFilho(expressao());
        }
        consumir(TokenType.PONTO_VIRGULA);
        return noDeclaracao;
    }

    // tipo: INTEIRO | REAL | BOOL | CARACTER | TEXTO;
    private ASTNode tipo() {
        String tipo = null;
        switch (tokenAtual.getType()) {
            case INTEIRO: tipo = "inteiro"; break;
            case REAL: tipo = "real"; break;
            case BOOL: tipo = "bool"; break;
            case CARACTER: tipo = "caracter"; break;
            case TEXTO: tipo = "texto"; break;
            default: erro("Tipo inválido");
        }
        consumir(tokenAtual.getType());
        return new ASTNode("tipo", tipo);
    }

    // instrucao: estruturaCondicional | estruturaRepeticaoEnquanto | estruturaRepeticaoPara | comandoEscreva | comandoLeia | atribuicao;
    private ASTNode instrucao() {
        switch (tokenAtual.getType()) {
            case SE: return estruturaCondicional();
            case ENQUANTO: return estruturaRepeticaoEnquanto();
            case PARA: return estruturaRepeticaoPara();
            case ESCREVA: return comandoEscreva();
            case LEIA: return comandoLeia();
            case IDENTIFICADOR: return atribuicao();
            default: erro("Instrução inválida"); return null;
        }
    }

    // estruturaCondicional: SE PARENTESES_E condicao PARENTESES_D DELIMITADOR_E bloco DELIMITADOR_D (SENAO DELIMITADOR_E bloco DELIMITADOR_D)?;
    private ASTNode estruturaCondicional() {
        consumir(TokenType.SE);
        consumir(TokenType.PARENTESES_E);
        ASTNode condicao = condicao();
        consumir(TokenType.PARENTESES_D);
        consumir(TokenType.DELIMITADOR_E);
        ASTNode blocoSe = bloco();
        consumir(TokenType.DELIMITADOR_D);
        ASTNode noCondicional = new ASTNode("se");
        noCondicional.adicionarFilho(condicao);
        noCondicional.adicionarFilho(blocoSe);
        if (tokenAtual.getType() == TokenType.SENAO) {
            consumir(TokenType.SENAO);
            consumir(TokenType.DELIMITADOR_E);
            ASTNode blocoSenao = bloco();
            consumir(TokenType.DELIMITADOR_D);
            noCondicional.adicionarFilho(blocoSenao);
        }
        return noCondicional;
    }

    // estruturaRepeticaoEnquanto: ENQUANTO PARENTESES_E condicao PARENTESES_D DELIMITADOR_E bloco DELIMITADOR_D;
    private ASTNode estruturaRepeticaoEnquanto() {
        consumir(TokenType.ENQUANTO);
        consumir(TokenType.PARENTESES_E);
        ASTNode condicao = condicao();
        consumir(TokenType.PARENTESES_D);
        consumir(TokenType.DELIMITADOR_E);
        ASTNode blocoEnquanto = bloco();
        consumir(TokenType.DELIMITADOR_D);
        ASTNode noEnquanto = new ASTNode("enquanto");
        noEnquanto.adicionarFilho(condicao);
        noEnquanto.adicionarFilho(blocoEnquanto);
        return noEnquanto;
    }

    // estruturaRepeticaoPara: PARA PARENTESES_E IDENTIFICADOR ATE expressao PARENTESES_D DELIMITADOR_E bloco DELIMITADOR_D;
    private ASTNode estruturaRepeticaoPara() {
        consumir(TokenType.PARA);
        consumir(TokenType.PARENTESES_E);
        Token id = tokenAtual;
        consumir(TokenType.IDENTIFICADOR);
        consumir(TokenType.ATE);
        ASTNode expr = expressao();
        consumir(TokenType.PARENTESES_D);
        consumir(TokenType.DELIMITADOR_E);
        ASTNode blocoPara = bloco();
        consumir(TokenType.DELIMITADOR_D);
        ASTNode noPara = new ASTNode("para");
        noPara.adicionarFilho(new ASTNode("identificador", id.getLexema()));
        noPara.adicionarFilho(expr);
        noPara.adicionarFilho(blocoPara);
        return noPara;
    }

    // comandoEscreva: ESCREVA PARENTESES_E expressao PARENTESES_D PONTO_VIRGULA;
    private ASTNode comandoEscreva() {
        consumir(TokenType.ESCREVA);
        consumir(TokenType.PARENTESES_E);
        ASTNode expr = expressao();
        consumir(TokenType.PARENTESES_D);
        consumir(TokenType.PONTO_VIRGULA);
        ASTNode noEscreva = new ASTNode("escreva");
        noEscreva.adicionarFilho(expr);
        return noEscreva;
    }

    // comandoLeia: LEIA PARENTESES_E IDENTIFICADOR PARENTESES_D PONTO_VIRGULA;
    private ASTNode comandoLeia() {
        consumir(TokenType.LEIA);
        consumir(TokenType.PARENTESES_E);
        Token id = tokenAtual;
        consumir(TokenType.IDENTIFICADOR);
        consumir(TokenType.PARENTESES_D);
        consumir(TokenType.PONTO_VIRGULA);
        ASTNode noLeia = new ASTNode("leia");
        noLeia.adicionarFilho(new ASTNode("identificador", id.getLexema()));
        return noLeia;
    }

    // atribuicao: IDENTIFICADOR RECEBE expressao PONTO_VIRGULA;
    private ASTNode atribuicao() {
        Token id = tokenAtual;
        consumir(TokenType.IDENTIFICADOR);
        consumir(TokenType.RECEBE);
        ASTNode expr = expressao();
        consumir(TokenType.PONTO_VIRGULA);
        ASTNode noAtribuicao = new ASTNode("atribuicao");
        noAtribuicao.adicionarFilho(new ASTNode("identificador", id.getLexema()));
        noAtribuicao.adicionarFilho(expr);
        return noAtribuicao;
    }

    // condicao: expressao (opRelacional expressao)?;
    private ASTNode condicao() {
        ASTNode esquerda = expressao();
        if (ehOpRelacional(tokenAtual.getType())) {
            String op = tokenAtual.getLexema();
            consumir(tokenAtual.getType());
            ASTNode direita = expressao();
            ASTNode noCondicao = new ASTNode("op_relacional", op);
            noCondicao.adicionarFilho(esquerda);
            noCondicao.adicionarFilho(direita);
            return noCondicao;
        }
        return esquerda;
    }

    // expressao: termo ((ADICAO | SUBTRACAO | MULTIPLICACAO | DIVISAO) termo)*;
    private ASTNode expressao() {
        ASTNode no = termo();
        while (tokenAtual.getType() == TokenType.ADICAO ||
               tokenAtual.getType() == TokenType.SUBTRACAO ||
               tokenAtual.getType() == TokenType.MULTIPLICACAO ||
               tokenAtual.getType() == TokenType.DIVISAO) {
            String op = tokenAtual.getLexema();
            consumir(tokenAtual.getType());
            ASTNode direito = termo();
            ASTNode novoNo = new ASTNode("op_aritmetico", op);
            novoNo.adicionarFilho(no);
            novoNo.adicionarFilho(direito);
            no = novoNo;
        }
        return no;
    }

    // termo: IDENTIFICADOR | NUMERICO | TEXTO | CARACTER | VERDADEIRO | FALSO | PARENTESES_E expressao PARENTESES_D;
    private ASTNode termo() {
        switch (tokenAtual.getType()) {
            case IDENTIFICADOR: {
                Token id = tokenAtual;
                consumir(TokenType.IDENTIFICADOR);
                return new ASTNode("identificador", id.getLexema());
            }
            case NUMERICO: {
                Token num = tokenAtual;
                consumir(TokenType.NUMERICO);
                return new ASTNode("numero", num.getLexema());
            }
            case TEXTO: {
                Token txt = tokenAtual;
                consumir(TokenType.TEXTO);
                return new ASTNode("texto", txt.getLexema());
            }
            case CARACTER: {
                Token carac = tokenAtual;
                consumir(TokenType.CARACTER);
                return new ASTNode("caracter", carac.getLexema());
            }
            case VERDADEIRO: {
                Token verdadeiro = tokenAtual;
                consumir(TokenType.VERDADEIRO);
                return new ASTNode("booleano", verdadeiro.getLexema());
            }
            case FALSO: {
                Token falso = tokenAtual;
                consumir(TokenType.FALSO);
                return new ASTNode("booleano", falso.getLexema());
            }
            case PARENTESES_E: {
                consumir(TokenType.PARENTESES_E);
                ASTNode expr = expressao();
                consumir(TokenType.PARENTESES_D);
                return expr;
            }
            default:
                erro("Termo inválido: " + tokenAtual.getLexema());
                return null;
        }
    }

    // Métodos auxiliares para decisão
    private boolean ehDeclaracao() {
        return tokenAtual.getType() == TokenType.INTEIRO ||
               tokenAtual.getType() == TokenType.REAL ||
               tokenAtual.getType() == TokenType.BOOL ||
               tokenAtual.getType() == TokenType.CARACTER ||
               tokenAtual.getType() == TokenType.TEXTO;
    }

    private boolean ehInstrucao() {
        switch (tokenAtual.getType()) {
            case SE:
            case ENQUANTO:
            case PARA:
            case ESCREVA:
            case LEIA:
            case IDENTIFICADOR:
                return true;
            default:
                return false;
        }
    }

    private boolean ehOpRelacional(TokenType tipo) {
        switch (tipo) {
            case IGUAL:
            case MAIOR_QUE:
            case MENOR_QUE:
            case MAIOR_IGUAL:
            case MENOR_IGUAL:
            case DIFERENTE:
                return true;
            default:
                return false;
        }
    }
} 