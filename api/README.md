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

| Método | Ruta | Uso en la app |
|---|---|---|
| GET | `/users?email={email}&password={password}` | Login: devuelve el usuario si las credenciales coinciden |
| GET | `/tickets` | Descarga las solicitudes para la bandeja |
| GET | `/tickets/{id}` | Detalle de una solicitud |
| POST | `/tickets` | Crea una solicitud |
| PUT | `/tickets/{id}` | Actualiza una solicitud (estado, asignado e historial) |

Los datos quedan guardados en `db.json`, así que los cambios hechos desde la app se mantienen al reiniciar el servidor.

## Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Solicitante | solicitante@panalsuite.test | Demo1234 |
| Resolutor | resolutor@panalsuite.test | Demo1234 |
| Resolutor | resolutor2@panalsuite.test | Demo1234 |
| Supervisor | supervisor@panalsuite.test | Demo1234 |

Son cuentas ficticias, solo para pruebas.
