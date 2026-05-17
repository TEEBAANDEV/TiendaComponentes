CREATE TABLE IF NOT EXISTS resenas(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    calificacion INT NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
    comentario VARCHAR(500),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);