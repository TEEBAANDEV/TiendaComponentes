CREATE TABLE inventario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_producto BIGINT,
    nombre_producto VARCHAR(100),
    descripcion_producto VARCHAR(200),
    cantidad int
);