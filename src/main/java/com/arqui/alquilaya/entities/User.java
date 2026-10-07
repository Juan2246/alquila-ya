package com.arqui.alquilaya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de usuario para el inicio de sesión (puede ser el correo electrónico)
    @Column(unique = true, nullable = false)
    private String username;

    // Contraseña encriptada con BCrypt (nunca se almacena en texto plano).
    // WRITE_ONLY: se acepta al recibir (login) pero nunca se incluye en las respuestas,
    // porque el User viaja anidado dentro de Cliente, Propietario, Contrato, etc.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    // Campo que indica si la cuenta está activa o deshabilitada
    private Boolean enabled;

    /**
     * Relación muchos-a-muchos con Rol (equivalente a Authority en el proyecto del profesor).
     * */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = {
                    @JoinColumn(
                            name = "user_id",
                            referencedColumnName = "id",
                            nullable = false
                    )
            },
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "rol_id",
                            referencedColumnName = "id",
                            nullable = false
                    )
            }
    )
    private List<Rol> roles;
}
