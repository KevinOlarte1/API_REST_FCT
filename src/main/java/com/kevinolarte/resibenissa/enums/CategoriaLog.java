package com.kevinolarte.resibenissa.enums;

import lombok.Getter;

@Getter
public enum CategoriaLog {
    DEBUG_INSERT(3L), DEBUG_UPDATE(4L), DEBUG_DELETE(5L), DEBUG_SELECT(6L),
    ERROR_INSERT(7L), ERROR_UPDATE(8L), ERROR_DELETE(9L), ERROR_SELECT(10L);
    private final Long valor;
    CategoriaLog(Long valor) {
        this.valor = valor;
    }

}
