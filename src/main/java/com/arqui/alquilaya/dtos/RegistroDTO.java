package com.arqui.alquilaya.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class RegistroDTO {
    @NotBlank @Size(max = 100) private String username;
    @NotBlank @Size(min = 8, max = 72)
    @ToString.Exclude @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank private String rol;
    @NotBlank @Size(max = 100) private String nombre;
    @NotBlank @Size(max = 100) private String apellido;
    @NotBlank @Pattern(regexp = "[0-9]{8}") private String dni;
    @NotBlank @Email @Size(max = 150) private String correo;
    @NotNull @Min(18) @Max(120) private Integer edad;
    @Size(max = 250) private String descripcion;
}
