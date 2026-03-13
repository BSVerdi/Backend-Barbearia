package br.com.alvorada.barbearia.model;

import br.com.alvorada.barbearia.dto.cliente.AtualizacaoDadosClienteDto;
import br.com.alvorada.barbearia.dto.cliente.CadastroClienteDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

  @Id
  private Long id;

  private String nome;
  private String email;
  private String telefone;
  private Boolean ativo;

//  @OneToMany
//  private List<Agendamento> agentamentos = new ArrayList<>();

  public Cliente(Long id, CadastroClienteDto dto) {

    this.id = id;
    this.nome = dto.nome();
    this.email = dto.email();
    this.telefone = dto.telefone();
    this.ativo = true;

  }

  public void desativar() {
    this.ativo = false;
  }

  public void atualizarDados(AtualizacaoDadosClienteDto dto) {
    if (dto.nome() != null) {
      this.nome = dto.nome();
    }

    if (dto.telefone() != null) {
      this.telefone = dto.telefone();
    }
  }

  public void ativar() {
    this.ativo = true;
  }
}
