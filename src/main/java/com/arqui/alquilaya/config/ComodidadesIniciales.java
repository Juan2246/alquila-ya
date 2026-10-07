package com.arqui.alquilaya.config;
import com.arqui.alquilaya.entities.Comodidad;
import com.arqui.alquilaya.repositories.ComodidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ComodidadesIniciales implements CommandLineRunner {
    private final ComodidadRepository comodidades;
    @Override @Transactional
    public void run(String... args) {
        for (var dato : List.of(new String[]{"Wifi", "wifi"}, new String[]{"Cocina", "kitchen"},
                new String[]{"Estacionamiento", "local_parking"}, new String[]{"Piscina", "pool"})) {
            if (comodidades.findAll().stream().noneMatch(c -> dato[0].equalsIgnoreCase(c.getNombre())))
                comodidades.save(new Comodidad(null, dato[0], dato[1], null));
        }
    }
}
