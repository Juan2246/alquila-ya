package com.arqui.alquilaya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha de inicio del período de alquiler
    private LocalDateTime fechaInicio;

    // Fecha de finalización del período de alquiler
    private LocalDateTime fechaFin;

    // Ruta o URL del documento PDF del contrato firmado
    private String pdf;

    // Estado del contrato: ACTIVO, FINALIZADO, CANCELADO
    private String estado;

    // Ruta de la imagen de la firma para cerrar el acuerdo visualmente
    private String firmaImagenUrl;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;

    // Un contrato puede generar múltiples pagos (mensuales, por ejemplo)
    @JsonIgnore
    @OneToMany(mappedBy = "contrato", fetch = FetchType.LAZY)
    private List<Pago> pagos;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;

    @Version
    private Long version;
}
