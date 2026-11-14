# PluviaSentinela

Sistema comunitário de monitoramento de chuva intensa e risco de alagamento em pontos vulneráveis. O projeto apoia moradores, equipes da prefeitura e agentes comunitários na decisão de vistoriar drenagens, orientar deslocamentos e preparar uma resposta local.

## Integrantes e divisão de responsabilidades

- **Leonardo de Magalhães Piassa — RM 563663 — integrante 1:** domínio, exceções e persistência.
- **Manoella dos Santos Ginez — RM 564469 — integrante 2:** previsões, alertas, menu e testes.

## Funcionalidades

- componente territorial abstrato e ponto vulnerável concreto;
- sensores simulados de chuva, nível da água e umidade do solo;
- validação das leituras e dos dados territoriais com exceções próprias;
- histórico de medições e alertas persistido no Oracle da FIAP;
- previsão pela média das cinco leituras mais recentes;
- previsão pela comparação entre o período recente e o anterior;
- alertas automáticos nos níveis `ATENCAO`, `ALERTA` e `CRITICO`;
- justificativa com sensor, valor previsto, unidade e limite atingido;
- menu de console para operar e consultar o sistema;
- onze testes unitários no padrão AAA, incluindo `assertThrows` e Mockito.

## Arquitetura

```text
src/main/java/br/com/pluviasentinela/
├── apresentacao/    Menu de console
├── dominio/         Entidades, sensores e estratégias de previsão
├── excecao/         Exceções customizadas
├── persistencia/    Repositórios Spring Data JPA
├── servico/         Monitoramento e fábrica de previsões
└── PluviaSentinelaApplication.java
```

As estratégias recebem listas de medições e não acessam o banco diretamente. O serviço consulta os repositórios, escolhe a estratégia por meio da fábrica e persiste os alertas. Essa separação permite testar a lógica sem depender do Oracle.

## Persistência

Foi escolhido Spring Data JPA com o Oracle da FIAP, seguindo o conteúdo da Aula 13. Os repositórios declaram as consultas e o Spring gerencia conexões e recursos.

As credenciais não ficam no repositório. Elas são fornecidas pelas variáveis de ambiente `DB_USERNAME` e `DB_PASSWORD`.

## Padrão criacional

O projeto aplica o padrão **Factory** em `FabricaPrevisao`. A fábrica recebe um `TipoPrevisao` e cria `PrevisaoMediaMovel` ou `PrevisaoComparacao`.

A Factory foi escolhida porque o principal ponto de variação do sistema é a estratégia usada no cálculo. Assim, menu e serviço dependem apenas de `EstrategiaPrevisao`, sem `instanceof`, sem comparar nomes de classes e sem espalhar construções pelo código. Uma nova estratégia pode ser incluída em um único ponto.

## Como configurar

Requisitos:

- Java 21 ou superior;
- Maven 3.8 ou superior;
- acesso ao servidor Oracle da FIAP;
- credenciais válidas.

Linux ou macOS:

```bash
export DB_USERNAME="seu_RM"
export DB_PASSWORD="sua_senha"
```

PowerShell:

```powershell
$env:DB_USERNAME="seu_RM"
$env:DB_PASSWORD="sua_senha"
```

## Como testar

Os testes são unitários e não abrem conexão com o Oracle:

```bash
mvn clean test
```

Resultado esperado:

```text
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Como executar

Depois de configurar as variáveis de ambiente:

```bash
mvn clean package
mvn spring-boot:run
```

O menu oferece:

```text
1 - Verificar sensores
2 - Registrar leituras
3 - Rodar previsões
4 - Ver histórico por sensor
5 - Ver alertas emitidos
6 - Gerenciar dados do território
7 - Exibir status completo
0 - Sair
```

Para executar uma previsão, registre pelo menos seis rodadas de leitura. A média móvel exige cinco medições e a comparação de períodos exige seis.

O código inicial para alterar população e limiares no menu é `2026`. Cada tipo de sensor possui limiares próprios, pois chuva, nível da água e umidade usam unidades e escalas diferentes.

## Estratégias de previsão

### Média móvel

Ordena o histórico do sensor por data e calcula a média das cinco leituras mais recentes. O resultado reduz o efeito de uma única oscilação isolada.

### Comparação de períodos

Compara a média das três leituras mais recentes com a média das três anteriores. A diferença entre as médias representa a tendência, que é somada à média recente. O resultado nunca pode ser negativo.

Essas regras são deliberadamente simples: o objetivo acadêmico é demonstrar orientação a objetos, polimorfismo, persistência, exceções e testes, não produzir um modelo meteorológico profissional.

## Alertas

Cada sensor possui uma configuração territorial com três limiares. A avaliação começa pelo limite mais alto:

```text
valor previsto >= crítico  → CRITICO
valor previsto >= alerta   → ALERTA
valor previsto >= atenção  → ATENCAO
valor previsto < atenção   → nenhum alerta
```

Todo alerta emitido é salvo no Oracle. Previsões abaixo do limiar de atenção não criam registros desnecessários.

## Reflexão crítica

O sistema deve se calar quando não possui histórico suficiente, quando o sensor está indisponível ou quando as leituras misturam sensores e unidades diferentes. Emitir uma previsão nessas situações produziria uma sensação de precisão que os dados não sustentam. Por isso, as estratégias exigem cinco ou seis medições e lançam uma exceção clara quando não podem calcular um resultado confiável.

Os dados também não representam todo o território. Um sensor instalado em uma região não detecta necessariamente um bueiro obstruído em outra rua, diferenças de relevo, impermeabilização do solo ou uma falha localizada de drenagem. Além disso, os sensores deste projeto são simulados e as estratégias observam somente períodos curtos. O histórico ajuda a perceber tendência, mas não substitui informações meteorológicas oficiais nem inspeções locais.

Por esses motivos, um alerta apoia a decisão humana em vez de decidir pela pessoa. A equipe responsável deve combinar o nível calculado com observação do território, fontes públicas, disponibilidade de equipes e protocolos municipais. O PluviaSentinela organiza evidências e explica por que um limite foi atingido; a decisão final continua com moradores e agentes responsáveis.

## Segurança e privacidade

- nenhuma credencial do Oracle é versionada;
- nenhum dado pessoal de moradores é coletado;
- o território é identificado somente por região, sensor e ponto vulnerável;
- arquivos gerados em `target/` permanecem fora do Git.
