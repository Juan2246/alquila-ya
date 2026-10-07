import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Login } from './components/login/login';
import { Registro } from './components/registro/registro';
import { Home } from './components/home/home';
import { DetallePropiedad } from './components/propiedades/detalle-propiedad/detalle-propiedad';
import { MisPropiedades } from './components/propiedades/mis-propiedades/mis-propiedades';
import { PublicarPropiedad } from './components/propiedades/publicar-propiedad/publicar-propiedad';
import { MisReservas } from './components/reservas/mis-reservas/mis-reservas';
import { FirmarContrato } from './components/contratos/firmar-contrato/firmar-contrato';
import { MisContratos } from './components/contratos/mis-contratos/mis-contratos';
import { DashboardPropietario } from './components/propietario/dashboard-propietario/dashboard-propietario';
import { ReservasRecibidas } from './components/propietario/reservas-recibidas/reservas-recibidas';
import { VisitasRecibidas } from './components/propietario/visitas-recibidas/visitas-recibidas';
import { ContratosPagos } from './components/propietario/contratos-pagos/contratos-pagos';
import { MisResenasPropietario } from './components/propietario/mis-resenas-propietario/mis-resenas-propietario';

import { authGuard } from './guards/auth-guard';
import { propietarioGuard } from './guards/propietario-guard';
import { clienteGuard } from './guards/cliente-guard';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'inicio', component: Home, canActivate: [authGuard] },
  { path: 'propiedades/:id', component: DetallePropiedad, canActivate: [authGuard] },
  { path: 'mis-propiedades', component: MisPropiedades, canActivate: [propietarioGuard] },
  { path: 'publicar-propiedad', component: PublicarPropiedad, canActivate: [propietarioGuard] },
  { path: 'publicar-propiedad/:id', component: PublicarPropiedad, canActivate: [propietarioGuard] },
  { path: 'dashboard', component: DashboardPropietario, canActivate: [propietarioGuard] },
  { path: 'reservas-recibidas', component: ReservasRecibidas, canActivate: [propietarioGuard] },
  { path: 'visitas-recibidas', component: VisitasRecibidas, canActivate: [propietarioGuard] },
  { path: 'contratos-pagos', component: ContratosPagos, canActivate: [propietarioGuard] },
  { path: 'mis-resenas', component: MisResenasPropietario, canActivate: [propietarioGuard] },
  { path: 'mis-reservas', component: MisReservas, canActivate: [clienteGuard] },
  { path: 'mis-contratos', component: MisContratos, canActivate: [clienteGuard] },
  { path: 'contratos/:id/firmar', component: FirmarContrato, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
