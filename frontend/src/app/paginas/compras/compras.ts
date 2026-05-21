import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TurnosService } from '../../service/turnos.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-compras',
  imports: [CommonModule, RouterLink],
  templateUrl: './compras.html',
  styleUrl: './compras.css',
})
export class Compras implements OnInit {
  compras: any[] = [];
  cargando = true;
  mensaje = '';

  constructor(
    private turnosService: TurnosService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const usuario = this.authService.obtenerSesion();
    if (!usuario) {
      this.router.navigate(['/mi-cuenta/login']);
      return;
    }
    this.cargarCompras(usuario.id);
  }

  cargarCompras(usuarioId: number) {
    this.turnosService.obtenerComprasUsuario(usuarioId).subscribe({
      next: (data) => { 
        this.compras = data; 
        this.cargando = false; 
        this.cdr.detectChanges();
      },
      error: () => {
        this.mensaje = 'Error al cargar el historial de compras.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  formatPrecio(precio: number): string {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(precio);
  }

  formatFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-CO', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  }

  getIcono(tipo: string): string {
    const iconos: any = { MOVIL: '📱', HOGAR: '🏠', ENTRETENIMIENTO: '🎬' };
    return iconos[tipo] || '📦';
  }
}
