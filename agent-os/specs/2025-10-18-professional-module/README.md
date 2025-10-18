# Especificación: Módulo de Profesional

**Fecha de Creación**: 2025-10-18  
**Estado**: En Desarrollo  
**Tipo**: Módulo Completo  

## Resumen

Desarrollo completo del módulo de profesional para la aplicación UnTigrito, incluyendo todas las funcionalidades necesarias para que los profesionales gestionen sus servicios, busquen trabajos, envíen propuestas y se comuniquen con clientes.

## Funcionalidades Principales

- **Trabajos**: Búsqueda y filtrado de trabajos disponibles
- **Propuestas**: Gestión de ofertas enviadas
- **Mensajes**: Sistema de comunicación con clientes
- **Mis Servicios**: Gestión del perfil de servicios del profesional

## Endpoints Backend Utilizados

Basado en la documentación `back.md`, se utilizarán los siguientes endpoints:

### Autenticación
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/logout`
- `POST /api/auth/refresh`

### Profesionales
- `GET /api/professionals/list`
- `GET /api/professionals/[id]`
- `GET /api/professionals/[id]/stats`
- `POST /api/professionals/profile/create`
- `GET /api/professionals/profile`
- `PUT /api/professionals/profile/update`
- `GET /api/professionals/search`

### Servicios
- `GET /api/services/postings/list`
- `POST /api/services/postings/create`
- `POST /api/services/offers/create`

### Usuarios
- `GET /api/users/profile`
- `PUT /api/users/profile/update`

### Verificación
- `POST /api/user/send-otp`
- `POST /api/user/verify-otp`
- `POST /api/user/verify-id`
- `POST /api/user/verify-phone`

## User Stories Incluidas

### Subflujo: Trabajos (P-TR-001 a P-TR-007)
- Listado de trabajos disponibles
- Búsqueda y filtrado
- Detalles de trabajo
- Generación de propuestas

### Subflujo: Propuestas (P-PR-001 a P-PR-003)
- Gestión de propuestas enviadas
- Filtrado por estado
- Seguimiento de ofertas

### Subflujo: Mensajes (P-MS-001 a P-MS-003)
- Chat con clientes
- Soporte técnico
- Indicadores de mensajes no leídos

### Subflujo: Mis Servicios (P-SR-001 a P-SR-006)
- Gestión de servicios publicados
- Creación y edición de servicios
- Configuración de precios y categorías

## Estructura de Archivos

```
2025-10-18-professional-module/
├── README.md
├── spec.md
├── requirements.md
├── tasks.md
├── technical-spec.md
├── planning/
├── implementation/
└── verification/
```
