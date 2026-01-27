<?php
// Incluir el archivo de conexión
include 'conexion.php';

$mensaje_enviado = false; // Variable para verificar si el mensaje se envió correctamente

// Verificar si el formulario ha sido enviado
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Recoger los datos del formulario
    $nombre = $_POST['nombres'];
    $celular = $_POST['celular'];
    $email = $_POST['email'];
    $plan = $_POST['plan'];
    $mensaje = $_POST['message'];

    // Preparar la consulta SQL para insertar los datos en la base de datos
    $sql = "INSERT INTO contacto (nombre, celular, email, plan, mensaje)
            VALUES ('$nombre', '$celular', '$email', '$plan', '$mensaje')";

    // Ejecutar la consulta y verificar si la inserción fue exitosa
    if ($conexion->query($sql) === TRUE) {
        $mensaje_enviado = true; // Si se insertó correctamente, asignamos true
    } else {
        echo "Error: " . $sql . "<br>" . $conexion->error;
    }
}

// Cerrar la conexión a la base de datos
$conexion->close();
?>

<!-- Mostrar mensaje de éxito si $mensaje_enviado es true -->
<?php if ($mensaje_enviado): ?>
    <script>
        alert('Mensaje enviado con éxito!');
        window.location.href = 'contacto.html'; // Redirigir después de mostrar el mensaje
    </script>
<?php endif; ?>
