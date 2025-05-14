package com.kevinolarte.resibenissa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevinolarte.resibenissa.enums.Role;
import com.kevinolarte.resibenissa.models.modulojuego.RegistroJuego;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entidad que representa un usuario del sistema.
 * <p>
 * Implementa la interfaz {@link UserDetails} de Spring Security para integrarse
 * con el sistema de autenticación.
 * Cada usuario está asociado a una residencia.
 *
 * @author Kevin Olarte
 */
@Entity
@Table(name = "usuarios")
@Setter
@Getter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    /**
     * Correo electrónico del usuario, usado como username para iniciar sesión.
     * Debe ser único en el sistema.
     */
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    /**
     * Código de verificación utilizado en el proceso de activación de la cuenta.
     */
    @Column(name = "verification_code")
    private String verificationCode;

    /**
     * Fecha y hora de expiración del código de verificación.
     */
    @Column(name = "verification_expiration")
    private LocalDateTime verificationExpiration;

    /**
     * Indica si el usuario está habilitado para acceder al sistema.
     */
    private boolean enabled;

    /**
     * Ruta de la foto de perfil.
     */
    @Column(name = "foto_perfil")
    private String fotoPerfil;

    /**
     * Roles o permisos del usuario. En este caso no se están usando, se devuelve una lista vacía.
     */
    @JsonIgnore
    private boolean accountNonExpired;
    @JsonIgnore
    private boolean credentialsNonExpired;
    @JsonIgnore
    private boolean accountNonLocked;

    /**
     * Relación con la residencia a la que pertenece el usuario.
     * Varios usuarios pueden estar asociados a la misma residencia.
     */
    @ManyToOne
    @JoinColumn(name = "fk_residencia", nullable = false)
    @JsonIgnore
    private Residencia residencia;

    private boolean baja;
    private LocalDateTime fechaBaja;

    /**
     * Relacion con los registros de los juegos que son los que se encargan de asignar el jugador.
     * Para llevar un mayor control.
     * <p>
     *  Este ususario puede tener varios registros.
     */
    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private Set<RegistroJuego> registroJuegos = new LinkedHashSet<>();

    private Role role;

    public User() {

    }
    public User(String nombre, String apellido, String email, String password){
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.baja = false;
        this.role = Role.NORMAL;

    }
    public User(String nombre, String apellido, String email, String password, Role role){
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.baja = false;
        this.role = role;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return List.of();

    }

    @Override
    public String getPassword(){
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email; // el correo es usado como nombre de usuario
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "nombre='" + nombre + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", apellido='" + apellido + '\'' +
                ", password='" + password + '\'' +
                ", residencia=" + residencia.getId() +
                '}';
    }
}
