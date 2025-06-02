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


@RequestMapping("/resi/resident/{idResidente}/wallet")
@RestController
@AllArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/informe")
    public ResponseEntity<byte[]> getInforme(
            @PathVariable Long idResidente) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) auth.getPrincipal();

        try{
            byte[] pdf = walletService.getInforme(currentUser.getResidencia().getId(), idResidente);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_wallet_" + currentUser.getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }catch (ResiException e){
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable Long idResidente,
            @RequestBody MovimientoRequestDTO input) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        try {
            walletService.deposit(currentUser.getResidencia().getId(), idResidente, input);
            return ResponseEntity.ok("Deposit successful");
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    @PostMapping("/retire")
    public ResponseEntity<String> retire(
            @PathVariable Long idResidente,
            @RequestBody MovimientoRequestDTO input) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        try {
            walletService.retire(currentUser.getResidencia().getId(), idResidente, input);
            return ResponseEntity.ok("Retire successful");
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }

    @GetMapping("/getSaldo")
    public ResponseEntity<Double> getSaldo(
            @PathVariable Long idResidente) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        try {
            Double saldo = walletService.getSaldo(currentUser.getResidencia().getId(), idResidente);
            return ResponseEntity.ok(saldo);
        } catch (ResiException e) {
            throw new ApiException(e, currentUser);
        } catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.PROBLEMA_INTERNO), currentUser, e.getMessage());
        }
    }
}
