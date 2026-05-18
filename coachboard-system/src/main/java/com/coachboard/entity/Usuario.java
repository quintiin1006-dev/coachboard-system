// Usuario.java
package com.coachboard.entity;

import com.coachboard.enums.Rol;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que gestiona las credenciales de acceso al sistema.
 *
 * Separada de Cliente porque no todos los usuarios son clientes:
 * PLATFORM_ADMIN, ORG_ADMIN y ORG_STAFF tienen usuario pero no
 * tienen membresía ni inscripciones en el sistema.
 *
 * Flujo de creación de credenciales:
 * - PLATFORM_ADMIN crea ORG_ADMIN → entrega username + password temporal
 * - ORG_ADMIN crea ORG_STAFF     → entrega username + password temporal
 * - ORG_ADMIN/STAFF crea cliente → entrega username + password temporal
 * - Cualquier usuario puede cambiar su password después del primer login
 *
 * Aislamiento multi-tenant:
 * PLATFORM_ADMIN tiene organizacion = null (opera sobre toda la plataforma).
 * Todos los demás roles tienen organizacion obligatoria (nullable = false
 * se valida en el Service según el rol, no con una constraint global).
 */
@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único en toda la plataforma.
     * Se usa junto con la contraseña en el endpoint de login.
     * Recomendación de formato: nombre.apellido o documento.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Contraseña almacenada como hash BCrypt.
     * NUNCA se guarda en texto plano.
     * El Service aplica passwordEncoder.encode() antes de persistir.
     * Longitud sin límite en BD porque BCrypt siempre produce 60 chars.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Correo electrónico único en toda la plataforma.
     * Usado como identificador alternativo y para futuras
     * notificaciones por email (fuera del alcance actual).
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Rol del usuario — determina qué endpoints puede consumir
     * y qué datos puede ver a través de Spring Security.
     * Se persiste como texto para legibilidad directa en la BD.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    /**
     * Organización a la que pertenece el usuario.
     *
     * Es NULL únicamente para PLATFORM_ADMIN — este rol opera
     * sobre toda la plataforma y no está ligado a ningún tenant.
     *
     * Para ORG_ADMIN, ORG_STAFF y ORG_CLIENTE siempre tiene valor.
     * El Service valida esta regla antes de persistir.
     *
     * Este campo es la clave del aislamiento multi-tenant:
     * el JWT incluye organizacion.slug para que cada request
     * sepa a qué tenant pertenece el usuario autenticado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = true)
    private Organizacion organizacion;

    /**
     * Indica si la cuenta está habilitada.
     * Un usuario inactivo no puede autenticarse aunque sus
     * credenciales sean correctas. Permite suspender accesos
     * sin eliminar el historial del usuario.
     */
    @Column(nullable = false)
    private boolean activo;

    /**
     * Fecha y hora exacta en que se creó la cuenta.
     * No modificable (updatable = false) para trazabilidad.
     * Asignada automáticamente en @PrePersist.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaDeCreacion;

    /**
     * Inicializa valores por defecto al crear el usuario.
     * activo = true: toda cuenta recién creada está habilitada.
     * fechaDeCreacion: registra el momento exacto de creación.
     */
    @PrePersist
    public void prePersist() {
        this.fechaDeCreacion = LocalDateTime.now();
        this.activo = true;
    }
}