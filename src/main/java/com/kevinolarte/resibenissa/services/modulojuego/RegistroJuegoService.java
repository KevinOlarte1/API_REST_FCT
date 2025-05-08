package com.kevinolarte.resibenissa.services.modulojuego;

import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.dto.out.modulojuego.RegistroJuegoResponseDto;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
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



    public RegistroJuegoResponseDto add(Long idResidencia, Long idJuego,RegistroJuegoDto input) throws ApiException {
        if (input == null || input.getDuracion() == null || input.getNum() == null ||
                input.getIdResidente() == null || input.getIdUsuario() == null ||
                input.getDificultad() == null || idJuego == null || idResidencia == null) {
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }

        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si el juego pertence a la residencia
        if (!Objects.equals(juego.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        //Comprobar si el residente existe
        Residente residente = residenteService.findById(input.getIdResidente());
        if (residente == null)
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
        //Comprobar si el residente pertence a la residencia
        if (!Objects.equals(residente.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);

        //Comprobar si el usuario existe
        User usuario = userService.findById(input.getIdUsuario());
        if (usuario == null)
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        //Comprobar si el usuario pertence a la residencia
        if (!Objects.equals(usuario.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);


        //Ver si el juego es de la misma residencia que el residente
        if (!Objects.equals(juego.getResidencia().getId(), residente.getResidencia().getId())) {
            throw new ApiException(ApiErrorCode.CONFLICTO_REFERENCIAS);
        }
        //Ver si el juego es de la misma residencia que el usuario/trabajador.
        if (!Objects.equals(usuario.getResidencia().getId(), juego.getResidencia().getId())) {
            throw new ApiException(ApiErrorCode.CONFLICTO_REFERENCIAS);
        }

        //No puede haber fallos negativos
        if (input.getNum() < 0){
            throw new ApiException(ApiErrorCode.VALORES_NEGATIVOS);
        }

        RegistroJuego registroJuego = new RegistroJuego(input);
        registroJuego.setJuego(juego);
        registroJuego.setResidente(residente);
        registroJuego.setUsuario(usuario);
        RegistroJuego registro = registroJuegoRepository.save(registroJuego);
        return new RegistroJuegoResponseDto(registro);
    }


    public RegistroJuegoResponseDto update(Long idResidencia, Long idJuego, Long idRegistroJuego, RegistroJuegoDto input){
       if (idResidencia == null || idJuego == null || idRegistroJuego == null)
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
        if (input != null){
            if (input.getObservacion() != null){
                registroJuego.setObservacion(input.getObservacion());
                //guardar el registro
                registroJuego = registroJuegoRepository.save(registroJuego);
            }
        }
        return new RegistroJuegoResponseDto(registroJuego);
    }



    public List<RegistroJuegoResponseDto> getAll(Long idResidencia, Long idJuego, Dificultad dificultad, Long idResidente, Long idUser, Integer year, Integer month) {
        if (idResidencia == null || idJuego == null || dificultad == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        List<RegistroJuego> baseList;

        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si el juego pertence a la residencia
        if (!Objects.equals(juego.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);

        User user = null;
        if (idUser != null){
            //Comprobar si el usuario existe
            user = userService.findById(idUser);
            if (user == null)
                throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
            //Comprobar si el usuario pertence a la residencia
            if (!Objects.equals(user.getResidencia().getId(), idResidencia))
                throw new ApiException(ApiErrorCode.USUARIO_INVALIDO);
        }


        //Filtrar de residente y usuario
        if (idResidente == null){
            if (idUser == null){
                baseList = registroJuegoRepository.findByJuegoAndDificultad(juego, dificultad);
            }
            else
                baseList = registroJuegoRepository.findByJuegoAndDificultadAndUsuario(juego, dificultad, user);
        }
        else{
            //Comprobar si el residente existe
            Residente residente = residenteService.findById(idResidente);
            if (residente == null)
                throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);
            //Comprobar si el residente pertence a la residencia
            if (!Objects.equals(residente.getResidencia().getId(), idResidencia))
                throw new ApiException(ApiErrorCode.RESIDENTE_INVALIDO);

            if (idUser == null){
                baseList = registroJuegoRepository.findByJuegoAndDificultadAndResidente(juego, dificultad, residente);
            }
            else{
                baseList = registroJuegoRepository.findByJuegoAndDificultadAndResidenteAndUsuario(juego, dificultad, residente, user);
            }
        }

        //Filtra por dia, por mes, por año o combinado. //Mapping clase origen clase destino. DOZER, MapStruct
        if (year != null || month != null) {
            baseList = baseList.stream()
                    .filter(r -> {
                        boolean match = true;
                        if (year != null) match = r.getFecha().getYear() == year;
                        if (month != null) match = match && r.getFecha().getMonthValue() == month;
                        return match;
                    })
                    .toList();
        }

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
    public void delete(Long idResidencia, Long idJuego, Long idRegistroJuego) {
        if (idResidencia == null || idJuego == null || idRegistroJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si el juego pertence a la residencia
        if (!Objects.equals(juego.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si existe el registro de juego
        RegistroJuego registroJuego = registroJuegoRepository.findById(idRegistroJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO));
        //Comprobar si el registro pertenece al juego
        if (!Objects.equals(registroJuego.getJuego().getId(), idJuego))
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);

        //Eliminar el registro
        registroJuegoRepository.delete(registroJuego);
    }

    public RegistroJuegoResponseDto get(Long idResidencia, Long idJuego, Long idRegistroJuego) {
        if (idResidencia == null || idJuego == null || idRegistroJuego == null){
            throw new ApiException(ApiErrorCode.CAMPOS_OBLIGATORIOS);
        }
        //Comprobar si el juego existe
        Juego juego = juegoService.findById(idJuego);
        if (juego == null)
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si el juego pertence a la residencia
        if (!Objects.equals(juego.getResidencia().getId(), idResidencia))
            throw new ApiException(ApiErrorCode.JUEGO_INVALIDO);
        //Comprobar si existe el registro de juego
        RegistroJuego registroJuego = registroJuegoRepository.findById(idRegistroJuego)
                .orElseThrow(() -> new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO));
        //Comprobar si el registro pertenece al juego
        if (!Objects.equals(registroJuego.getJuego().getId(), idJuego))
            throw new ApiException(ApiErrorCode.REGISTRO_JUEGO_INVALIDO);

        return new RegistroJuegoResponseDto(registroJuego);

    }
}
