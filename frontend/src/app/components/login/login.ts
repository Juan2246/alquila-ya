import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { UserService } from '../../services/user-service';
import { LoginDTO } from '../../models/userDTO';
import { TokenDTO } from '../../models/tokenDTO';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  errorCarga = '';

  loginForm!: FormGroup;
  cargando: boolean = false;

  constructor(
    private userService: UserService,
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private router: Router
  ) {}

  ngOnInit() {
    this.errorCarga = '';
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  Ingresar() {
    if (this.cargando || this.loginForm.invalid) {
      return;
    }

    const userDTO: LoginDTO = {
      username: this.loginForm.get('username')?.value,
      password: this.loginForm.get('password')?.value,
    };

    this.cargando = true;
    this.userService.login(userDTO).subscribe({
      next: (data: TokenDTO) => {
        this.userService.getPerfil().subscribe({
          next: () => {
            this.cargando = false;
            this.snackBar.open('¡Bienvenido de nuevo!', '', { duration: 2000 });
            this.router.navigate(['/inicio']);
          },
          error: () => {
            this.cargando = false;
            this.userService.logout();
            this.snackBar.open('No se pudo cargar tu perfil. Vuelve a intentarlo.', 'Cerrar', { duration: 5000 });
          }
        });
      },
      error: (err) => {
        this.cargando = false;
        this.errorCarga = 'No se pudo completar la solicitud. Revisa la conexión e inténtalo otra vez.';
        this.snackBar.open('Usuario o contraseña incorrectos', '', { duration: 2500 });
      }
    });
  }
}
