# Initial Spec Idea

## User's Initial Description
Quiero hacer el modulo de cliente basado en esta documentacion, tambien para estilos toma de referencia estas vistas @HomeScreen.kt solo el estilo, todo el modulo se va a realizar desde 0 aqui. Haz uso de los endpoins necesarios basados en esta documentacion @back.md 

Documento de Funcionalidad
Título
Flujo Principal de Navegación del Cliente
Contexto
Aplicación móvil para la contratación y provisión de servicios profesionales (similar a Upwork), enfocada en el flujo del usuario que actúa como Cliente (solicitante de servicios). El flujo comienza después de que el usuario ha sido autenticado.
UI (Interfaz de Usuario)
A continuación se describe la estructura de las vistas clave del flujo de Cliente basándose en las imágenes proporcionadas:
Home (image_7797c2.png)
Encabezado: Nombre del usuario ("Juan Pérez"), icono de correo, icono de notificación.
Balance: Muestra el saldo disponible (e.j., "15,000.00 Bs"), con botones para "Recargar" e "Historial".
Promociones/Banners: Sección de banners publicitarios (e.j., "Yummy Rides", "Compra a crédito con Cashea").
Explora categorías: Carrusel o listado de categorías (e.j., "Plomería", "Electricidad", "Albañilería").
Tigres mejor calificados: Carrusel/Listado de profesionales destacados con nombre, especialidad, ubicación y calificación (e.j., "Juan Pérez, Electricista | Valencia, 4.3 (120)").
Publica tu Solicitud: Botón destacado para iniciar el proceso de creación de una solicitud de servicio.
Servicios: Listado de servicios cercanos o relevantes (e.j., "Reparación de Fugas", "Reparación de neveras") mostrando título, proveedor, calificación y distancia.
Barra de Navegación Inferior (Tabs): Inicio, Servicios, Solicitudes, Perfil.
Crear Solicitud de Servicio (image_778c79.png)
Título: Campo de texto para el título de la solicitud (e.j., "Plomería urgente").
Detalles del servicio: Área de texto para la descripción detallada del problema.
Categoría: Desplegable para seleccionar la categoría (e.g., "Electricidad").
Ubicación: Campo de texto o botón para buscar/seleccionar ubicación.
Urgente: Toggle switch para indicar si el servicio es urgente.
¿Cuándo lo necesitas?: Campos de fecha y hora.
Adjuntar fotos o videos (opcional): Área para subir archivos multimedia.
Rango de precios (opcional): Slider o campos de texto para definir un rango Min/Max.
Botón: "Publicar solicitud".
Servicios (image_779385.png)
Encabezado: Nombre del usuario ("Juan Pérez"), icono de correo, icono de notificación.
Buscador: Campo de texto para buscar servicios o profesionales.
Filtros: Carrusel de categorías (e.j., "Plomería", "Electricidad", "Limpieza", "Mudanza").
Tabs: "Servicios" y "Profesionales" para alternar listados.
Listado de Servicios (Tab Servicios): Muestra tarjetas con nombre del servicio, detalles del proveedor, precio base/rango, calificación y distancia.
Listado de Profesionales (Tab Profesionales): Muestra tarjetas con foto, nombre, especialidad, número de reseñas, calificación y distancia.
Barra de Navegación Inferior (Tabs): Inicio, Servicios, Solicitudes, Perfil.
Detalle del Servicio (image_778c23.png y image_778fc0.png, 3 vistas)
Título: "Reparación de filtración urgente en lavadero"
Descripción: Detalle del trabajo y requisitos (e.g., "que pueda emitir factura", "que tenga seguro").
Precio Estimado: Muestra un rango de precio sugerido (e.g., "$50 - $100 Estimado").
Tags: Categorías asociadas (e.g., "Plomería", "Urgente").
Detalles del Empleo: Distancia, Ubicación, Fecha/Hora.
Mapa de la Zona de Servicio (Simulado): (image_778fc0.png - primera vista)
Sección de Negociación: Toggle "Negociar" y campo "Nota" (image_778fc0.png - segunda vista).
Sección de Oferta con Rango: Al activar "Negociar", muestra campos Min/Max con rango ($50-$100) y campo "Nota" (image_778fc0.png - tercera vista).
Botón: "Realizar Solicitud" o "Solicitar".
Ofertas Recibidas: Listado de ofertas si se trata de un servicio ya publicado (image_778c23.png).
Solicitudes (image_7792e7.png, 3 vistas)
Encabezado: Nombre del usuario ("Juan Pérez"), icono de correo, icono de notificación.
Buscador: Campo de texto para buscar servicios o profesionales.
Tabs: "Abiertas", "En Curso", "Historial" para filtrar el estado de las solicitudes.
Listado de Solicitudes: Muestra tarjetas con título, ubicación, rango de precio.
Abiertas: Muestra número de ofertas recibidas y botón "Revisar ofertas". Posibilidad de "Editar" o "Eliminar" la solicitud.
En Curso: Muestra estado de la solicitud y botón "Finalizar". Posibilidad de "Cancelar".
Historial: Muestra solicitudes "cancelado" o "finalizado". Opción para ver "Historial de Pago" (en "finalizado") y "Eliminar".
Barra de Navegación Inferior (Tabs): Inicio, Servicios, Solicitudes, Perfil.
Perfil (image_778fff.png, 2 vistas)
Datos del Usuario: Nombre ("Juan Pérez"), Imagen de perfil, Contacto (Teléfono, Dirección - si está verificado).
Toggle: "Soy un profesional" para cambiar de rol.
Verificaciones: Estado de la verificación de la cuenta (No verificado/Verificado).
Historial de Servicios: Enlace para ver servicios anteriores.
Cerrar sesión: Opción para cerrar la sesión.
Verifícate!: Banner de llamado a la acción para la verificación (si no está verificado).
Barra de Navegación Inferior (Tabs): Inicio, Servicios, Solicitudes, Perfil.
Flujo de Pagos / Cuenta (image_778f63.png y image_778ba3.png)
Detalles de la Cuenta: Muestra el saldo disponible. Botones "Recargar" y "Retirar".
Historial: Lista de transacciones (Recargo, Retiro) con monto y fecha.
Métodos de Recarga: Opciones para recargar saldo ("Cuenta UnTigrito", "Transferencia", "Pago móvil").
Realizar Pago (Recarga): Muestra detalles bancarios (Teléfono, RIF, Banco) con opción de "Copiar todo" y botón "Ya pagué".
Métodos de Retiro: Opciones para retirar saldo ("Transferencia", "Pago móvil").
Realizar Retiro: Campos para datos bancarios (Teléfono, Cédula/RIF, Banco) y botón "Realizar".

## Metadata
- Date Created: 2025-10-18
- Spec Name: client-module
- Spec Path: agent-os/specs/2025-10-18-client-module
