package Lexema;

import java.util.ArrayList;

/**
 * Analisador sintático para a linguagem CBR
 */
public class AnalisadorSintatico {
    private ArrayList<Token> tokens;    
    private int posicaoAtual;            
    private int limite;                  
    private Token tokenAtual;          
    private No raiz;                    
    private boolean temErro;            

    public AnalisadorSintatico(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.posicaoAtual = 0;
        this.limite = tokens.size();
        this.temErro = false;
    }

    /**
     * Inicia a análise sintática
     * @return Árvore sintática resultante
     */
    public Arvore analisar() {
        raiz = new No("Programa"); 
        Arvore arvore = new Arvore(raiz); 
        
        if (limite > 0) { 
            tokenAtual = tokens.get(posicaoAtual);
            programa(); 
        }
        
        if (!temErro) { 
            System.out.println("Análise Sintática concluída com sucesso!");
        }
        
        return arvore;
    }

    /**
     * Analisa a estrutura principal do programa
     */
    private void programa() {
        
        if (verificarToken(TokenType.PROGRAMA)) {
            No noProgramaDecl = raiz.acrescentarFilho("Declaração de Programa"); 
            noProgramaDecl.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            
            if (verificarToken(TokenType.DELIMITADOR_E)) {
                noProgramaDecl.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
                
                No blocoPrograma = raiz.acrescentarFilho("Bloco de Programa");
                bloco(blocoPrograma);
                
                if (verificarToken(TokenType.DELIMITADOR_D)) {
                    noProgramaDecl.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                    avancarToken();
                } 
                else {
                    erro("Esperado '}' para fechar o bloco do programa");
                }
            } 
            else {
                erro("Esperado '{' para iniciar o bloco do programa");
            }
        } 
        else {
            erro("Programa deve começar com a palavra-chave 'programa'");
        }
    }

    /**
     * Analisa um bloco de código
     * @param noBloco Nó que representa o bloco na árvore
     */
    private void bloco(No noBloco) {
        while (posicaoAtual < limite && !verificarTokenSemAvancar(TokenType.DELIMITADOR_D)) {
            
            if (verificarTokenSemAvancar(TokenType.INTEIRO) || 
                verificarTokenSemAvancar(TokenType.REAL) || 
                verificarTokenSemAvancar(TokenType.BOOL) || 
                verificarTokenSemAvancar(TokenType.CARACTER_TYPE) || 
                verificarTokenSemAvancar(TokenType.TEXTO)) {
                declaracaoVariavel(noBloco);
            } 
            else if (verificarTokenSemAvancar(TokenType.SE)) {
                estruturaCondicional(noBloco);
            } 
            else if (verificarTokenSemAvancar(TokenType.ENQUANTO)) {
                estruturaRepeticaoEnquanto(noBloco);
            } 
            else if (verificarTokenSemAvancar(TokenType.PARA)) {
                estruturaRepeticaoPara(noBloco);
            } 
            else if (verificarTokenSemAvancar(TokenType.ESCREVA)) {
                comandoEscreva(noBloco);
            } 
            else if (verificarTokenSemAvancar(TokenType.LEIA)) {
                comandoLeia(noBloco);
            } 
            else if (verificarTokenSemAvancar(TokenType.IDENTIFICADOR)) {
                atribuicao(noBloco);
            } 
            else {
                erro("Comando não reconhecido: " + tokenAtual.getLexema());
                avancarToken(); 
            }
        }
    }

    /**
     * Analisa uma declaração de variável
     * @param noPai Nó pai na árvore
     */
    private void declaracaoVariavel(No noPai) {
        No noDeclaracao = noPai.acrescentarFilho("Declaração de Variável");
        
        // Tipo da variável
        No noTipo = noDeclaracao.acrescentarFilho("Tipo");
        noTipo.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
        avancarToken();
        
        // Nome da variável
        if (verificarToken(TokenType.IDENTIFICADOR)) {
            No noId = noDeclaracao.acrescentarFilho("Identificador");
            noId.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            // Verificar se há atribuição / Caso não tenha, é necessário ao menos ';'
            if (verificarToken(TokenType.RECEBE)) {
                noDeclaracao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
                
                expressao(noDeclaracao);
            }
            
            // Verificar ponto e vírgula
            if (verificarToken(TokenType.PONTO_VIRGULA)) {
                noDeclaracao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
            } 
            else {
                erro("Esperado ';' após declaração de variável");
            }
        } 
        else {
            erro("Esperado identificador após tipo");
        }
    }

