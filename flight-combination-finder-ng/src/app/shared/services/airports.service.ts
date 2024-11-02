import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../../environment/environment';
import { Airport } from '../../model/airport';
import { ResponseError } from '../../model/response-error';

@Injectable({
  providedIn: 'root'
})
export class AirportsService {

  private readonly url = `${environment.backendUrl}/airports`;

  constructor(private readonly httpClient: HttpClient) { }

  searchAirports(query: string): Observable<Airport[]> {

    const headers = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });

    const params = `airport=${query}`;

    return this.httpClient.get<Airport[]>(`${this.url}/search?${params}`, { headers: headers })
      .pipe(
        catchError(
          (error: HttpErrorResponse) => {
            const responseError: ResponseError = {
              status: error.status,
              code: error.error.code,
              message: error.error.message || 'An unexpected error occurred.'
            };
            return throwError(() => responseError);
          }
        )
      );
  }

}
