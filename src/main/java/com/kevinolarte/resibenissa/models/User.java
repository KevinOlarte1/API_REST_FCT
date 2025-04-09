package com.kevinolarte.resibenissa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    @JoinColumn(name = "fk_residencia")
    private Residencia residencia;

    public User() {}
    public User(String nombre, String apellido, String email, String password){
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;

    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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


}
