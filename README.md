# Proyecto Final - Claro Colombia (Turnero + Tienda)

Aplicacion full stack desarrollada como proyecto final academico. Simula la experiencia de un cliente de **Claro Colombia**: solicitar turnos en sucursal, comprar planes, gestionar cuenta y consultar historial.

| Capa | Tecnologia |
|------|------------|
| Frontend | Angular 21, TypeScript, RxJS |
| Backend | Spring Boot 4, Java 17, JPA/Hibernate |
| Base de datos | MySQL 8 (turnero_claro) |
| API REST | Spring Boot en puerto 8080 |

**Autor:** Duvan

---

## Estructura del proyecto

```
proyecto_FinalSandra/
├── backend/claro/           API REST (Spring Boot)
│   ├── src/main/java/       Controladores, servicios, modelos
│   ├── turnero_claro_db.sql Script de creacion de BD
│   └── pom.xml
├── frontend/                Aplicacion Angular
│   ├── src/app/             Paginas y servicios
│   └── proxy.conf.json      Proxy /api -> localhost:8080
└── README.md
```

---

## Funcionalidades principales

### Modulo Turnero
- Solicitud de turnos por departamento.
- Cola con prioridad y atencion preferencial.
- Algoritmo ratio 3:1 y aging (15 min).
- Simulador automatico cada 60 segundos con notificacion por correo.

### Modulo Tienda y compras
- Catalogo de planes, carrito e historial de compras.

### Modulo Usuarios
- Registro, login y panel de cuenta.

---

## Requisitos previos

- Java 17+
- Maven (o IDE con soporte Spring Boot)
- Node.js 20+ y npm
- Angular CLI: npm install -g @angular/cli
- MySQL 8

---

## 1. Base de datos

1. Ejecutar backend/claro/turnero_claro_db.sql en MySQL.
2. Configurar en backend/claro/src/main/resources/application.properties:

```properties
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASENA
server.port=8080
```

---

## 2. Backend (Spring Boot)

```bash
cd backend/claro
mvn spring-boot:run
```

O ejecutar com.example.claro.ClaroApplication desde el IDE.

URL: http://localhost:8080

---

## 3. API REST - Endpoints principales

Base URL: **http://localhost:8080**

| Modulo | Base | Operaciones |
|--------|------|-------------|
| Autenticacion | /api/auth | POST /registro, POST /login |
| Departamentos | /api/departamentos | GET, GET /{id} |
| Turnos | /api/turnos | GET /cola, GET /usuario/{id}, POST, PUT /siguiente/llamar, PUT /{id}/llamar, PUT /{id}/atender, PUT /{id}/cancelar |
| Planes | /api/planes | GET, GET /tipo/{tipo}, GET /{id} |
| Compras | /api/compras | GET /usuario/{id}, POST |

Ejemplo POST /api/turnos:

```json
{
  "usuarioId": 1,
  "departamentoId": 1,
  "esPrioritario": false
}
```

---

## 4. Frontend (Angular)

```bash
cd frontend
npm install
ng serve
```

URL: http://localhost:4200

| Ruta | Descripcion |
|------|-------------|
| /inicio | Pagina principal |
| /turnero | Sistema de turnos |
| /tienda | Catalogo de planes |
| /carrito | Carrito |
| /compras | Historial |
| /mi-cuenta/login | Login |
| /mi-cuenta/registro | Registro |
| /mi-cuenta/panel | Panel cliente |

---

## Orden de ejecucion

1. MySQL + script SQL
2. Backend (puerto 8080)
3. Frontend (puerto 4200)

---

## Notas para evaluacion

- Arquitectura en capas: Controller -> Service -> Repository -> Entity
- Validacion con Jakarta Validation y GlobalExceptionHandler
- Tareas programadas: TurnoSimuladorService (avance automatico de la cola)

---

## Problemas frecuentes

| Sintoma | Solucion |
|---------|----------|
| ECONNREFUSED | Iniciar Spring Boot en 8080 |
| 404 en POST /api/turnos | Reiniciar backend tras cambios |
| Pantalla de turnos vacia | Verificar MySQL y application.properties |

---

Contacto: sernaduvan4@gmail.com