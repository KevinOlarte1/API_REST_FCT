package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.services.JwtService;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.ParticipanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar operaciones públicas relacionadas con participantes en eventos.
 * Permite confirmar o denegar permisos de participantes en eventos de una residencia.
 * <P>
 * URL Base: {@code /public}
 * @author Kevin Olarte
 *
 */
@RequestMapping("/public")
@RestController
@AllArgsConstructor
public class PublicController {

    private final JwtService jwtService;
    private final ParticipanteService participanteService;

    /**
     * Endpoint para confirmar el permiso de un participante en un evento.
     * @param token Token JWT que contiene la información del participante, evento y residencia.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
    @GetMapping("/allowParticipante")
    public ResponseEntity<String> confirmarPermiso(@RequestParam String token) {
        try {
            Long idParticipante = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idParticipante").toString()));
            Long idEvento = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idEvento").toString()));
            Long idResidencia = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idResidencia").toString()));

            participanteService.allow(idResidencia, idEvento, idParticipante);
            return ResponseEntity.ok("✅ Permiso registrado correctamente.");

        } catch (ResiException e) {
            throw new ApiException(e, null);
        }catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO), null);
        }
    }

    /**
     * Endpoint para confirmar la denegación de un participante en un evento.
     * @param token Token JWT que contiene la información del participante, evento y residencia.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
    @GetMapping("/denyParticipante")
    public ResponseEntity<String> confirmarDenegacion(@RequestParam String token) {
        try {

            Long idParticipante = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idParticipante").toString()));
            Long idEvento = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idEvento").toString()));
            Long idResidencia = Long.parseLong(jwtService.extractClaim(token, claims -> claims.get("idResidencia").toString()));
            participanteService.deny(idResidencia, idEvento, idParticipante);
            return ResponseEntity.ok("✅ Denegación registrada correctamente.");

        } catch (ResiException e) {
            throw new ApiException(e, null);
        }catch (Exception e) {
            throw new ApiException(new ResiException(ApiErrorCode.ENDPOINT_PROTEGIDO), null);
        }
    }
}
