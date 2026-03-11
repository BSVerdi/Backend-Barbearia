package br.com.alvorada.barbearia.model;

import br.com.alvorada.barbearia.dto.CadastroUsuarioDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nome;
  private String email;
  private String senha;

  private Boolean ativo;

  @ManyToMany(fetch = FetchType.EAGER)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinTable(
          name = "usuarios_perfis",
          joinColumns = @JoinColumn(name = "usuario_id"),
          inverseJoinColumns = @JoinColumn(name = "perfil_id")
  )
  private List<Perfil> perfis = new ArrayList<>();

  public Usuario(CadastroUsuarioDto dto, String senhaCriptografada, Perfil perfil) {

    this.nome = dto.nome();
    this.email = dto.email();
    this.senha = senhaCriptografada;

    this.ativo = true;

    this.perfis.add(perfil);

  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return perfis;
  }

  @Override
  public @Nullable String getPassword() {
    return senha;
  }

  @Override
  public String getUsername() {
    return email;
  }

}
