package br.com.pluviasentinela.apresentacao;

import br.com.pluviasentinela.dominio.Alerta;
import br.com.pluviasentinela.dominio.DadosTerritorio;
import br.com.pluviasentinela.dominio.Medicao;
import br.com.pluviasentinela.dominio.PontoVulneravel;
import br.com.pluviasentinela.dominio.ResultadoPrevisao;
import br.com.pluviasentinela.dominio.Sensor;
import br.com.pluviasentinela.dominio.SensorNivelAgua;
import br.com.pluviasentinela.dominio.SensorPluviometrico;
import br.com.pluviasentinela.dominio.SensorUmidadeSolo;
import br.com.pluviasentinela.dominio.StatusComponente;
import br.com.pluviasentinela.dominio.TipoPrevisao;
import br.com.pluviasentinela.excecao.DadosInvalidosException;
import br.com.pluviasentinela.excecao.FonteIndisponivelException;
import br.com.pluviasentinela.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Component
public class SistemaMonitoramento implements CommandLineRunner {

    private static final String CODIGO_INICIAL = "2026";
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ServicoMonitoramento servico;
    private final Map<String, Sensor> sensores = new LinkedHashMap<>();
    private final Map<String, DadosTerritorio> configuracoes = new LinkedHashMap<>();
    private final PontoVulneravel pontoVulneravel;

    public SistemaMonitoramento(ServicoMonitoramento servico) {
        this.servico = servico;

        adicionarSensor(new SensorPluviometrico("PLUV-01", "Vila Esperanca"), 30, 50, 80);
        adicionarSensor(new SensorNivelAgua("NIVEL-01", "Vila Esperanca"), 120, 200, 300);
        adicionarSensor(new SensorUmidadeSolo("SOLO-01", "Vila Esperanca"), 60, 75, 90);

        this.pontoVulneravel = new PontoVulneravel(
                "PV-01", "Baixada do Corrego", "Vila Esperanca", StatusComponente.ATIVO, 45);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean executando = true;

        exibirCabecalho();
        while (executando) {
            exibirMenu();
            if (!scanner.hasNextLine()) {
                System.out.println("Entrada encerrada. Finalizando o PluviaSentinela.");
                break;
            }

            String opcao = scanner.nextLine().trim();
            try {
                executando = executarOpcao(opcao, scanner);
            } catch (DadosInvalidosException | FonteIndisponivelException e) {
                System.out.println("Nao foi possivel concluir: " + e.getMessage());
            } catch (DataAccessException e) {
                System.out.println("Falha ao acessar o Oracle. Verifique rede, usuario e senha.");
            } catch (RuntimeException e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            }
        }

        System.out.println("PluviaSentinela encerrado.");
    }

    private boolean executarOpcao(String opcao, Scanner scanner) {
        return switch (opcao) {
            case "1" -> {
                verificarSensores();
                yield true;
            }
            case "2" -> {
                registrarRodadas(scanner);
                yield true;
            }
            case "3" -> {
                rodarPrevisao(scanner);
                yield true;
            }
            case "4" -> {
                exibirHistorico(scanner);
                yield true;
            }
            case "5" -> {
                exibirAlertas();
                yield true;
            }
            case "6" -> {
                gerenciarTerritorio(scanner);
                yield true;
            }
            case "7" -> {
                exibirStatusCompleto();
                yield true;
            }
            case "0" -> false;
            default -> {
                System.out.println("Opcao invalida. Escolha um numero do menu.");
                yield true;
            }
        };
    }

    private void verificarSensores() {
        System.out.println("\n--- Sensores ---");
        sensores.values().forEach(sensor -> System.out.printf(
                "%s | %-14s | regiao: %-16s | %s%n",
                sensor.getIdentificador(), sensor.retornarTipo(), sensor.getRegiao(),
                sensor.verificarFuncionamento() ? "FUNCIONANDO" : "INDISPONIVEL"));
    }

    private void registrarRodadas(Scanner scanner) {
        int quantidade = lerInteiro(scanner, "Quantidade de rodadas (1 a 24): ");
        if (quantidade < 1 || quantidade > 24) {
            throw new DadosInvalidosException("quantidadeRodadas", quantidade,
                    "informe um valor entre 1 e 24");
        }

        for (int rodada = 1; rodada <= quantidade; rodada++) {
            System.out.println("\nRodada " + rodada + ":");
            for (Sensor sensor : sensores.values()) {
                try {
                    Medicao medicao = servico.registrarLeitura(sensor);
                    System.out.printf("  %s -> %.2f %s (gravada)%n",
                            medicao.getSensorIdentificador(), medicao.getValor(), medicao.getUnidadeMedida());
                } catch (FonteIndisponivelException e) {
                    System.out.println("  " + sensor.getIdentificador() + " -> " + e.getMessage());
                }
            }
        }
    }

    private void rodarPrevisao(Scanner scanner) {
        Sensor sensor = selecionarSensor(scanner);
        System.out.println("1 - Media movel");
        System.out.println("2 - Comparacao entre periodos");
        int escolha = lerInteiro(scanner, "Estrategia: ");
        TipoPrevisao tipo = switch (escolha) {
            case 1 -> TipoPrevisao.MEDIA_MOVEL;
            case 2 -> TipoPrevisao.COMPARACAO;
            default -> throw new DadosInvalidosException("estrategia", escolha,
                    "escolha 1 para media movel ou 2 para comparacao");
        };

        ResultadoPrevisao resultado = servico.executarPrevisao(sensor.getIdentificador(), tipo);
        System.out.printf("\nPrevisao: %.2f %s%n", resultado.valorPrevisto(), resultado.unidadeMedida());
        System.out.println(resultado.explicacao());

        DadosTerritorio configuracao = configuracoes.get(sensor.getIdentificador());
        Optional<Alerta> alerta = servico.avaliarEEmitirAlerta(
                sensor.getIdentificador(), resultado, configuracao);

        if (alerta.isPresent()) {
            System.out.println("ALERTA EMITIDO: " + alerta.get().getJustificativa());
        } else {
            System.out.println("Previsao abaixo do limiar de atencao. Nenhum alerta emitido.");
        }
    }

