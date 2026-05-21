import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  registroForm: FormGroup;
  tiposDocumento = ['Cédula de Ciudadanía', 'Cédula de Extranjería', 'Pasaporte'];

  constructor(
    private fb: FormBuilder, 
    private authService: AuthService,
    private router: Router
  ) {
    this.registroForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      tipoDocumento: ['', Validators.required],
      numeroDocumento: [{value: '', disabled: true}, Validators.required],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      celular: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      telefonoAdicional: [''],
      terminos: [false, Validators.requiredTrue]
    });

    // Activar el campo de número de documento solo cuando se seleccione un tipo
    this.registroForm.get('tipoDocumento')?.valueChanges.subscribe(val => {
      if (val) {
        this.registroForm.get('numeroDocumento')?.enable();
      } else {
        this.registroForm.get('numeroDocumento')?.disable();
      }
    });
  }

  onSubmit() {
    if (this.registroForm.valid) {
      const datosForm = this.registroForm.getRawValue();
      
      // Adaptamos los datos al formato que espera el backend
      const usuarioNuevo = {
        email: datosForm.email,
        password: datosForm.numeroDocumento, // Se usa el documento como contraseña inicial
        nombres: datosForm.nombres + ' ' + datosForm.apellidos,
        documento: datosForm.numeroDocumento,
        celular: datosForm.celular
      };

      this.authService.registrar(usuarioNuevo).subscribe({
        next: (res) => {
          alert('¡Cuenta creada exitosamente! Tu contraseña temporal es tu número de documento.');
          this.router.navigate(['/mi-cuenta/login']);
        },
        error: (err) => {
          console.error(err);
          alert('Error al crear la cuenta: ' + (typeof err.error === 'string' ? err.error : 'Verifica que la base de datos esté corriendo.'));
        }
      });
    } else {
      // Si el form es invalido, marca todo como tocado para que salgan los bordes rojos
      this.registroForm.markAllAsTouched();
    }
  }
}
