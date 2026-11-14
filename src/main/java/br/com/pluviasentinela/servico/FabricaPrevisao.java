package br.com.pluviasentinela.servico;

import br.com.pluviasentinela.dominio.EstrategiaPrevisao;
import br.com.pluviasentinela.dominio.PrevisaoComparacao;
import br.com.pluviasentinela.dominio.PrevisaoMediaMovel;
import br.com.pluviasentinela.dominio.TipoPrevisao;
import br.com.pluviasentinela.excecao.DadosInvalidosException;
import org.springframework.stereotype.Component;

@Component
public class FabricaPrevisao {

    public EstrategiaPrevisao criar(TipoPrevisao tipo) {
        if (tipo == null) {
            throw new DadosInvalidosException("tipoPrevisao", null,
                    "o tipo de previsao deve ser informado");
        }

        return switch (tipo) {
            case MEDIA_MOVEL -> new PrevisaoMediaMovel();
            case COMPARACAO -> new PrevisaoComparacao();
        };
    }
}
