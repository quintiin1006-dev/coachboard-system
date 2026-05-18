// Organizacion.java
package com.coachboard.entity;

import com.coachboard.enums.PlanSaaS;
import com.coachboard.enums.TipoDeporte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad central del modelo SaaS — representa a cada club deportivo
 * registrado en la plataforma como cliente del servicio.
 *
 * Cada organización es un tenant independiente: sus clientes,
 * membresías, inscripciones, pagos y notificaciones están
 * completamente aislados de los de otras organizaciones.
 *
 * El aislamiento se garantiza por la FK organizacion_id presente
 * en todas las entidades de negocio, filtrada automáticamente
 * en cada query a través del TenantFilter.
 *
 * Creada exclusivamente por PLATFORM_ADMIN junto con el primer
 * usuario ORG_ADMIN en una sola transacción atómica.
 */
@Entity
@Table(name = "organizaciones")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Organizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre comercial del club deportivo.
     * Único en la plataforma para evitar confusiones entre tenants.
     * Ejemplo: "Fusion Sport", "Club Natación Norte", "Dojo Bushido".
     */
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Identificador URL-friendly de la organización.
     * Único, en minúsculas y sin espacios — se incluye en el JWT
     * para identificar el tenant en cada request.
     * Ejemplo: "fusion-sport", "club-natacion-norte".
     * No es modificable después de la creación para no romper tokens activos.
     */
    @Column(nullable = false, unique = true, length = 50, updatable = false)
    private String slug;

    /**
     * Categoría deportiva de la organización.
     * Permite personalizar etiquetas y comportamientos en el dashboard
     * según el tipo de entidad (gym, natación, artes marciales, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeporte tipoDeporte;

    /**
     * Plan de suscripción contratado con la plataforma.
     * Controla los límites operativos: máximo de clientes,
     * número de staff permitido, tipos de membresía y
     * funcionalidades del dashboard.
     * Solo PLATFORM_ADMIN puede cambiar el plan de una organización.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanSaaS plan;

    /**
     * Indica si la organización está habilitada en la plataforma.
     * Una organización inactiva no puede autenticar ninguno de sus usuarios.
     * Permite suspender un tenant sin eliminarlo de la base de datos.
     */
    @Column(nullable = false)
    private boolean activa;

    /**
     * Fecha y hora en que PLATFORM_ADMIN registró la organización.
     * No modificable — sirve como inicio del período de facturación
     * y para calcular antigüedad del cliente en el SaaS.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaDeCreacion;

    /**
     * Usuarios que pertenecen a esta organización.
     * Incluye ORG_ADMIN, ORG_STAFF y ORG_CLIENTE.
     * PLATFORM_ADMIN NO aparece aquí — no pertenece a ninguna org.
     */
    @OneToMany(mappedBy = "organizacion", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    /**
     * Tipos de membresía configurados por esta organización.
     * Cada org define sus propios planes (mensual, trimestral, etc.)
     * de forma independiente a las demás organizaciones.
     */
    @OneToMany(mappedBy = "organizacion", fetch = FetchType.LAZY)
    private List<Membresia> membresias;

    /**
     * Inicializa valores por defecto al crear la organización.
     * activa = true: toda org recién creada está habilitada.
     * fechaDeCreacion: se registra el momento exacto del registro.
     */
    @PrePersist
    public void prePersist() {
        this.activa = true;
        this.fechaDeCreacion = LocalDateTime.now();
    }
}