import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';

  constructor(private http: HttpClient) { }

  registrar(usuario: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/registro`, usuario);
  }

  login(credenciales: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credenciales);
  }

  // Guardar usuario en LocalStorage al iniciar sesión
  guardarSesion(usuario: any) {
    localStorage.setItem('usuario_claro', JSON.stringify(usuario));
  }

  cerrarSesion() {
    localStorage.removeItem('usuario_claro');
  }

  obtenerSesion() {
    const usuarioStr = localStorage.getItem('usuario_claro');
    return usuarioStr ? JSON.parse(usuarioStr) : null;
  }
}
