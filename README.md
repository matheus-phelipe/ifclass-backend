
# 📚 IFClass Backend: API RESTful

## 📖 Sobre o Projeto

O **IFClass Backend** é a camada de serviços e lógica de negócio do sistema IFClass. Construído com Spring Boot, ele fornece uma API RESTful para gerenciar todos os dados e operações do sistema, incluindo autenticação de usuários, gestão de blocos e salas, e controle de acesso baseado em permissões.

### 🔑 Principais Módulos

- **API de Autenticação e Autorização:** Gerencia o login de usuários, a criação de tokens (JWT) e a verificação de perfis de acesso (`ROLE_ADMIN`, `ROLE_USER`).
- **Gerenciamento de Blocos:** Endpoints CRUD para administração dos blocos do campus.
- **Gerenciamento de Salas:** Endpoints CRUD para salas associadas aos blocos, com campos para posição (X, Y), largura e altura para a planta baixa interativa.
- **Persistência de Dados:** Integração com MySQL via Spring Data JPA para armazenamento de dados seguro e eficiente.

## 🛠️ Tecnologias Utilizadas

- **Java Development Kit (JDK):** Versão 17
- **Spring Boot:** Framework líder para desenvolvimento de aplicações Java e APIs REST.
- **Spring Data JPA:** Facilita a persistência de dados e a criação de repositórios.
- **Spring Security:** Funcionalidades robustas de segurança.
- **PostgreSQL:** Sistema de Gerenciamento de Banco de Dados Relacional.
- **Maven / Gradle:** Ferramenta para gerenciamento de dependências e build.
- **Lombok:** Biblioteca para reduzir código repetitivo.
- **RESTful API:** Arquitetura padrão para comunicação entre serviços.

## ✅ Pré-requisitos

Para configurar e rodar o backend do IFClass, você precisará de:

- **Java Development Kit (JDK)** (versão 17)
- **Maven** (versão 3.x ou superior)
- Um IDE (ex: IntelliJ IDEA, Eclipse ou VS Code com extensões Java)
- (Opcional) **Git** para clonar o repositório

## 🚀 Configuração e Execução

Siga os passos abaixo para configurar e iniciar o backend do IFClass:

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/matheus-phelipe/ifclass-backend.git
cd ifclass-backend
```

**Importante:** Certifique-se de estar no diretório `ifclass-backend`.

### 2️⃣ Configurar o Banco de Dados PostgreSql

Crie um banco de dados PostgreSql para o projeto:

```sql
CREATE DATABASE IF NOT EXISTS ifclass;

Edite o arquivo `src/main/resources/application.properties` e atualize as propriedades de conexão:

```properties
# Configurações do Banco de Dados PostgreSql
spring.datasource.url=jdbc:postgresql://localhost:5432/ifclass
spring.datasource.username=postgres
spring.datasource.password=postgres

# Configurações do JPA e Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

```

### 3️⃣ Rodar o Aplicativo Backend

Dentro do diretório `ifclass-backend`, inicie a aplicação Spring Boot:

**Usando Maven:**

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080` (ou na porta configurada).

## 🔗 Acesso à API

Com o backend rodando, a API RESTful estará pronta para ser consumida pelo frontend ou por ferramentas como Postman/Insomnia.

## ⚖️ Licença

Este projeto backend está sob a licença **[MIT License / Apache 2.0 ]**.
