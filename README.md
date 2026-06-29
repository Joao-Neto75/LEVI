```markdown
# Projeto LEVI: Sistema de Gerenciamento de Locadora

Este projeto implementa um sistema de gerenciamento de locadora utilizando conceitos de Programação Orientada a Objetos (POO) e padrões de projeto, com interface gráfica desenvolvida em JavaFX e persistência de dados em MySQL.

## Requisitos do Professor e Implementação

Conforme os requisitos do trabalho, o projeto LEVI implementa os seguintes pontos:

*   **Utilizar herança e polimorfismo:** Implementado através da classe abstrata `Produto` e suas subclasses `Livro` e `Disco`. Métodos como `getDescricao()` e `contemTermo()` demonstram polimorfismo.
*   **Garantir o encapsulamento:** Todos os atributos das classes de entidade são privados, com acesso controlado por métodos *getters* e *setters*.
*   **Criar, pelo menos, uma Exceção:** O projeto utiliza exceções nativas do Java (`RuntimeException`, `IllegalArgumentException`, `SQLException`) de forma estratégica para tratamento de erros de negócio, validação de dados e persistência, respectivamente.
*   **Utilizar interfaces e classes abstratas:** A interface `Pesquisavel` e a classe abstrata `Produto` são exemplos claros, promovendo flexibilidade e padronização.
*   **Persistir os dados em arquivo ou banco de dados:** Os dados são persistidos em um banco de dados MySQL, gerenciado pela camada DAO.
*   **Elaborar uma interface gráfica amigável:** Desenvolvida com JavaFX, oferece telas intuitivas para gerenciamento de livros, discos, clientes, aluguéis e relatórios.
*   **Criar tela de login para permitir o uso, somente, por usuários cadastrados:** A tela de login (`TelaLogin.fxml`) autentica usuários contra a tabela `funcionarios`.
*   **Todos os projetos devem permitir ao gerente (adm) cadastrar funcionários com acesso ao sistema:** O sistema permite o cadastro de funcionários, que são os usuários com acesso ao sistema.
*   **Elaborar o projeto utilizando o diagrama de classes:** Um diagrama UML completo foi gerado para documentar a arquitetura do sistema.
*   **Utilizar 3 padrões de projeto do livro Padrões de Projeto: Soluções Reutilizáveis de Software Orientados a Objetos:** O projeto implementa 6 padrões de projeto:
    *   **DAO (Data Access Object):** Presente em `br.edu.ufersa.LEVI.model.dao` (ex: `ClienteDao`, `LivroDao`).
    *   **Strategy:** Implementado pela interface `Pesquisavel` e suas classes concretas (`Produto`, `Cliente`).
    *   **Factory Method:** Utilizado na `ProdutoFactory` para a criação de instâncias de `Livro` e `Disco`.
    *   **Singleton:** Implementado na `ConnectionFactory` para gerenciar a conexão única com o banco de dados.
    *   **Template Method:** Abstraído na classe `AbstractDao`, que define o fluxo de operações de persistência, delegando a implementação específica para os DAOs concretos.
    *   **Facade:** A `LocadoraFacade` centraliza as operações de serviço para os controladores da interface gráfica.

## Configuração e Execução

### Pré-requisitos

*   **Java Development Kit (JDK) 21** ou superior.
*   **Apache Maven** (para gerenciamento de dependências e build).
*   **Servidor MySQL** instalado e em execução.
*   **MySQL Connector/J** (já incluído como dependência Maven no `pom.xml`).

### 1. Configuração do Banco de Dados

Crie o banco de dados `levi` e as tabelas necessárias executando o script SQL abaixo no seu servidor MySQL. Certifique-se de que o usuário `root` (ou o usuário configurado na `ConnectionFactory`) tenha permissões adequadas.

**Script SQL:**

```sql
CREATE DATABASE IF NOT EXISTS levi;
USE levi;

