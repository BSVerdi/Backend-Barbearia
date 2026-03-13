CREATE TABLE agendamentos(

    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(64) NOT NULL,
    cliente_id BIGINT NOT NULL,
    barbeiro_id BIGINT NOT NULL,

    CONSTRAINT fk_agendamentos_cliente_id FOREIGN KEY(cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_agendamentos_barbeiro_id FOREIGN KEY(barbeiro_id) REFERENCES barbeiros(id)

);