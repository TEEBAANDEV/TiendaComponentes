CREATE TABLE carrito_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL ,
    id_producto BIGINT NOT NULL ,
    nombre_producto VARCHAR(100),
    descripcion_producto VARCHAR(200),
    cantidad int
);