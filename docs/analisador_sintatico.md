# Documentação do Analisador Sintático CBR

## Índice
1. [Visão Geral](#visão-geral)
2. [Estrutura de Dados](#estrutura-de-dados)
   - [Árvore Sintática](#árvore-sintática)
   - [Nó](#nó)
3. [Analisador Sintático](#analisador-sintático)
   - [Estrutura e Componentes](#estrutura-e-componentes)
   - [Algoritmo de Análise](#algoritmo-de-análise)
   - [Métodos Principais](#métodos-principais)
4. [Gramática da Linguagem CBR](#gramática-da-linguagem-cbr)
5. [Exemplos de Uso](#exemplos-de-uso)

## Visão Geral

O Analisador Sintático é um componente crucial do compilador CBR, responsável por verificar se a sequência de tokens gerada pelo analisador léxico segue a gramática definida para a linguagem. Ele constrói uma árvore sintática que representa a estrutura hierárquica do programa fonte.

## Estrutura de Dados

### Árvore Sintática

A classe `Arvore` representa a estrutura de dados que armazena a análise sintática do programa. Ela mantém uma referência para o nó raiz e fornece métodos para manipulação e visualização da árvore.

### Nó

A classe `No` representa um elemento individual na árvore sintática. Cada nó contém:
- Um valor (String) que descreve o tipo de elemento sintático
- Uma lista de filhos que representam subelementos
- Métodos para manipulação da estrutura da árvore

#### Métodos do Nó

##### getValor()
- Retorna o valor do nó
- Complexidade: O(1)
```java
public String getValor() {
    return valor;
}
```

##### getFilhos()
- Retorna a lista de filhos do nó
- Útil para navegação e manipulação da árvore
- Complexidade: O(1)
```java
public List<No> getFilhos() {
    return filhos;
}
```

##### acrescentarFilho(String valor)
- Cria e adiciona um novo nó filho com o valor especificado
- Retorna o nó filho criado para permitir encadeamento
- Complexidade: O(1)
```java
public No acrescentarFilho(String valor) {
    No filho = new No(valor);
    filhos.add(filho);
    return filho;
}
```

##### acrescentarFilho(No filho)
- Adiciona um nó filho existente à lista de filhos
- Complexidade: O(1)
```java
public void acrescentarFilho(No filho) {
    filhos.add(filho);
}
```

##### imprimirArvore()
- Imprime a árvore sintática em formato hierárquico
- Utiliza indentação para representar os níveis da árvore
- Complexidade: O(n), onde n é o número de nós na árvore
```java
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
```

### Analisador Sintático

O `AnalisadorSintatico` é composto por:
- Lista de tokens a serem analisados
- Posição atual na análise
- Token atual sendo processado
- Nó raiz da árvore sintática
- Flag de erro

#### Métodos Principais

##### analisar()
- Inicia a análise sintática do programa
- Cria a árvore sintática
- Retorna a árvore resultante
- Complexidade: O(n), onde n é o número de tokens
```java
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
```

##### programa()
- Analisa a estrutura principal do programa
- Verifica a palavra-chave 'programa'
- Processa o bloco principal
- Complexidade: O(n), onde n é o número de tokens
```java
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
```

##### bloco()
- Analisa um bloco de código
- Processa declarações, comandos e estruturas de controle
- Complexidade: O(n), onde n é o número de tokens no bloco
```java
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
```

##### declaracaoVariavel()
- Analisa declarações de variáveis
- Verifica tipo, identificador e atribuição opcional
- Complexidade: O(1)
```java
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
        
        // Verificar se há atribuição
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
```

##### expressao()
- Analisa expressões aritméticas e lógicas
- Processa termos e operadores
- Complexidade: O(n), onde n é o tamanho da expressão
```java
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
```

##### termo()
- Analisa termos da expressão
- Processa identificadores, números, textos e expressões entre parênteses
- Complexidade: O(1)
```java
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
```

## Gramática da Linguagem CBR

A gramática da linguagem CBR pode ser definida em formato ANTLR da seguinte forma:

```antlr
grammar CBR;

// Regra inicial
programa: 'programa' DELIMITADOR_E bloco DELIMITADOR_D;

// Bloco de código
bloco: (declaracao | comando)*;

// Declarações
declaracao: tipo IDENTIFICADOR (RECEBE expressao)? PONTO_VIRGULA;

// Tipos de dados
tipo: INTEIRO | REAL | BOOL | CARACTER | TEXTO;

// Comandos
comando: 
    | estruturaCondicional
    | estruturaRepeticaoEnquanto
    | estruturaRepeticaoPara
    | comandoEscreva
    | comandoLeia
    | atribuicao;

// Estrutura condicional
estruturaCondicional: 
    SE PARENTESES_E condicao PARENTESES_D 
    DELIMITADOR_E bloco DELIMITADOR_D 
    (SENAO DELIMITADOR_E bloco DELIMITADOR_D)?;

// Estruturas de repetição
estruturaRepeticaoEnquanto: 
    ENQUANTO PARENTESES_E condicao PARENTESES_D 
    DELIMITADOR_E bloco DELIMITADOR_D;

estruturaRepeticaoPara: 
    PARA PARENTESES_E IDENTIFICADOR ATE expressao PARENTESES_D 
    DELIMITADOR_E bloco DELIMITADOR_D;

// Comandos de entrada/saída
comandoEscreva: 
    ESCREVA PARENTESES_E expressao PARENTESES_D PONTO_VIRGULA;

comandoLeia: 
    LEIA PARENTESES_E IDENTIFICADOR PARENTESES_D PONTO_VIRGULA;

// Atribuição
atribuicao: 
    IDENTIFICADOR RECEBE expressao PONTO_VIRGULA;

// Condição
condicao: 
    expressao (opRelacional expressao)?;

// Expressões
expressao: 
    termo ((ADICAO | SUBTRACAO | MULTIPLICACAO | DIVISAO) termo)*;

termo: 
    | IDENTIFICADOR
    | NUMERICO
    | TEXTO
    | CARACTER
    | VERDADEIRO
    | FALSO
    | PARENTESES_E expressao PARENTESES_D;

// Operadores relacionais
opRelacional: 
    IGUAL | MAIOR_QUE | MENOR_QUE | MAIOR_IGUAL | MENOR_IGUAL | DIFERENTE;

// Tokens
PROGRAMA: 'programa';
SE: 'se';
SENAO: 'senao';
ENQUANTO: 'enquanto';
PARA: 'para';
ATE: 'ate';
ESCREVA: 'escreva';
LEIA: 'leia';
INTEIRO: 'int';
REAL: 'real';
BOOL: 'bool';
CARACTER: 'carac';
TEXTO: 'texto';
VERDADEIRO: 'verdadeiro';
FALSO: 'falso';

// Operadores
ADICAO: '+';
SUBTRACAO: '-';
MULTIPLICACAO: '.';
DIVISAO: '/';
RECEBE: '<<';
IGUAL: '=';
MAIOR_QUE: '>';
MENOR_QUE: '<';
MAIOR_IGUAL: '>=';
MENOR_IGUAL: '<=';
DIFERENTE: '<>';

// Delimitadores
PARENTESES_E: '(';
PARENTESES_D: ')';
DELIMITADOR_E: '{';
DELIMITADOR_D: '}';
PONTO_VIRGULA: ';';

// Identificadores e literais
IDENTIFICADOR: [a-zA-Z_][a-zA-Z0-9_]*;
NUMERICO: [0-9]+ (',' [0-9]+)?;
TEXTO: '"' ~["\r\n]* '"';
CARACTER: '\'' ~['\r\n] '\'';

// Comentários
COMENTARIO: '*' ~[\r\n]* -> skip;

// Espaços em branco
WS: [ \t\r\n]+ -> skip;
```

Esta gramática reflete a implementação atual do analisador sintático, incluindo:

1. Estrutura básica do programa com bloco principal
2. Declarações de variáveis com tipos suportados
3. Comandos de controle (if/else, while, for)
4. Comandos de entrada/saída (escreva, leia)
5. Expressões aritméticas e lógicas
6. Operadores e delimitadores específicos da linguagem
7. Regras para identificadores e literais
8. Tratamento de comentários e espaços em branco

A gramática segue as convenções da linguagem CBR conforme implementada no analisador sintático, incluindo:
- Uso de `<<` para atribuição
- Uso de `.` para multiplicação
- Estrutura específica do comando `para` com `ate`
- Tipos de dados suportados
- Formato de comentários com `*`

## Exemplos de Uso

### Exemplo 1: Programa Simples
```cbr
programa {
    inteiro x << 10;
    escreva(x);
}
```

### Exemplo 2: Estrutura Condicional
```cbr
programa {
    inteiro x << 10;
    se (x > 5) {
        escreva("Maior que 5");
    } senao {
        escreva("Menor ou igual a 5");
    }
}
```

### Exemplo 3: Estrutura de Repetição
```cbr
programa {
    inteiro i << 0;
    enquanto (i < 10) {
        escreva(i);
        i << i + 1;
    }
}
```

## Notas de Implementação

1. O analisador sintático utiliza recursão para processar estruturas aninhadas
2. A árvore sintática é construída de forma incremental durante a análise
3. Erros são reportados imediatamente quando encontrados
4. O analisador mantém o contexto da análise através da posição atual nos tokens

## Considerações de Performance

- Complexidade de tempo: O(n), onde n é o número de tokens
- Complexidade de espaço: O(h), onde h é a altura da árvore sintática
- A recursão é gerenciada de forma eficiente para evitar estouro de pilha 