# 🚀 Inicio Rápido con Supabase

Esta guía te ayudará a comenzar a usar Supabase en UnTigrito en 5 minutos.

## ✅ Pasos Completados

La integración de Supabase ya está configurada en el proyecto:

- ✅ Dependencias agregadas en `gradle/libs.versions.toml`
- ✅ Plugin de serialización de Kotlin habilitado
- ✅ Cliente de Supabase configurado (`SupabaseClient.kt`)
- ✅ Módulo de Hilt creado (`SupabaseModule.kt`)
- ✅ Servicios de ejemplo implementados:
  - `SupabaseAuthService.kt` - Autenticación
  - `SupabaseDatabaseService.kt` - Base de datos
- ✅ ViewModel de ejemplo (`SupabaseExampleViewModel.kt`)
- ✅ Documentación completa (`SUPABASE_INTEGRATION.md`)
- ✅ Scripts SQL de configuración (`supabase-setup.sql`)

## 🎯 Próximos Pasos

### ✅ Tu Proyecto Ya Está Configurado

**¡Buenas noticias!** Tu proyecto de Supabase ya está configurado y las credenciales están en el código.

- **URL**: `https://wcyyphrkkudovnizwpsr.supabase.co`
- **Proyecto**: `wcyyphrkkudovnizwpsr`
- **Credenciales**: ✅ Ya configuradas

### ⚠️ IMPORTANTE: No Necesitas Crear Tablas

**Tu base de datos ya está configurada** a través de tu backend con Prisma.

❌ **NO ejecutes** scripts SQL  
✅ **Usa** las tablas existentes directamente

Tu schema de Prisma ya tiene todas las tablas necesarias:
- `User`
- `ProfessionalService`
- `ServicePosting`
- `Offer`
- `ServiceTransaction`
- `Payment`
- Y muchas más...

### 1️⃣ Sincronizar Gradle (1 minuto)

1. En Android Studio, haz clic en:
   - `File` → `Sync Project with Gradle Files`
2. Espera a que termine la sincronización
3. Verifica que no haya errores

## 🎉 ¡Listo para Usar!

Ya puedes empezar a usar Supabase en tu app. Aquí tienes algunos ejemplos:

### Ejemplo 1: Autenticación en un ViewModel

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val supabaseAuth: SupabaseAuthService
) : ViewModel() {
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            supabaseAuth.signInWithEmail(email, password)
                .onSuccess { user ->
                    // Usuario autenticado
                    println("Bienvenido: ${user?.email}")
                }
                .onFailure { error ->
                    // Error al autenticar
                    println("Error: ${error.message}")
                }
        }
    }
}
```

### Ejemplo 2: Consultar Datos

```kotlin
@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val database: SupabaseDatabaseService
) : ViewModel() {
    
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services = _services.asStateFlow()
    
    fun loadServices() {
        viewModelScope.launch {
            database.getAll<Service>("services")
                .onSuccess { servicesList ->
                    _services.value = servicesList
                }
        }
    }
}
```

### Ejemplo 3: Crear un Registro

```kotlin
fun createService(title: String, description: String, price: Double) {
    viewModelScope.launch {
        val service = Service(
            id = "", // Supabase lo genera
            title = title,
            description = description,
            price = price,
            professionalId = currentUserId
        )
        
        database.insert("services", service)
            .onSuccess { newService ->
                println("Servicio creado: ${newService?.id}")
            }
    }
}
```

## 📚 Recursos Adicionales

- 📖 **Guía completa**: `app/docs/SUPABASE_INTEGRATION.md`
- 🗄️ **Scripts SQL**: `app/docs/supabase-setup.sql`
- 💡 **ViewModel de ejemplo**: `presentation/viewmodel/SupabaseExampleViewModel.kt`
- 🌐 **Documentación oficial**: [https://supabase.com/docs](https://supabase.com/docs)

## ⚠️ Importante para Producción

Antes de lanzar a producción:

1. **Seguridad**:
   - ✅ Usa variables de entorno para credenciales
   - ✅ Configura RLS (Row Level Security) correctamente
   - ✅ Revisa las políticas de acceso

2. **Performance**:
   - ✅ Agrega índices a columnas frecuentemente consultadas
   - ✅ Implementa paginación para listas grandes
   - ✅ Usa cache local con Room

3. **Monitoreo**:
   - ✅ Configura alertas en el dashboard de Supabase
   - ✅ Monitorea uso de API y cuotas
   - ✅ Implementa logging de errores

## 🐛 Solución de Problemas

### Error: "Unable to resolve dependency"
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Error: "Connection refused"
- Verifica que tu URL de Supabase sea correcta
- Verifica tu conexión a Internet
- Verifica que el proyecto en Supabase esté activo

### Error: "Authentication failed"
- Verifica que la clave anónima sea correcta
- Verifica que no haya espacios extra en las credenciales

### Error: "Table does not exist"
- Ejecuta los scripts SQL en `supabase-setup.sql`
- Verifica que la tabla esté creada en el dashboard de Supabase

## 💬 ¿Necesitas Ayuda?

- 📖 Lee la documentación completa en `SUPABASE_INTEGRATION.md`
- 🌐 Visita la [documentación oficial de Supabase](https://supabase.com/docs)
- 💬 Consulta la [comunidad de Supabase](https://github.com/supabase/supabase/discussions)
- 🐛 Abre un issue en el repositorio del proyecto

---

**¡Felicidades! Ya puedes usar Supabase en tu aplicación UnTigrito** 🎉

Para más detalles sobre funcionalidades avanzadas (Realtime, Storage, etc.), consulta la guía completa.

