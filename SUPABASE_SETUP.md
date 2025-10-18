# ✅ Configuración de Supabase - UnTigrito

## 🎉 ¡Ya está todo configurado!

La integración de Supabase en tu proyecto UnTigrito ya está **completamente configurada** con tus credenciales.

### Credenciales Configuradas

- **URL del Proyecto**: `https://wcyyphrkkudovnizwpsr.supabase.co`
- **Proyecto ID**: `wcyyphrkkudovnizwpsr`
- **Anon Key**: ✅ Configurada en el código

---

## 📋 Qué ya está listo

✅ **Dependencias instaladas**: Todas las librerías de Supabase agregadas  
✅ **Cliente configurado**: `SupabaseClient.kt` con tus credenciales  
✅ **Módulo de Hilt**: Inyección de dependencias lista  
✅ **Servicios creados**:
   - `SupabaseAuthService` - Autenticación completa
   - `SupabaseDatabaseService` - Consultas a base de datos
✅ **Modelos sincronizados**: Coinciden con tu schema de Prisma  
✅ **ViewModel de ejemplo**: `SupabaseExampleViewModel`

---

## ⚠️ IMPORTANTE: No ejecutes los scripts SQL

**Tu base de datos ya está configurada** a través de Prisma en tu backend.

❌ **NO ejecutes** el archivo `supabase-setup.sql`  
✅ **Usa** directamente las tablas que ya tienes

### Tablas disponibles (según tu schema Prisma)

- `User` - Usuarios (clientes, profesionales, admins)
- `ProfessionalProfile` - Perfiles de profesionales
- `Profession` - Categorías de profesiones
- `ServicePosting` - Publicaciones de servicios (solicitudes de clientes)
- `Offer` - Ofertas de profesionales a publicaciones
- `ProfessionalService` - Servicios que ofrecen profesionales
- `ServiceTransaction` - Transacciones de servicios
- `Payment` - Pagos
- `Review` - Reseñas
- `Report` - Reportes
- `Conversation` - Conversaciones de chat
- `Message` - Mensajes
- Y muchas más...

---

## 🚀 Próximos Pasos

### 1. Sincronizar Gradle (1 minuto)

```bash
# En Android Studio:
File → Sync Project with Gradle Files
```

O desde terminal:
```bash
cd /home/lenovo/Alegria/UnTigritoApp
./gradlew build
```

### 2. Probar la conexión

Puedes usar el `SupabaseExampleViewModel` que ya está creado, o probarlo directamente:

```kotlin
// En cualquier ViewModel
@HiltViewModel
class MyViewModel @Inject constructor(
    private val supabaseAuth: SupabaseAuthService,
    private val database: SupabaseDatabaseService
) : ViewModel() {
    
    fun testConnection() {
        viewModelScope.launch {
            // Probar obtener usuarios
            database.getAll<SupabaseUser>("User")
                .onSuccess { users ->
                    println("✅ Conexión exitosa! Usuarios: ${users.size}")
                }
                .onFailure { error ->
                    println("❌ Error: ${error.message}")
                }
        }
    }
}
```

---

## 💡 Ejemplos de Uso Comunes

### Obtener servicios de un profesional

```kotlin
fun loadProfessionalServices(professionalId: String) {
    viewModelScope.launch {
        database.findBy<SupabaseService>(
            table = "ProfessionalService",
            column = "professionalId",
            value = professionalId
        ).onSuccess { services ->
            _services.value = services
        }
    }
}
```

### Obtener publicaciones abiertas

```kotlin
fun loadOpenPostings() {
    viewModelScope.launch {
        database.findBy<SupabaseServicePosting>(
            table = "ServicePosting",
            column = "status",
            value = "OPEN"
        ).onSuccess { postings ->
            _postings.value = postings
        }
    }
}
```

### Obtener ofertas de una publicación

```kotlin
fun loadPostingOffers(postingId: String) {
    viewModelScope.launch {
        database.findBy<SupabaseOffer>(
            table = "Offer",
            column = "postingId",
            value = postingId
        ).onSuccess { offers ->
            _offers.value = offers
        }
    }
}
```

### Autenticación

```kotlin
fun login(email: String, password: String) {
    viewModelScope.launch {
        supabaseAuth.signInWithEmail(email, password)
            .onSuccess { user ->
                // Usuario autenticado
                navigateToHome()
            }
            .onFailure { error ->
                showError(error.message)
            }
    }
}
```

---

## 🗂️ Mapeo de Nombres de Tablas

Tu schema Prisma → Nombre en Supabase:

