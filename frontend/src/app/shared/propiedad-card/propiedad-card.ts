import { archivoUrl } from '../archivo-url';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Propiedad } from '../../models/propiedad';

@Component({
  selector: 'app-propiedad-card',
  standalone: false,
  templateUrl: './propiedad-card.html',
  styleUrl: './propiedad-card.css',
})
export class PropiedadCard {
  @Input() propiedad!: Propiedad;
  @Input() modoPropietario: boolean = false;

  @Output() eliminar = new EventEmitter<number>();
  @Output() editar = new EventEmitter<number>();

  imagenPorDefecto: string = '/sin-foto.svg';

  get imagenPrincipal(): string {
    if (this.propiedad?.fotos && this.propiedad.fotos.length > 0) {
      return archivoUrl(this.propiedad.fotos[0].url);
    }
    return this.imagenPorDefecto;
  }

  onEliminar(event: Event) {
    event.stopPropagation();
    event.preventDefault();
    this.eliminar.emit(this.propiedad.id);
  }

  onEditar(event: Event) {
    event.stopPropagation();
    event.preventDefault();
    this.editar.emit(this.propiedad.id);
  }
}
