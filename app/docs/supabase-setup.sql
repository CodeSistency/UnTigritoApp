-- ========================================
-- SCRIPTS DE CONFIGURACIÓN PARA SUPABASE
-- Aplicación: UnTigrito
-- ========================================

-- IMPORTANTE: Ejecuta estos scripts en el SQL Editor de tu proyecto en Supabase
-- URL: https://app.supabase.com/project/_/sql

-- ========================================
-- 1. TABLA DE USUARIOS (EJEMPLO)
-- ========================================

-- Crear tabla de usuarios extendida (además de auth.users de Supabase)
CREATE TABLE IF NOT EXISTS public.users (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    name TEXT,
    phone TEXT,
    role TEXT NOT NULL DEFAULT 'CLIENT' CHECK (role IN ('CLIENT', 'PROFESSIONAL', 'ADMIN')),
    is_verified BOOLEAN DEFAULT FALSE,
    is_id_verified BOOLEAN DEFAULT FALSE,
    balance DECIMAL(10, 2) DEFAULT 0.0,
    is_suspended BOOLEAN DEFAULT FALSE,
    location_lat DOUBLE PRECISION,
    location_lng DOUBLE PRECISION,
    location_address TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_users_email ON public.users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON public.users(role);
CREATE INDEX IF NOT EXISTS idx_users_phone ON public.users(phone);

-- ========================================
-- 2. TABLA DE SERVICIOS
-- ========================================

CREATE TABLE IF NOT EXISTS public.services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    professional_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    category TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_services_professional ON public.services(professional_id);
CREATE INDEX IF NOT EXISTS idx_services_category ON public.services(category);
CREATE INDEX IF NOT EXISTS idx_services_active ON public.services(is_active);

-- ========================================
-- 3. TABLA DE SOLICITUDES
-- ========================================

CREATE TABLE IF NOT EXISTS public.requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    service_id UUID NOT NULL REFERENCES public.services(id) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    description TEXT,
    scheduled_date TIMESTAMP WITH TIME ZONE,
    completed_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_requests_client ON public.requests(client_id);
CREATE INDEX IF NOT EXISTS idx_requests_service ON public.requests(service_id);
CREATE INDEX IF NOT EXISTS idx_requests_status ON public.requests(status);

-- ========================================
-- 4. TABLA DE TRANSACCIONES
-- ========================================

CREATE TABLE IF NOT EXISTS public.transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    request_id UUID NOT NULL REFERENCES public.requests(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    type TEXT NOT NULL CHECK (type IN ('PAYMENT', 'REFUND', 'WITHDRAWAL', 'DEPOSIT')),
    status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    payment_method TEXT,
    transaction_ref TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_transactions_request ON public.transactions(request_id);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON public.transactions(status);

-- ========================================
-- 5. FUNCIONES PARA ACTUALIZAR updated_at
-- ========================================

-- Función para actualizar automáticamente updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar trigger a todas las tablas
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON public.users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_services_updated_at BEFORE UPDATE ON public.services
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_requests_updated_at BEFORE UPDATE ON public.requests
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_transactions_updated_at BEFORE UPDATE ON public.transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ========================================
-- 6. ROW LEVEL SECURITY (RLS)
-- ========================================

-- Habilitar RLS en todas las tablas
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.services ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

-- ========================================
-- POLÍTICAS DE SEGURIDAD - USERS
-- ========================================

-- Los usuarios pueden leer su propio perfil
CREATE POLICY "Users can read their own profile"
ON public.users FOR SELECT
TO authenticated
USING (auth.uid() = id);

-- Los usuarios pueden actualizar su propio perfil
CREATE POLICY "Users can update their own profile"
ON public.users FOR UPDATE
TO authenticated
USING (auth.uid() = id);

-- Los usuarios autenticados pueden leer perfiles públicos (limitar campos si es necesario)
CREATE POLICY "Authenticated users can read public profiles"
ON public.users FOR SELECT
TO authenticated
USING (true);

-- Solo usuarios autenticados pueden insertar su propio perfil
CREATE POLICY "Users can insert their own profile"
ON public.users FOR INSERT
TO authenticated
WITH CHECK (auth.uid() = id);

-- ========================================
-- POLÍTICAS DE SEGURIDAD - SERVICES
-- ========================================

-- Todos pueden leer servicios activos
CREATE POLICY "Anyone can read active services"
ON public.services FOR SELECT
TO authenticated
USING (is_active = true);

-- Los profesionales pueden crear servicios
CREATE POLICY "Professionals can create services"
ON public.services FOR INSERT
TO authenticated
WITH CHECK (
    auth.uid() = professional_id AND
    EXISTS (
        SELECT 1 FROM public.users 
        WHERE id = auth.uid() AND role = 'PROFESSIONAL'
    )
);

-- Los profesionales pueden actualizar sus propios servicios
CREATE POLICY "Professionals can update their own services"
ON public.services FOR UPDATE
TO authenticated
USING (auth.uid() = professional_id);

-- Los profesionales pueden eliminar sus propios servicios
CREATE POLICY "Professionals can delete their own services"
ON public.services FOR DELETE
TO authenticated
USING (auth.uid() = professional_id);

-- ========================================
-- POLÍTICAS DE SEGURIDAD - REQUESTS
-- ========================================

-- Los clientes pueden leer sus propias solicitudes
CREATE POLICY "Clients can read their own requests"
ON public.requests FOR SELECT
TO authenticated
USING (auth.uid() = client_id);

-- Los profesionales pueden leer solicitudes de sus servicios
CREATE POLICY "Professionals can read requests for their services"
ON public.requests FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.services 
        WHERE id = service_id AND professional_id = auth.uid()
    )
);

