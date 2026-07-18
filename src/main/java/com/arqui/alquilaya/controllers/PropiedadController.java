package com.arqui.alquilaya.controllers;

import com.arqui.alquilaya.dtos.CotizacionDTO;
import com.arqui.alquilaya.dtos.PropiedadDTO;
import com.arqui.alquilaya.entities.Propiedad;
import com.arqui.alquilaya.services.PropiedadService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/alquilaya")
public class PropiedadController {

    @Autowired
    PropiedadService propiedadService;

    /** Listar todas las propiedades disponibles. */
    @GetMapping("/propiedades")
    public ResponseEntity<List<Propiedad>> listAll() {
        return new ResponseEntity<>(propiedadService.listAll(), HttpStatus.OK);
    }

    /** Buscar una propiedad por su id */
    @GetMapping("/propiedades/{propiedadId}")
    public ResponseEntity<Propiedad> findById(@PathVariable("propiedadId") Long id) {
        Propiedad found = propiedadService.findById(id);
        if (found == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    /** Listar propiedades de un propietario específico. */
    @GetMapping("/propiedades/propietario/{propietarioId}")
    public ResponseEntity<List<Propiedad>> listByPropietarioId(@PathVariable("propietarioId") Long id) {
        return new ResponseEntity<>(propiedadService.listByPropietarioId(id), HttpStatus.OK);
    }

    /**
     * Buscador avanzado con filtros dinámicos.
     * Todos los parámetros son opcionales.
     * Ejemplo: GET /propiedades/buscar?distrito=Miraflores&precioMin=1000&precioMax=3000&capacidad=2
     */
    @GetMapping("/propiedades/buscar")
    public ResponseEntity<List<Propiedad>> buscarConFiltros(
            @RequestParam(required = false) String distrito,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer capacidad
    ) {
        List<Propiedad> resultados = propiedadService.buscarConFiltros(distrito, precioMin, precioMax, capacidad);
        return new ResponseEntity<>(resultados, HttpStatus.OK);
    }

    /**
     * Cotizador automático.
     * Recibe las fechas de un viaje y calcula matemáticamente el precio total a pagar.
     * Fórmula: noches × precio por noche.
     * Ejemplo: GET /propiedades/3/cotizar?checkIn=2024-07-01&checkOut=2024-07-05
     */
    @GetMapping("/propiedades/{propiedadId}/cotizar")
    public ResponseEntity<CotizacionDTO> cotizar(
            @PathVariable("propiedadId") Long id,
            @RequestParam String checkIn,
            @RequestParam String checkOut
    ) {
        Propiedad propiedad = propiedadService.findById(id);

        LocalDate fechaCheckIn = LocalDate.parse(checkIn);
        LocalDate fechaCheckOut = LocalDate.parse(checkOut);

        if (!fechaCheckOut.isAfter(fechaCheckIn)) {
            throw new ValidationException("La fecha de check-out debe ser posterior a la fecha de check-in");
        }

        long noches = ChronoUnit.DAYS.between(fechaCheckIn, fechaCheckOut);
        BigDecimal precioTotal = propiedad.getPrecio().multiply(BigDecimal.valueOf(noches));

        CotizacionDTO cotizacion = new CotizacionDTO(
                propiedad.getId(),
                propiedad.getTitulo(),
                checkIn,
                checkOut,
                noches,
                propiedad.getPrecio(),
                precioTotal
        );

        return new ResponseEntity<>(cotizacion, HttpStatus.OK);
    }

    /** Publicar una nueva propiedad (solo PROPIETARIO). Recibe un DTO con los IDs de relaciones. */
    @PostMapping("/propiedades")
    public ResponseEntity<PropiedadDTO> add(@RequestBody PropiedadDTO propiedadDTO) {
        PropiedadDTO newPropiedadDTO = propiedadService.addDTO(propiedadDTO);
        return new ResponseEntity<>(newPropiedadDTO, HttpStatus.CREATED);
    }

    /** Actualizar una propiedad existente (solo PROPIETARIO). */
    @PutMapping("/propiedades")
    public ResponseEntity<Propiedad> update(@RequestBody Propiedad propiedad) {
        Propiedad updated = propiedadService.update(propiedad);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /** Eliminar una propiedad por su ID (solo PROPIETARIO). */
    @DeleteMapping("/propiedades/{propiedadId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("propiedadId") Long id) {
        propiedadService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
