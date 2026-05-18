// PlanSaaS.java
package com.coachboard.enums;

/**
 * Planes de suscripción disponibles en la plataforma SaaS.
 *
 * El plan controla las funcionalidades disponibles para cada organización.
 * La validación de límites por plan se aplica en la capa Service,
 * no en el Controller — es lógica de negocio, no de presentación.
 *
 * Límites por plan:
 * ┌─────────────────────────┬─────────┬──────────────┐
 * │ Funcionalidad           │ BASICO  │ PROFESIONAL  │
 * ├─────────────────────────┼─────────┼──────────────┤
 * │ Clientes máximos        │ 50      │ Ilimitados   │
 * │ Usuarios staff          │ 0       │ Hasta 5      │
 * │ Tipos de membresía      │ 3       │ Ilimitados   │
 * │ Dashboard métricas      │ Básico  │ Avanzado     │
 * │ Notificaciones          │ Cliente │ Cliente+Admin│
 * └─────────────────────────┴─────────┴──────────────┘
 */
public enum PlanSaaS {

    /**
     * Plan de entrada para clubs pequeños.
     * No permite crear staff — el admin opera todo el sistema.
     * Máximo 50 clientes y 3 tipos de membresía.
     */
    BASICO,

    /**
     * Plan para clubs medianos con personal operativo.
     * Permite hasta 5 usuarios de staff (recepcionistas).
     * Clientes y membresías ilimitadas.
     * Dashboard con métricas avanzadas de ingresos y renovaciones.
     */
    PROFESIONAL
}