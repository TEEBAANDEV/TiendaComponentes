CREATE TABLE recibo(
   id_recibo BIGINT PRIMARY KEY AUTO_INCREMENT,
   id_venta  BIGINT,
   id_usuario BIGINT,
    nombre_producto TEXT,
   monto_total DOUBLE,
   metodo_pago VARCHAR(100),
   fecha_emision DATETIME
);