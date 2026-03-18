package br.com.alvorada.barbearia.Factory;

import br.com.alvorada.barbearia.model.Perfil;
import br.com.alvorada.barbearia.model.Usuario;

public class UsuarioFactory {

  public static Usuario criarUsuarioComTodosOsCampos() {
    return new Usuario(
            1L,
            "exemplo",
            "exemplo@email.com",
            "exemploSenha",
            true,
            Perfil.CLIENTE
    );
  }
}
