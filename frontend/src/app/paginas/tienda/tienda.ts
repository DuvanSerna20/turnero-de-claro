import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TiendaService } from '../../service/tienda.service';
import { TurnosService } from '../../service/turnos.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';
import { CarritoService } from '../../service/carrito.service';

@Component({
  selector: 'app-tienda',
  imports: [CommonModule],
  templateUrl: './tienda.html',
  styleUrl: './tienda.css',
})
export class Tienda implements OnInit {
  planes: any[] = [];
  filtroActivo: string = 'TODOS';
  cargando = true;
  mensaje = '';
  mensajeError = false;

  constructor(
    private tiendaService: TiendaService,
    private turnosService: TurnosService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private carritoService: CarritoService
  ) {}

  ngOnInit() {
    this.cargarPlanes();
  }

  cargarPlanes() {
    this.cargando = true;
    this.tiendaService.obtenerTodos().subscribe({
      next: (data: any[]) => { 
        this.planes = data; 
        this.cargando = false; 
        this.cdr.detectChanges();
      },
      error: () => { 
        this.mensaje = 'Error al cargar los planes. Verifica que el servidor esté activo.'; 
        this.mensajeError = true; 
        this.cargando = false; 
        this.cdr.detectChanges();
      }
    });
  }

  filtrar(tipo: string) {
    this.filtroActivo = tipo;
    this.cargando = true;
    if (tipo === 'TODOS') {
      this.cargarPlanes();
    } else {
      this.tiendaService.obtenerPorTipo(tipo as any).subscribe({
        next: (data: any[]) => { 
          this.planes = data; 
          this.cargando = false; 
          this.cdr.detectChanges();
        },
        error: () => { 
          this.cargando = false; 
          this.cdr.detectChanges();
        }
      });
    }
  }

  agregarAlCarrito(plan: any) {
    this.carritoService.agregar(plan);
    this.mensaje = `✅ Plan "${plan.nombre}" agregado al carrito.`;
    this.mensajeError = false;
    this.cdr.detectChanges();
    setTimeout(() => { this.mensaje = ''; this.cdr.detectChanges(); }, 3000);
  }

  formatPrecio(precio: number): string {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(precio);
  }

  getIcono(tipo: string): string {
    const iconos: any = { MOVIL: '📱', HOGAR: '🏠', ENTRETENIMIENTO: '🎬' };
    return iconos[tipo] || '📦';
  }
}
