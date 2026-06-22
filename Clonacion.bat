@echo off
echo Creando carpeta TiendaComponentes
timeout /t 5
call git clone https://github.com/TEEBAANDEV/TiendaComponentes.git
cd tiendacomponentes
echo clonando repositorio y ramas
timeout /t 5
call git clone -b Producto --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Productos
call git clone -b Inventario --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Inventario
call git clone -b Usuario --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Usuario
call git clone -b Carrito --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Carrito
call git clone -b Ventas --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Ventas
call git clone -b Recibo --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Recibo
call git clone -b Reportes --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Reportes
call git clone -b Envio --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Envios
call git clone -b Comentarios --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Comentarios
call git clone -b Wishlist --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git Wishlist
call git clone -b api-gateway --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git api-gateway
call git clone -b eureka-server --single-branch https://github.com/TEEBAANDEV/TiendaComponentes.git eureka-server
