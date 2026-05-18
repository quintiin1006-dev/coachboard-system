// Cliente.java
package com.coachboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad que representa el perfil de negocio de un miembro del club.
 *
 * Separada de Usuario porque contiene datos propios del contexto deportivo
 * (documento, celular, fecha de registro) que no tienen relación con
 * la autenticación. Un Cliente siempre tiene un Usuario asociado,
 * pero ORG_ADMIN y ORG_STAFF tienen Usuario sin tener Cliente.
 *
 * Aislamiento multi-tenant: cada Cliente pertenece a una organización
 * específica a través de su Usuario. Las queries se filtran por
 * organizacion_id para garantizar que un club nunca vea clientes de otro.
 */
@Entity
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del miembro del club.
     * NO es unique — pueden existir homónimos (dos "Juan García").
     * La unicidad de identidad se garantiza por el campo documento.
     */
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Número de celular de contacto.
     * Único dentro de toda la plataforma para evitar duplicados
     * entre organizaciones. Longitud 10 para formato colombiano.
     */
    @Column(nullable = false, unique = true, length = 10)
    private String celular;

    /**
     * Número de documento de identidad (cédula de ciudadanía).
     * Identificador único de negocio del cliente, independiente
     * del id técnico de la base de datos.
     * Longitud 10 cubre la cédula colombiana actual.
     */
    @Column(nullable = false, unique = true, length = 10)
    private String documento;

    /**
     * Fecha en que el cliente fue registrado en el club.
     * No modificable (updatable = false) para trazabilidad histórica.
     * Asignada automáticamente en @PrePersist.
     */
    @Column(nullable = false, updatable = false)
    private LocalDate fechaDeRegistro;

    /**
     * Credenciales de acceso del cliente al dashboard.
     * OneToOne: cada cliente tiene exactamente un usuario y
     * cada usuario de tipo ORG_CLIENTE pertenece a un solo cliente.
     * La FK usuario_id vive en la tabla clientes.
     * LAZY: no se carga el usuario en cada consulta de cliente.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    /**
     * Historial completo de inscripciones del cliente.
     * Un cliente puede tener múltiples inscripciones a lo largo
     * del tiempo — una por cada renovación de membresía.
     * mappedBy indica que la FK está en la tabla inscripciones.
     * LAZY: la lista solo se carga cuando se accede explícitamente.
     */
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones;

    /**
     * Notificaciones de vencimiento y alertas dirigidas a este cliente.
     * Permite mostrar el historial de alertas y las no leídas
     * en el dashboard del cliente y en el panel del admin.
     */
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Notificacion> notificaciones;

    /**
     * Asigna la fecha de registro automáticamente al momento
     * de persistir el cliente por primera vez en la base de datos.
     */
    @PrePersist
    public void prePersist() {
        this.fechaDeRegistro = LocalDate.now();
    }
}