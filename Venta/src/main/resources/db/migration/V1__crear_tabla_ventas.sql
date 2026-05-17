CREATE TABLE ventas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    total DOUBLE NOT NULL,
    fecha DATETIME NOT NULL,
    estado VARCHAR(50) NOT NULL
);

CREATE TABLE venta_detalles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    venta_id BIGINT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL ,
    CONSTRAINT fk_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE
);