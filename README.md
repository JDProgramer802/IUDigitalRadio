<p align="center">
  <img src="app/src/main/res/drawable-nodpi/logo_iudigital_radio.png" alt="IU Digital Radio" width="180" />
</p>

# IU Digital Radio

Aplicación Android nativa (Kotlin + Jetpack Compose) para escuchar **IU Digital Radio**, la emisora
oficial de la Institución Universitaria Digital de Antioquia, y descubrir otras emisoras del mundo.

Proyecto académico — *Evidencia de aprendizaje 3 – Taller práctico: Aplicación Móvil Android "IU Digital Radio"*.

📄 **Manual técnico completo:** [docs/MANUAL_TECNICO.md](docs/MANUAL_TECNICO.md)

## Funcionalidades

- Reproducción en vivo del **stream oficial** de IU Digital Radio con Media3 ExoPlayer.
- Controles **Play, Pause y Mute** con **vibración** al presionarlos.
- Estado "EN VIVO", vinilo animado, visualizador y título de lo que suena (si la emisora lo envía).
- **Otras emisoras** reales desde la API gratuita [Radio Browser](https://www.radio-browser.info),
  organizadas por tipo de radio (Colombia, Populares, Rock, Pop, Salsa, Jazz, Clásica).
- Cambio de emisora desde una **LazyColumn**, con buscador.
- Foto de perfil con la **cámara** (`TakePicturePreview`) o desde la **galería**.
- Estado conservado al rotar el celular (`mutableStateOf`, `rememberSaveable`, `ViewModel`).
- Tema claro y oscuro, y manejo de errores de red, de stream y de permisos.

## Tecnologías

Kotlin 2.2 · Jetpack Compose (Material 3) · Navigation Compose · ViewModel · Media3 ExoPlayer ·
Coil · Radio Browser API · minSdk 24 · targetSdk 37

## Requisitos

- Android Studio reciente (compatible con Android Gradle Plugin 9.3).
- Android SDK Platform 37.
- JDK 25: el JBR que trae Android Studio sirve.
- Internet la primera vez, para descargar las dependencias.

## Instalación y ejecución

1. Clonar el repositorio y abrirlo en Android Studio (**File → Open**).
2. Esperar el **Gradle Sync**.
3. Conectar un celular con depuración USB (o iniciar un emulador) y presionar **Run ▶**.

## Generar el APK

Desde Android Studio: **Build → Generate App Bundles or APKs → Generate APKs**.

Desde PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleDebug      # app/build/outputs/apk/debug/app-debug.apk
.\gradlew.bat assembleRelease    # app/build/outputs/apk/release/app-release.apk (optimizado)
```

> El APK release se firma con la llave de depuración para que sea instalable en la entrega académica.
> Para publicar en Google Play se debe usar una llave propia, sin subirla al repositorio.

## Configuración del streaming oficial

La URL está en `app/src/main/java/com/iudigital/iudigitalradio/data/OfficialRadio.kt`:

```kotlin
const val OFFICIAL_RADIO_STREAM = "https://streamingcwsradio30.com:8316/stream"
```

Se tomó del reproductor que incrusta la página oficial <https://www.iudigital.edu.co/index.php/radio>.
El servidor se identifica como `icy-name: IU Digital Radio` (AAC+, 128 kbps). Si la institución
cambia de proveedor, solo hay que actualizar esa constante.

## Configuración de Radio Browser

No requiere API key ni configuración. El cliente (`data/remote/RadioBrowserApi.kt`) consulta
`https://all.api.radio-browser.info` y, si falla, `https://de1.api.radio-browser.info`. Solo se
muestran emisoras HTTPS en MP3 o AAC. Si la API no responde, la emisora oficial sigue disponible.

## Estructura del proyecto

```
app/src/main/java/com/iudigital/iudigitalradio/
├── MainActivity.kt
├── audio/         RadioPlayer (ExoPlayer) y PlayerState
├── camera/        Cámara, galería y almacenamiento de la foto
├── data/          Emisora oficial, modelos, cliente de Radio Browser y repositorio
├── permissions/   Manejo de permisos en tiempo de ejecución
├── ui/            ViewModel, navegación, pantallas, componentes y tema
└── utils/         Vibración y mensajes de error
```

## Pruebas

```powershell
.\gradlew.bat testDebugUnitTest            # Pruebas unitarias (JVM)
.\gradlew.bat connectedDebugAndroidTest    # Pruebas de interfaz (requiere celular o emulador)
```

## Permisos

Solo `CAMERA` (se pide al tomar la foto), `VIBRATE` e `INTERNET`. La galería usa el selector de
fotos del sistema, que no requiere permisos.

## Equipo

Proyecto desarrollado con el esquema **A2 – Casa de Desarrollo Unificada**: cada rol aporta un módulo
al repositorio central por medio de ramas `feature/…` y *pull requests*.

| Rol | Integrante | Módulo |
|---|---|---|
| UI/UX & Layout | [JDProgramer802](https://github.com/JDProgramer802) | Tema, pantallas y componentes visuales en Compose |
| Product Owner & Tech Lead | [danielaanzueta-eng](https://github.com/danielaanzueta-eng) | Estructura del proyecto, convenciones e integración |
| State & Logic | [angela-iu](https://github.com/angela-iu) | ViewModel, estado, navegación e interacción |
| Core Hardware & Permissions | [sebastian9330-gif](https://github.com/sebastian9330-gif) | Cámara, galería, vibración y permisos |
| Audio & API Integration | [Danilo0528](https://github.com/Danilo0528) | Media3 ExoPlayer, stream oficial y Radio Browser |
| QA & DevOps / Release | [dcalle2025](https://github.com/dcalle2025) | Pruebas, build de release, APK y documentación |

Detalle de responsabilidades en la sección "Equipo y roles" del
[manual técnico](docs/MANUAL_TECNICO.md#21-equipo-y-roles).
