package br.com.alvorada.barbearia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime dataHora;

  @Enumerated(EnumType.STRING)
  private StatusAgendamento status;

  @ManyToOne(fetch = FetchType.LAZY)
  private Cliente cliente;

  @ManyToOne(fetch = FetchType.LAZY)
  private Barbeiro barbeiro;


  public Agendamento(String dataHora, Cliente cliente, Barbeiro barbeiro) {
    this.dataHora = LocalDateTime.parse(dataHora);
    this.cliente = cliente;
    this.barbeiro = barbeiro;
    this.status = StatusAgendamento.ATIVO;
  }

  public boolean estaAtivo() {
    return this.status == StatusAgendamento.ATIVO;
  }

  public void cancelarAgendamento() {
    this.status = StatusAgendamento.CANCELADO;
  }

  public void concluirAgendamento() {
    this.status = StatusAgendamento.CONCLUIDO;
  }

}
