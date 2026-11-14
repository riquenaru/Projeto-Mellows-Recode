# Mellows

O Mellows é uma plataforma educacional para descobrir histórias, conhecer níveis de leitura e demonstrar interesse em trocas de livros. O projeto começou como uma atividade colaborativa do Recode Pro; esta versão organiza a experiência em um monorepo full-stack com interface web, API REST e PostgreSQL.

## Fluxo principal

1. Pesquise livros por título, autor ou nível de leitura no catálogo Mellows, ou encontre títulos adicionais no Google Books.
2. Abra os detalhes de um livro.
3. Envie um pedido de interesse em troca; a API valida os campos e grava o pedido no PostgreSQL.
4. Cadastre-se na comunidade com consentimento explícito; a API normaliza e-mail e impede cadastros duplicados.

O catálogo de demonstração é carregado por uma migration versionada do Flyway. As páginas usam o mesmo backend que serve a interface, então a aplicação pode ser executada em uma única origem. As imagens permanecem no diretório Img/ na raiz e são copiadas para os recursos estáticos durante o build.

## Tecnologias

### Front-end

- HTML semântico e acessível, CSS responsivo e JavaScript sem framework.
- Bootstrap 5.3.3 para navegação responsiva e componentes.
- Busca e filtro de catálogo ligados à API; formulários exibem estados de carregamento, validação e resultado.
- Busca no catálogo público do Google Books, feita por um endpoint da API Mellows; a chave fica somente no servidor.

### Back-end e dados

- Java 21 e Spring Boot 3.5.16.
- Spring Web para endpoints REST, Spring Data JPA para persistência e Jakarta Bean Validation para validação de entrada.
- PostgreSQL 17 e Flyway para schema e dados iniciais reproduzíveis.
- Actuator expõe o endpoint de saúde da aplicação.
- Docker Compose para iniciar o banco e a aplicação; GitHub Actions compila e executa os testes unitários.

## Executar com Docker

Requisitos: Docker com Docker Compose. Para habilitar buscas no Google Books, gere uma chave de API no Google Cloud Console, habilite a Books API e configure `GOOGLE_BOOKS_API_KEY` no ambiente antes de iniciar. Restrinja a chave ao uso da Books API e mantenha-a fora do controle de versão. Sem essa chave, o restante do site funciona e a busca externa informa que não está configurada.

~~~bash
docker compose up --build
~~~

Opcionalmente, configure a chave no shell antes do comando (`export GOOGLE_BOOKS_API_KEY=sua_chave`) ou em um arquivo `.env` local, que é ignorado pelo Git.

Quando os serviços estiverem prontos, abra [http://localhost:8080](http://localhost:8080). A primeira inicialização compila a API, inicia o PostgreSQL, aplica as migrations e carrega os livros de exemplo.

Para encerrar, use Ctrl+C e depois:

~~~bash
docker compose down
~~~

O banco usa um volume local chamado mellows-data, que mantém os registros entre reinicializações. Para apagar esse banco local de demonstração, use:

~~~bash
docker compose down -v
~~~

### Executar sem Docker para a aplicação

Requisitos: Java 21, Maven 3.6.3 ou superior e PostgreSQL 17. Inicie primeiro o PostgreSQL com o banco mellows, usuário mellows e senha mellows_dev (ou configure SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME e SPRING_DATASOURCE_PASSWORD). Depois:

~~~bash
mvn -f backend/pom.xml spring-boot:run
~~~

O Maven copia os arquivos do diretório frontend/ para a aplicação durante o build. Abra [http://localhost:8080](http://localhost:8080).

## API

| Método | Endpoint | Descrição |
| --- | --- | --- |
| GET | /api/books?q=alice&level=AVANCADO | Lista livros ativos com filtros opcionais |
| GET | /api/books/{id} | Retorna os detalhes de um livro |
| GET | /api/google-books?q=infantil | Pesquisa títulos públicos no Google Books (requer `GOOGLE_BOOKS_API_KEY`) |
| POST | /api/community-signups | Cria um cadastro de comunidade com consentimento |
| POST | /api/exchange-requests | Registra interesse em trocar um livro |
| GET | /actuator/health | Estado de saúde da API |

Exemplo de pedido de troca:

~~~bash
curl -X POST http://localhost:8080/api/exchange-requests \
  -H 'Content-Type: application/json' \
  -d '{"bookId":1,"name":"Leitor Mellows","email":"leitor@example.com","message":"Tenho interesse neste livro."}'
~~~

## Modelo de dados

As tabelas são criadas por backend/src/main/resources/db/migration/V1__create_catalog_and_requests.sql. Os modelos originais do brModelo permanecem em Modelo banco de dados/.

~~~mermaid
erDiagram
    BOOKS ||--o{ EXCHANGE_REQUESTS : recebe
    BOOKS {
        bigint id PK
        varchar title
        varchar author
        varchar reading_level
        boolean active
    }
    EXCHANGE_REQUESTS {
        bigint id PK
        bigint book_id FK
        varchar name
        varchar email
        varchar status
        timestamptz created_at
    }
    COMMUNITY_SIGNUPS {
        bigint id PK
        varchar name
        varchar email UK
        boolean consent
        timestamptz created_at
    }
~~~

## Qualidade

Os testes unitários da camada de serviço e da validação de entrada ficam em backend/src/test/java. O script scripts/check_frontend.py confere links locais, IDs duplicados e metadados básicos de acessibilidade. A automação em .github/workflows/ci.yml executa mvn verify e a verificação estática do front-end em pull requests e atualizações da branch principal. Execute localmente:

~~~bash
mvn -f backend/pom.xml test
python3 scripts/check_frontend.py
~~~

## Estrutura do repositório

~~~text
.
├── backend/       # API Spring Boot, migrations e testes
├── frontend/      # Páginas, estilos e scripts
├── Img/           # Imagens de capa e identidade visual
├── Modelo banco de dados/  # Arquivos de modelo brModelo originais
├── docker-compose.yml
└── README.md
~~~

## Escopo demonstrativo e privacidade

O projeto persiste cadastros de comunidade e pedidos de troca no banco configurado. Use apenas dados fictícios na demonstração. Ainda não há login, painel administrativo, moderação ou fluxo para apagar cadastros; antes de publicar para usuários reais, implemente esses controles, política de retenção e proteção contra abuso. As credenciais padrão do Compose são somente para desenvolvimento local.

O repositório não inclui uma demonstração hospedada. Para publicação, a aplicação precisa ser implantada em um ambiente que execute Java e PostgreSQL; GitHub Pages sozinho não executa a API.
