CREATE TABLE carrito_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL ,
    id_producto BIGINT NOT NULL ,
    cantidad int
);