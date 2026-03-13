package br.com.alvorada.barbearia.model;

import br.com.alvorada.barbearia.dto.barbeiro.AtualizacaoDadosBarbeiroDto;
import br.com.alvorada.barbearia.dto.barbeiro.CadastroBarbeiroDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "barbeiros")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Barbeiro {

  @Id
  private Long id;

  private String nome;
  private String email;
  private String telefone;

  private LocalTime horarioInicio;
  private LocalTime horarioFim;

  private Boolean ativo;

  @OneToMany(mappedBy = "barbeiro")
  private List<Agendamento> agendamentos;

  public Barbeiro(Long id, CadastroBarbeiroDto dto) {
    this.id = id;
    this.nome = dto.nome();
    this.email = dto.email();
    this.telefone = dto.telefone();

    this.horarioInicio = LocalTime.parse(dto.horarioInicio());
    this.horarioFim = LocalTime.parse(dto.horarioFim());

    this.ativo = true;
  }

  public void atualizarDados(AtualizacaoDadosBarbeiroDto dto) {
    if (dto.nome() != null) {
      this.nome = dto.nome();
    }

    if (dto.telefone() != null) {
      this.telefone = dto.telefone();
    }

    if (dto.horarioInicio() != null) {
      this.horarioInicio = dto.horarioInicio();
    }

    if (dto.horarioFim() != null) {
      this.horarioFim = dto.horarioFim();
    }
  }

  public void desativar() {
    this.ativo = false;
  }

  public void ativar() {
    this.ativo = true;
  }
}
