// Pago.java
package com.coachboard.entity;

import com.coachboard.enums.EstadoDelPago;
import com.coachboard.enums.MetodoDePago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que registra cada transacción de dinero en el sistema.
 *
 * Separada de Inscripcion porque una membresía puede pagarse en varias
 * cuotas o tener pagos parciales, y se necesita el historial completo
 * de movimientos para el dashboard financiero del ORG_ADMIN y para
 * las métricas globales del PLATFORM_ADMIN.
 *
 * La transición de estadoDelPago a COMPLETADO es el evento que dispara
 * la activación de la inscripción asociada. Esa lógica vive en
 * InscripcionService — no en PagoService — para mantener el principio
 * de responsabilidad única.
 */
@Entity
@Table(name = "pagos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Valor monetario del pago en pesos colombianos.
     * BigDecimal es obligatorio para dinero — int y double introducen
     * errores de redondeo en operaciones financieras acumuladas.
     * precision=10 soporta hasta $9.999.999,99.
     * scale=2 permite representar centavos correctamente.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    /**
     * Fecha y hora exacta en que se registró el pago en el sistema.
     * No modificable (updatable = false) para integridad del registro.
     * Asignada automáticamente en @PrePersist.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaDePago;

    /**
     * Medio de pago utilizado por el cliente.
     * Se persiste como texto para legibilidad en la BD
     * y en los reportes del dashboard financiero.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoDePago metodoDePago;

    /**
     * Estado actual del pago en su ciclo de vida.
     * La transición PENDIENTE → COMPLETADO activa la inscripción
     * asociada a través de InscripcionService.
     * Un pago RECHAZADO no activa nada — se crea un nuevo pago.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDelPago estadoDelPago;

    /**
     * Inscripción a la que corresponde este pago.
     * ManyToOne: múltiples pagos pueden pertenecer a la misma
     * inscripción (cuotas, abono inicial + pago restante).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    /**
     * Usuario (ORG_ADMIN o ORG_STAFF) que registró este pago.
     * Trazabilidad operativa: saber quién cobró cada transacción.
     * Útil para cuadrar caja y para auditoría del administrador.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", nullable = false)
    private Usuario registradoPor;

    /**
     * Asigna la fecha y hora del pago automáticamente al momento
     * de persistir el registro — garantiza precisión sin depender
     * del cliente que hace el request.
     */
    @PrePersist
    public void prePersist() {
        this.fechaDePago = LocalDateTime.now();
    }
}