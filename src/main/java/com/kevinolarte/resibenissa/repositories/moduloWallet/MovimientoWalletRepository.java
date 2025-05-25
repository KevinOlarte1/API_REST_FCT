package com.kevinolarte.resibenissa.repositories.moduloWallet;

import com.kevinolarte.resibenissa.models.moduloWallet.MovimientoWallet;
import com.kevinolarte.resibenissa.models.moduloWallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoWalletRepository extends JpaRepository<MovimientoWallet, Long> {
    List<MovimientoWallet> findByWallet(Wallet wallet);
}

