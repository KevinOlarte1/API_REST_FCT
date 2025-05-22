package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.services.JwtService;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.ParticipanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/public")
@RestController
@AllArgsConstructor
public class PublicController {

    private final JwtService jwtService;
    private final ParticipanteService participanteService;

    @GetMapping("/allowParticipante")
    public ResponseEntity<String> confirmarPermiso(@RequestParam String token) {
        System.out.println("Holaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        try {
            Long idParticipante = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idParticipante").toString()));
            Long idEvento = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idEvento").toString()));
            Long idResidencia = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idResidencia").toString()));

            participanteService.allow(idResidencia, idEvento, idParticipante);
            return ResponseEntity.ok("✅ Permiso registrado correctamente.");

        } catch (Exception e) {
            throw new ApiException(ApiErrorCode.ENDPOINT_PROTEGIDO);
        }
    }
    @GetMapping("/denyParticipante")
    public ResponseEntity<String> confirmarDenegacion(@RequestParam String token) {
        try {
            System.out.println("asdasdasdasdasdasdasdasd");
            Long idParticipante = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idParticipante").toString()));
            Long idEvento = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idEvento").toString()));
            Long idResidencia = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idResidencia").toString()));
            System.out.println("Holaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            participanteService.deny(idResidencia, idEvento, idParticipante);
            return ResponseEntity.ok("✅ Denegación registrada correctamente.");

        } catch (Exception e) {
            throw new ApiException(ApiErrorCode.ENDPOINT_PROTEGIDO);
        }
    }
}
