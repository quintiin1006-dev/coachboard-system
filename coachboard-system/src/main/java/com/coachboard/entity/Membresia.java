// Membresia.java
package com.coachboard.entity;

import com.coachboard.enums.PlanSaaS;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entidad que representa un plan o tipo de membresía configurado
 * por una organización específica.
 *
 * Cada organización define sus propios planes de forma independiente:
 * un gym puede tener "Mensual", "Trimestral" y "Anual", mientras que
 * una academia de artes marciales puede tener "Cinturón Blanco" y
 * "Cinturón Negro" con duraciones y precios distintos.
 *
 * Multi-tenant: una membresía siempre pertenece a una organización.
 * Nunca se comparten planes entre organizaciones distintas.
 *
 * Límite por plan SaaS:
 * - BASICO: máximo 3 tipos de membresía activos por organización.
 * - PROFESIONAL: sin límite.
 * Esta validación se aplica en MembresiaService antes de persistir.
 */
@Entity
@Table(name = "membresias")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del plan de membresía.
     * Único dentro de la misma organización para evitar confusión,
     * pero puede repetirse entre organizaciones distintas
     * (dos gyms pueden tener un plan llamado "Mensual").
     * La unicidad por organización se valida en el Service.
     */
    @Column(nullable = false, length = 80)
    private String nombre;

    /**
     * Descripción detallada del plan.
     * Explica qué incluye y qué beneficios tiene el miembro.
     * Ejemplo: "Acceso ilimitado a todas las zonas + 2 clases grupales".
     */
    @Column(nullable = false)
    private String descripcion;

    /**
     * Duración del plan expresada en días.
     * Se usa para calcular la fecha de vencimiento al crear una inscripción:
     * fechaDeFin = fechaDeInicio.plusDays(duracionDias).
     * Ejemplos: 30 (mensual), 90 (trimestral), 365 (anual).
     */
    @Column(nullable = false)
    private int duracionDias;

    /**
     * Precio del plan en pesos colombianos.
     * BigDecimal es obligatorio para dinero — evita errores de precisión
     * que ocurren con int o double en cálculos financieros.
     * precision=10 soporta hasta $9.999.999,99.
     * scale=2 permite representar centavos si aplica.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * Indica si el plan está disponible para nuevas inscripciones.
     * Permite desactivar planes obsoletos sin eliminarlos,
     * preservando el historial de inscripciones ya existentes.
     * Inicializado en true por @PrePersist.
     */
    @Column(nullable = false)
    private boolean activo;

    /**
     * Organización dueña de este plan de membresía.
     * FK obligatoria — una membresía siempre pertenece a un tenant.
     * Garantiza el aislamiento: un club nunca ve los planes de otro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    /**
     * Historial de inscripciones que usaron este plan.
     * Relación inversa — la FK vive en la tabla inscripciones.
     * LAZY: no se carga la lista completa al consultar un plan.
     */
    @OneToMany(mappedBy = "tipoDeMembresia", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones;

    /**
     * Activa el plan automáticamente al crearlo.
     * Un plan recién creado siempre debe estar disponible de inmediato.
     */
    @PrePersist
    public void prePersist() {
        this.activo = true;
    }
}