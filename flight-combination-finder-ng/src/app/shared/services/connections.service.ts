import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environment/environment';
import { RoutesGraph } from '../../model/graph/routes-graph';
import { ResponseError } from '../../model/response-error';
import { RoutesQuery } from '../../model/routes-query';

@Injectable({
  providedIn: 'root'
})
export class ConnectionsService {

  private readonly backendUrl = environment.backendUrl;
  private readonly url = `${this.backendUrl}/airports/routes/graph`;

  constructor(private httpClient: HttpClient) { }

  getFlightConnectionsGraph(query: RoutesQuery): Observable<RoutesGraph> {

    let data = JSON.stringify(query);
    const headers = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });

    return this.httpClient.post<RoutesGraph>(this.url, data, { headers: headers })
      .pipe(
        map(response => ({
          ...response
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
