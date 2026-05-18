// Inscripcion.java
package com.coachboard.entity;

import com.coachboard.enums.EstadoInscripcion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad central del negocio — representa el contrato entre
 * un cliente y un plan de membresía para un período específico.
 *
 * Es el eje del proceso transaccional principal del sistema.
 * El flujo completo que coordina esta entidad en el Service es:
 * 1. ORG_ADMIN o ORG_STAFF registra la inscripción (PENDIENTE_PAGO)
 * 2. Se calcula fechaDeFin = fechaDeInicio + membresia.duracionDias
 * 3. Se registra el Pago asociado
 * 4. Al confirmar el pago → inscripción pasa a ACTIVA
 * 5. Se genera Notificacion de vencimiento próximo
 * 6. El scheduler diario cambia inscripciones a VENCIDA cuando corresponde
 *
 * Multi-tenant: cada inscripción pertenece implícitamente a la organización
 * del cliente — no necesita FK propia a organización porque se accede
 * siempre a través del cliente o del usuario que la creó.
 */
@Entity
@Table(name = "inscripciones")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha en que inicia la vigencia de la membresía.
     * No modificable (updatable = false) para preservar
     * la trazabilidad del contrato original.
     */
    @Column(nullable = false, updatable = false)
    private LocalDate fechaDeInicio;

    /**
     * Fecha en que vence la membresía.
     * Calculada en el Service como:
     * fechaDeFin = fechaDeInicio.plusDays(membresia.getDuracionDias())
     * Solo el admin puede modificarla manualmente para extender vigencia.
     */
    @Column(nullable = false)
    private LocalDate fechaDeFin;

    /**
     * Estado actual en el ciclo de vida de la inscripción.
     * Transiciones válidas:
     * PENDIENTE_PAGO → ACTIVA (al confirmar pago)
     * ACTIVA → VENCIDA (scheduler detecta fechaDeFin superada)
     * PENDIENTE_PAGO o ACTIVA → CANCELADA (cancelación manual)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado;

    /**
     * Cliente titular de esta inscripción.
     * ManyToOne: un cliente puede acumular varias inscripciones
     * a lo largo del tiempo (una por cada renovación).
     * LAZY: no se cargan todos los datos del cliente en cada query.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Plan de membresía aplicado en esta inscripción.
     * ManyToOne: muchas inscripciones pueden usar el mismo plan
     * (ej: todos los clientes con membresía "Mensual").
     * Si el plan se desactiva, las inscripciones existentes no se ven afectadas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_membresia_id", nullable = false)
    private Membresia tipoDeMembresia;

    /**
     * Usuario (ORG_ADMIN o ORG_STAFF) que registró esta inscripción.
     * Trazabilidad operativa: saber quién creó cada contrato.
     * Útil para auditoría y para el dashboard de actividad del admin.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    /**
     * Pagos registrados sobre esta inscripción.
     * OneToMany: una inscripción puede tener múltiples pagos
     * (abono + saldo, o pagos en cuotas según el acuerdo).
     * Sin cascade destructivo — los pagos nunca se eliminan por error.
     * LAZY: la lista solo se carga cuando se accede explícitamente.
     */
    @OneToMany(mappedBy = "inscripcion", fetch = FetchType.LAZY)
    private List<Pago> pagos;

    /**
     * Notificaciones generadas a partir de esta inscripción.
     * Permite consultar todas las alertas de vencimiento
     * asociadas a un contrato específico y evitar duplicados.
     */
    @OneToMany(mappedBy = "inscripcion", fetch = FetchType.LAZY)
    private List<Notificacion> notificaciones;
}