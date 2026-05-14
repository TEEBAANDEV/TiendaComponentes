CREATE TABLE envio(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   id_recibo BIGINT,
    id_usuario BIGINT,
    direccion_destino VARCHAR(500),
    empresa_transporte VARCHAR(500),
    codigo_seguimiento VARCHAR(20),
    estado_envio VARCHAR(50),
    fecha_actualizacion DATE,
    fecha_despacho DATETIME DEFAULT CURRENT_TIMESTAMP
);
