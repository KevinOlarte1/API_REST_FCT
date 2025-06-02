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
import net.datafaker.Faker;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    private final Faker faker = new Faker();
    private final Set<String> dniGenerados = new HashSet<>();
    private final Set<String> emailsGenerados = new HashSet<>();
    private final Random random = new Random();


    @PostConstruct
    public void init() {
        Residencia residenciaAdmin = residenciaRepository.save(new Residencia("Residencia Admin", "resiAdmin@gmail.com"));
        User user = new User("Kevin", "olarte", "dafault@gmail.com", passwordEncoder.encode("default"), Role.ADMIN);
        user.setResidencia(residenciaAdmin);
        user.setEnabled(true);
        user = userRepository.save(user);
        // Cargar datos de prueba al iniciar la aplicación
        cargarDatosPrueba();
        cargarJuegosPrueba();
        cargarRegistrosJuegosPrueba();
        cargarEventosSalidaPrueba();

    }

    public void cargarRegistrosJuegosPrueba() {
        List<Residente> residentes = residenteRepository.findAll();
        List<Juego> juegos = juegoRepository.findAll();

        if (residentes.isEmpty() || juegos.isEmpty()) {
            System.out.println("⚠️ No hay residentes o juegos cargados en la base de datos.");
            return;
        }

        for (Residente residente : residentes) {
            for (int i = 0; i < 7; i++) {
                RegistroJuego registro = new RegistroJuego();
                registro.setResidente(residente);
                registro.setJuego(juegos.get(random.nextInt(juegos.size())));
                registro.setFecha(LocalDateTime.now().minusDays(random.nextInt(30)));
                registro.setNum(random.nextInt(10)); // Fallos entre 0 y 9
                registro.setDuracion(30 + random.nextDouble() * 90); // Entre 30 y 120 segundos
                registro.setDificultad(Dificultad.values()[random.nextInt(Dificultad.values().length)]);
                registro.setObservacion("Prueba generada automáticamente");

                registroJuegoRepository.save(registro);
            }
        }
    }

    private void cargarJuegosPrueba() {
        Juego juego1 = new Juego("Seguir la línea");
        Juego juego2 = new Juego("Memory");
        Juego juego3 = new Juego("Bongo");

        juegoRepository.save(juego1);
        juegoRepository.save(juego2);
        juegoRepository.save(juego3);
    }

    public void cargarEventosSalidaPrueba() {
        List<Residencia> residencias = residenciaRepository.findAll();

        for (Residencia residencia : residencias) {
            // 1 evento esta mañana
            eventoSalidaRepository.save(crearEvento(residencia, LocalDateTime.now().with(LocalTime.of(10, 0))));

            // 2 eventos pasado mañana
            eventoSalidaRepository.save(crearEvento(residencia, LocalDateTime.now().plusDays(2).with(LocalTime.of(9 + random.nextInt(3), 0))));
            eventoSalidaRepository.save(crearEvento(residencia, LocalDateTime.now().plusDays(2).with(LocalTime.of(14 + random.nextInt(3), 0))));

            // 2 eventos la semana que viene
            eventoSalidaRepository.save(crearEvento(residencia, LocalDateTime.now().plusDays(7).with(LocalTime.of(10, 0))));
            eventoSalidaRepository.save(crearEvento(residencia, LocalDateTime.now().plusDays(8).with(LocalTime.of(17, 30))));
        }
    }

    private EventoSalida crearEvento(Residencia residencia, LocalDateTime fecha) {
        EventoSalida evento = new EventoSalida();
        evento.setNombre(faker.funnyName().name());
        evento.setDescripcion(faker.lorem().sentence(10));
        evento.setFechaInicio(fecha);
        evento.setEstado(EstadoSalida.ABIERTO); // Ajusta si usas otro valor por defecto
        evento.setResidencia(residencia);
        return evento;
    }

    public void cargarDatosPrueba() {
        for (int i = 0; i < 3; i++) {
            String ciudad = faker.address().cityName().replaceAll("[^A-Za-z]", "");
            String emailResidencia = generarEmailUnico("residencia" + ciudad.toLowerCase());

            Residencia residencia = new Residencia();
            residencia.setNombre("Residencia " + ciudad);
            residencia.setEmail(emailResidencia);
            residenciaRepository.save(residencia);
            User user = new User();
            user.setNombre("Admin " + ciudad);
            user.setApellido("Administrador " + ciudad);
            user.setEnabled(true);
            user.setEmail("admin@" + ciudad.toLowerCase() + ".com");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setResidencia(residencia);
            user.setRole(Role.NORMAL);
            userRepository.save(user);


            for (int j = 0; j < 30; j++) {
                String nombre = faker.name().firstName();
                String apellido = faker.name().lastName();
                LocalDate nacimiento = faker.date().birthday(65, 95).toLocalDateTime().toLocalDate();
                String dni = generarDniUnico();
                String familiar1 = faker.name().fullName();
                String familiar2 = faker.bool().bool() ? faker.name().fullName() : null;

                Residente residente = new Residente(nombre, apellido, nacimiento, dni, familiar1, familiar2);
                residente.setResidencia(residencia);
                residenteRepository.save(residente);
            }

        }
    }

    private String generarDniUnico() {
        String dni;
        do {
            // DNI español típico: 8 números + letra final (omitimos la letra final para cumplir tu validador de 8)
            dni = String.format("%08d", faker.number().numberBetween(10000000, 99999999));
        } while (!dniGenerados.add(dni));
        return dni;
    }


    private String generarEmailUnico(String base) {
        String email;
        int intento = 0;
        do {
            email = base + (intento == 0 ? "" : intento) + "@resisuite.com";
            intento++;
        } while (!emailsGenerados.add(email));
        return email;
    }
}



