package com.kevinolarte.resibenissa.enums.moduloOrgSalida;

/**
 * Enumeración que representa los posibles estados de una salida.
 * @author Kevin Olarte
 */
public enum EstadoSalida {
    ABIERTO(0), CERRADO(1), EN_CURSO(2), FINALIZADA(3);


    private final int estado;
    EstadoSalida(int estado) {
        this.estado = estado;
    }
    public int getEstado() {
        return estado;
    }
}