    /**
     * Analisa uma estrutura condicional (if/else)
     * @param noPai Nó pai na árvore
     */
    private void estruturaCondicional(No noPai) {
        No noCondicional = noPai.acrescentarFilho("Estrutura Condicional");
        
        // Palavra-chave 'se'
        noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
        avancarToken();
        
        // Parêntese de abertura
        if (verificarToken(TokenType.PARENTESES_E)) {
            noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            // Condição
            No noCondicao = noCondicional.acrescentarFilho("Condição");
            condicao(noCondicao);
            
            // Parêntese de fechamento
            if (verificarToken(TokenType.PARENTESES_D)) {
                noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
                
                // Bloco do if
                if (verificarToken(TokenType.DELIMITADOR_E)) {
                    noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                    avancarToken();
                    
                    No noBloco = noCondicional.acrescentarFilho("Bloco SE");
                    bloco(noBloco);
                    
                    if (verificarToken(TokenType.DELIMITADOR_D)) {
                        noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                        avancarToken();
                        
                        // Verificar se há um 'senao'
                        if (verificarTokenSemAvancar(TokenType.SENAO)) {
                            noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                            avancarToken();
                            
                            if (verificarToken(TokenType.DELIMITADOR_E)) {
                                noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                                avancarToken();
                                
                                No noBlocoElse = noCondicional.acrescentarFilho("Bloco SENAO");
                                bloco(noBlocoElse);
                                
                                if (verificarToken(TokenType.DELIMITADOR_D)) {
                                    noCondicional.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                                    avancarToken();
                                } 
                                else {
                                    erro("Esperado '}' para fechar o bloco 'senao'");
                                }
                            } 
                            else {
                                erro("Esperado '{' para iniciar o bloco 'senao'");
                            }
                        }
                    } 
                    else {
                        erro("Esperado '}' para fechar o bloco 'se'");
                    }
                } 
                else {
                    erro("Esperado '{' para iniciar o bloco 'se'");
                }
            } 
            else {
                erro("Esperado ')' após a condição");
            }
        } 
        else {
            erro("Esperado '(' após 'se'");
        }
    }

    /**
     * Analisa uma condição
     * @param noPai Nó pai na árvore
     */
    private void condicao(No noPai) {
        expressao(noPai);
        
        if (verificarTokenSemAvancar(TokenType.IGUAL) || 
            verificarTokenSemAvancar(TokenType.MAIOR_QUE) || 
            verificarTokenSemAvancar(TokenType.MENOR_QUE) || 
            verificarTokenSemAvancar(TokenType.MAIOR_IGUAL) || 
            verificarTokenSemAvancar(TokenType.MENOR_IGUAL) || 
            verificarTokenSemAvancar(TokenType.DIFERENTE)) {
            
            noPai.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            expressao(noPai);
        }
    }

    /**
     * Analisa uma expressão
     * @param noPai Nó pai na árvore
     */
    private void expressao(No noPai) {
        No noExpressao = noPai.acrescentarFilho("Expressão");
        termo(noExpressao);
        
        while (verificarTokenSemAvancar(TokenType.ADICAO) || 
               verificarTokenSemAvancar(TokenType.SUBTRACAO) || 
               verificarTokenSemAvancar(TokenType.MULTIPLICACAO) || 
               verificarTokenSemAvancar(TokenType.DIVISAO)) {
            
            noExpressao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            termo(noExpressao);
        }
    }

