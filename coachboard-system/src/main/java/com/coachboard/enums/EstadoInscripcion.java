// EstadoInscripcion.java
package com.coachboard.enums;

/**
 * Ciclo de vida de una inscripción en el sistema.
 *
 * Transiciones válidas:
 * PENDIENTE_PAGO → ACTIVA     (pago confirmado por admin o staff)
 * ACTIVA         → VENCIDA    (scheduler detecta fechaDeFin superada)
 * PENDIENTE_PAGO → CANCELADA  (cancelación manual antes de pagar)
 * ACTIVA         → CANCELADA  (cancelación manual durante vigencia)
 *
 * Una inscripción VENCIDA o CANCELADA nunca vuelve a ACTIVA —
 * se crea una nueva inscripción para renovaciones.
 */
public enum EstadoInscripcion {

    /** Inscripción creada, esperando confirmación de pago. El cliente aún no tiene acceso. */
    PENDIENTE_PAGO,

    /** Membresía vigente — el cliente tiene acceso completo al club. */
    ACTIVA,

    /** La fechaDeFin fue superada sin que el cliente renovara. */
    VENCIDA,

    /** Cancelada manualmente por el admin antes de su vencimiento natural. */
    CANCELADA
}