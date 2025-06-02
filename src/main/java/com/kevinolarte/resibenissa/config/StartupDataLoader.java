package com.kevinolarte.resibenissa.config;

import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.EventoSalidaDto;
import com.kevinolarte.resibenissa.dto.in.moduloOrgSalida.ParticipanteDto;
import com.kevinolarte.resibenissa.dto.in.modulojuego.RegistroJuegoDto;
import com.kevinolarte.resibenissa.enums.Role;
import com.kevinolarte.resibenissa.enums.moduloOrgSalida.EstadoSalida;
import com.kevinolarte.resibenissa.enums.modulojuego.Dificultad;
import com.kevinolarte.resibenissa.models.Residencia;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.EventoSalida;
import com.kevinolarte.resibenissa.models.moduloOrgSalida.Participante;
import com.kevinolarte.resibenissa.models.moduloWallet.Wallet;
import com.kevinolarte.resibenissa.models.modulojuego.Juego;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import com.kevinolarte.resibenissa.repositories.ResidenciaRepository;
import com.kevinolarte.resibenissa.repositories.ResidenteRepository;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.EventoSalidaRepository;
import com.kevinolarte.resibenissa.repositories.moduloOrgSalida.ParticipanteRepository;
import com.kevinolarte.resibenissa.repositories.moduloWallet.WalletRepository;
import com.kevinolarte.resibenissa.repositories.modulojuego.JuegoRepository;
import com.kevinolarte.resibenissa.repositories.modulojuego.RegistroJuegoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class StartupDataLoader {

    private final ResidenciaRepository residenciaRepository;
    private final UserRepository userRepository;
    private final ResidenteRepository residenteRepository;
    private final JuegoRepository juegoRepository;
    private final RegistroJuegoRepository registroJuegoRepository;
    private final EventoSalidaRepository eventoSalidaRepository;
    private final ParticipanteRepository participanteRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    @PostConstruct
    public void init() {

        Residencia residenciaDefault = residenciaRepository.save(new Residencia("Residencia Benissa", "resibenissa@gmail.com"));
        Residencia residenciaAdmin = residenciaRepository.save(new Residencia("Residencia Admin", "resiAdmin@gmail.com"));
        User user = new User("Kevin", "olarte", "dafault@gmail.com", passwordEncoder.encode("default"), Role.ADMIN);
        User sender = new User("Kevin", "olarte", "sender@gmail.com", passwordEncoder.encode("sender"), Role.SENDER);
        user.setResidencia(residenciaAdmin);
        user.setEnabled(true);

        sender.setResidencia(residenciaDefault);
        sender.setEnabled(true);
        user = userRepository.save(user);
        sender = userRepository.save(sender);

        Residente residente1 = new Residente("Residente", "1", LocalDate.of(1999, 1,1), "000000001", "kevinolarte.ko@gmail.com", null);
        residente1.setResidencia(residenciaDefault);
        Residente residente2 = new Residente("Residente", "2", LocalDate.of(1999, 1,1), "000000002", "kevinolarte.ko@gmail.com", null);
        residente2.setResidencia(residenciaDefault);
        Residente residente3 = new Residente("Residente", "3", LocalDate.of(1999, 1,1), "000000003", "kevinolarte.ko@gmail.com", null);
        residente3.setResidencia(residenciaDefault);
        Residente residente4 = new Residente("Residente", "4", LocalDate.of(1999, 1,1), "000000004", "kevinolarte.ko@gmail.com", null);
        residente4.setResidencia(residenciaDefault);
        Residente residente5 = new Residente("Residente", "5", LocalDate.of(1999, 1,1), "000000005", "kevinolarte.ko@gmail.com", null);
        residente5.setResidencia(residenciaDefault);
        Residente residente6 = new Residente("Residente", "6", LocalDate.of(1999, 1,1), "000000006", "kevinolarte.ko@gmail.com", null);
        residente6.setResidencia(residenciaDefault);
        Residente residente7 = new Residente("Residente", "7", LocalDate.of(1999, 1,1), "000000007", "kevinolarte.ko@gmail.com", null);
        residente7.setResidencia(residenciaDefault);
        Residente residente8 = new Residente("Residente", "8", LocalDate.of(1999, 1,1), "000000008", "kevinolarte.ko@gmail.com", null);
        residente8.setResidencia(residenciaDefault);
        Residente residente9 = new Residente("Residente", "9", LocalDate.of(1999, 1,1), "000000009", "kevinolarte.ko@gmail.com", null);
        residente9.setResidencia(residenciaDefault);
        Residente residente10 = new Residente("Residente", "10", LocalDate.of(1999, 1,1), "000000010", "kevinolarte.ko@gmail.com", null);
        residente10.setResidencia(residenciaDefault);

        residente1 = residenteRepository.save(residente1);

        Wallet wallet1 = new Wallet();
        wallet1.setSaldoTotal(1000.0);
        wallet1.setResidente(residente1);
        walletRepository.save(wallet1);
        residente2 = residenteRepository.save(residente2);
        residente3 = residenteRepository.save(residente3);
        residente4 = residenteRepository.save(residente4);
        residente5 =  residenteRepository.save(residente5);
        residente6 = residenteRepository.save(residente6);
        residente7 =  residenteRepository.save(residente7);
        residente8 =  residenteRepository.save(residente8);
        residente9 = residenteRepository.save(residente9);
        residente10 = residenteRepository.save(residente10);

        Juego juego = new Juego("Juego 1");
        juego = juegoRepository.save(juego);
        Random rnd = new Random();

        for(int i = 0; i < 25; i++){
            RegistroJuegoDto dto = new RegistroJuegoDto();
            dto.setIdJuego(juego.getId());
            dto.setIdResidente(rnd.nextLong(1, 10));
            dto.setDificultad(Dificultad.DIFICULTAD1); //Dificultad.values()[rnd.nextInt(0, 3)]
            dto.setIdUsuario(user.getId());
            dto.setNum(rnd.nextInt(1, 100));
            dto.setDuracion(rnd.nextDouble(1, 100));
            RegistroJuego registroJuego = new RegistroJuego(dto);
            registroJuego.setJuego(juego);
            registroJuego.setResidente(residenteRepository.findById(dto.getIdResidente()).orElseThrow());
            registroJuego.setUsuario(user);
            registroJuegoRepository.save(registroJuego);
        }

        EventoSalidaDto eventoSalidaDto = new EventoSalidaDto();
        eventoSalidaDto.setNombre("Casa 1");
        eventoSalidaDto.setDescripcion("Salida a casa 1");
        eventoSalidaDto.setFecha(LocalDateTime.now().plusYears(1));

        EventoSalida eventoSalida = new EventoSalida(eventoSalidaDto);
        eventoSalida.setResidencia(residenciaDefault);
        eventoSalidaRepository.save(eventoSalida);

        List<Residente> residentes = residenteRepository.findAll();
        for (Residente residente : residentes) {
            ParticipanteDto par = new ParticipanteDto();
            par.setRecursosHumanos(false);
            par.setRecursosMateriales(true);
            par.setPreOpinion("No me gusta");
            par.setIdResidente(residente.getId());
            Participante participante = new Participante();
            participante.setEvento(eventoSalida);
            participante.setResidente(residente);
            participante.setRecursosHumanos(par.getRecursosHumanos());
            participante.setRecursosMateriales(par.getRecursosMateriales());
            participante.setPreOpinion(par.getPreOpinion());

            participanteRepository.save(participante);


        }

        // Generar registros aleatorios para los residentes
        generarRegistrosAleatorios(residente1, juego, user, 100);
        generarRegistrosAleatorios(residente2, juego, user, 50);





    }
    private void generarRegistrosAleatorios(Residente residente, Juego juego, User usuario, int cantidad) {
        Random random = new Random();
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int maxDay = 28;

        for (int i = 0; i < cantidad; i++) {
            // Día aleatorio dentro del mes actual
            int randomDay = random.nextInt(maxDay) + 1; // Día entre 1 y maxDay
            int randomMonth = random.nextInt(12) + 1; // Mes entre 1 y 12
            int hour = random.nextInt(12) + 8; // Horario entre 8:00 y 20:00 aprox
            int minute = random.nextInt(60);

            LocalDateTime fechaAleatoria = LocalDateTime.of(year, randomMonth, randomDay, hour, minute);

            RegistroJuegoDto dto = new RegistroJuegoDto();
            dto.setIdJuego(juego.getId());
            dto.setIdResidente(residente.getId());
            dto.setDificultad(Dificultad.DIFICULTAD1); //(Dificultad.values()[random.nextInt(Dificultad.values().length)]
            dto.setIdUsuario(usuario.getId());
            dto.setNum(random.nextInt(5) + 1); // entre 1 y 5 errores
            dto.setDuracion(20 + random.nextDouble() * 80); // entre 20 y 100

            RegistroJuego registro = new RegistroJuego(dto, fechaAleatoria);
            registro.setResidente(residente);
            registro.setJuego(juego);
            registro.setUsuario(usuario);

            registroJuegoRepository.save(registro);
        }
    }

}
