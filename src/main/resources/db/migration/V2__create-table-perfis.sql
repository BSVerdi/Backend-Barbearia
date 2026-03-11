CREATE TABLE perfis(

    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(128) NOT NULL UNIQUE

);

CREATE TABLE usuarios_perfis(

    usuario_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,

    PRIMARY KEY(usuario_id, perfil_id),
    CONSTRAINT usuarios_perfis_fk_usuario FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT usuarios_perfis_fk_perfil FOREIGN KEY(perfil_id) REFERENCES perfis(id)

);

INSERT INTO perfis(nome) VALUE('CLIENTE');
INSERT INTO perfis(nome) VALUE('ADMIN');
INSERT INTO perfis(nome) VALUE('COLABORADOR');