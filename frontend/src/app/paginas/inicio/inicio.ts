import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TiendaService } from '../../service/tienda.service';

@Component({
  selector: 'app-inicio',
  imports: [CommonModule, RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio implements OnInit {
  planes: any[] = [];
  cargando = true;
  error = false;

  constructor(
    private tiendaService: TiendaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.tiendaService.obtenerTodos().subscribe({
      next: (data) => {
        console.log('✅ Planes recibidos:', data);
        this.planes = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ Error al cargar planes:', err);
        this.error = true;
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  formatPrecio(precio: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency', currency: 'COP', maximumFractionDigits: 0
    }).format(precio);
  }

  getIcono(tipo: string): string {
    const mapa: any = { MOVIL: '📱', HOGAR: '🏠', ENTRETENIMIENTO: '🎬' };
    return mapa[tipo] || '📦';
  }

  getColor(tipo: string): string {
    const mapa: any = { MOVIL: '#e30613', HOGAR: '#0066cc', ENTRETENIMIENTO: '#8b00ff' };
    return mapa[tipo] || '#333';
  }
}

