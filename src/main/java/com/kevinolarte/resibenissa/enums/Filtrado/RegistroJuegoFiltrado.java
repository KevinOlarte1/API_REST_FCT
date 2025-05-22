package com.kevinolarte.resibenissa.enums.Filtrado;

import org.springframework.data.domain.Sort;

public enum RegistroJuegoFiltrado {
    FECHA_ASC("fecha", Sort.Direction.ASC),
    FECHA_DESC("fecha", Sort.Direction.DESC),
    NUM_ASC("num", Sort.Direction.ASC),
    NUM_DESC("num", Sort.Direction.DESC),
    DURACION_ASC("duracion", Sort.Direction.ASC),
    DURACION_DESC("duracion", Sort.Direction.DESC);

    private final String campo;
    private final Sort.Direction direccion;

    RegistroJuegoFiltrado(String campo, Sort.Direction direccion) {
        this.campo = campo;
        this.direccion = direccion;
    }

    public Sort toSort() {
        return Sort.by(direccion, campo);
    }
}
