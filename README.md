# Tecnomundo Connect

Aplicación Android que conecta a productores agrícolas con aplicadores de servicios de fumigación mediante drones.

Proyecto académico para **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC).
Los datos son ficticios y la app funciona como MVP: no incluye pagos, contratación ni ejecución real del servicio.

---

## Funcionalidades

- **Registro y login** con dos roles: productor o aplicador. Los usuarios se guardan en Room.
- **Publicar solicitud** (productor): cultivo, hectáreas, tipo de producto, turno, obstáculos y fotos tomadas con la cámara.
- **Explorar solicitudes** (aplicador): lista ordenable por cercanía, puntaje o cultivo.
- **Enviar cotización**: tarifa por hectárea en UF, con total calculado automáticamente.
- **Comparar propuestas** (productor): ordenadas por cercanía, calificación y especialidad; permite aceptar una.
- **Calificar al aplicador** de 1 a 5 estrellas con comentario.
- **Perfil** con los datos del usuario y el promedio de calificaciones.

---

## Tecnologías

| Elemento | Versión |
|---|---|
| Kotlin | 2.2.10 |
| Jetpack Compose (BOM) | 2026.02.01 |
| Material 3 | incluido en la BOM |
| Navigation Compose | 2.10.2 |
| Room (KSP) | 2.8.5 |
| Android Gradle Plugin | 9.3.0 |
| minSdk / targetSdk | 24 / 37 |

Arquitectura **MVVM**:

```
ui/         pantallas, componentes y ViewModels
domain/     modelos, validaciones y utilidades
data/       repositorios y base de datos Room
```

---

## Cómo ejecutar

1. Abrir la carpeta del proyecto en Android Studio.
2. Esperar el **Gradle Sync**.
3. Ejecutar con **Run 'app'** en un emulador o dispositivo con Android 7.0 (API 24) o superior.
4. Para usar la cámara, el emulador debe tener una cámara configurada, o usar un teléfono físico.

> Nota: el proyecto usa AGP 9.3.0. Si Android Studio ofrece actualizar a una versión mayor, conviene rechazarlo para no romper la compilación.

---

## Estructura del proyecto

```
app/src/main/java/cl/duoc/agricotech/
├── MainActivity.kt
├── data/
│   ├── local/          → Room: entidad, DAO y base de datos
│   └── repository/     → usuarios, solicitudes, cotizaciones y calificaciones
├── domain/
│   ├── model/          → RolUsuario, Solicitud, Cotizacion, Calificacion
│   ├── security/       → hash de contraseñas
│   └── validation/     → validaciones de formularios
└── ui/
    ├── components/     → componentes reutilizables
    ├── navigation/     → rutas de la aplicación
    ├── screens/        → una pantalla por archivo
    ├── theme/          → colores, tipografía y tema
    └── viewmodel/      → estado y lógica de cada pantalla
```

---

## Alcance y limitaciones

- Todos los datos son ficticios, según lo exige el caso.
- Los **usuarios** se guardan en Room; las solicitudes, cotizaciones y calificaciones viven en memoria durante la sesión.
- El **chat interno** entre productor y aplicador no está implementado.
- Las contraseñas se guardan con hash SHA-256 (para el alcance del MVP; lo ideal en producción sería bcrypt o Argon2).
- La comuna de las solicitudes publicadas queda fija en "Rancagua" para las pruebas.

---