-- Los clientes pueden crear solicitudes
CREATE POLICY "Clients can create requests"
ON public.requests FOR INSERT
TO authenticated
WITH CHECK (
    auth.uid() = client_id AND
    EXISTS (
        SELECT 1 FROM public.users 
        WHERE id = auth.uid() AND role = 'CLIENT'
    )
);

-- Los clientes pueden actualizar sus propias solicitudes
CREATE POLICY "Clients can update their own requests"
ON public.requests FOR UPDATE
TO authenticated
USING (auth.uid() = client_id);

-- Los profesionales pueden actualizar solicitudes de sus servicios
CREATE POLICY "Professionals can update requests for their services"
ON public.requests FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.services 
        WHERE id = service_id AND professional_id = auth.uid()
    )
);

-- ========================================
-- POLÍTICAS DE SEGURIDAD - TRANSACTIONS
-- ========================================

-- Los usuarios pueden leer sus propias transacciones (como cliente)
CREATE POLICY "Users can read their own transactions as client"
ON public.transactions FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.requests 
        WHERE id = request_id AND client_id = auth.uid()
    )
);

-- Los profesionales pueden leer transacciones de sus servicios
CREATE POLICY "Professionals can read transactions for their services"
ON public.transactions FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.requests r
        JOIN public.services s ON r.service_id = s.id
        WHERE r.id = request_id AND s.professional_id = auth.uid()
    )
);

-- Solo el sistema puede crear transacciones (esto se manejaría con service_role_key en backend)
-- Para desarrollo, permitir a usuarios autenticados crear transacciones
CREATE POLICY "Authenticated users can create transactions"
ON public.transactions FOR INSERT
TO authenticated
WITH CHECK (
    EXISTS (
        SELECT 1 FROM public.requests 
        WHERE id = request_id AND 
        (client_id = auth.uid() OR EXISTS (
            SELECT 1 FROM public.services 
            WHERE id = service_id AND professional_id = auth.uid()
        ))
    )
);

-- ========================================
-- 7. FUNCIONES AUXILIARES
-- ========================================

-- Función para obtener el rol del usuario actual
CREATE OR REPLACE FUNCTION get_user_role()
RETURNS TEXT AS $$
BEGIN
    RETURN (
        SELECT role FROM public.users 
        WHERE id = auth.uid()
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Función para verificar si un usuario es profesional
CREATE OR REPLACE FUNCTION is_professional()
RETURNS BOOLEAN AS $$
BEGIN
    RETURN (
        SELECT role = 'PROFESSIONAL' FROM public.users 
        WHERE id = auth.uid()
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ========================================
-- 8. DATOS DE EJEMPLO (OPCIONAL)
-- ========================================

-- Insertar usuarios de ejemplo (solo para desarrollo)
-- NOTA: Los usuarios deben ser creados primero en auth.users mediante registro

-- Insertar servicios de ejemplo (reemplaza los UUIDs con IDs reales)
/*
INSERT INTO public.services (title, description, price, professional_id, category)
VALUES 
    ('Plomería Residencial', 'Reparación de tuberías, instalación de grifos, etc.', 50.00, 'UUID_DEL_PROFESIONAL', 'PLUMBING'),
    ('Electricidad Básica', 'Instalación de tomacorrientes, reparación de interruptores', 45.00, 'UUID_DEL_PROFESIONAL', 'ELECTRICAL'),
    ('Jardinería', 'Corte de césped, poda de árboles, diseño de jardines', 30.00, 'UUID_DEL_PROFESIONAL', 'GARDENING');
*/

-- ========================================
-- 9. CONFIGURACIÓN DE REALTIME (OPCIONAL)
-- ========================================

-- Habilitar Realtime para las tablas que necesitas actualizaciones en tiempo real
-- Esto se puede hacer desde la UI de Supabase o con SQL:

ALTER PUBLICATION supabase_realtime ADD TABLE public.services;
ALTER PUBLICATION supabase_realtime ADD TABLE public.requests;
ALTER PUBLICATION supabase_realtime ADD TABLE public.transactions;

-- ========================================
-- 10. VISTAS ÚTILES (OPCIONAL)
-- ========================================

-- Vista para obtener servicios con información del profesional
CREATE OR REPLACE VIEW public.services_with_professional AS
SELECT 
    s.*,
    u.name as professional_name,
    u.email as professional_email,
    u.phone as professional_phone
FROM public.services s
JOIN public.users u ON s.professional_id = u.id;

-- Vista para obtener solicitudes con información completa
CREATE OR REPLACE VIEW public.requests_full AS
SELECT 
    r.*,
    c.name as client_name,
    c.email as client_email,
    s.title as service_title,
    s.price as service_price,
    p.name as professional_name
FROM public.requests r
JOIN public.users c ON r.client_id = c.id
JOIN public.services s ON r.service_id = s.id
JOIN public.users p ON s.professional_id = p.id;

-- ========================================
-- FIN DE SCRIPTS
-- ========================================

-- Para verificar que todo se creó correctamente, ejecuta:
SELECT 
    schemaname,
    tablename,
    tableowner
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;

-- Ver políticas de RLS:
SELECT 
    schemaname,
    tablename,
    policyname,
    permissive,
    roles,
    cmd
FROM pg_policies
WHERE schemaname = 'public'
ORDER BY tablename, policyname;

