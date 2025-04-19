package com.kevinolarte.resibenissa.enums;

/**
 * Enum que representa la dificultad de una partida.
 * <p>
 * La interpretación de la dificultad depende del tipo de juego:
 * <ul>
 *   <li>En juegos tipo A, determina el modo de juego (p. ej. memoria, parejas, secuencia).</li>
 *   <li>En juegos tipo B, define la complejidad de las reglas (p. ej. número de cartas, tiempo disponible).</li>
 * </ul>
 * Es responsabilidad del juego interpretar estos valores adecuadamente.
 *
 * author Kevin Olarte
 */
public enum Dificultad {
    FACIL,MEDIO,DIFICIL;
}