    private void exibirHistorico(Scanner scanner) {
        Sensor sensor = selecionarSensor(scanner);
        List<Medicao> historico = servico.listarHistorico(sensor.getIdentificador());

        System.out.println("\n--- Historico de " + sensor.getIdentificador() + " ---");
        if (historico.isEmpty()) {
            System.out.println("Nenhuma medicao registrada.");
            return;
        }

        historico.forEach(medicao -> System.out.printf("%s | %.2f %s | %s%n",
                medicao.getRegistradaEm().format(DATA_HORA), medicao.getValor(),
                medicao.getUnidadeMedida(), medicao.getRegiao()));
    }

    private void exibirAlertas() {
        List<Alerta> alertas = servico.listarAlertas();
        System.out.println("\n--- Alertas persistidos ---");
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido.");
            return;
        }

        alertas.forEach(alerta -> System.out.printf("%s | %-7s | %s%n",
                alerta.getEmitidoEm().format(DATA_HORA), alerta.getNivel(), alerta.getJustificativa()));
    }

    private void gerenciarTerritorio(Scanner scanner) {
        Sensor sensor = selecionarSensor(scanner);
        DadosTerritorio territorio = configuracoes.get(sensor.getIdentificador());

        System.out.printf("Limiares atuais de %s: atencao %.2f, alerta %.2f, critico %.2f %s%n",
                sensor.getIdentificador(), territorio.getLimiarAtencao(), territorio.getLimiarAlerta(),
                territorio.getLimiarCritico(), sensor.getUnidadeMedida());

        String codigo = lerTexto(scanner, "Codigo de acesso: ");
        int populacao = lerInteiro(scanner, "Nova populacao estimada: ");
        double atencao = lerDouble(scanner, "Novo limiar de atencao: ");
        double alerta = lerDouble(scanner, "Novo limiar de alerta: ");
        double critico = lerDouble(scanner, "Novo limiar critico: ");

        territorio.alterarPopulacaoEstimada(codigo, populacao);
        territorio.alterarLimiares(codigo, atencao, alerta, critico);
        System.out.println("Dados territoriais atualizados com sucesso.");
    }

    private void exibirStatusCompleto() {
        System.out.println("\n--- Status territorial completo ---");
        System.out.println(pontoVulneravel.verificarStatus());
        sensores.values().forEach(sensor -> {
            DadosTerritorio territorio = configuracoes.get(sensor.getIdentificador());
            System.out.printf("%s: %s | medicoes: %d | limiares: %.2f / %.2f / %.2f %s%n",
                    sensor.getIdentificador(), sensor.verificarFuncionamento() ? "ATIVO" : "INDISPONIVEL",
                    servico.listarHistorico(sensor.getIdentificador()).size(), territorio.getLimiarAtencao(),
                    territorio.getLimiarAlerta(), territorio.getLimiarCritico(), sensor.getUnidadeMedida());
        });
        System.out.println("Alertas persistidos: " + servico.listarAlertas().size());
    }

    private Sensor selecionarSensor(Scanner scanner) {
        List<Sensor> lista = sensores.values().stream().toList();
        System.out.println("\nSensores disponiveis:");
        for (int indice = 0; indice < lista.size(); indice++) {
            Sensor sensor = lista.get(indice);
            System.out.printf("%d - %s (%s)%n", indice + 1, sensor.getIdentificador(), sensor.retornarTipo());
        }

        int escolha = lerInteiro(scanner, "Sensor: ");
        if (escolha < 1 || escolha > lista.size()) {
            throw new DadosInvalidosException("sensor", escolha, "escolha um sensor da lista");
        }
        return lista.get(escolha - 1);
    }

    private int lerInteiro(Scanner scanner, String mensagem) {
        String texto = lerTexto(scanner, mensagem);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("entrada", texto, "informe um numero inteiro");
        }
    }

    private double lerDouble(Scanner scanner, String mensagem) {
        String texto = lerTexto(scanner, mensagem).replace(',', '.');
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("entrada", texto, "informe um numero valido");
        }
    }

    private String lerTexto(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        if (!scanner.hasNextLine()) {
            throw new DadosInvalidosException("entrada", "encerrada", "nao ha mais dados para leitura");
        }
        return scanner.nextLine().trim();
    }

    private void adicionarSensor(Sensor sensor, double atencao, double alerta, double critico) {
        sensores.put(sensor.getIdentificador(), sensor);
        configuracoes.put(sensor.getIdentificador(), new DadosTerritorio(
                sensor.getRegiao(), 8_500, atencao, alerta, critico, CODIGO_INICIAL));
    }

    private void exibirCabecalho() {
        System.out.println("==============================================");
        System.out.println(" PluviaSentinela - monitoramento territorial ");
        System.out.println("==============================================");
    }

    private void exibirMenu() {
        System.out.println("\n1 - Verificar sensores");
        System.out.println("2 - Registrar leituras");
        System.out.println("3 - Rodar previsoes");
        System.out.println("4 - Ver historico por sensor");
        System.out.println("5 - Ver alertas emitidos");
        System.out.println("6 - Gerenciar dados do territorio");
        System.out.println("7 - Exibir status completo");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }
}
