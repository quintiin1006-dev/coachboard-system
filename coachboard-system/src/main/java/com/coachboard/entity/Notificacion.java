// Notificacion.java
package com.coachboard.entity;

import com.coachboard.enums.TipoDeNotificacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una alerta generada automáticamente
 * por el scheduler del sistema o manualmente por el administrador.
 *
 * Las notificaciones se persisten para:
 * 1. Mostrar el historial en el dashboard del cliente y del admin.
 * 2. Evitar enviar la misma alerta más de una vez por inscripción y día.
 * 3. Saber cuántas alertas no leídas tiene cada usuario (badge en UI).
 *
 * El scheduler evalúa inscripciones ACTIVAS diariamente a las 8am
 * y crea notificaciones según los umbrales configurados.
 * Antes de crear una, verifica que no exista ya del mismo tipo
 * para esa inscripción — evita duplicados.
 *
 * Según el plan de la organización:
 * - BASICO: notificaciones solo al cliente.
 * - PROFESIONAL: notificaciones al cliente Y al ORG_ADMIN.
 */
@Entity
@Table(name = "notificaciones")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Categoría de la notificación.
     * Define el ícono, color y mensaje en el dashboard.
     * Persiste como texto para legibilidad directa en la BD.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeNotificacion tipo;

    /**
     * Mensaje legible para mostrar al usuario en el dashboard.
     * Ejemplo: "Tu membresía vence en 5 días. ¡Renueva ahora!"
     * Generado dinámicamente en el Service según el tipo y diasRestantes.
     */
    @Column(nullable = false, length = 255)
    private String mensaje;

    /**
     * Fecha y hora exacta en que el sistema generó la notificación.
     * No modificable (updatable = false) para trazabilidad.
     * Asignada automáticamente en @PrePersist.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    /**
     * Indica si el destinatario ya visualizó esta notificación.
     * Permite mostrar el badge de "no leídas" en el dashboard
     * y filtrar las alertas pendientes de revisión.
     * Se inicializa en false en @PrePersist — toda notificación nace sin leer.
     */
    @Column(nullable = false)
    private boolean leida;

    /**
     * Días restantes hasta el vencimiento de la membresía.
     * Solo tiene valor para el tipo VENCIMIENTO_PROXIMO.
     * Es null para MEMBRESIA_VENCIDA y PAGO_PENDIENTE.
     * Permite mostrar el conteo exacto en el dashboard sin recalcular.
     */
    private Integer diasRestantes;

    /**
     * Cliente destinatario de esta notificación.
     * ManyToOne: un cliente acumula muchas notificaciones
     * a lo largo de su historial de membresías.
     * Permite filtrar todas las alertas de un cliente específico.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Inscripción que originó esta notificación.
     * Permite al scheduler verificar si ya generó una alerta
     * para este vencimiento específico y evitar duplicados.
     * También permite al admin ver de qué inscripción viene cada alerta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;

    /**
     * Inicializa los campos por defecto al crear la notificación.
     * leida = false: toda notificación nueva está pendiente de lectura.
     * fechaGeneracion: registra el momento exacto de creación.
     */
    @PrePersist
    public void prePersist() {
        this.fechaGeneracion = LocalDateTime.now();
        this.leida = false;
    }
}