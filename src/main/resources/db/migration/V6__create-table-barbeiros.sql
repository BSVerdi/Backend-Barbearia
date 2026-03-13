CREATE TABLE barbeiros(

    id BIGINT NOT NULL,
    nome VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    senha VARCHAR(128) NOT NULL,
    telefone VARCHAR(128) NOT NULL,
    horario_inicio TIME NOT NULL,
    horario_fim TIME NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT 1

)