package com.kevinolarte.resibenissa.enums.Filtrado;

import org.springframework.data.domain.Sort;

public enum ResidenteFiltrado {
    FECHA_NAC_ASC("fechaNacimiento", Sort.Direction.ASC),
    FECHA_NAC_DESC("fechaNacimiento", Sort.Direction.DESC),
    NOMBRE_ASC("nombre", Sort.Direction.ASC),
    NOMBRE_DESC("nombre", Sort.Direction.DESC),
    APELLIDO_ASC("apellido", Sort.Direction.ASC),
    APELLIDO_DESC("apellido", Sort.Direction.DESC);

    private final String campo;
    private final Sort.Direction direccion;

    ResidenteFiltrado(String campo, Sort.Direction direccion) {
        this.campo = campo;
        this.direccion = direccion;
    }

    public Sort toSort() {
        return Sort.by(direccion, campo);
    }
}
