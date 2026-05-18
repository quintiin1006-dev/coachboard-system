// Rol.java
package com.coachboard.enums;

/**
 * Roles del sistema — definen qué puede hacer cada usuario
 * y a qué nivel opera dentro de la plataforma.
 *
 * Jerarquía de autoridad:
 * PLATFORM_ADMIN > ORG_ADMIN > ORG_STAFF > ORG_CLIENTE
 *
 * PLATFORM_ADMIN no pertenece a ninguna organización.
 * Los demás roles siempre están ligados a una organización específica.
 */
public enum Rol {

    /**
     * Administrador de la plataforma SaaS (el equipo desarrollador).
     * Único rol que puede crear organizaciones y sus admins.
     * Su organizacion_id en el JWT es null — opera sobre toda la plataforma.
     */
    PLATFORM_ADMIN,

    /**
     * Dueño o director del club deportivo.
     * Creado por PLATFORM_ADMIN junto con su organización.
     * Puede crear staff (según su plan), registrar clientes y pagos.
     * Si no tiene staff, él mismo opera como recepcionista.
     */
    ORG_ADMIN,

    /**
     * Personal de recepción u operativo del club.
     * Creado por ORG_ADMIN — su existencia es opcional según el plan.
     * Solo puede registrar clientes y pagos de su propia organización.
     * No puede crear otros usuarios ni ver métricas financieras.
     */
    ORG_STAFF,

    /**
     * Miembro o cliente del club deportivo.
     * Creado por ORG_ADMIN o ORG_STAFF con sus credenciales.
     * Solo accede a su propio dashboard: membresía, pagos, notificaciones.
     * No puede ver datos de otros clientes ni de otras organizaciones.
     */
    ORG_CLIENTE
}