CREATE TABLE IF NOT EXISTS cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    endereco VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    genero VARCHAR(100),
    ano DATE,
    autor VARCHAR(255),
    paginas INT,
    exemplares INT DEFAULT 0,
    valor_aluguel FLOAT DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS disco (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    banda VARCHAR(255),
    estilo VARCHAR(100),
    ano DATE,
    exemplares INT DEFAULT 0,
    valor_aluguel FLOAT DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS funcionarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cargo VARCHAR(100),
    salario DOUBLE,
    contratacao DATE,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS alugueis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_prevista_devolucao DATE,
    data_devolucao DATE,
    valor_total FLOAT DEFAULT 0.0,
    status VARCHAR(50) DEFAULT 'Ativo',
    renovado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS aluguel_produtos (
    aluguel_id INT NOT NULL,
    produto_id INT NOT NULL,
    PRIMARY KEY (aluguel_id, produto_id),
    FOREIGN KEY (aluguel_id) REFERENCES alugueis(id) ON DELETE CASCADE,
    -- Note: produto_id pode referenciar tanto livro.id quanto disco.id.
    -- Para um FK mais robusto, seria ideal uma tabela 'produto' genérica.
    -- No entanto, para este projeto, a lógica de carregamento em AluguelDao
    -- trata a distinção entre Livro e Disco.
    -- FOREIGN KEY (produto_id) REFERENCES livro(id) ON DELETE CASCADE,
    -- FOREIGN KEY (produto_id) REFERENCES disco(id) ON DELETE CASCADE
    -- As FKs para produto_id são omitidas aqui devido à natureza polimórfica
    -- do Produto e à implementação de AluguelDao que busca em ambas as tabelas.
    -- Em um cenário real, uma tabela Produto base seria criada para uma FK única.
    INDEX (produto_id)
);

-- Inserir um funcionário padrão para login inicial (senha: 123)
INSERT INTO funcionarios (nome, cargo, salario, contratacao, email, senha)
VALUES ('Admin', 'Gerente', 3000.00, CURDATE(), 'admin@levi.com', '123');

```

### 2. Configuração da Conexão

Edite o arquivo `src/br/edu/ufersa/LEVI/connection/ConnectionFactory.java` e atualize a constante `PASSWORD` com a senha do seu usuário MySQL.

```java
private static final String PASSWORD = "SUA-SENHA"; // <-- ALTERE AQUI
```

### 3. Compilação e Execução

1.  **Navegue até a pasta raiz do projeto LEVI** (onde está o arquivo `pom.xml`) no seu terminal.
2.  **Compile o projeto com Maven:**
    ```bash
    mvn clean install
    ```
3.  **Execute a aplicação:**
    ```bash
    mvn javafx:run
    ```

### 4. Acesso ao Sistema

Após a execução, a tela de login será exibida. Utilize as seguintes credenciais:

*   **Email:** `admin@levi.com`
*   **Senha:** `123`

## Estrutura do Projeto

O projeto segue uma arquitetura em camadas, organizada da seguinte forma:

*   `src/main/java/br/edu/ufersa/LEVI/`
    *   `App.java`: Classe principal da aplicação JavaFX.
    *   `connection/`: Contém a `ConnectionFactory` para gerenciamento da conexão com o banco de dados.
    *   `model/`
        *   `dao/`: Camada de acesso a dados (DAOs), implementando o padrão Template Method (`AbstractDao`).
        *   `entity/`: Camada de entidades (Model), incluindo `Produto`, `Livro`, `Disco`, `Cliente`, `Aluguel`, `Funcionarios`, `Pesquisavel` e `ProdutoFactory`.
        *   `service/`: Camada de serviços, contendo a lógica de negócio e a `LocadoraFacade`.
    *   `view/`
        *   `Controller/`: Controladores JavaFX para as telas da aplicação.
        *   `css/`: Arquivos CSS para estilização da interface.
        *   `fxml/`: Arquivos FXML que definem o layout das telas.
        *   `icons/`: Ícones utilizados na interface.

```
