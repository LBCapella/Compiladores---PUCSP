# Documentação do Parser Sintático

## 1. **Introdução**

Este parser realiza a **análise sintática** de uma linguagem baseada em gramática livre de contexto (GLC), validando a ordem dos tokens produzidos pelo analisador léxico e construindo uma **árvore sintática abstrata (AST)** genérica. O parser segue o modelo **descendente recursivo**, onde cada não-terminal da gramática é implementado como um método Java.

---

## 2. **Gramática da Linguagem**

A gramática utilizada é inspirada no padrão ANTLR4, adaptada para a linguagem do projeto:

```
programa: 'programa' PARENTESES_E PARENTESES_D DELIMITADOR_E bloco DELIMITADOR_D;

bloco: (declaracao | instrucao)*;

declaracao: tipo IDENTIFICADOR (RECEBE expressao)? PONTO_VIRGULA;

tipo: INTEIRO | REAL | BOOL | CARACTER | TEXTO;

instrucao: 
    | estruturaCondicional
    | estruturaRepeticaoEnquanto
    | estruturaRepeticaoPara
    | comandoEscreva
    | comandoLeia
    | atribuicao;

estruturaCondicional: 
    SE PARENTESES_E condicao PARENTESES_D 
    DELIMITADOR_E bloco DELIMITADOR_D 
    (SENAO DELIMITADOR_E bloco DELIMITADOR_D)?;

estruturaRepeticaoEnquanto: 
    ENQUANTO PARENTESES_E condicao PARENTESES_D 
    DELIMITADOR_E bloco DELIMITADOR_D;

estruturaRepeticaoPara: 
    PARA PARENTESES_E IDENTIFICADOR ATE expressao PARENTESES_D 
    DELIMITADOR_E bloco DELIMITADOR_D;

comandoEscreva: 
    ESCREVA PARENTESES_E expressao PARENTESES_D PONTO_VIRGULA;

comandoLeia: 
    LEIA PARENTESES_E IDENTIFICADOR PARENTESES_D PONTO_VIRGULA;

atribuicao: 
    IDENTIFICADOR RECEBE expressao PONTO_VIRGULA;

condicao: 
    expressao (opRelacional expressao)?;

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

opRelacional: 
    IGUAL | MAIOR_QUE | MENOR_QUE | MAIOR_IGUAL | MENOR_IGUAL | DIFERENTE;
```

---

## 3. **Funcionamento do Parser**

### **Estrutura Geral**

- O parser recebe uma lista de tokens do lexer.
- Mantém um ponteiro (`pos`) para o token atual.
- Cada método corresponde a um não-terminal da gramática.
- Ao reconhecer uma produção, constrói um nó da AST genérica (`ASTNode`), com tipo, valor (quando aplicável) e filhos.

### **AST Genérica**

- **ASTNode**: classe única para todos os nós, com campos:
  - `tipo`: String representando o tipo do nó (ex: `"declaracao"`, `"se"`, `"escreva"`, etc.).
  - `valor`: valor do nó (ex: nome do identificador, valor numérico, etc.).
  - `filhos`: lista de filhos (outros nós ASTNode).

---

### **Lógica dos Principais Métodos**

#### **programa()**
Reconhece a estrutura principal do programa:
```java
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
```
- Cria um nó `"programa"` com um filho: o bloco principal.

#### **bloco()**
Reconhece uma sequência de declarações e instruções:
```java
private ASTNode bloco() {
    ASTNode noBloco = new ASTNode("bloco");
    while (ehDeclaracao() || ehInstrucao()) {
        if (ehDeclaracao()) {
            noBloco.adicionarFilho(declaracao());
        } else {
            noBloco.adicionarFilho(instrucao());
        }
    }
    return noBloco;
}
```
- Cria um nó `"bloco"` com filhos para cada declaração ou instrução encontrada.

#### **declaracao()**
Reconhece declarações de variáveis:
```java
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
```
- Cria um nó `"declaracao"` com filhos para tipo, identificador e (opcionalmente) expressão de inicialização.

#### **instrucao()**
Decide qual instrução está presente e chama o método correspondente:
```java
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
```

#### **estruturaCondicional()**
Reconhece instruções `se` e `senao`:
```java
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
```
- Cria um nó `"se"` com filhos para condição, bloco do `se` e (opcionalmente) bloco do `senao`.

#### **expressao() e termo()**
Reconhecem expressões aritméticas e seus termos:
```java
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
```
- Constrói uma árvore binária para operações aritméticas, respeitando a ordem de avaliação da esquerda para a direita.

---

### **Tratamento de Erros**

- O método `erro(String mensagem)` lança uma exceção com mensagem detalhada e linha do erro.
- O método `consumir(TokenType tipo)` verifica se o token atual é do tipo esperado; caso contrário, chama `erro`.

---

## 4. **Exemplo de Uso**

No arquivo `CBR.java`:

```java
Lexer lexer = new Lexer(codigoFonte);
List<Token> tokens = lexer.analisarTokens();
Parser parser = new Parser(tokens);
ASTNode arvore = parser.analisar();
imprimirAST(arvore, 0);
```

- Os tokens são impressos antes da AST, para facilitar o debug.
- A AST é impressa de forma indentada, mostrando a estrutura sintática do programa.

---

## 5. **Relação com o Caderno da Disciplina**

- **Padrão Descendente Recursivo:** Cada não-terminal da gramática é um método, como sugerido no caderno.
- **AST Genérica:** Segue a ideia de simplificação da árvore sintática, focando apenas nos elementos relevantes para análise semântica e posterior geração de código.
- **Tratamento de Erros:** Mensagens claras, com linha e descrição, conforme boas práticas sugeridas no caderno.
- **Separação Léxico/Sintático:** O parser consome tokens do lexer, mantendo a modularidade e clareza do projeto, como exemplificado no material da disciplina.

---

## 6. **Resumo**

- O parser cobre toda a gramática proposta, exceto operadores lógicos compostos (que podem ser adicionados facilmente).
- A AST é construída de forma genérica, facilitando futuras extensões.
- O projeto segue as recomendações do caderno, tanto na estrutura do parser quanto na construção da árvore sintática.

---

Se quiser expandir para operadores lógicos, precedência de operadores, ou adicionar mais exemplos, basta pedir! 