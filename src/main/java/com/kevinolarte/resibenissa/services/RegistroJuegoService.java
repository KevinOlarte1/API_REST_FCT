package com.kevinolarte.resibenissa.services;

import com.kevinolarte.resibenissa.dto.in.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.dto.out.ResidenteResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.Juego;
import com.kevinolarte.resibenissa.models.RegistroJuego;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.RegistroJuegoRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegistroJuegoService {

    private final RegistroJuegoRepository registroJuegoRepository;
    private final ResidenteService residenteService;
    private final JuegoService juegoService;
    private final UserService userService;

    @PersistenceContext
    private EntityManager entityManager;



    public RegistroJuegoResponseDto save(RegistroJuegoDto input) throws ApiException {
        if (input.getDuracion() == null || input.getFallos() == null ||
                input.getIdResidente() == null || input.getIdJuego() == null || input.getIdUsuario() == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //No puede haber fallos negativos
        if (input.getFallos() < 0){
            throw new ApiException(ApiErrorCode.VALORES_NEGATIVOS);
        }
        Juego juego = juegoService.findById(input.getIdJuego());
        Residente residente = residenteService.findById(input.getIdResidente());
        User usuario = userService.findById(input.getIdUsuario());
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        if (residente == null)
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        if (usuario == null)
            throw  new ApiException(ApiErrorCode.USUARIO_INVALIDO);

        //Ver si el juego es de la misma residencia que el residente
        if (!Objects.equals(juego.getResidencia().getId(), residente.getResidencia().getId())) {
            throw new ApiException(ApiErrorCode.CONFLICTO_REFERENCIAS);
        }
        //Ver si el juego es de la misma residencia que el usuario/trabajador.
        if (!Objects.equals(usuario.getResidencia().getId(), juego.getResidencia().getId())) {
            throw new ApiException(ApiErrorCode.CONFLICTO_REFERENCIAS);
        }
        RegistroJuego registroJuego = new RegistroJuego(input.getFallos(), input.getDuracion(), input.getDificultad());
        registroJuego.setJuego(juego);
        registroJuego.setResidente(residente);
        registroJuego.setUsuario(usuario);
        RegistroJuego registro = registroJuegoRepository.save(registroJuego);
        return new RegistroJuegoResponseDto(registro);
    }



    /**
     * Recupera estadísticas de juegos jugados por los residentes, aplicando filtros dinámicos.
     * <p>
     * Filtros disponibles:
     * <ul>
     *   <li><b>idResidente</b>: juegos jugados por un residente específico</li>
     *   <li><b>idResidencia</b>: juegos de todos los residentes de una residencia</li>
     *   <li><b>idJuego</b>: juegos por tipo de juego</li>
     *   <li><b>year, month, day</b>: filtro por fecha parcial o completa</li>
     * </ul>
     * </p>
     *
     * @param idResidente ID del residente (opcional)
     * @param idResidencia ID de la residencia (opcional)
     * @param idJuego ID del juego (opcional)
     * @param year Año de juego (opcional)
     * @param month Mes de juego (opcional)
     * @param day Día de juego (opcional)
     * @return Lista de registros de juego convertidos a DTO
     */
    public List<RegistroJuegoResponseDto> getStats(Long idResidente, Long idResidencia, Long idJuego, Integer year, Integer month, Integer day) {
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

        //Filtra por dia, por mes, por año o combinado. //Mapping clase origen clase destino. DOZER, MapStruct
        if (year != null || month != null || day != null) {
            baseList = baseList.stream()
                    .filter(r -> {
                        boolean match = true;
                        if (year != null) match = r.getFecha().getYear() == year;
                        if (month != null) match = match && r.getFecha().getMonthValue() == month;
                        if (day != null) match = match && r.getFecha().getDayOfMonth() == day;
                        return match;
                    })
                    .toList();
        }
        //TODO: -ALTERNATIVA QUERY DINAMICA
        return baseList.stream().map(RegistroJuegoResponseDto::new).collect(Collectors.toList());
    }




}
