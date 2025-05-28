package com.kevinolarte.resibenissa.dto.in.modeloWallet;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoRequestDTO {
    private double cantidad;
    private String concepto; // opcional, se puede usar "Depósito manual" por defecto si es null
}