package com.kevinolarte.resibenissa.services.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ApiException;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.modulojuego.RegistroJuegoRepository;

import com.kevinolarte.resibenissa.services.ResidenteService;
import com.kevinolarte.resibenissa.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los registros de juegos jugados
 * por los residentes de una residencia.
 * <p>
 * Permite crear registros, aplicar filtros por fechas y entidades, y eliminar entradas específicas.
 * También valida que los registros pertenezcan a una misma residencia para mantener coherencia.
 * </p>
 *
 * @author Kevin Olarte
 */
@Service
@AllArgsConstructor
public class RegistroJuegoService {

    private final RegistroJuegoRepository registroJuegoRepository;
    private final ResidenteService residenteService;
    private final JuegoService juegoService;
    private final UserService userService;


    /**
     * Crea y guarda un nuevo registro de juego en la base de datos.
     * <p>
     * Realiza validaciones sobre los campos obligatorios, valores negativos, y que todas las entidades
     * asociadas (juego, usuario, residente) existan y pertenezcan a la misma residencia.
     * </p>
     *
     * @param input DTO de entrada con los datos del juego a registrar.
     * @return DTO con los datos del registro creado.
     * @throws ApiException si falta algún campo obligatorio, hay valores inválidos, o las entidades no son compatibles.
     */
    public RegistroJuegoResponseDto save(RegistroJuegoDto input) throws ApiException {
        if (input.getDuracion() == null || input.getFallos() == null ||
                input.getIdResidente() == null || input.getIdJuego() == null || input.getIdUsuario() == null ||
                input.getObservacion() == null){
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
        RegistroJuego registroJuego = new RegistroJuego(input.getFallos(), input.getDuracion(), input.getDificultad(), input.getObservacion());
        registroJuego.setJuego(juego);
        registroJuego.setResidente(residente);
        registroJuego.setUsuario(usuario);
        RegistroJuego registro = registroJuegoRepository.save(registroJuego);
        return new RegistroJuegoResponseDto(registro);
    }


    public RegistroJuegoResponseDto updateRegistro(Long idResidencia, Long idJuego, Long idRegistroJuego, RegistroJuegoDto input){
       if (idResidencia == null || idJuego == null || idRegistroJuego == null || input == null)
           throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);

        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si el juego pertence a la residencia
        if (!Objects.equals(juego.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);


        //Comprobar si existe el registro de juego
        RegistroJuego registroJuego = registroJuegoRepository.findById(idRegistroJuego).orElse(null);
        if (registroJuego == null)
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);
        //Comprobar si el registro pertenece al juego
        if (!Objects.equals(registroJuego.getJuego().getId(), idJuego))
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);

        //Comprobar si tiene observaciones el input
        if (input.getObservacion() != null){
            registroJuego.setObservacion(input.getObservacion());
        }
        return new RegistroJuegoResponseDto(registroJuego);
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


    /**
     * Elimina un registro de juego del sistema según su ID.
     * <p>
     * Este método busca el registro por su identificador y, si lo encuentra, devuelve un DTO con su información
     * antes de eliminarlo del repositorio. Si no existe, lanza una excepción {@link com.kevinolarte.resibenissa.exceptions.ApiException}
     * con el código de error {@link com.kevinolarte.resibenissa.exceptions.ApiErrorCode#REGISTRO_JUEGO_INVALIDO}.
     * </p>
     *
     * @param idRegistroJuego ID del registro de juego que se desea eliminar.
     * @return DTO con la información del registro de juego eliminado.
     * @throws com.kevinolarte.resibenissa.exceptions.ApiException si el registro de juego no existe.
     */
    public RegistroJuegoResponseDto remove(Long idRegistroJuego) {
        RegistroJuego registroTmp = registroJuegoRepository.findById(idRegistroJuego).orElse(null);
        if (registroTmp == null){
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);
        }
        return new RegistroJuegoResponseDto(registroTmp);
    }
}
