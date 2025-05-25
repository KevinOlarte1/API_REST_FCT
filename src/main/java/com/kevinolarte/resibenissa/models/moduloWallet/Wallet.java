package com.kevinolarte.resibenissa.models.moduloWallet;

import com.kevinolarte.resibenissa.models.Residente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "fk_residente", unique = true)
    private Residente residente;

    private Double saldoTotal;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private Set<MovimientoWallet> movimientos = new LinkedHashSet<>();
    public Wallet() {
        // Constructor por defecto
    }
}
