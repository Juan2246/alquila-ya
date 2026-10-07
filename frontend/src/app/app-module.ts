import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { MaterialModule } from './modules/material/material-module';

import { Cabecera } from './components/cabecera/cabecera';
import { Login } from './components/login/login';
import { Registro } from './components/registro/registro';
import { Home } from './components/home/home';
import { DetallePropiedad } from './components/propiedades/detalle-propiedad/detalle-propiedad';
import { MisPropiedades } from './components/propiedades/mis-propiedades/mis-propiedades';
import { PublicarPropiedad } from './components/propiedades/publicar-propiedad/publicar-propiedad';
import { MisReservas } from './components/reservas/mis-reservas/mis-reservas';
import { FirmarContrato } from './components/contratos/firmar-contrato/firmar-contrato';
import { MisContratos } from './components/contratos/mis-contratos/mis-contratos';
import { ConfirmacionEliminar } from './components/confirmaciones/confirmacion-eliminar/confirmacion-eliminar';
import { PropiedadCard } from './shared/propiedad-card/propiedad-card';
import { DashboardPropietario } from './components/propietario/dashboard-propietario/dashboard-propietario';
import { ReservasRecibidas } from './components/propietario/reservas-recibidas/reservas-recibidas';
import { VisitasRecibidas } from './components/propietario/visitas-recibidas/visitas-recibidas';
import { ContratosPagos } from './components/propietario/contratos-pagos/contratos-pagos';
import { MisResenasPropietario } from './components/propietario/mis-resenas-propietario/mis-resenas-propietario';

import { autorizacionInterceptor } from './interceptors/autorizacion-interceptor';

@NgModule({
  declarations: [
    App,
    Cabecera,
    Login,
    Registro,
    Home,
    DetallePropiedad,
    MisPropiedades,
    PublicarPropiedad,
    MisReservas,
    FirmarContrato,
    MisContratos,
    ConfirmacionEliminar,
    PropiedadCard,
    DashboardPropietario,
    ReservasRecibidas,
    VisitasRecibidas,
    ContratosPagos,
    MisResenasPropietario,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([autorizacionInterceptor])),
  ],
  bootstrap: [App],
})
export class AppModule {}
