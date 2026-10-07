import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { UserService } from '../../services/user-service';
import { RegistroDTO } from '../../models/registroDTO';

@Component({
  selector: 'app-registro',
  standalone: false,
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  errorCarga = '';

  registroForm!: FormGroup;
  cargando: boolean = false;

  constructor(
    private userService: UserService,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private router: Router
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.registroForm = this.formBuilder.group({
      rol: ['ROLE_CLIENTE', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
      nombre: ['', [Validators.required]],
      apellido: ['', [Validators.required]],
      dni: ['', [Validators.required, Validators.pattern(/^[0-9]{8}$/)]],
      correo: ['', [Validators.required, Validators.email]],
      edad: ['', [Validators.required, Validators.min(18)]],
      descripcion: [''],
    });
  }

  Registrarse() {
    if (this.registroForm.invalid) {
      this.snackBar.open('Completa todos los campos obligatorios', '', { duration: 2500 });
      return;
    }

    const registroDTO: RegistroDTO = this.registroForm.value;

    this.cargando = true;
    this.userService.registro(registroDTO).subscribe({
      next: () => {
        this.cargando = false;
        this.snackBar.open('¡Cuenta creada! Ya puedes ingresar', '', { duration: 2500 });
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.cargando = false;
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open('No se pudo crear la cuenta. Verifica los datos.', '', { duration: 3000 });
      }
    });
  }
}
