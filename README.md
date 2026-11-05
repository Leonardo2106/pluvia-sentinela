# PluviaSentinela

Sistema comunitario de monitoramento de chuva intensa e risco de alagamento em pontos vulneraveis. O projeto apoia moradores, equipes da prefeitura e agentes comunitarios a observar o territorio e decidir quando vistoriar drenagens, orientar deslocamentos ou preparar uma resposta local.

## Integrantes e divisao de responsabilidades

- Leonardo de Magalhaes Piassa — RM 563663
- Manoella dos Santos Ginez — RM 564469

## Estado atual

Esta primeira etapa contem somente a parte do integrante 1:

- `ComponenteTerritorial` abstrata e a implementacao `PontoVulneravel`;
- contrato `Sensor` e sensores de chuva, nivel da agua e umidade do solo;
- `DadosTerritorio`, com dados encapsulados e codigo para alterar os limiares;
- excecoes `DadosInvalidosException` e `FonteIndisponivelException`;
- entidades JPA `Medicao` e `Alerta`;
- repositorios Spring Data para consultar historico por sensor, regiao e nivel;
- banco Oracle da FIAP, configurado por variaveis de ambiente.

As estrategias de previsao, a logica que emite alertas, o menu e os testes serao implementados na etapa da integrante 2.

## Decisao de persistencia

Foi escolhido Spring Data JPA com o banco Oracle da FIAP, conforme o caminho apresentado na Aula 13. Os repositorios declaram as consultas e o Spring gerencia conexoes e recursos. As credenciais nao ficam gravadas no repositorio: sao fornecidas pelas variaveis de ambiente `DB_USERNAME` e `DB_PASSWORD`.

## Como executar esta etapa

Requisitos: Java 21 ou superior, Maven 3.8 ou superior, acesso ao servidor Oracle da FIAP e credenciais validas.

```bash
export DB_USERNAME="seu_RM"
export DB_PASSWORD="sua_senha"
mvn clean package
mvn spring-boot:run
```

No PowerShell, use `$env:DB_USERNAME="seu_RM"` e `$env:DB_PASSWORD="sua_senha"` antes dos comandos Maven.

Nesta etapa a aplicacao apenas inicializa o contexto, conecta ao Oracle e prepara as tabelas. O fluxo interativo sera conectado ao menu na segunda etapa.

## Estrutura principal

```text
src/main/java/br/com/pluviasentinela/
├── dominio/
├── excecao/
├── persistencia/
└── PluviaSentinelaApplication.java
```
