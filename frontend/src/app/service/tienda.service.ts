import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Servicio de la tienda: consulta planes al backend (/api/planes).
 */
@Injectable({
  providedIn: 'root'
})
export class TiendaService {
  private apiUrl = '/api/planes';

  constructor(private http: HttpClient) {}

  obtenerTodos(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  obtenerPorTipo(tipo: 'MOVIL' | 'HOGAR' | 'ENTRETENIMIENTO'): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/tipo/${tipo}`);
  }

  obtenerPorId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}
