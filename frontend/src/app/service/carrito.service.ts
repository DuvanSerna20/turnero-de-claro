import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  private carrito: any[] = [];

  constructor() {
    const guardado = localStorage.getItem('carrito_claro');
    if (guardado) {
      this.carrito = JSON.parse(guardado);
    }
  }

  obtenerCarrito() {
    return this.carrito;
  }

  agregar(plan: any) {
    this.carrito.push(plan);
    this.guardar();
  }

  eliminar(index: number) {
    this.carrito.splice(index, 1);
    this.guardar();
  }

  vaciar() {
    this.carrito = [];
    this.guardar();
  }

  getTotal(): number {
    return this.carrito.reduce((total, item) => total + item.precio, 0);
  }

  private guardar() {
    localStorage.setItem('carrito_claro', JSON.stringify(this.carrito));
  }
}
