package com.kevinolarte.resibenissa.repositories.moduloWallet;

import com.kevinolarte.resibenissa.models.moduloWallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
