import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './nav.html',
  styleUrl: './nav.css',
})
export class Nav {
  isMenuOpen = false;

  constructor(public authService: AuthService) {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  obtenerLinkCuenta() {
    return this.authService.obtenerSesion() ? '/mi-cuenta/panel' : '/mi-cuenta/login';
  }

  cerrarSesion() {
    this.authService.cerrarSesion();
    window.location.href = '/inicio';
  }
}
