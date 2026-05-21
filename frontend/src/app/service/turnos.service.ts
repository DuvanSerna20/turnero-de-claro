import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TurnosService {
  private apiBase = '/api';

  constructor(private http: HttpClient) {}

  // ── Departamentos ────────────────────────────────────────────────
  obtenerDepartamentos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/departamentos`);
  }

  // ── Turnos ───────────────────────────────────────────────────────
  obtenerCola(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/turnos/cola`);
  }

  obtenerTurnosUsuario(usuarioId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/turnos/usuario/${usuarioId}`);
  }

  crearTurno(usuarioId: number, departamentoId: number, esPrioritario: boolean = false): Observable<any> {
    return this.http.post<any>(`${this.apiBase}/turnos`, { usuarioId, departamentoId, esPrioritario });
  }

  llamarSiguiente(): Observable<any> {
    return this.http.put<any>(`${this.apiBase}/turnos/siguiente/llamar`, {});
  }

  llamarTurno(turnoId: number): Observable<any> {
    return this.http.put<any>(`${this.apiBase}/turnos/${turnoId}/llamar`, {});
  }

  atenderTurno(turnoId: number): Observable<any> {
    return this.http.put<any>(`${this.apiBase}/turnos/${turnoId}/atender`, {});
  }

  cancelarTurno(turnoId: number): Observable<any> {
    return this.http.put<any>(`${this.apiBase}/turnos/${turnoId}/cancelar`, {});
  }

  // ── Compras ──────────────────────────────────────────────────────
  obtenerComprasUsuario(usuarioId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/compras/usuario/${usuarioId}`);
  }

  realizarCompra(usuarioId: number, planId: number, metodoPago: string = 'Tarjeta de Crédito'): Observable<any> {
    return this.http.post<any>(`${this.apiBase}/compras`, { usuarioId, planId, metodoPago });
  }
}
