# Sistema de Gestión Médica y Monitoreo de Pacientes con Telemedicina

TP individual de la Universidad de Palermo. Backend en **Java 21 + Spring Boot 3.5**, con **PostgreSQL + MongoDB** (base híbrida), **REST + GraphQL** y **2 microservicios** (métricas y notificaciones), más un **frontend React** (web app). Todo está dockerizado.

## Requisitos
Solo **Docker** (Docker Desktop con Docker Compose). No hace falta instalar Java, Maven ni Node.

## Cómo levantarlo
```powershell
docker compose build frontend     
docker compose up -d --build
docker compose ps
```
- El primer arranque tarda varios minutos: descarga imágenes y compila los 4 módulos Java y el front.
- Está listo cuando `docker compose ps` muestra los **8 contenedores**: `core-service`, `metricas-service`, `notificaciones-service` y `frontend` en **(healthy)**, `iot-simulator` **Up**, y `postgres`, `mongo` y `mailhog` arriba.

Para bajarlo: `docker compose down`. Con `docker compose down -v` también se borran los datos (al volver a levantar, se cargan de nuevo los datos de prueba).

## URLs

| Qué | URL |
|---|---|
| **Web app (frontend React)** | **http://localhost:3000** |
| Swagger del core (backend principal) | http://localhost:8080/swagger-ui.html |
| GraphiQL (consola GraphQL) | http://localhost:8080/graphiql |
| Swagger de métricas | http://localhost:8081/swagger-ui.html |
| Swagger de notificaciones | http://localhost:8082/swagger-ui.html |
| MailHog (bandeja de mails de alerta) | http://localhost:8025 |
| Ping de cada servicio | http://localhost:8080/api/ping, :8081/api/ping, :8082/api/ping |

## Usuarios de prueba

| Rol | Email | Contraseña | Id | Qué ve en la web app |
|---|---|---|---|---|
| Admin | `admin@tp.com` | `Admin123!` | 1 | Pacientes y Profesionales (ABM), todas las Citas y Alertas |
| Profesional (Clínica médica) | `clinica@tp.com` | `Medico123!` | 2 | Métricas, Pacientes, Mi agenda, Expedientes, Mis alertas |
| Profesional (Cardiología) | `cardio@tp.com` | `Medico123!` | 3 | Ídem |
| Paciente Ana Gómez | `ana.gomez@tp.com` | `Paciente123!` | 4 | Mis métricas, Mis citas, Mi expediente |
| Paciente Juan Pérez | `juan.perez@tp.com` | `Paciente123!` | 5 | Ídem |
| Paciente Lucía Díaz | `lucia.diaz@tp.com` | `Paciente123!` | 6 | Ídem |

Dispositivos IoT: 1 (oxímetro de Ana), 2 (pulsera de Ana), 3 (tensiómetro de Juan), 4 (termómetro de Juan), 5 (oxímetro de Lucía).


## Estructura
```
core-service/            Backend principal: REST + GraphQL + PostgreSQL
metricas-service/        Microservicio de métricas en tiempo real + MongoDB
notificaciones-service/  Microservicio de notificaciones y alertas (email)
iot-simulator/           Dispositivo IoT simulado (Java puro)
frontend/                Web app React (Vite) servida por nginx, con proxy al core
scripts/                 Smoke tests y empaquetado de la entrega
docs/                    Informe, guías por fase, decisiones y diagramas
```

## Documentación
- [`docs/INFORME.md`](docs/INFORME.md) (y `docs/INFORME.docx`): informe del TP.
- [`docs/GUIA_DEL_CODIGO.md`](docs/GUIA_DEL_CODIGO.md): mapa de archivos y en qué orden leerlos.
- [`docs/guia/`](docs/guia/): qué se hizo en cada fase (00–11) y preguntas para la defensa.
- [`docs/DECISIONES.md`](docs/DECISIONES.md) y [`docs/MEJORAS_FUTURAS.md`](docs/MEJORAS_FUTURAS.md).
- [`docs/diagramas/`](docs/diagramas/): DER, UML y Diagrama de Sistema.
