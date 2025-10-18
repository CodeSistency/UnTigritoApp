# 🧪 Guía de Prueba - Login con Supabase

## 📋 Cambios Implementados

### LoginViewModel

✅ **Nuevo método agregado**: `loginWithSupabase(identifier: String, password: String)`

Este método:
1. Valida el identificador (email o teléfono venezolano)
2. Valida que la contraseña tenga al menos 6 caracteres
3. Hace una consulta directa a la tabla `User` en Supabase
4. Busca un usuario que coincida con email/phone Y password
5. Si encuentra el usuario, lo convierte al modelo de dominio y actualiza el estado
6. Maneja errores y muestra mensajes apropiados

### LoginScreen

✅ **Botón de login actualizado** para usar `loginWithSupabase()` en lugar de `login()`

---

## 🔧 Configurar Usuario de Prueba en Supabase

### Opción 1: Crear Usuario desde el Dashboard de Supabase (Recomendado)

1. **Ve a tu proyecto en Supabase**:
   - URL: [https://app.supabase.com/project/wcyyphrkkudovnizwpsr](https://app.supabase.com/project/wcyyphrkkudovnizwpsr)

2. **Abre el Table Editor**:
   - En el menú lateral, haz clic en `Table Editor`
   - Selecciona la tabla `User`

3. **Inserta un nuevo usuario**:
   - Haz clic en `Insert` → `Insert row`
   - Completa los campos:
     ```
     id: (se genera automáticamente si usas cuid/uuid)
     email: luisjose@gmail.com
     phone: null (o un teléfono venezolano como 04121234567)
     password: 1234567
     name: Luis José
     role: CLIENT
     isVerified: false
     isIDVerified: false
     balance: 0.0
     isSuspended: false
     ```
   - Haz clic en `Save`

### Opción 2: Insertar Usuario con SQL

1. **Ve al SQL Editor**:
   - En el menú lateral, haz clic en `SQL Editor`
   - Haz clic en `New query`

2. **Ejecuta este SQL**:
   ```sql
   INSERT INTO "User" (
       id,
       email,
       password,
       name,
       role,
       "isVerified",
       "isIDVerified",
       balance,
       "isSuspended",
       "createdAt",
       "updatedAt"
   ) VALUES (
       gen_random_uuid()::text,  -- o usar cuid si tu backend lo genera
       'luisjose@gmail.com',
       '1234567',
       'Luis José',
       'CLIENT',
       false,
       false,
       0.0,
       false,
       NOW(),
       NOW()
   );
   ```

3. **Verifica que se creó**:
   ```sql
   SELECT * FROM "User" WHERE email = 'luisjose@gmail.com';
   ```

---

## 🧪 Probar el Login

### Datos de prueba pre-configurados en LoginScreen:

```kotlin
var identifier by remember { mutableStateOf("luisjose@gmail.com") }
var password by remember { mutableStateOf("1234567") }
```

### Pasos para probar:

1. **Sincroniza Gradle** (si no lo has hecho):
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

2. **Ejecuta la aplicación**:
   - En Android Studio: Run → Run 'app'
   - O: `./gradlew installDebug`

3. **En la pantalla de Login**:
   - Los campos ya vienen pre-llenados con:
     - Email: `luisjose@gmail.com`
     - Contraseña: `1234567`
   - Haz clic en "Iniciar Sesión"

4. **Observa los logs**:
   - En Logcat, filtra por tag `TAG` o `Timber`
   - Deberías ver:
     ```
     🔐 SUPABASE LOGIN_START - Identifier: luisjose@gmail.com
     📊 SUPABASE - Consultando tabla User...
     📊 SUPABASE - Resultados encontrados: 1
     ✅ SUPABASE LOGIN_SUCCESS - User: [id]
     ✅ User name: Luis José
     ✅ User email: luisjose@gmail.com
     ✅ User role: CLIENT
     ```

---

## ✅ Casos de Prueba

### Caso 1: Login Exitoso con Email

**Datos**:
- Email: `luisjose@gmail.com`
- Password: `1234567`

**Resultado esperado**:
- ✅ Estado cambia a `AuthState.Loading`
- ✅ Consulta a Supabase exitosa
- ✅ Usuario encontrado
- ✅ Estado cambia a `AuthState.Authenticated`
- ✅ Navegación a pantalla principal

### Caso 2: Login con Credenciales Incorrectas

**Datos**:
- Email: `luisjose@gmail.com`
- Password: `wrongpassword`

**Resultado esperado**:
- ✅ Estado cambia a `AuthState.Loading`
- ✅ Consulta a Supabase exitosa
- ⚠️ Usuario no encontrado (lista vacía)
- ✅ Estado cambia a `AuthState.Error`
- ✅ Mensaje: "Email/teléfono o contraseña incorrectos"
- ✅ Snackbar muestra el error

### Caso 3: Login con Email Inválido

**Datos**:
- Email: `notanemail`
- Password: `1234567`

**Resultado esperado**:
- ⚠️ Validación falla antes de hacer consulta
- ✅ Estado cambia a `AuthState.Error`
- ✅ Mensaje: "Email o teléfono inválido"

### Caso 4: Login con Contraseña Corta

**Datos**:
- Email: `luisjose@gmail.com`
- Password: `123`

**Resultado esperado**:
- ⚠️ Validación falla antes de hacer consulta
- ✅ Estado cambia a `AuthState.Error`
- ✅ Mensaje: "La contraseña debe tener al menos 6 caracteres"

### Caso 5: Login con Teléfono (si configuraste un usuario con teléfono)

**Datos**:
- Phone: `04121234567`
- Password: `1234567`

**Resultado esperado**:
- ✅ Valida como teléfono venezolano
- ✅ Consulta campo `phone` en vez de `email`
- ✅ Login exitoso si las credenciales son correctas

---

## 🔍 Debugging

### Ver consultas SQL en tiempo real

1. Ve a tu proyecto en Supabase
2. Abre `Database` → `Query Performance`
3. O usa `Logs` → `Postgres Logs`

### Verificar datos en la tabla User

```sql
-- Ver todos los usuarios
SELECT id, email, phone, name, role FROM "User";

-- Ver usuario específico
SELECT * FROM "User" WHERE email = 'luisjose@gmail.com';

-- Contar usuarios
SELECT COUNT(*) FROM "User";
```

### Verificar políticas RLS

Si el login falla y no encuentras al usuario:

1. Ve a `Authentication` → `Policies`
2. Verifica que la tabla `User` tenga una política que permita SELECT
3. Temporal: Desactiva RLS para testing:
   ```sql
   ALTER TABLE "User" DISABLE ROW LEVEL SECURITY;
   ```
   
   ⚠️ **IMPORTANTE**: Reactivar después:
   ```sql
   ALTER TABLE "User" ENABLE ROW LEVEL SECURITY;
   ```

---

## 📊 Flujo del Login

```
1. Usuario ingresa email/password
         ↓
2. onClick llama a viewModel.loginWithSupabase()
         ↓
3. Validación de datos (email/phone y password)
         ↓
4. Estado → AuthState.Loading
         ↓
5. Consulta a Supabase:
   SELECT * FROM "User" 
   WHERE email = ? AND password = ?
         ↓
6a. Usuario encontrado:          6b. Usuario NO encontrado:
    - Convierte a User            - Estado → AuthState.Error
    - Estado → Authenticated      - Muestra "Credenciales incorrectas"
    - Guarda en AuthStateManager
    - Navega a home
```

---

## ⚠️ Notas Importantes

### Seguridad

**🚨 ADVERTENCIA**: Este método es solo para pruebas iniciales. 

**Problemas de seguridad**:
1. Las contraseñas están en texto plano (sin hash)
2. Se envía la contraseña al cliente para comparación
3. No hay protección contra fuerza bruta
4. Las contraseñas viajan por la red sin encriptar adicional

**Para producción, debes**:
1. Usar Supabase Auth (`SupabaseAuthService`)
2. Hashear contraseñas con bcrypt/argon2
3. Implementar rate limiting
4. Usar HTTPS (que ya tienes con Supabase)
5. Implementar MFA para cuentas sensibles

### RLS (Row Level Security)

Asegúrate de tener una política que permita SELECT en la tabla User:

```sql
-- Política para permitir login (solo lectura con credenciales)
CREATE POLICY "Users can login"
ON "User"
FOR SELECT
TO anon
USING (true);
```

Para más seguridad, puedes crear una vista o función:

```sql
-- Función para login seguro (ejemplo)
CREATE OR REPLACE FUNCTION public.login_user(
    p_email TEXT,
    p_password TEXT
)
RETURNS TABLE (
    id TEXT,
    email TEXT,
    name TEXT,
    role TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.email, u.name, u.role
    FROM "User" u
    WHERE u.email = p_email 
      AND u.password = p_password
      AND u."isSuspended" = false;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

---

## 🎯 Siguiente: Mejorar la Autenticación

Una vez que el login básico funcione, considera:

1. **Usar Supabase Auth** (`SupabaseAuthService`)
   - Manejo automático de sesiones
   - JWT tokens
   - Refresh tokens
   - Passwords hasheadas

2. **Hashear contraseñas**
   - Usar bcrypt en el backend
   - Nunca almacenar en texto plano

3. **Implementar OAuth**
   - Google Sign-In ya está configurado
   - Usar con Supabase Auth

4. **Agregar verificación de email/teléfono**
   - Ya tienes `isVerified` en el modelo
   - Implementar flujo de verificación

---

## 📝 Checklist

Antes de probar:
- [ ] Gradle sincronizado sin errores
- [ ] Usuario de prueba creado en Supabase
- [ ] Credenciales correctas en LoginScreen
- [ ] App compilada y ejecutada en emulador/dispositivo
- [ ] Logcat abierto para ver logs

Durante la prueba:
- [ ] Login exitoso con credenciales correctas
- [ ] Error mostrado con credenciales incorrectas
- [ ] Loading indicator se muestra durante la consulta
- [ ] Navegación funciona después del login exitoso
- [ ] Logs muestran el flujo completo

---

**¡Listo para probar!** 🚀

Si encuentras algún error, revisa los logs y las políticas RLS en Supabase.

