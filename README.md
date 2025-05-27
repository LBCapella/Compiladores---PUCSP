# Compilador CBR - PUCSP

Este projeto implementa um compilador para a linguagem CBR, desenvolvido como parte da disciplina de Compiladores do curso de Ciência da Computação da PUC-SP.

## Estrutura do Projeto

```
.
├── src/
│   └── Cbr/
│       ├── AnalisadorSintatico.java  # Analisador sintático
│       ├── Arvore.java              # Implementação da árvore sintática
│       ├── CBR.java                 # Classe principal do compilador
│       ├── ExemploCompleto.java     # Exemplo de uso do compilador
│       ├── Lexer.java               # Analisador léxico
│       ├── No.java                  # Implementação de nó da árvore
│       ├── PalavrasReservadas.java  # Definição das palavras reservadas
│       ├── Token.java               # Implementação de token
│       └── TokenType.java           # Enumeração dos tipos de tokens
├── bin/                            # Diretório de compilação
├── docs/                           # Documentação
└── out/                            # Arquivos de saída
```

## Características da Linguagem CBR

A linguagem CBR é uma linguagem de programação simples com as seguintes características:

### Tipos de Dados
- Inteiro
- Real
- Booleano
- Caractere
- Texto

### Estruturas de Controle
- Condicional (se/senao)
- Repetição (enquanto)
- Repetição (para)

### Comandos
- Atribuição (<<)
- Leitura (leia)
- Escrita (escreva)

### Operadores
- Aritméticos: +, -, *, /
- Relacionais: ==, >, <, >=, <=, !=

## Como Compilar e Executar

1. Certifique-se de ter o JDK instalado (versão 8 ou superior)
2. Compile o projeto:
   ```bash
   javac -d bin src/Cbr/*.java
   ```
3. Execute o compilador:
   ```bash
   java -cp bin Cbr.CBR arquivo.cbr
   ```

## Exemplo de Código CBR

```cbr
programa {
    inteiro x << 10;
    enquanto (x > 0) {
        escreva(x);
        x << x - 1;
    }
}
```

## Componentes do Compilador

### Analisador Léxico (Lexer)
- Responsável por identificar tokens no código fonte
- Implementa a classe `Lexer.java`
- Gera uma sequência de tokens para o analisador sintático

### Analisador Sintático (Parser)
- Implementa a classe `AnalisadorSintatico.java`
- Verifica a estrutura gramatical do código
- Gera uma árvore sintática

### Árvore Sintática
- Implementada pelas classes `Arvore.java` e `No.java`
- Representa a estrutura hierárquica do código fonte
- Usada para análise semântica e geração de código

## Desenvolvimento

### Requisitos
- Java Development Kit (JDK) 8 ou superior
- IDE compatível com Java

### Estrutura de Classes

#### Token.java
- Representa um token do código fonte
- Contém tipo, lexema e linha do token

#### TokenType.java
- Enumeração dos tipos de tokens possíveis
- Inclui operadores, palavras reservadas e identificadores

#### PalavrasReservadas.java
- Define as palavras reservadas da linguagem
- Mapeia palavras reservadas para seus tipos de token

#### AnalisadorSintatico.java
- Implementa a análise sintática
- Verifica a estrutura gramatical do código
- Gera a árvore sintática

## Licença

Este projeto é parte da disciplina de Compiladores do curso de Ciência da Computação da PUC-SP.

## Autores

- Luan Bonasorte Capella
- Kauã Cordeiro Cavalheiro
- João Avila Harduin
- Patrick Barreira

## Agradecimentos

- Professores e colegas do curso de Compiladores da PUC-SP
- Comunidade de desenvolvimento de compiladores 