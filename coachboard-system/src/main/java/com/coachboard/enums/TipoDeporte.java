// TipoDeporte.java
package com.coachboard.enums;

/**
 * Tipos de entidad deportiva soportados por la plataforma.
 *
 * Permite categorizar las organizaciones registradas y
 * personalizar etiquetas en el dashboard según el contexto
 * (ej: "socio" en lugar de "cliente" para un club de natación).
 *
 * Se puede extender fácilmente sin afectar las entidades existentes.
 */
public enum TipoDeporte {

    /** Gimnasio de musculación y fitness general. */
    GYM,

    /** Club o academia de natación. */
    NATACION,

    /** Academia de artes marciales (karate, judo, taekwondo, etc.). */
    ARTES_MARCIALES,

    /** Box de CrossFit o entrenamiento funcional de alta intensidad. */
    CROSSFIT,

    /** Club de fútbol, baloncesto, voleibol u otro deporte de equipo. */
    DEPORTE_EQUIPO,

    /**
     * Categoría genérica para entidades que no encajan
     * en las anteriores (yoga, pilates, danza, etc.).
     */
    OTRO
}