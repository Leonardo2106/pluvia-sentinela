package br.com.pluviasentinela.persistencia;

import br.com.pluviasentinela.dominio.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    List<Medicao> findBySensorIdentificadorOrderByRegistradaEmDesc(String sensorIdentificador);

    List<Medicao> findByRegiaoOrderByRegistradaEmDesc(String regiao);

    List<Medicao> findAllByOrderByRegistradaEmDesc();
}
