# 🚀 GUÍA DE INICIO RÁPIDO: Módulo de Profesional

**Fecha**: 2025-10-18  
**Estado**: LISTO PARA IMPLEMENTACIÓN  

---

## ⚡ COMIENZA EN 5 MINUTOS

### Paso 1: Identifica Tu Rol

| Rol | Tareas | Documento |
|-----|--------|-----------|
| 🟣 android-developer | 34 | `planning/INSTRUCCIONES-ANDROID-DEVELOPER.md` |
| 🟠 database-engineer | 3 | `planning/PHASE2-DELEGATIONS.md` |
| 🔵 api-engineer | 6 | `planning/PHASE2-DELEGATIONS.md` |
| 🟢 testing-engineer | 8 | `planning/PHASE2-DELEGATIONS.md` |

### Paso 2: Lee Tu Documentación

1. **Todos comienzan aquí**:
   - `spec.md` - Especificación completa (15 min)
   - `technical-spec.md` - Detalles técnicos (20 min)
   - `requirements.md` - Requisitos (10 min)

2. **Tu rol específico**:
   - Busca tu nombre en `planning/PHASE2-DELEGATIONS.md`
   - Lee tus tareas asignadas
   - Sigue el orden propuesto

### Paso 3: Descarga Tu Checklist

```
planning/INSTRUCCIONES-[TU-ROL].md
```

---

## 📋 ESTRUCTURA DEL PROYECTO

```
agent-os/specs/2025-10-18-professional-module/
├── 📖 ESPECIFICACIÓN (LEE PRIMERO)
│   ├── spec.md                          ← Especificación general
│   ├── technical-spec.md                ← Detalles técnicos
│   ├── requirements.md                  ← Requisitos funcionales
│   └── README.md                        ← Resumen ejecutivo
│
├── 📋 TAREAS
│   └── tasks.md                         ← 51 tareas en 18 grupos
│
├── 📚 PLANIFICACIÓN (planning/)
│   ├── subagent-assignments.yml         ← Asignaciones YAML
│   ├── INSTRUCCIONES-ANDROID-DEVELOPER.md ← Tu guía (si eres android-dev)
│   ├── PHASE2-DELEGATIONS.md            ← Delegaciones desglosadas
│   ├── PHASE3-VERIFICATIONS.md          ← Plan de verificación
│   └── PROYECTO-COMPLETADO.md           ← Resumen final
│
├── 💾 IMPLEMENTACIÓN (implementation/)
│   └── (Tus reportes irán aquí)
│
└── ✅ VERIFICACIÓN (verification/)
    └── (Reportes de verificación irán aquí)
```

---

## 🎯 ORDEN DE LECTURA RECOMENDADO

### Para TODOS (30 minutos)
1. Este documento (5 min)
2. `README.md` (5 min)
3. `spec.md` (10 min)
4. `requirements.md` (10 min)

### Para TU ROL ESPECÍFICO (60-90 minutos)
1. Tu documento de instrucciones (30 min)
2. `technical-spec.md` (30 min)
3. `tasks.md` (30 min)

---

## 🔥 ACCIONES INMEDIATAS

### 🟣 Si eres **android-developer**
```
1. Lee planning/INSTRUCCIONES-ANDROID-DEVELOPER.md
2. Comienza con TASK-001, 002, 003 (Semana 1)
3. Sigue el cronograma semanal
4. Reporta en implementation/[número]-[nombre].md
```

### 🟠 Si eres **database-engineer**
```
1. Lee planning/PHASE2-DELEGATIONS.md (sección database-engineer)
2. Comienza con TASK-004, 005, 006
3. Coordina con api-engineer
4. Reporta en implementation/01-modelos-datos.md
```

### 🔵 Si eres **api-engineer**
```
1. Lee planning/PHASE2-DELEGATIONS.md (sección api-engineer)
2. Comienza con TASK-007, 008 (Semana 1)
3. Coordina con android-developer
4. Reporta en implementation/02-servicios-api.md
```

