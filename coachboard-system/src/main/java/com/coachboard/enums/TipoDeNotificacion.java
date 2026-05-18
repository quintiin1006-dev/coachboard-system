// TipoDeNotificacion.java
package com.coachboard.enums;

/**
 * Categorías de alertas que genera el scheduler automáticamente.
 *
 * El scheduler evalúa inscripciones ACTIVAS cada día a las 8am
 * y genera notificaciones según estos tipos. Antes de crear una
 * notificación, el Service verifica que no exista ya una del mismo
 * tipo para esa inscripción en el día actual — evita duplicados.
 */
public enum TipoDeNotificacion {

    /**
     * La membresía vence dentro de los próximos días configurados
     * (por defecto: 7, 3 y 1 día antes del vencimiento).
     * El campo diasRestantes en Notificacion indica cuántos días quedan.
     */
    VENCIMIENTO_PROXIMO,

    /**
     * La fechaDeFin ya fue superada.
     * Se genera una sola vez cuando el scheduler detecta el vencimiento
     * y cambia el estado de la inscripción a VENCIDA.
     */
    MEMBRESIA_VENCIDA,

    /**
     * Existe una inscripción con estado PENDIENTE_PAGO sin resolver.
     * Se genera si el pago lleva más de 24 horas sin confirmarse.
     */
    PAGO_PENDIENTE
}