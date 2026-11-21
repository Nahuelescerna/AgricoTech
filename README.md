# Tecnomundo Connect

Aplicación Android que conecta a productores agrícolas con aplicadores de servicios de fumigación mediante drones.

Proyecto académico para **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC).
Los datos son ficticios y la app funciona como MVP: no incluye pagos, contratación ni ejecución real del servicio.

---

## Funcionalidades

- **Registro y login** con dos roles: productor o aplicador. Los usuarios se guardan en Room.
- **Publicar solicitud** (productor): cultivo, hectáreas, tipo de producto, turno, obstáculos, fotos con la cámara y coordenadas del teléfono.
- **Explorar solicitudes** (aplicador): lista ordenable por cercanía real (GPS), puntaje o cultivo.
- **Enviar cotización**: tarifa por hectárea en UF, con total calculado automáticamente.
- **Comparar propuestas** (productor): ordenadas por cercanía, calificación y especialidad (RNF-04); permite aceptar una.
- **Calificar al aplicador** de 1 a 5 estrellas con comentario.
- **Perfil** con los datos del usuario y el promedio de calificaciones.

Las solicitudes, cotizaciones y calificaciones se persisten en una **API externa (Xano)**; los usuarios y sus contraseñas viven en **Room**.

---

## Tecnologías

| Elemento | Versión |
|---|---|
| Kotlin | 2.2.10 |
| Jetpack Compose (BOM) | 2026.02.01 |
| Material 3 | incluido en la BOM |
| Navigation Compose | 2.10.2 |
| Room (KSP) | 2.8.5 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| kotlinx.serialization | 1.9.0 |
| Android Gradle Plugin | 9.3.0 |
| minSdk / targetSdk | 24 / 37 |

> El proyecto usa AGP 9.3.0. Si Android Studio ofrece actualizar a una versión mayor, hay que rechazarlo: la versión mayor no compila con esta configuración.

---

## Arquitectura

La app sigue **MVVM** con una única dirección de dependencias:

```
ui/screens  →  ui/viewmodel  →  data/repository  →  data/remote  (Retrofit + Xano)
                                              └──►  data/local   (Room)
                                domain/  ←  modelos y reglas sin dependencias de Android
```

- **`ui/screens`**: solo composición y eventos del usuario. No hacen llamadas de red.
- **`ui/viewmodel`**: exponen un `StateFlow` con el estado de la pantalla (`cargando`, `error`, datos) y lanzan las operaciones en `viewModelScope`.
- **`data/repository`**: única fuente de verdad para la UI. Mantienen una caché en `StateFlow` y hablan con la API mediante funciones `suspend` que devuelven `Result`.
- **`data/remote`**: cliente Retrofit, DTOs y mapeo a dominio. La UI nunca ve un DTO.
- **`data/local`**: Room para los usuarios.
- **`domain`**: modelos, validaciones, hash de contraseñas, cálculo de distancias y orden de propuestas. Es Kotlin puro, por eso se puede probar con JUnit sin Android.

---

## API externa (Xano)

La URL base está en `data/remote/RetrofitCliente.kt`:

```
https://x8ki-letl-twmt.n7.xano.io/api:lgIQKxKC/
```

| Método | Ruta | Uso en la app |
|---|---|---|
| `GET` | `/solicitud` | Lista del home |
| `GET` | `/solicitud/{id}` | Detalle de la solicitud |
| `POST` | `/solicitud` | Publicar una solicitud |
| `GET` | `/cotizacion` | Propuestas para comparar |
| `POST` | `/cotizacion` | Enviar una cotización |
| `PATCH` | `/cotizacion/{id}` | Aceptar una propuesta |
| `GET` | `/calificacion` | Promedio del aplicador |
| `POST` | `/calificacion` | Calificar el servicio |

Son 4 lecturas y 4 escrituras (el mínimo pedido era 7 endpoints con al menos un `GET` y un `POST`).

Al entrar al home se sincronizan las tres listas. Si la red falla, las pantallas muestran un mensaje con botón **Reintentar**; el detalle cae a la copia en caché. Los `POST` y el `PATCH` deshabilitan su botón mientras envían y muestran el error sin perder el formulario.

---

## Recursos nativos

**Cámara.** `ActivityResultContracts.TakePicture` con `FileProvider`. No se declara el permiso `CAMERA` porque la foto se toma con la app de cámara del sistema. Las imágenes quedan en la caché del teléfono.

**GPS.** `LocationManager` (sin Google Play Services) con permisos `ACCESS_COARSE_LOCATION` y `ACCESS_FINE_LOCATION`. Se usa la última ubicación conocida o se pide una lectura con un máximo de 12 segundos. Con esas coordenadas:

- el home calcula la distancia real a cada parcela con la **fórmula de Haversine** (`domain/geo/Distancia.kt`);
- al publicar se envían `latitud` y `longitud` en el `POST /solicitud`.

Si el usuario niega el permiso o no hay señal, la app sigue funcionando con las distancias que vienen de la API.

---

## Pruebas unitarias

```bash
JAVA_HOME=/ruta/al/jbr ./gradlew :app:testDebugUnitTest
```

| Archivo | Qué comprueba |
|---|---|
| `ValidacionesTest` | Email, RUT (solo formato) y teléfono móvil |
| `OrdenPropuestasTest` | RNF-04: cercanía → puntaje → especialidad, sin descartar propuestas |

El reporte HTML queda en `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## Cómo compilar

Requisitos: Android Studio con su JDK (JBR), SDK 37 y un emulador o teléfono con Android 7.0 (API 24) o superior.

```bash
# APK de depuración
JAVA_HOME=/ruta/al/jbr ./gradlew :app:assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Para usar las funciones de ubicación, el emulador debe tener una ubicación configurada; para la cámara, una cámara virtual o un teléfono físico.

---

## Estructura del proyecto

```
app/src/main/java/cl/duoc/agricotech/
├── MainActivity.kt
├── data/
│   ├── local/          → Room: entidad, DAO y base de datos
│   ├── location/       → lectura de ubicación con LocationManager
│   ├── remote/         → Retrofit, DTOs y mapeo a dominio
│   └── repository/     → solicitudes, cotizaciones, calificaciones y sesión
├── domain/
│   ├── geo/            → distancia entre coordenadas (Haversine)
│   ├── model/          → RolUsuario, Solicitud, Cotizacion, Calificacion, Ubicacion
│   ├── orden/          → orden de propuestas (RNF-04)
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

- Los **usuarios** se guardan en Room; el resto de los datos vive en Xano.
- La **sesión no persiste** entre reinicios: al cerrar la app hay que volver a entrar.
- Las **fotos** tomadas con la cámara quedan en el teléfono; la API guarda los datos de la solicitud, no las imágenes.
- El **chat interno** entre productor y aplicador no está implementado; su pestaña queda como vista previa de una funcionalidad futura.
- La comuna de las solicitudes publicadas queda fija en "Rancagua"; lo que viene del GPS son las coordenadas.
- La validación de **RUT es solo de formato** (7-8 dígitos, guion y dígito verificador), sin módulo 11.
- Las contraseñas se guardan con hash **SHA-256**; en producción correspondería bcrypt o Argon2.
