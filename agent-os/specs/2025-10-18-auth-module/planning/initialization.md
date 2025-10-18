# Initial Spec Idea

## User's Initial Description
Quiero hacer el módulo de auth basado en esta documentación, también para estilos toma de referencia estas vistas @LoginScreen.kt @RegisterScreen.kt solo el estilo, todo el módulo se va a realizar desde 0 aquí. Haz uso de los endpoints necesarios basados en esta documentación @back.md 

Documento de Flujo de Autenticación

Contexto
Esta tarea abarca la implementación completa del flujo de autenticación de la aplicación UnTigrito®, incluyendo las funcionalidades de inicio de sesión (Login), registro (Registro), y recuperación de contraseña. El objetivo principal es establecer un diagrama de navegación claro, documentar las vistas (UI) involucradas y describir la lógica del flujo en sus diferentes ramas. El flujo principal inicia con la pantalla de bienvenida ($\text{Splash}$), y a partir del $\text{Login}$, se despliegan tres opciones: $\text{Login}$ directo, $\text{Registro}$ o $\text{Recuperar Contraseña}$. Además, se identifican procesos opcionales posteriores al registro, como la Verificación de Identificación y Verificación de Teléfono.

UI
Las siguientes vistas (pantallas de la aplicación) están involucradas en el flujo de autenticación, según las imágenes proporcionadas:
1. Splash
Propósito: Pantalla de bienvenida o carga inicial.
Contenido: El logo de $\text{UnTigrito®}$ y el nombre de la aplicación.
Navegación: Automáticamente dirige al $\text{Login}$ tras un breve periodo.
2. Login (Iniciar Sesión)
Propósito: Permite a un usuario existente acceder a su cuenta.
Campos: Correo (Ejemplo: $\text{luisjose@gmail.com}$), Contraseña (Ejemplo: $\text{1234567}$).
Acciones/Enlaces:
Botón "Iniciar Sesión".
Enlace "¿Olvidaste tu contraseña?" (Dirige a $\text{Recuperar Contraseña}$).
Botón "Iniciar sesión con Google" (Opción de autenticación social).
Enlace "¿No tienes una cuenta? Regístrate" (Dirige a $\text{Registro}$).
3. Registro (Crear Cuenta)
Propósito: Permite a un nuevo usuario crear una cuenta.
Campos: Nombre y apellido (Ejemplo: $\text{Luis jose}$), Correo (Ejemplo: $\text{luisjose@gmail.com}$), Contraseña (Ejemplo: $\text{1234567}$), Confirmación de contraseña.
Acciones/Enlaces:
Botón "Registrarse".
Botón "Iniciar sesión con Google" (Opción de autenticación social).
Enlace "¿Ya tienes una cuenta? Iniciar Sesión" (Dirige a $\text{Login}$).
4. Android Compact - 3 (Introducción a la Verificación)
Propósito: Informa al usuario sobre los beneficios y requisitos de la verificación de identidad.
Requisitos Destacados: $\text{Cédula de identidad}$ y $\text{Número de teléfono}$.
Acciones/Enlaces:
Botón "Continuar" (Dirige a $\text{Registrar cédula}$).
Enlace "Omitir" (Permite saltar el proceso, dirigiendo al $\text{Home}$).
5. Registrar cédula (Verificación de Identidad)
Propósito: Proceso de verificación de identidad del usuario.
Pasos:
Número de Cédula (Campo de texto, Ejemplo: $\text{27483383}$).
Paso 1: Sube la imagen de tu cédula ($\text{Ház click para subir una imagen}$).
Paso 2: Escanea tu rostro (Área para el escaneo facial).
Acciones: Botón "Verificar y continuar" (Dirige a $\text{Registrar teléfono}$).
6. Registrar teléfono (Ingresa tu teléfono)
Propósito: Recopilar el número de teléfono para verificación.
Campos: Ingresa tu teléfono (Ejemplo: $\text{04120386216}$).
Acciones: Botón "Continuar" (Dirige a $\text{Validar teléfono}$).
7. Validar teléfono (Verifica tu teléfono)
Propósito: Verificar la propiedad del número de teléfono mediante un código.
Contenido: Muestra el número a verificar ($\text{+58 412 0386216}$) y $\text{5}$ campos para ingresar el código de verificación ($\text{OTP}$).
Acciones/Enlaces:
Botón "Verificar Código".
Enlace "Reenviar código".

