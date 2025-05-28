package com.kevinolarte.resibenissa.controllers.moduloWallet;

import com.kevinolarte.resibenissa.dto.in.modeloWallet.MovimientoRequestDTO;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.services.moduloWallet.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/resi/{idResidencia}/resi/{idResidente}/wallet")
@AllArgsConstructor
public class WalletAdminController {

    private final WalletService walletService;

    @GetMapping("/informe")
    public ResponseEntity<byte[]> getInforme(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente) {

        try{
            byte[] pdf = walletService.getInforme(idResidencia, idResidente);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_wallet_" + idResidente + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }catch (ResiException e){
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente,
            @RequestBody MovimientoRequestDTO input) {

        try {
            walletService.deposit(idResidencia, idResidente, input);
            return ResponseEntity.ok("Deposit successful");
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    @PostMapping("/retire")
    public ResponseEntity<String> retire(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente,
            @RequestBody MovimientoRequestDTO input) {


        try {
            walletService.retire(idResidencia, idResidente, input);
            return ResponseEntity.ok("Retire successful");
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

    @GetMapping("/getSaldo")
    public ResponseEntity<Double> getSaldo(
            @PathVariable Long idResidencia,
            @PathVariable Long idResidente) {

        try {
            Double saldo = walletService.getSaldo(idResidencia, idResidente);
            return ResponseEntity.ok(saldo);
        } catch (ResiException e) {
            throw new ApiException(e, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), e.getMessage());
        }
    }

}


