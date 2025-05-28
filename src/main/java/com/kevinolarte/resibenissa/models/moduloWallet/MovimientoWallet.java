package com.kevinolarte.resibenissa.models.moduloWallet;

import com.kevinolarte.resibenissa.enums.moduloWallet.TipoMovimiento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class MovimientoWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_wallet", nullable = false)
    private Wallet wallet;

    private Double cantidad;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo; // IN / OUT

    private String concepto;

    private LocalDateTime fecha;

    public MovimientoWallet() {
        // Constructor por defecto
    }

}