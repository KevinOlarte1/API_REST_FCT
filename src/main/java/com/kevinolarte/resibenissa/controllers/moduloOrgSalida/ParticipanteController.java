package com.kevinolarte.resibenissa.controllers.moduloOrgSalida;


import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.out.moduloOrgSalida.ParticipanteResponseDto;
import com.kevinolarte.resibenissa.services.moduloOrgSalida.ParticipanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/resi/{idResidencia}/evento/{idSalida}/participante")
@RestController
@AllArgsConstructor
public class ParticipanteController {

    private final ParticipanteService participanteService;

    @PostMapping("/add")
    public ResponseEntity<ParticipanteResponseDto> addParticipante(
                                                    @PathVariable Long idResidencia,
                                                    @PathVariable Long idSalida,
                                                    @RequestBody ParticipanteDto participanteDto) {

        return ResponseEntity.ok(participanteService.addParticipante(participanteDto, idSalida, idResidencia));
    }

    @GetMapping("{idParticipante}/get")
    public ResponseEntity<ParticipanteResponseDto> getParticipante(
                                                    @PathVariable Long idResidencia,
                                                    @PathVariable Long idSalida,
                                                    @PathVariable Long idParticipante) {
        return ResponseEntity.ok(participanteService.getParticipante(idResidencia, idSalida, idParticipante));
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<ParticipanteResponseDto>> getAllParticipantes(
                                                        @PathVariable Long idResidencia,
                                                        @PathVariable Long idSalida,
                                                        @RequestBody(required = false) ParticipanteDto participanteDto) {
        return ResponseEntity.ok(participanteService.getParticiapnte(idResidencia, idSalida, participanteDto));

    }

    @PatchMapping("/{idParticipante}/update")
    public ResponseEntity<ParticipanteResponseDto> updateParticipante(
                                                    @PathVariable Long idResidencia,
                                                    @PathVariable Long idSalida,
                                                    @PathVariable Long idParticipante,
                                                    @RequestBody ParticipanteDto participanteDto) {
        return ResponseEntity.ok(participanteService.updateParticipante(participanteDto, idResidencia, idSalida, idParticipante));
    }

    @DeleteMapping("/{idParticipante}/delete")
    public void deleteParticipante(
                                    @PathVariable Long idResidencia,
                                    @PathVariable Long idSalida,
                                    @PathVariable Long idParticipante) {
        participanteService.deleteParticipante(idResidencia, idSalida, idParticipante);
    }
}
