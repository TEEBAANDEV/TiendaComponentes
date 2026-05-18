CREATE TABLE envios(
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       id_recibo BIGINT,
                       id_usuario BIGINT,
                       direccion_destino VARCHAR(500),
                       empresa_transporte VARCHAR(200),
                       codigo_seguimiento TEXT,
                       estado_envio VARCHAR(50),
                       fecha_actualizacion DATE,
                       fecha_despacho DATETIME
);