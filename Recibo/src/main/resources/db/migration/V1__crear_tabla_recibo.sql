CREATE TABLE recibo(
   idRecibo BIGINT PRIMARY KEY AUTO_INCREMENT,
   idVenta  BIGINT,
   idUsuario BIGINT,
    nombreProducto TEXT,
   montoTotal DOUBLE,
   metodoPago VARCHAR(100),
   fechaEmision DATETIME
);