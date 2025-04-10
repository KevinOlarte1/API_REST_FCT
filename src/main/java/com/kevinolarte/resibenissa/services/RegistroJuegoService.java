package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.in.RegistroJuegoDto;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.models.RegistroJuego;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.repositories.RegistroJuegoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los registros de juegos.
 * <p>
 * Permite guardar nuevas sesiones de juego realizadas por los residentes y
 * consultar estadísticas o historial filtrando por múltiples parámetros.
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class RegistroJuegoService {

    private final RegistroJuegoRepository registroJuegoRepository;
    private final ResidenteService residenteService;
    private final JuegoService juegoService;


    /**
     * Guarda un nuevo registro de juego a partir de un DTO con los datos necesarios.
     *
     * @param input Objeto con los datos de entrada (residente, juego, fallos y duración).
     * @return El objeto {@link RegistroJuego} guardado.
     * @throws RuntimeException si hay datos inválidos o inconsistentes.
     */
    public RegistroJuego save(RegistroJuegoDto input) throws RuntimeException{
        if (input.getDuracion() == null || input.getFallos() == null ||
                input.getIdResidente() == null || input.getIdJuego() == null){
            throw new RuntimeException("No se deje ningun campo nullo");
        }

        //No puede haber fallos negativos
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

        //Ver si el juego es de la misma residencia que el residente
        if (!Objects.equals(juego.getResidencia().getId(), residente.getResidencia().getId())) {
            throw new RuntimeException("No posible");
        }

        RegistroJuego registroJuego = new RegistroJuego(input.getFallos(), input.getDuracion());
        registroJuego.setJuego(juego);
        registroJuego.setResidente(residente);
        return registroJuegoRepository.save(registroJuego);
    }


    /**
     * Devuelve una lista de registros de juegos filtrados por residente, residencia, juego, y fecha (año/mes/día).
     *
     * @param idResidente ID del residente (opcional).
     * @param idResidencia ID de la residencia (opcional).
     * @param idJuego ID del juego (opcional).
     * @param year Año del registro (opcional).
     * @param month Mes del registro (opcional).
     * @param day Día del registro (opcional).
     * @return Lista de registros de juegos que cumplen con los filtros.
     */
    public List<RegistroJuego> getStats(Long idResidente, Long idResidencia, Long idJuego, Integer year, Integer month, Integer day) {
        List<RegistroJuego> baseList;

        //1- filtro mira de mas pequeño a mas grade idResidente, idResidencia sino todo.
        if (idResidente != null) {
            baseList = registroJuegoRepository.findByResidenteId(idResidente);
        } else if (idResidencia != null) {
            baseList = registroJuegoRepository.findByResidente_Residencia_Id(idResidencia);
        } else {
            baseList = registroJuegoRepository.findAll();
        }

        //Mira si tiene algún juego por filtrar
        if (idJuego != null) {
            baseList = baseList.stream()
                    .filter(r -> r.getJuego().getId().equals(idJuego))
                    .toList();
        }

        //Filtra por dia, por mes, por año o combinado.
        if (year != null || month != null || day != null) {
            baseList = baseList.stream()
                    .filter(r -> {
                        boolean match = true;
                        if (year != null) match = match && r.getFecha().getYear() == year;
                        if (month != null) match = match && r.getFecha().getMonthValue() == month;
                        if (day != null) match = match && r.getFecha().getDayOfMonth() == day;
                        return match;
                    })
                    .toList();
        }

        return baseList;
    }

}
