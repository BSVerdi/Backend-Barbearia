package br.com.alvorada.barbearia.repository;

import br.com.alvorada.barbearia.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
  List<Agendamento> findByClienteId(Long id);

  List<Agendamento> findByBarbeiroId(Long barbeiroId);

  boolean existsByBarbeiroIdAndDataHora(Long barbeiroId, LocalDateTime dataHora);

  boolean existsByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);
}
