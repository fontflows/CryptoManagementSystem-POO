# CryptoCurrencyManagementSystem

Sistema de gestão de criptomoedas com autenticação para administradores e endpoints públicos para clientes. Os dados são armazenados em arquivos `.txt` para simplificação.

## Tecnologias Utilizadas

- **Java 17**: Linguagem de programação principal
- **Spring Boot**: Framework para criar aplicações Java stand-alone
- **Spring Security**: Módulo do Spring para autenticação e autorização
- **Swagger**: Para documentação da API
- **Maven**: Gerenciador de dependências e build

## Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- Java Development Kit (JDK) 17
- Maven 3.6+
- Git

## Configuração do Projeto

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/CryptoCurrencyManagementSystem.git
cd CryptoCurrencyManagementSystem
```

### 2. Instalação de Dependências

O projeto utiliza Maven para gerenciar dependências. Para instalar todas as dependências necessárias, execute o seguinte comando na raiz do projeto:

```bash
mvn clean install
```

Este comando baixará todas as dependências especificadas no arquivo `pom.xml` e compilará o projeto.

### 3. Configuração de Arquivos .txt

O projeto armazena dados em arquivos `.txt`. Certifique-se de que os arquivos de dados estão na pasta correta, ou serão criados automaticamente na execução.

#### Exemplo de arquivos de dados:
- `users.txt` para informações de usuários.
- `cryptos.txt` para informações de criptomoedas.

Por padrão, estes arquivos devem estar localizados na pasta `src/main/resources/data/`. Se desejar alterar este local, você pode modificar o caminho no arquivo de configuração `application.properties`.

### 4. Configuração do Banco de Dados (Opcional)

Caso deseje utilizar um banco de dados relacional em vez de arquivos `.txt`, você precisará:

1. Adicionar a dependência do driver do banco de dados no `pom.xml`
2. Configurar as propriedades de conexão no `application.properties`
3. Modificar o código para utilizar JPA ou outro ORM para persistência

### 5. Rodar o Projeto

Você pode rodar o projeto de duas formas:

#### Usando Maven:

```bash
mvn spring-boot:run
```

#### Usando sua IDE (por exemplo, IntelliJ IDEA):

1. Abra o projeto na sua IDE
2. Localize a classe principal com a anotação `@SpringBootApplication`
3. Execute esta classe como uma aplicação Java

O servidor irá iniciar e estará disponível em:

```
http://localhost:8080
```

## Segurança

O sistema implementa dois níveis de acesso:

- **Admin**: Acesso protegido para administradores com autenticação.
  - Usuário padrão: `admin`
  - Senha padrão: `admin123`
  - Endpoints protegidos: `/api/admin/**`

- **Cliente**: Endpoints públicos para operações de compra e consulta de criptomoedas.
  - Endpoints públicos: `/api/public/**`

Para alterar as credenciais do admin ou adicionar novos usuários, modifique o arquivo `users.txt` ou ajuste a configuração no `SecurityConfig.java`.

## Documentação da API

A documentação da API está disponível via Swagger UI. Após iniciar a aplicação, acesse:

```
http://localhost:8080/swagger-ui.html
```

## Testes

Para executar os testes unitários e de integração, use o comando:

```bash
mvn test
```

## Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Suporte

Para suporte, por favor abra uma issue no GitHub ou entre em contato por email: alvarocampioni@usp.br, christyan.nantes@usp.br, davi.gabriel.domingues@usp.br e eduardo.fontenele@usp.br

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE.md](LICENSE.md) para detalhes.
