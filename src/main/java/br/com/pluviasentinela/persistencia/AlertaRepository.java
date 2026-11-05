package br.com.pluviasentinela.persistencia;

import br.com.pluviasentinela.dominio.Alerta;
import br.com.pluviasentinela.dominio.NivelAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findBySensorIdentificadorOrderByEmitidoEmDesc(String sensorIdentificador);

    List<Alerta> findByNivelOrderByEmitidoEmDesc(NivelAlerta nivel);

    List<Alerta> findAllByOrderByEmitidoEmDesc();
}
