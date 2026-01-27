<?php
// Datos de conexión
$host = "localhost";  // Servidor de MySQL
$usuario = "root";    // Usuario de MySQL (por defecto en XAMPP es root)
$clave = "";         // La contraseña está vacía por defecto
$base_de_datos = "artesluisdb";  // Nombre de tu base de datos

// Crear la conexión
$conexion = new mysqli($host, $usuario, $clave, $base_de_datos);

// Verificar si la conexión fue exitosa
if ($conexion->connect_error) {
    die("Conexión fallida: " . $conexion->connect_error);
}
?>
