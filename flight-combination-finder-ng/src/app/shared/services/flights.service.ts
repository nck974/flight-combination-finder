import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environment/environment';
import { Flight } from '../../model/flight';
import { FlightQuery } from '../../model/flight-query';
import { FlightsResponse } from '../../model/flights-response';
import { ResponseError } from '../../model/response-error';
import { Route } from '../../model/route';

@Injectable({
  providedIn: 'root'
})
export class FlightsService {

  private readonly backendUrl = environment.backendUrl;
  private readonly url = `${this.backendUrl}/flights`;

  constructor(private httpClient: HttpClient) { }

  private convertDates(flights: (Flight | Route)[]): (Flight | Route)[] {
    return flights.map(item => {
      item.departureDate = new Date(item.departureDate);
      item.landingDate = new Date(item.landingDate);
      return item;
    });
  }

  getFlights(query: FlightQuery): Observable<FlightsResponse> {

    let data = JSON.stringify(query);
    const headers = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });

    return this.httpClient.post<FlightsResponse>(this.url, data, { headers: headers })
      .pipe(
        map(response => ({
          ...response,
          flights: this.convertDates(response.flights) as Flight[],
          availableRoutes: this.convertDates(response.availableRoutes) as Route[]
        })),
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
