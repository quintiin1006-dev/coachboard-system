// MetodoDePago.java
package com.coachboard.enums;

/**
 * Métodos de pago aceptados por las organizaciones en la plataforma.
 *
 * Se mantiene simple para el alcance actual del proyecto.
 * En una versión futura con pasarela de pagos integrada,
 * se agregarían valores como TARJETA_ONLINE o PSE.
 */
public enum MetodoDePago {

    /** Pago con billetes o monedas físicas en el punto de atención. */
    EFECTIVO,

    /** Transferencia bancaria, Nequi, Daviplata o similar. */
    TRANSFERENCIA,

    /** Pago con tarjeta débito o crédito en datáfono físico. */
    TARJETA
}