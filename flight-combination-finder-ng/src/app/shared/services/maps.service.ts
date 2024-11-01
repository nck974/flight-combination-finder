import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapsService {

  constructor(private readonly http: HttpClient) { }

  getWorldMap(): Observable<any> {
    return this.http.get('assets/maps/world.json');
  }
}