    /**
     * Analisa um termo
     * @param noPai Nó pai na árvore
     */
    private void termo(No noPai) {
        No noTermo = noPai.acrescentarFilho("Termo");
        
        if (verificarToken(TokenType.IDENTIFICADOR)) {
            noTermo.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
        } 
        else if (verificarToken(TokenType.NUMERICO)) {
            noTermo.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
        } 
        else if (verificarToken(TokenType.TEXTO)) {
            noTermo.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
        } 
        else if (verificarToken(TokenType.VERDADEIRO) || verificarToken(TokenType.FALSO)) {
            noTermo.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
        } 
        else if (verificarToken(TokenType.PARENTESES_E)) {
            noTermo.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            expressao(noTermo);
            
            if (verificarToken(TokenType.PARENTESES_D)) {
                noTermo.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
            } 
            else {
                erro("Esperado ')' para fechar expressão");
            }
        } 
        else {
            erro("Termo inválido: " + tokenAtual.getLexema());
        }
    }

    /**
     * Analisa uma estrutura de repetição 'enquanto'
     * @param noPai Nó pai na árvore
     */
    private void estruturaRepeticaoEnquanto(No noPai) {
        No noRepeticao = noPai.acrescentarFilho("Estrutura de Repetição Enquanto");
        
        // Palavra-chave 'enquanto'
        noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
        avancarToken();
        
        // Parêntese de abertura
        if (verificarToken(TokenType.PARENTESES_E)) {
            noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            // Condição
            No noCondicao = noRepeticao.acrescentarFilho("Condição");
            condicao(noCondicao);
            
            // Parêntese de fechamento
            if (verificarToken(TokenType.PARENTESES_D)) {
                noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
                
                // Bloco do enquanto
                if (verificarToken(TokenType.DELIMITADOR_E)) {
                    noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                    avancarToken();
                    
                    No noBloco = noRepeticao.acrescentarFilho("Bloco Enquanto");
                    bloco(noBloco);
                    
                    if (verificarToken(TokenType.DELIMITADOR_D)) {
                        noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                        avancarToken();
                    } 
                    else {
                        erro("Esperado '}' para fechar o bloco 'enquanto'");
                    }
                } 
                else {
                    erro("Esperado '{' para iniciar o bloco 'enquanto'");
                }
            } 
            else {
                erro("Esperado ')' após a condição");
            }
        } 
        else {
            erro("Esperado '(' após 'enquanto'");
        }
    }

    /**
     * Analisa uma estrutura de repetição 'para'
     * @param noPai Nó pai na árvore
     */
    private void estruturaRepeticaoPara(No noPai) {
        No noRepeticao = noPai.acrescentarFilho("Estrutura de Repetição Para");
        
        // Palavra-chave 'para'
        noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
        avancarToken();
        
        // Parêntese de abertura
        if (verificarToken(TokenType.PARENTESES_E)) {
            noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            // Inicialização
            if (verificarTokenSemAvancar(TokenType.INTEIRO) || 
                verificarTokenSemAvancar(TokenType.REAL)) {
                declaracaoVariavel(noRepeticao);
            } 
            else if (verificarTokenSemAvancar(TokenType.IDENTIFICADOR)) {
                atribuicao(noRepeticao);
            } 
            else {
                erro("Esperada inicialização no 'para'");
            }
            
            // Condição
            No noCondicao = noRepeticao.acrescentarFilho("Condição");
            condicao(noCondicao);
            
            if (verificarToken(TokenType.PONTO_VIRGULA)) {
                noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
                
                // Incremento
                if (verificarToken(TokenType.IDENTIFICADOR)) {
                    atribuicao(noRepeticao);
                } 
                else {
                    erro("Esperado incremento no 'para'");
                }
                
                // Parêntese de fechamento
                if (verificarToken(TokenType.PARENTESES_D)) {
                    noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                    avancarToken();
                    
                    // Bloco do para
                    if (verificarToken(TokenType.DELIMITADOR_E)) {
                        noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                        avancarToken();
                        
                        No noBloco = noRepeticao.acrescentarFilho("Bloco Para");
                        bloco(noBloco);
                        
                        if (verificarToken(TokenType.DELIMITADOR_D)) {
                            noRepeticao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                            avancarToken();
                        } 
                        else {
                            erro("Esperado '}' para fechar o bloco 'para'");
                        }
                    } 
                    else {
                        erro("Esperado '{' para iniciar o bloco 'para'");
                    }
                } 
                else {
                    erro("Esperado ')' após incremento");
                }
            } 
            else {
                erro("Esperado ';' após condição");
            }
        } 
        else {
            erro("Esperado '(' após 'para'");
        }
    }

