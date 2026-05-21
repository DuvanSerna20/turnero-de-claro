import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panel.html',
  styleUrl: './panel.css',
})
export class Panel implements OnInit {
  usuario: any = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.usuario = this.authService.obtenerSesion();
    if (!this.usuario) {
      this.router.navigate(['/mi-cuenta/login']);
    }
  }

  cerrarSesion() {
    this.authService.cerrarSesion();
    this.router.navigate(['/inicio']);
  }
}