### 🟢 Si eres **testing-engineer**
```
1. Lee planning/PHASE2-DELEGATIONS.md (sección testing-engineer)
2. Comienza en Semana 2 (espera código)
3. Ejecuta tests paralelos a implementación
4. Reporta en verification/[número]-testing-[tipo].md
```

---

## 📊 CRONOGRAMA

```
📅 SEMANA 1 (CRÍTICA) ⭐
   🟠 database-engineer: TASK-004, 005, 006
   🔵 api-engineer: TASK-007, 008
   🟣 android-developer: TASK-001, 002, 003

📅 SEMANA 2-5
   🟣 android-developer: Subflujos principales
   🟢 testing-engineer: Tests paralelos
   
📅 SEMANA 6
   🟣 android-developer: Optimizaciones
   🟢 testing-engineer: Testing final
```

---

## 📝 FORMATO DE REPORTE

Después de completar tu grupo de tareas, genera:

```markdown
# Implementación: [Nombre del Grupo]

**Subagente**: [Tu Nombre]
**Tareas**: TASK-XXX, TASK-YYY, ...
**Fecha**: YYYY-MM-DD
**Horas**: X
**Estado**: ✅ COMPLETADA

## Tareas Completadas
- [x] TASK-XXX: Descripción
- [x] TASK-YYY: Descripción

## Desafíos Encontrados
- Desafío 1: Solución
- Desafío 2: Workaround

## Archivos Generados
- archivo1.kt
- archivo2.kt
```

Guarda esto en: `implementation/[NN]-[nombre-grupo].md`

---

## 🔗 DEPENDENCIAS CRÍTICAS

⚠️ **Orden de ejecución**:
```
SEMANA 1 (Debe completarse antes de Semana 2):
  ├─ database-engineer (modelos)
  ├─ api-engineer (servicios)
  └─ android-developer (configuración)
     ↓
SEMANA 2+ (Puede comenzar después de Semana 1):
  ├─ android-developer (pantallas)
  ├─ testing-engineer (tests)
  └─ api-engineer (lógica)
```

---

## 📞 PREGUNTAS FRECUENTES

**P: ¿Dónde encuentro mis tareas específicas?**
R: En `planning/PHASE2-DELEGATIONS.md` o en `planning/INSTRUCCIONES-[TU-ROL].md`

**P: ¿Cuál es el orden de implementación?**
R: Ver sección "CRONOGRAMA" arriba. Semana 1 es CRÍTICA.

**P: ¿Dónde reporto mi progreso?**
R: En `implementation/[NN]-[nombre-grupo].md` (crea los archivos)

**P: ¿Qué si encuentro un problema de dependencia?**
R: Comunica inmediatamente al equipo. Coordina con otros roles.

**P: ¿Cuándo comienza el testing?**
R: testing-engineer comienza en Semana 2, paralelo a desarrollo.

---

## ✅ CHECKLIST DE INICIO

- [ ] He leído este documento (5 min)
- [ ] He leído `spec.md` (15 min)
- [ ] He leído `technical-spec.md` (20 min)
- [ ] He leído mi documento de instrucciones (30 min)
- [ ] Entiendo mis tareas asignadas
- [ ] Conozco el orden de ejecución
- [ ] Tengo claro dónde reportar mi progreso
- [ ] Estoy listo para comenzar la Semana 1

---

## 🚀 ¡COMIENZA AHORA!

**Tu rol**: [Identifica arriba]  
**Primeras tareas**: Ver sección "ACCIONES INMEDIATAS"  
**Duración total**: 6 semanas  
**Equipo**: 4 especialistas + 2 verificadores  

---

**¿Preguntas? Revisa los documentos en `planning/` o contacta al líder del proyecto.**

**Estado**: ✅ LISTO PARA IMPLEMENTACIÓN  
**Fecha de Inicio**: 2025-10-18 (Semana 1 comienza)

¡Adelante! 🚀
