# PanalSuite API

API REST de prueba para la app PanalSuite, hecha con [json-server](https://github.com/typicode/json-server).

## Cómo ejecutarla

Requiere Node.js.

```bash
cd api
npm install
npm start
```

La API queda en `http://localhost:3000`. Desde el emulador de Android se accede con `http://10.0.2.2:3000/`.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/users?email={email}&password={password}` | Login: devuelve el usuario si las credenciales coinciden |
| GET | `/tickets` | Lista todas las solicitudes |
| GET | `/tickets?requesterId={id}` | Solicitudes de un solicitante |
| GET | `/tickets/{id}` | Detalle de una solicitud |
| POST | `/tickets` | Crea una solicitud |
| PATCH | `/tickets/{id}` | Actualiza el estado de una solicitud |

## Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Solicitante | solicitante@panalsuite.cl | panal2026 |
| Resolutor | resolutor@panalsuite.cl | panal2026 |
| Supervisor | supervisor@panalsuite.cl | panal2026 |

Son cuentas ficticias, solo para pruebas.
