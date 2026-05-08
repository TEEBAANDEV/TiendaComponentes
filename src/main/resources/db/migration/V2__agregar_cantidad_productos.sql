INSERT INTO db_inventario.inventario (id_producto, nombre_producto, descripcion_producto, cantidad)
SELECT
    id,
    nombre,
    descripcion,
    50
FROM db_producto.producto;