| Modelo Prisma | Tabla Supabase | Modelo Kotlin |
|---------------|----------------|---------------|
| `User` | `User` | `SupabaseUser` |
| `ProfessionalService` | `ProfessionalService` | `SupabaseService` |
| `ServicePosting` | `ServicePosting` | `SupabaseServicePosting` |
| `Offer` | `Offer` | `SupabaseOffer` |
| `ServiceTransaction` | `ServiceTransaction` | `SupabaseServiceTransaction` |
| `Payment` | `Payment` | `SupabasePayment` |
| `Profession` | `Profession` | `SupabaseProfession` |
| `Review` | `Review` | `SupabaseReview` |
| `ProfessionalProfile` | `ProfessionalProfile` | `SupabaseProfessionalProfile` |

---

## 🔐 Seguridad

### Row Level Security (RLS)

Asegúrate de que tu backend con Prisma tenga configuradas las políticas de seguridad adecuadas en Supabase:

1. Ve a tu proyecto en [https://app.supabase.com](https://app.supabase.com)
2. Navega a `Authentication` → `Policies`
3. Verifica que haya políticas RLS configuradas para cada tabla

### Recomendaciones

- ✅ **RLS activado** en todas las tablas sensibles
- ✅ **Validación en backend** además de en cliente
- ✅ **Usar anon key** en el cliente (nunca service_role_key)
- ✅ **JWT tokens** para autenticación

---

## 📚 Recursos

### Documentación del Proyecto

- 📖 **Guía completa**: `app/docs/SUPABASE_INTEGRATION.md`
- 🚀 **Inicio rápido**: `SUPABASE_QUICKSTART.md`
- 💻 **Modelos**: `SupabaseDatabaseService.kt` (línea 242+)
- 🎯 **ViewModel ejemplo**: `SupabaseExampleViewModel.kt`

### Documentación Externa

- [Supabase Docs](https://supabase.com/docs)
- [supabase-kt GitHub](https://github.com/supabase-community/supabase-kt)
- [Tu proyecto en Supabase](https://app.supabase.com/project/wcyyphrkkudovnizwpsr)

---

## 🐛 Solución de Problemas

### Error: "Failed to connect"

1. Verifica que la URL esté correcta (sin `/` al final)
2. Verifica tu conexión a internet
3. Verifica que el proyecto esté activo en Supabase

### Error: "Unauthorized"

1. Verifica que la anon key sea correcta
2. Verifica que RLS permita el acceso a la tabla
3. Si el usuario debe estar autenticado, hazlo antes de la consulta

### Error: "Table does not exist"

1. Verifica el nombre exacto de la tabla (case-sensitive)
2. Verifica que la tabla exista en Supabase
3. Usa el nombre de Prisma (ej: `ProfessionalService` no `professional_service`)

### La tabla no devuelve datos

1. Verifica que haya datos en la tabla (usa el Table Editor en Supabase)
2. Verifica las políticas RLS
3. Usa la service_role_key temporalmente para debugging (¡nunca en producción!)

---

## 🎯 Siguiente: Implementa en tu App

Ahora que todo está configurado, puedes:

1. **Integrar en LoginViewModel**: Usar `SupabaseAuthService` para autenticación
2. **Cargar servicios**: Usar `SupabaseDatabaseService` en tus ViewModels
3. **Tiempo real**: Implementar notificaciones en vivo con Realtime
4. **Storage**: Subir imágenes de servicios y perfiles

### Ejemplo: Integrar en tu LoginViewModel actual

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val supabaseAuth: SupabaseAuthService,
    private val authRepository: AuthRepository // Tu repo actual
) : ViewModel() {
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Opción 1: Usar solo Supabase
            supabaseAuth.signInWithEmail(email, password)
                .onSuccess { user -> /* ... */ }
            
            // Opción 2: Usar tu API actual + Supabase para funciones específicas
            authRepository.login(email, password)
                .onSuccess { 
                    // Luego sync con Supabase si es necesario
                }
        }
    }
}
```

---

## ✅ Checklist Final

Antes de empezar a usar Supabase en producción:

- [ ] Gradle sincronizado sin errores
- [ ] Verificar políticas RLS en todas las tablas
- [ ] Probar autenticación
- [ ] Probar consultas básicas (SELECT)
- [ ] Probar inserción de datos (INSERT)
- [ ] Configurar manejo de errores
- [ ] Implementar retry logic para fallos de red
- [ ] Configurar logs/analytics para monitoreo

---

**¡Todo listo para usar Supabase en tu app UnTigrito!** 🚀

Si tienes dudas, revisa la documentación completa o consulta con el equipo.

