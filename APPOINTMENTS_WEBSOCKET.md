# Sistema de WebSocket para Appointments (Citas)

## Descripción General

El sistema de appointments está completamente integrado con WebSocket para proporcionar notificaciones en tiempo real sobre cambios en las citas. Esto permite que los clientes reciban actualizaciones inmediatas sin necesidad de hacer polling.

## Topics WebSocket Disponibles

### Topics Generales

- `/topic/appointments/new` - Nueva cita creada
- `/topic/appointments/updated` - Cita actualizada
- `/topic/appointments/cancelled` - Cita cancelada/eliminada
- `/topic/appointments/status-changed` - Cambio de estado de cita
- `/topic/appointments/by-date-consulted` - Consulta de citas por fecha

### Topics por Teléfono de Contacto

- `/topic/phone/{phoneNumber}/appointments/new` - Nueva cita para un teléfono específico
- `/topic/phone/{phoneNumber}/appointments/updated` - Cita actualizada para un teléfono específico
- `/topic/phone/{phoneNumber}/appointments/cancelled` - Cita cancelada para un teléfono específico
- `/topic/phone/{phoneNumber}/appointments/status-changed` - Cambio de estado para un teléfono específico

## Eventos que Disparan Notificaciones

### 1. Creación de Cita
- **Trigger**: `POST /api/appointments`
- **Notificación**: `/topic/appointments/new`
- **Payload**: Objeto `Appointment` completo

### 2. Actualización de Cita
- **Trigger**: `PUT /api/appointments/{id}`
- **Notificación**: `/topic/appointments/updated`
- **Payload**: Objeto `Appointment` actualizado

### 3. Eliminación de Cita
- **Trigger**: `DELETE /api/appointments/{id}`
- **Notificación**: `/topic/appointments/cancelled`
- **Payload**: Objeto `Appointment` eliminado

### 4. Cambio de Estado
- **Trigger**: `PATCH /api/appointments/{id}/status?newStatus={status}`
- **Notificación**: `/topic/appointments/status-changed`
- **Payload**: 
```json
{
  "appointment": { /* objeto appointment */ },
  "oldStatus": "UNASSIGNED",
  "newStatus": "CONFIRMED",
  "timestamp": 1234567890
}
```

### 5. Consulta por Fecha
- **Trigger**: `GET /api/appointments/date/{date}`
- **Notificación**: `/topic/appointments/by-date-consulted`
- **Payload**:
```json
{
  "date": "2024-01-15",
  "appointments": [ /* array de appointments */ ],
  "count": 5,
  "timestamp": 1234567890
}
```

## Estados de Cita Disponibles

- `UNASSIGNED` - Sin asignar (por defecto)
- `RESERVED` - Reservada
- `CONFIRMED` - Confirmada
- `CANCELLED` - Cancelada

## Ejemplo de Implementación del Cliente

### JavaScript/WebSocket

```javascript
// Conectar al WebSocket
const socket = new WebSocket('ws://localhost:8080/ws');

// Suscribirse a notificaciones generales
socket.addEventListener('open', function (event) {
    // Suscribirse a nuevos appointments
    socket.send(JSON.stringify({
        destination: '/topic/appointments/new',
        type: 'SUBSCRIBE'
    }));
    
    // Suscribirse a cambios de estado
    socket.send(JSON.stringify({
        destination: '/topic/appointments/status-changed',
        type: 'SUBSCRIBE'
    }));
});

// Escuchar notificaciones
socket.addEventListener('message', function (event) {
    const data = JSON.parse(event.data);
    
    if (data.destination === '/topic/appointments/new') {
        console.log('Nueva cita:', data.payload);
        // Actualizar UI
    } else if (data.destination === '/topic/appointments/status-changed') {
        console.log('Estado cambiado:', data.payload);
        // Actualizar UI
    }
});
```

### React Hook

```javascript
import { useEffect, useState } from 'react';

export const useAppointmentWebSocket = () => {
    const [socket, setSocket] = useState(null);
    const [appointments, setAppointments] = useState([]);

    useEffect(() => {
        const ws = new WebSocket('ws://localhost:8080/ws');
        
        ws.onopen = () => {
            console.log('WebSocket conectado');
            // Suscribirse a topics
            ws.send(JSON.stringify({
                destination: '/topic/appointments/new',
                type: 'SUBSCRIBE'
            }));
        };
        
        ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            
            if (data.destination === '/topic/appointments/new') {
                setAppointments(prev => [...prev, data.payload]);
            } else if (data.destination === '/topic/appointments/updated') {
                setAppointments(prev => 
                    prev.map(apt => 
                        apt.id === data.payload.id ? data.payload : apt
                    )
                );
            }
        };
        
        setSocket(ws);
        
        return () => {
            ws.close();
        };
    }, []);

    return { appointments, socket };
};
```

## Endpoints de Prueba

### Probar Notificación de Appointment
```
GET /api/ws/test-appointment-notification
```

### Verificar Estado de WebSocket
```
GET /api/ws/status
```

## Configuración del Servidor

El sistema WebSocket está configurado en `WebSocketConfig.java` y utiliza:

- **Endpoint**: `/ws`
- **Broker**: `/topic` para mensajes públicos
- **User Destinations**: `/user/{userId}/...` para mensajes privados
- **CORS**: Configurado para permitir conexiones desde cualquier origen

## Logs y Debugging

Todas las notificaciones WebSocket se registran en los logs del servidor con nivel INFO. Los errores se registran con nivel ERROR.

Para habilitar logs detallados de WebSocket, agregar en `application.properties`:

```properties
logging.level.org.springframework.web.socket=DEBUG
logging.level.com.lucia.service.WebSocketNotificationService=DEBUG
```

## Consideraciones de Seguridad

- Los topics por teléfono son públicos, pero solo contienen información no sensible
- Para información confidencial, usar mensajes privados con `/user/{userId}/...`
- Implementar autenticación en el cliente para filtrar mensajes según permisos