    /**
     * Analisa um comando 'escreva'
     * @param noPai Nó pai na árvore
     */
    private void comandoEscreva(No noPai) {
        No noComando = noPai.acrescentarFilho("Comando Escreva");
        
        // Palavra-chave 'escreva'
        noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
        avancarToken();
        
        // Parêntese de abertura
        if (verificarToken(TokenType.PARENTESES_E)) {
            noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            // Expressão
            expressao(noComando);
            
            // Parêntese de fechamento
            if (verificarToken(TokenType.PARENTESES_D)) {
                noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
                
                // Ponto e vírgula
                if (verificarToken(TokenType.PONTO_VIRGULA)) {
                    noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                    avancarToken();
                } 
                else {
                    erro("Esperado ';' após comando 'escreva'");
                }
            } 
            else {
                erro("Esperado ')' após expressão");
            }
        } 
        else {
            erro("Esperado '(' após 'escreva'");
        }
    }

    /**
     * Analisa um comando 'leia'
     * @param noPai Nó pai na árvore
     */
    private void comandoLeia(No noPai) {
        No noComando = noPai.acrescentarFilho("Comando Leia");
        
        // Palavra-chave 'leia'
        noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
        avancarToken();
        
        // Parêntese de abertura
        if (verificarToken(TokenType.PARENTESES_E)) {
            noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            // Identificador
            if (verificarToken(TokenType.IDENTIFICADOR)) {
                No noId = noComando.acrescentarFilho("Identificador");
                noId.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
                
                // Parêntese de fechamento
                if (verificarToken(TokenType.PARENTESES_D)) {
                    noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                    avancarToken();
                    
                    // Ponto e vírgula
                    if (verificarToken(TokenType.PONTO_VIRGULA)) {
                        noComando.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                        avancarToken();
                    } 
                    else {
                        erro("Esperado ';' após comando 'leia'");
                    }
                } 
                else {
                    erro("Esperado ')' após identificador");
                }
            } 
            else {
                erro("Esperado identificador após '('");
            }
        } 
        else {
            erro("Esperado '(' após 'leia'");
        }
    }

    /**
     * Analisa uma atribuição
     * @param noPai Nó pai na árvore
     */
    private void atribuicao(No noPai) {
        No noAtribuicao = noPai.acrescentarFilho("Atribuição");
        
        // Identificador
        No noId = noAtribuicao.acrescentarFilho("Identificador");
        noId.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
        avancarToken();
        
        // Operador de atribuição
        if (verificarToken(TokenType.RECEBE)) {
            noAtribuicao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
            avancarToken();
            
            // Expressão
            expressao(noAtribuicao);
            
            // Ponto e vírgula
            if (verificarToken(TokenType.PONTO_VIRGULA)) {
                noAtribuicao.acrescentarFilho(tokenAtual.getType() + ": " + tokenAtual.getLexema());
                avancarToken();
            } 
            else {
                erro("Esperado ';' após atribuição");
            }
        } 
        else {
            erro("Esperado '<<' para atribuição");
        }
    }

    /**
     * Verifica se o token atual é do tipo esperado e avança para o próximo token
     * @param tipo Tipo de token esperado
     * @return true se o token atual é do tipo esperado, false caso contrário
     */
    private boolean verificarToken(TokenType tipo) {
        if (tokenAtual.getType() == tipo) {
            return true;
        }
        return false;
    }

    /**
     * Verifica se o token atual é do tipo esperado sem avançar para o próximo token
     * @param tipo Tipo de token esperado
     * @return true se o token atual é do tipo esperado, false caso contrário
     */
    private boolean verificarTokenSemAvancar(TokenType tipo) {
        return tokenAtual.getType() == tipo;
    }

    /**
     * Avança para o próximo token
     */
    private void avancarToken() {
        if (posicaoAtual < limite - 1) {
            posicaoAtual++;
            tokenAtual = tokens.get(posicaoAtual);
        }
    }

    /**
     * Reporta um erro sintático
     * @param mensagem Mensagem de erro
     */
    private void erro(String mensagem) {
        temErro = true;
        System.err.println("Erro sintático (linha " + tokenAtual.getLinha() + "): " + mensagem);
    }
} 