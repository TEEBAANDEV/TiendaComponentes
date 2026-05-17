CREATE TABLE reporte (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_Recibo BIGINT NOT NULL ,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_reporte VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);