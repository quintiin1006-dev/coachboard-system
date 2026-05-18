// EstadoDelPago.java
package com.coachboard.enums;

/**
 * Estados posibles de un registro de pago.
 *
 * La transición PENDIENTE → COMPLETADO es el evento que dispara
 * la activación de la inscripción asociada en el Service.
 * Esa lógica vive en InscripcionService, no en PagoService,
 * para respetar el principio de responsabilidad única.
 */
public enum EstadoDelPago {

    /** Pago registrado pero pendiente de verificación (ej: efectivo por contar). */
    PENDIENTE,

    /** Dinero recibido y verificado — activa la inscripción asociada. */
    COMPLETADO,

    /** Transacción fallida: tarjeta rechazada, transferencia no aplicada, etc. */
    RECHAZADO
}