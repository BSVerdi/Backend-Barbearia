package br.com.alvorada.barbearia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

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

  @Enumerated(EnumType.STRING)
  private Perfil perfil;

  public Usuario(String nome, String email, String senhaCriptografada, Perfil perfil) {
    this.nome = nome;
    this.email = email;
    this.senha = senhaCriptografada;
    this.perfil = perfil;
    this.ativo = true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
  }

  @Override
  public @Nullable String getPassword() {
    return senha;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public void atualizarPerfil(@NotBlank Perfil perfil) {
    this.perfil = perfil;
  }

  public void desativar() {
    this.ativo = false;
  }

  public void atualizarNome(String nome) {
    if (nome != null) {
      this.nome = nome;
    }
  }

  public boolean ehDono(Long id) {
    return this.id == id;
  }

  public boolean ehAdmin() {
    return this.perfil == Perfil.ADMIN;
  }

  public void ativar() {
    this.ativo = true;
  }
}
