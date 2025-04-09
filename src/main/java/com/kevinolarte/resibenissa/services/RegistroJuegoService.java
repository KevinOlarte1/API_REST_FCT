package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.RegistroJuegoDto;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.models.RegistroJuego;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.repositories.RegistroJuegoRepository;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class RegistroJuegoService {

    private final RegistroJuegoRepository registroJuegoRepository;
    private final ResidenteService residenteService;
    private final JuegoService juegoService;

    public RegistroJuego save(RegistroJuegoDto input) throws RuntimeException{
        if (input.getDuraccion() == null || input.getFallos() == null ||
                input.getIdResidente() == null || input.getIdJuego() == null){
            throw new RuntimeException("No se deje ningun campo nullo");
        }
        if (input.getFallos() < 0){
            throw new RuntimeException("Los fallos no pueden ser negativos");
        }
        Juego juego = juegoService.findById(input.getIdJuego());
        Residente residente = residenteService.findById(input.getIdResidente());
        if (juego == null || residente == null) {
            String msg = "";
            if(juego == null)
                msg += " Juego - No encontrado ";
            else
                msg += " Residente - No encontrado ";
            throw new RuntimeException(msg);
        }
        RegistroJuego registroJuego = new RegistroJuego(input.getFallos(), input.getDuraccion());
        registroJuego.setJuego(juego);
        registroJuego.setResidente(residente);
        return registroJuegoRepository.save(registroJuego);
    }


    public List<RegistroJuego> getStats(Long idResidente, Long idResidencia, Long idJuego, Integer year, Integer month, Integer day) {
        List<RegistroJuego> baseList;

        if (idResidente != null) {
            baseList = registroJuegoRepository.findByResidenteId(idResidente);
        } else if (idResidencia != null) {
            baseList = registroJuegoRepository.findByResidente_Residencia_Id(idResidencia);
        } else {
            baseList = registroJuegoRepository.findAll();
        }

        if (idJuego != null) {
            baseList = baseList.stream()
                    .filter(r -> r.getJuego().getId().equals(idJuego))
                    .toList();
        }

        if (year != null && month != null && day != null) {
            LocalDate date = LocalDate.of(year, month, day);
            baseList = baseList.stream()
                    .filter(r -> r.getFecha().isEqual(date))
                    .toList();
        }

        return baseList;
    }

}
