# 🚀 Guía de Integración de Supabase en UnTigrito

Esta guía documenta la integración del cliente de Supabase en la aplicación Android UnTigrito.

## 📋 Tabla de Contenidos
- [Configuración Inicial](#configuración-inicial)
- [Arquitectura](#arquitectura)
- [Autenticación](#autenticación)
- [Base de Datos](#base-de-datos)
- [Almacenamiento](#almacenamiento)
- [Tiempo Real](#tiempo-real)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Mejores Prácticas](#mejores-prácticas)

---

## 🔧 Configuración Inicial

### 1. Dependencias

Las siguientes dependencias ya están configuradas en `gradle/libs.versions.toml`:

```toml
[versions]
supabase = "2.0.0"
ktor = "2.3.6"

[libraries]
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt", version.ref = "supabase" }
supabase-gotrue = { group = "io.github.jan-tennert.supabase", name = "gotrue-kt", version.ref = "supabase" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt", version.ref = "supabase" }
supabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt", version.ref = "supabase" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
```

### 2. Obtener Credenciales de Supabase

1. Ve a [https://app.supabase.com](https://app.supabase.com)
2. Crea un nuevo proyecto o selecciona uno existente
3. Ve a `Settings` → `API`
4. Copia:
   - **Project URL**: Tu URL de Supabase
   - **anon/public key**: Tu clave pública

### 3. Configurar el Cliente

Edita el archivo `SupabaseClient.kt` con tus credenciales:

```kotlin
private const val SUPABASE_URL = "https://tu-proyecto.supabase.co"
private const val SUPABASE_ANON_KEY = "tu_clave_anon_key"
```

**IMPORTANTE**: Para producción, usa variables de entorno o BuildConfig para las credenciales.

---

## 🏗️ Arquitectura

### Estructura de Archivos

```
data/
└── datasource/
    └── remote/
        ├── SupabaseClient.kt           # Cliente principal de Supabase
        ├── SupabaseAuthService.kt      # Servicio de autenticación
        └── SupabaseDatabaseService.kt  # Servicio de base de datos

di/
└── SupabaseModule.kt                   # Módulo de inyección de dependencias
```

### Inyección de Dependencias con Hilt

El módulo `SupabaseModule` proporciona:
- `SupabaseClient`: Cliente principal
- `Auth`: Módulo de autenticación
- `Postgrest`: Módulo de base de datos
- `Realtime`: Módulo de tiempo real
- `Storage`: Módulo de almacenamiento

---

## 🔐 Autenticación

### Registro con Email

```kotlin
class AuthViewModel @Inject constructor(
    private val supabaseAuth: SupabaseAuthService
) : ViewModel() {
    
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            supabaseAuth.signUpWithEmail(email, password)
                .onSuccess { user ->
                    // Usuario registrado exitosamente
                    println("Usuario: ${user?.email}")
                }
                .onFailure { error ->
                    // Manejo de errores
                    println("Error: ${error.message}")
                }
        }
    }
}
```

### Login con Email

```kotlin
fun signIn(email: String, password: String) {
    viewModelScope.launch {
        supabaseAuth.signInWithEmail(email, password)
            .onSuccess { user ->
                // Usuario autenticado
                navigateToHome()
            }
            .onFailure { error ->
                // Mostrar error al usuario
                showError(error.message)
            }
    }
}
```

### Obtener Usuario Actual

```kotlin
fun checkAuthStatus() {
    viewModelScope.launch {
        val user = supabaseAuth.getCurrentUser()
        if (user != null) {
            // Usuario autenticado
            _isAuthenticated.value = true
        } else {
            // Usuario no autenticado
            _isAuthenticated.value = false
        }
    }
}
```

### Cerrar Sesión

```kotlin
fun logout() {
    viewModelScope.launch {
        supabaseAuth.signOut()
            .onSuccess {
                navigateToLogin()
            }
            .onFailure { error ->
                showError(error.message)
            }
    }
}
```

### Recuperar Contraseña

```kotlin
fun resetPassword(email: String) {
    viewModelScope.launch {
        supabaseAuth.resetPasswordForEmail(email)
            .onSuccess {
                showMessage("Email de recuperación enviado")
            }
            .onFailure { error ->
                showError(error.message)
            }
    }
}
```

---

## 💾 Base de Datos (Postgrest)

### Definir Modelos

```kotlin
@Serializable
data class Service(
    val id: String? = null,
    val title: String,
    val description: String,
    val price: Double,
    val professionalId: String,
    val createdAt: String? = null
)
```

### Obtener Todos los Registros

```kotlin
class ServiceRepository @Inject constructor(
    private val database: SupabaseDatabaseService
) {
    
    suspend fun getAllServices(): Result<List<Service>> {
        return database.getAll("services")
    }
}
```

### Obtener por ID

```kotlin
suspend fun getServiceById(id: String): Result<Service?> {
    return database.getById("services", id)
}
```

### Insertar Registro

```kotlin
suspend fun createService(service: Service): Result<Service?> {
    return database.insert("services", service)
}
```

### Actualizar Registro

```kotlin
suspend fun updateService(id: String, service: Service): Result<Service?> {
    return database.update("services", id, service)
}
```

### Eliminar Registro

```kotlin
suspend fun deleteService(id: String): Result<Unit> {
    return database.delete("services", id)
}
```

### Consultas con Filtros

```kotlin
// Buscar servicios por profesional
suspend fun getServicesByProfessional(professionalId: String): Result<List<Service>> {
    return database.findBy("services", "professionalId", professionalId)
}
```

### Paginación

```kotlin
suspend fun getServicesPage(page: Int, pageSize: Int = 10): Result<List<Service>> {
    return database.getPaginated("services", page, pageSize)
}
```

### Consultas Personalizadas

```kotlin
@Inject
constructor(private val postgrest: Postgrest) {
    
    suspend fun searchServices(query: String): Result<List<Service>> {
        return try {
            val results = postgrest.from("services")
                .select {
                    filter {
                        or {
                            ilike("title", "%$query%")
                            ilike("description", "%$query%")
                        }
                    }
                }
                .decodeList<Service>()
            
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 📁 Almacenamiento (Storage)

### Subir Archivo

```kotlin
@Inject
constructor(private val storage: Storage) {
    
    suspend fun uploadImage(
        bucket: String,
        path: String,
        file: ByteArray
    ): Result<String> {
        return try {
            storage.from(bucket).upload(path, file)
            
            // Obtener URL pública
            val url = storage.from(bucket).publicUrl(path)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Descargar Archivo

```kotlin
suspend fun downloadImage(bucket: String, path: String): Result<ByteArray> {
    return try {
        val data = storage.from(bucket).downloadAuthenticated(path)
        Result.success(data)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Eliminar Archivo

```kotlin
suspend fun deleteFile(bucket: String, path: String): Result<Unit> {
    return try {
        storage.from(bucket).delete(path)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

---

## ⚡ Tiempo Real (Realtime)

### Suscribirse a Cambios

```kotlin
@Inject
constructor(private val realtime: Realtime) {
    
    fun subscribeToServices(): Flow<Service> = flow {
        val channel = realtime.createChannel("services")
        
        channel.postgresChangeFlow<PostgresAction.Insert>("public") {
            table = "services"
        }.collect { change ->
            val service = change.decodeRecord<Service>()
            emit(service)
        }
        
        channel.subscribe()
    }
}
```

### Escuchar Cambios en ViewModel

```kotlin
class ServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {
    
    init {
        viewModelScope.launch {
            serviceRepository.subscribeToServices()
                .collect { newService ->
                    // Actualizar UI con nuevo servicio
                    updateServiceList(newService)
                }
        }
    }
}
```

---

## 💡 Ejemplos de Uso Completos

### Ejemplo 1: Screen de Login con Supabase

```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
        
        when (loginState) {
            is LoginState.Loading -> CircularProgressIndicator()
            is LoginState.Success -> {
                // Navegar a home
            }
            is LoginState.Error -> {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = Color.Red
                )
            }
            else -> {}
        }
    }
}
```

### Ejemplo 2: Repository Pattern

```kotlin
interface ServiceRepository {
    suspend fun getAllServices(): Flow<Result<List<Service>>>
    suspend fun createService(service: Service): Result<Service?>
    suspend fun updateService(id: String, service: Service): Result<Service?>
    suspend fun deleteService(id: String): Result<Unit>
}

class ServiceRepositoryImpl @Inject constructor(
    private val database: SupabaseDatabaseService
) : ServiceRepository {
    
    override suspend fun getAllServices(): Flow<Result<List<Service>>> = flow {
        emit(database.getAll("services"))
    }.flowOn(Dispatchers.IO)
    
    override suspend fun createService(service: Service): Result<Service?> {
        return database.insert("services", service)
    }
    
    override suspend fun updateService(id: String, service: Service): Result<Service?> {
        return database.update("services", id, service)
    }
    
    override suspend fun deleteService(id: String): Result<Unit> {
        return database.delete("services", id)
    }
}
```

### Ejemplo 3: UseCase con Supabase

```kotlin
class GetServicesUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    operator fun invoke(): Flow<Result<List<Service>>> {
        return serviceRepository.getAllServices()
    }
}

class CreateServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(service: Service): Result<Service?> {
        // Validaciones
        if (service.title.isBlank()) {
            return Result.failure(Exception("El título no puede estar vacío"))
        }
        if (service.price <= 0) {
            return Result.failure(Exception("El precio debe ser mayor a 0"))
        }
        
        return serviceRepository.createService(service)
    }
}
```

---

## ✅ Mejores Prácticas

### 1. Manejo de Errores

```kotlin
suspend fun safeApiCall(block: suspend () -> Unit): Result<Unit> {
    return try {
        block()
        Result.success(Unit)
    } catch (e: Exception) {
        when (e) {
            is HttpException -> {
                // Error HTTP
                Timber.e(e, "Error HTTP: ${e.message}")
            }
            is IOException -> {
                // Error de red
                Timber.e(e, "Error de red: ${e.message}")
            }
            else -> {
                // Otros errores
                Timber.e(e, "Error desconocido: ${e.message}")
            }
        }
        Result.failure(e)
    }
}
```

### 2. Cache Local

Combina Supabase con Room para cache local:

```kotlin
class HybridServiceRepository @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val roomDao: ServiceDao
) {
    
    suspend fun getServices(): Flow<List<Service>> = flow {
        // Primero emitir datos locales
        emit(roomDao.getAllServices())
        
        // Luego actualizar desde Supabase
        supabaseDatabase.getAll<Service>("services")
            .onSuccess { remoteServices ->
                // Actualizar cache local
                roomDao.insertAll(remoteServices)
                emit(remoteServices)
            }
    }
}
```

### 3. Manejo de Sesiones

```kotlin
class SessionManager @Inject constructor(
    private val supabaseAuth: SupabaseAuthService
) {
    
    suspend fun refreshSessionIfNeeded() {
        val session = supabaseAuth.getCurrentSession()
        if (session?.isExpired() == true) {
            supabaseAuth.refreshSession()
        }
    }
}
```

### 4. Testing

```kotlin
@Test
fun `test service creation with Supabase`() = runTest {
    // Arrange
    val service = Service(
        title = "Test Service",
        description = "Test Description",
        price = 100.0,
        professionalId = "test-id"
    )
    
    // Act
    val result = serviceRepository.createService(service)
    
    // Assert
    assertTrue(result.isSuccess)
    assertNotNull(result.getOrNull()?.id)
}
```

### 5. Seguridad

- **Nunca** expongas tu `service_role_key` en el código
- Usa RLS (Row Level Security) en Supabase
- Valida datos en el cliente y en el servidor
- Usa políticas de seguridad adecuadas

### 6. Configuración de RLS en Supabase

Ejemplo de política para la tabla `services`:

```sql
-- Habilitar RLS
ALTER TABLE services ENABLE ROW LEVEL SECURITY;

-- Los usuarios autenticados pueden leer todos los servicios
CREATE POLICY "Users can read all services"
ON services FOR SELECT
TO authenticated
USING (true);

-- Los profesionales solo pueden crear sus propios servicios
CREATE POLICY "Professionals can create their own services"
ON services FOR INSERT
TO authenticated
WITH CHECK (auth.uid() = professional_id);

-- Los profesionales solo pueden actualizar sus propios servicios
CREATE POLICY "Professionals can update their own services"
ON services FOR UPDATE
TO authenticated
USING (auth.uid() = professional_id);
```

---

## 🔗 Referencias

- [Documentación oficial de Supabase](https://supabase.com/docs)
- [supabase-kt GitHub](https://github.com/supabase-community/supabase-kt)
- [Ktor Client](https://ktor.io/docs/client.html)
- [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html)

---

## 📝 Notas Adicionales

### Migración desde API Actual

Si estás migrando desde tu API actual en Vercel a Supabase:

1. **Mantén ambas APIs** durante la transición
2. **Migra módulo por módulo** (auth → servicios → solicitudes)
3. **Usa feature flags** para activar/desactivar Supabase
4. **Prueba exhaustivamente** antes de eliminar la API antigua

### Performance

- Usa índices en Supabase para consultas frecuentes
- Implementa paginación para listas grandes
- Usa cache local con Room
- Considera usar Realtime solo donde sea necesario

### Monitoreo

- Activa logs en modo debug
- Monitorea el uso de API en el dashboard de Supabase
- Implementa analytics para errores

---

**¡Integración de Supabase completada! 🎉**

Para cualquier duda o problema, consulta la documentación oficial o abre un issue en el repositorio del proyecto.

