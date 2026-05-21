import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CarritoService } from '../../service/carrito.service';
import { TurnosService } from '../../service/turnos.service';
import { AuthService } from '../../service/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-carrito',
  imports: [CommonModule, RouterModule],
  templateUrl: './carrito.html',
  styleUrl: './carrito.css',
})
export class Carrito implements OnInit {
  carrito: any[] = [];
  mensaje = '';
  mensajeError = false;
  procesando = false;
  metodoPagoSeleccionado: string = '';

  metodosPago = [
    { id: 'credito', nombre: 'Tarjeta de Crédito', icono: '💳' },
    { id: 'debito', nombre: 'Tarjeta de Débito', icono: '🏧' },
    { id: 'pse', nombre: 'PSE', icono: '🏦' }
  ];

  constructor(
    private carritoService: CarritoService,
    private turnosService: TurnosService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.cargarCarrito();
  }

  cargarCarrito() {
    this.carrito = this.carritoService.obtenerCarrito();
  }

  eliminarDelCarrito(index: number) {
    this.carritoService.eliminar(index);
    this.cargarCarrito();
  }

  getTotal(): number {
    return this.carritoService.getTotal();
  }

  formatPrecio(precio: number): string {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(precio);
  }

  seleccionarMetodo(metodoId: string) {
    this.metodoPagoSeleccionado = metodoId;
  }

  comprar() {
    if (this.carrito.length === 0) {
      this.mostrarMensaje('El carrito está vacío.', true);
      return;
    }

    if (!this.metodoPagoSeleccionado) {
      this.mostrarMensaje('Por favor selecciona un método de pago.', true);
      return;
    }

    const usuario = this.authService.obtenerSesion();
    if (!usuario) {
      this.mostrarMensaje('Debes iniciar sesión para comprar.', true);
      setTimeout(() => this.router.navigate(['/mi-cuenta/login']), 1500);
      return;
    }

    this.procesando = true;
    let comprasExitosas = 0;
    const totalCompras = this.carrito.length;
    let errores = 0;

    const metodo = this.metodosPago.find(m => m.id === this.metodoPagoSeleccionado)?.nombre || 'Desconocido';

    // Compramos uno por uno (ya que la API recibe un planId a la vez)
    this.carrito.forEach((plan, index) => {
      this.turnosService.realizarCompra(usuario.id, plan.id, metodo).subscribe({
        next: () => {
          comprasExitosas++;
          this.verificarFin(comprasExitosas, errores, totalCompras, null);
        },
        error: (err) => {
          errores++;
          const msg = err.error ? (typeof err.error === 'string' ? err.error : err.message) : 'Error desconocido';
          this.verificarFin(comprasExitosas, errores, totalCompras, msg);
        }
      });
    });
  }

  private verificarFin(exitosas: number, errores: number, total: number, ultimoError: string | null) {
    if (exitosas + errores === total) {
      this.procesando = false;
      if (errores === 0) {
        this.carritoService.vaciar();
        this.cargarCarrito();
        this.mostrarMensaje(`✅ Compra de ${exitosas} plan(es) realizada exitosamente.`, false);
        setTimeout(() => this.router.navigate(['/compras']), 2000);
      } else {
        this.carritoService.vaciar();
        this.cargarCarrito();
        this.mostrarMensaje(`⚠️ Hubo ${errores} error(es). Detalle: ${ultimoError}`, true);
      }
    }
  }

  private mostrarMensaje(texto: string, esError: boolean) {
    this.mensaje = texto;
    this.mensajeError = esError;
    this.cdr.detectChanges();
    setTimeout(() => {
      this.mensaje = '';
      this.cdr.detectChanges();
    }, 4000);
  }
}
