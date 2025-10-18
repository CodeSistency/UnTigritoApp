-- ========================================
-- Script para crear usuario de prueba
-- Para: Login con Supabase en UnTigrito
-- ========================================

-- IMPORTANTE: Ejecuta este script en el SQL Editor de Supabase
-- URL: https://app.supabase.com/project/wcyyphrkkudovnizwpsr/sql

-- ========================================
-- 1. Verificar si el usuario ya existe
-- ========================================

SELECT * FROM "User" WHERE email = 'luisjose@gmail.com';

-- Si ya existe, puedes actualizarlo o eliminarlo y volver a crear

-- ========================================
-- 2. Eliminar usuario si existe (OPCIONAL)
-- ========================================

-- DELETE FROM "User" WHERE email = 'luisjose@gmail.com';

-- ========================================
-- 3. Crear usuario de prueba
-- ========================================

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
    gen_random_uuid()::text,
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
)
ON CONFLICT (email) DO UPDATE SET
    password = EXCLUDED.password,
    name = EXCLUDED.name,
    "updatedAt" = NOW();

-- ========================================
-- 4. Verificar que se creó correctamente
-- ========================================

SELECT 
    id,
    email,
    name,
    role,
    "isVerified",
    "isIDVerified",
    "createdAt"
FROM "User" 
WHERE email = 'luisjose@gmail.com';

-- ========================================
-- 5. (OPCIONAL) Crear más usuarios de prueba
-- ========================================

-- Usuario profesional
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
    gen_random_uuid()::text,
    'maria@gmail.com',
    '1234567',
    'María Rodríguez',
    'PROFESSIONAL',
    true,
    true,
    150.50,
    false,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO UPDATE SET
    password = EXCLUDED.password,
    name = EXCLUDED.name,
    "updatedAt" = NOW();

-- Usuario con teléfono
INSERT INTO "User" (
    id,
    phone,
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
    gen_random_uuid()::text,
    '04121234567',
    '1234567',
    'Carlos Pérez',
    'CLIENT',
    false,
    false,
    0.0,
    false,
    NOW(),
    NOW()
)
ON CONFLICT (phone) DO UPDATE SET
    password = EXCLUDED.password,
    name = EXCLUDED.name,
    "updatedAt" = NOW();

-- ========================================
-- 6. Ver todos los usuarios de prueba
-- ========================================

SELECT 
    id,
    email,
    phone,
    name,
    role,
    "isVerified",
    "isIDVerified",
    balance,
    "isSuspended"
FROM "User" 
WHERE email IN ('luisjose@gmail.com', 'maria@gmail.com')
   OR phone = '04121234567'
ORDER BY "createdAt" DESC;

-- ========================================
-- 7. (OPCIONAL) Verificar/Configurar RLS
-- ========================================

-- Ver políticas actuales de la tabla User
SELECT 
    schemaname,
    tablename,
    policyname,
    permissive,
    roles,
    cmd,
    qual,
    with_check
FROM pg_policies
WHERE tablename = 'User';

-- Si no hay políticas o RLS bloquea el acceso, puedes:

-- Opción A: Desactivar RLS temporalmente para testing
-- ⚠️ SOLO PARA DESARROLLO - NO EN PRODUCCIÓN
-- ALTER TABLE "User" DISABLE ROW LEVEL SECURITY;

-- Opción B: Crear política permisiva para testing
-- ⚠️ SOLO PARA DESARROLLO - NO EN PRODUCCIÓN
/*
CREATE POLICY "Allow public read for testing"
ON "User"
FOR SELECT
TO anon, authenticated
USING (true);
*/

-- Opción C: Política más segura (recomendada)
/*
CREATE POLICY "Users can read own data"
ON "User"
FOR SELECT
TO authenticated
USING (auth.uid()::text = id);

CREATE POLICY "Allow login queries"
ON "User"
FOR SELECT
TO anon
USING (true);
*/

-- ========================================
-- RESULTADO ESPERADO
-- ========================================

-- Deberías ver los usuarios creados con sus datos
-- Ahora puedes usar estas credenciales en la app:

-- Cliente 1:
--   Email: luisjose@gmail.com
--   Password: 1234567

-- Profesional:
--   Email: maria@gmail.com
--   Password: 1234567

-- Cliente con teléfono:
--   Phone: 04121234567
--   Password: 1234567

