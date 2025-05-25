package com.kevinolarte.resibenissa.controllers.moduloWallet;

import com.kevinolarte.resibenissa.services.moduloWallet.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/resi/residente/{idResidente}/wallet")
@RestController
@AllArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/{walletId}/informe")
    public ResponseEntity<byte[]> getInforme(
            @PathVariable Long idResidente,
            @PathVariable Long walletId) {
        byte[] pdf = walletService.getInforme(walletId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_wallet_" + walletId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
