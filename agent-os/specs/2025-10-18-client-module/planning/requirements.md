# Spec Requirements: Módulo de Cliente

## Initial Description
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

## Requirements Discussion

### First Round Questions

**Q1:** Asumo que el módulo de cliente debe seguir la arquitectura Clean Architecture + MVVM ya establecida en el proyecto, con módulos separados como feature-client que contenga todas las pantallas del flujo del cliente. ¿Es correcto, o prefieres una estructura diferente?
**Answer:** si, es correcto

**Q2:** Estoy pensando en implementar la navegación usando Jetpack Navigation Compose con una BottomNavigationBar que contenga las 4 pestañas principales (Inicio, Servicios, Solicitudes, Perfil). ¿Deberíamos usar esta aproximación, o prefieres un enfoque diferente?
**Answer:** es correcto

**Q3:** Para el estado de la aplicación, asumo que usaremos ViewModel con StateFlow/Flow para manejar el estado de cada pantalla, siguiendo el patrón MVVM. ¿Es correcto, o tienes preferencias específicas para el manejo de estado?
**Answer:** es correcto

**Q4:** Para la integración con la API, asumo que crearemos servicios específicos para cada endpoint del backend (autenticación, profesionales, servicios, etc.) usando Ktor Client o Retrofit. ¿Prefieres alguna librería específica de networking?
**Answer:** si

**Q5:** Para el manejo de imágenes (fotos de perfil, adjuntos de solicitudes), asumo que usaremos Coil para cargar y cachear imágenes. ¿Es correcto, o prefieres otra librería?
**Answer:** si

**Q6:** Para la persistencia local (cache de datos, preferencias del usuario), asumo que usaremos Room para datos estructurados y EncryptedSharedPreferences para datos sensibles. ¿Es correcto?
**Answer:** si exacto

**Q7:** Para el flujo de pagos/recarga, asumo que inicialmente será solo para mostrar información bancaria y confirmación manual (sin integración de pagos reales), ya que el roadmap indica que los pagos complejos son para fases posteriores. ¿Es correcto?
**Answer:** es correcto

**Q8:** Para el diseño UI, asumo que seguiremos el estilo visual del HomeScreen.kt existente (colores naranjas, cards con bordes redondeados, etc.) pero creando todos los composables desde cero. ¿Es correcto?
**Answer:** si es correcto

**Q9:** ¿Hay alguna funcionalidad específica del flujo del cliente que NO debería incluirse en esta primera versión del módulo?
**Answer:** no, esta perfecto.

### Existing Code to Reference

**Similar Features Identified:**
- Feature: HomeScreen.kt - Path: `app/src/main/java/com/thecodefather/untigrito/vibecoding3/home/ui/HomeScreen.kt`
- Components to potentially reuse: Estilo visual (colores naranjas, cards con bordes redondeados, layout patterns)
- Backend logic to reference: Endpoints de la API documentados en `app/docs/back.md`

### Follow-up Questions
No follow-up questions were needed.

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
No visual assets provided.

## Requirements Summary

### Functional Requirements
- Implementar módulo feature-client con arquitectura Clean Architecture + MVVM
- Navegación con Jetpack Navigation Compose y BottomNavigationBar (4 pestañas: Inicio, Servicios, Solicitudes, Perfil)
- Manejo de estado con ViewModel + StateFlow/Flow siguiendo patrón MVVM
- Integración con API usando Ktor Client o Retrofit para endpoints del backend
- Manejo de imágenes con Coil para fotos de perfil y adjuntos
- Persistencia local con Room para datos estructurados y EncryptedSharedPreferences para datos sensibles
- Flujo de pagos/recarga con información bancaria y confirmación manual (sin integración real de pagos)
- Diseño UI siguiendo el estilo visual del HomeScreen.kt existente
- Todas las funcionalidades del flujo del cliente incluidas en esta versión

### Reusability Opportunities
- Estilo visual del HomeScreen.kt existente para mantener consistencia
- Patrones de layout y componentes UI del HomeScreen.kt
- Endpoints de la API ya documentados en back.md
- Arquitectura Clean Architecture + MVVM ya establecida

### Scope Boundaries
**In Scope:**
- Módulo completo feature-client con todas las pantallas del flujo del cliente
- Integración con todos los endpoints necesarios del backend
- Navegación completa con 4 pestañas principales
- Flujo de pagos/recarga con confirmación manual
- Diseño UI consistente con el estilo existente

**Out of Scope:**
- Integración real de pagos (para fases posteriores según roadmap)
- Funcionalidades de profesional (módulo separado)

### Technical Considerations
- Arquitectura Clean Architecture + MVVM establecida
- Tecnologías: Kotlin, Jetpack Compose, Hilt, Coroutines + Flow
- Networking: Ktor Client o Retrofit
- Persistencia: Room + EncryptedSharedPreferences
- Imágenes: Coil
- Navegación: Jetpack Navigation Compose
- Patrones de código existentes a seguir del HomeScreen.kt
