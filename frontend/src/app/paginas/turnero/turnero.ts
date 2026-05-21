import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TurnosService } from '../../service/turnos.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-turnero',
  imports: [CommonModule],
  templateUrl: './turnero.html',
  styleUrl: './turnero.css',
})
export class Turnero implements OnInit, OnDestroy {
  departamentos: any[] = [];
  cola: any[] = [];
  cargando = true;
  mensaje = '';
  mensajeError = false;
  turnoCreado: any = null;
  private autoRefresh: any;

  constructor(
    private turnosService: TurnosService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.cargarDatos();
    // Auto-refrescar la cola cada 5 segundos
    this.autoRefresh = setInterval(() => {
      this.cargarCola(true);
    }, 5000);
  }

  ngOnDestroy() {
    if (this.autoRefresh) {
      clearInterval(this.autoRefresh);
    }
  }

  cargarDatos() {
    this.cargando = true;
    this.turnosService.obtenerDepartamentos().subscribe({
      next: (depts) => {
        this.departamentos = depts;
        this.cargarCola();
        this.cdr.detectChanges();
      },
      error: () => {
        this.mensaje = 'Error al conectar con el servidor. Verifica que el backend esté activo en el puerto 8080.';
        this.mensajeError = true;
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarCola(esRefreshOculto = false) {
    if (!esRefreshOculto) this.cargando = true;
    this.turnosService.obtenerCola().subscribe({
      next: (cola) => { 
        this.cola = cola; 
        this.cargando = false; 

        // Verificar si el turno del usuario fue llamado
        if (this.turnoCreado) {
          const miTurnoSiguePendiente = this.cola.find(t => t.id === this.turnoCreado.id);
          if (!miTurnoSiguePendiente) {
            // Ya no está pendiente, fue llamado!
            this.mensaje = `🔔 ¡Tu turno ${this.turnoCreado.numeroCorrelativo} ha sido llamado a ventanilla!`;
            this.mensajeError = false;
            this.turnoCreado = null;
            // Limpiar mensaje después de un tiempo
            setTimeout(() => {
              if (this.mensaje.includes('llamado')) this.mensaje = '';
              this.cdr.detectChanges();
            }, 8000);
          }
        }

        this.cdr.detectChanges();
      },
      error: () => { 
        this.cargando = false; 
        this.cdr.detectChanges();
      }
    });
  }

  solicitarTurno(departamentoId: number) {
    const usuario = this.authService.obtenerSesion();
    if (!usuario) {
      this.mensaje = 'Debes iniciar sesión para solicitar un turno.';
      this.mensajeError = true;
      this.cdr.detectChanges();
      setTimeout(() => this.router.navigate(['/mi-cuenta/login']), 1500);
      return;
    }
    this.turnosService.crearTurno(usuario.id, departamentoId).subscribe({
      next: (turno) => {
        this.turnoCreado = turno;
        this.mensaje = `✅ Turno ${turno.numeroCorrelativo} creado exitosamente.`;
        this.mensajeError = false;
        this.cargarCola();
        this.cdr.detectChanges();
      },
      error: () => {
        this.mensaje = 'Error al crear el turno.';
        this.mensajeError = true;
        this.cdr.detectChanges();
      }
    });
  }

  cancelarTurno(turnoId: number) {
    this.turnosService.cancelarTurno(turnoId).subscribe({
      next: () => {
        this.turnoCreado = null;
        this.mensaje = 'Turno cancelado.';
        this.mensajeError = false;
        this.cargarCola();
        this.cdr.detectChanges();
      }
    });
  }

  calcularEspera(fechaCreacion: string): string {
    const minutos = Math.floor((Date.now() - new Date(fechaCreacion).getTime()) / 60000);
    return minutos < 1 ? 'Ahora' : `${minutos} min`;
  }
}
