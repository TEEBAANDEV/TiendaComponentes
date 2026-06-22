@echo off
echo Iniciando compilacion de microservicios
timeout /t 5
cd Productos
call .\mvnw.cmd compile package
cd ..\Inventario
call .\mvnw.cmd compile package
cd ..\Usuario
call .\mvnw.cmd compile package
cd ..\Carrito
call .\mvnw.cmd compile package
cd ..\Ventas
call .\mvnw.cmd compile package
cd ..\Recibo
call .\mvnw.cmd compile package
cd ..\Reportes
call .\mvnw.cmd compile package
cd ..\Envios
call .\mvnw.cmd compile package
cd ..\Comentarios
call .\mvnw.cmd compile package
cd ..\Wishlist
call .\mvnw.cmd compile package
cd ..\api-gateway
call .\mvnw.cmd compile package
cd ..\eureka-server
call .\mvnw.cmd compile package
cd ..
echo Iniciando empaquetado para Docker
timeout /t 3
call docker compose build
call docker compose up -d
echo Proceso Finalizado!