Integración
Diagrama de Flujos / Navegación
El flujo de autenticación y navegación se puede representar de la siguiente manera:
Flujo Principal:
$$\text{Splash} \rightarrow \text{Login}$$
Desde Login (3 Decisiones):
Opción A: Iniciar Sesión (Éxito) $\rightarrow \text{Home}$
Opción B: Registrarse $\rightarrow \text{Registro}$
Opción C: Recuperar Contraseña $\rightarrow \text{Vista de Recuperar Contraseña}$
Subflujo de Registro:
$$\text{Registro} \rightarrow \text{Autenticado automáticamente} \rightarrow \text{Introducción a la Verificación (Android Compact - 3)}$$
Flujo de Verificación (Opcional):
$$\text{Introducción a la Verificación} \xrightarrow{\text{Omitir}} \text{Home}$$
$$\text{Introducción a la Verificación} \xrightarrow{\text{Continuar}} \text{Registrar cédula}$$
$$\text{Registrar cédula} \xrightarrow{\text{Verificar y continuar}} \text{Registrar teléfono}$$
$$\text{Registrar teléfono} \xrightarrow{\text{Continuar}} \text{Validar teléfono}$$
$$\text{Validar teléfono} \xrightarrow{\text{Verificar Código}} \text{Home}$$
Subflujo de Recuperación de Contraseña:
$$\text{Vista de Recuperar Contraseña} \rightarrow \text{Recupera Contraseña} \rightarrow \text{Login}$$

Backend
Funcionalidad
Endpoint/Loˊgica
Payload/Descripcioˊn
Inicio de Sesión
$\text{POST /auth/login}$
$\text{JSON}$ con $\text{correo}$ y $\text{contraseña}$.
Registro de Usuario
$\text{POST /auth/register}$
$\text{JSON}$ con $\text{nombre\_apellido}$, $\text{correo}$, $\text{contraseña}$, $\text{confirmacion\_contraseña}$.
Login con Google
$\text{POST /auth/google}$
$\text{JSON}$ con el $\text{token}$ de Google obtenido en el cliente.
Recuperar Contraseña
$\text{POST /auth/recover-password}$
$\text{JSON}$ con $\text{correo}$ del usuario.
Verificación Cédula
$\text{POST /user/verify/id}$
$\text{Multipart form-data}$ con $\text{numero\_cedula}$, $\text{imagen\_cedula}$ y $\text{data\_escaneo\_rostro}$.
Enviar Código $\text{OTP}$
$\text{POST /user/send-otp}$
$\text{JSON}$ con $\text{numero\_telefono}$.
Verificar Código $\text{OTP}$
$\text{POST /user/verify/otp}$
$\text{JSON}$ con $\text{numero\_telefono}$ y $\text{codigo\_otp}$.


Notas
Registro y Autenticación: El flujo indica que al registrarse, el usuario queda autenticado automáticamente ($\text{te lleva a la vista de verificar telefono}$). Esto implica que el $\text{backend}$ debe retornar un $\text{token}$ de sesión tras un registro exitoso.
Opcionalidad en Verificación: Los procesos de verificación ($\text{Verificar Telefono}$ y $\text{Verificar Identificación}$) son opcionales ($\text{se puede saltear la vista es opcional}$). La $\text{UI}$ de $\text{Android Compact - 3}$ confirma esto con el enlace "Omitir".
Decisión de Flujo post-registro: Es crucial que la aplicación guarde el estado de si la verificación ha sido completada o no. Si el usuario omite la verificación, debe navegar directamente al Home.
Recuperar Contraseña: El flujo asume que la vista de recuperación dirige al $\text{Login}$ una vez que el proceso de restablecimiento ha finalizado (implica que el usuario debe loguearse con la nueva contraseña).

## Metadata
- Date Created: 2025-10-18
- Spec Name: auth-module
- Spec Path: agent-os/specs/2025-10-18-auth-module
