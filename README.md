# CryptoCurrencyManagementSystem

Sistema de gestão de criptomoedas com autenticação para administradores e endpoints públicos para clientes. Os dados são armazenados em arquivos `.txt` para simplificação.

## Tecnologias

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **Swagger** (para documentação da API)

## Configuração do Projeto

## 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/CryptoCurrencyManagementSystem.git
cd CryptoCurrencyManagementSystem
```

## 2. Configuração de Arquivos .txt
O projeto armazena dados em arquivos `.txt`. Certifique-se de que os arquivos de dados estão na pasta correta, ou serão criados automaticamente na execução.

### Exemplo de arquivos de dados:
- `users.txt` para informações de usuários.
- `cryptos.txt` para informações de criptomoedas.

## 3. Rodar o Projeto
No IntelliJ IDEA, abra o projeto e clique em **Run** no arquivo principal com `@SpringBootApplication`, ou use o Maven para rodar:

```bash
mvn spring-boot:run
```
O servidor irá rodar em:
```bash
http://localhost:8080
```
### Segurança
- **Admin**: Acesso protegido para administradores com autenticação. O usuário é `admin` e a senha é `admin123`
- **Cliente**: Endpoints públicos para operações de compra e consulta de criptomoedas.
