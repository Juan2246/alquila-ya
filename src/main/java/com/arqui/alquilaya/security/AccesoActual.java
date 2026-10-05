package com.arqui.alquilaya.security;

import com.arqui.alquilaya.entities.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Autoriza con el usuario autenticado y las relaciones persistidas, nunca con IDs del cuerpo. */
@Component
public class AccesoActual {
    public UserSecurity usuario() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !autenticacion.isAuthenticated()
                || !(autenticacion.getPrincipal() instanceof UserSecurity usuario)
                || usuario.getUser() == null || usuario.getUser().getId() == null || !usuario.isEnabled()) {
            throw new AccessDeniedException("Se requiere una sesión válida");
        }
        return usuario;
    }

    private boolean esCuenta(User cuenta, String rol) {
        UserSecurity actual = usuario();
        return cuenta != null && Objects.equals(cuenta.getId(), actual.getUser().getId())
                && actual.getAuthorities().stream().anyMatch(a -> rol.equals(a.getAuthority()));
    }

    public boolean esCliente(Cliente cliente) {
        return cliente != null && esCuenta(cliente.getUser(), "ROLE_CLIENTE");
    }

    public boolean esPropietario(Propietario propietario) {
        return propietario != null && esCuenta(propietario.getUser(), "ROLE_PROPIETARIO");
    }

    public void exigirCliente(Cliente cliente) {
        if (!esCliente(cliente)) throw new AccessDeniedException("No puedes acceder a ese cliente");
    }

    public void exigirPropietario(Propietario propietario) {
        if (!esPropietario(propietario)) throw new AccessDeniedException("No puedes gestionar esa propiedad");
    }

    public void exigirParticipante(Reserva reserva) {
        if (!esCliente(reserva.getCliente()) && (reserva.getPropiedad() == null
                || !esPropietario(reserva.getPropiedad().getPropietario()))) {
            throw new AccessDeniedException("No puedes acceder a esa reserva");
        }
    }

    public void exigirConsulta() {
        boolean permitido = usuario().getAuthorities().stream().anyMatch(a ->
                "ROLE_CLIENTE".equals(a.getAuthority()) || "ROLE_PROPIETARIO".equals(a.getAuthority()));
        if (!permitido) throw new AccessDeniedException("No puedes consultar la disponibilidad");
    }
}
