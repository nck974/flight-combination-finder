import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, tap } from 'rxjs';
import { Flight } from '../../model/flight';
import { FlightQuery } from '../../model/flight-query';
import { environment } from '../../../environment/environment';
import { FlightsResponse } from '../../model/flights-response';
import { Route } from '../../model/route';

@Injectable({
  providedIn: 'root'
})
export class FlightsService {

  private backendUrl = environment.backendUrl;
  private url = `${this.backendUrl}/flights`;

  constructor(private httpClient: HttpClient) { }

  private calculateFlightDuration(departureDate: Date, landingDate: Date): number {
    let landingHour = landingDate.getHours();
    let departureHour = departureDate.getHours();

    let duration = 0;
    // Normal time difference
    if (departureHour <= landingHour) {
      duration = landingHour - departureHour;
    }
    // Multi day flights
    else {
      duration = landingHour + 24 - departureHour;
    }
    // Round up if not sharp to the minute
    if (departureDate.getMinutes() > 0) {
      duration = duration + 1;
    }

    // Prevent no duration flights as the text can not be read
    if (duration < 1){
      duration = 1;
    }

    return duration;
  }

  // Ge the hours duration as an integer rounded to the top
  private setFlightDuration(flights: Flight[]): Flight[] {
    flights.map(flight => {
      flight.departureDate = new Date(flight.departureDate);
      flight.landingDate = new Date(flight.landingDate);
      flight.duration = this.calculateFlightDuration(flight.departureDate, flight.landingDate);
      return flight;
    })
    return flights;
  }

  private setRoutesDuration(routes: Route[]): Route[] {
    routes.map(route => {
      route.departureDate = new Date(route.departureDate);
      route.landingDate = new Date(route.landingDate);
      route.duration = this.calculateFlightDuration(route.departureDate, route.landingDate);
      return route;
    })
    return routes;
  }

  getFlights(query: FlightQuery): Observable<FlightsResponse> {

    let data = JSON.stringify(query);
    const headers = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });

    return this.httpClient.post<FlightsResponse>(this.url, data, { headers: headers })
      .pipe(
        map((response) => {
          const flights: Flight[] = response.flights.map((flight) => ({ ...flight }));
          response.flights = this.setFlightDuration(flights);

          const availableRoutes: Route[] = response.availableRoutes.map((route) => ({ ...route }));
          response.availableRoutes = this.setRoutesDuration(availableRoutes);

          return response;
        }),
        tap((flights) => console.log(flights)),
        // catchError(this.errorHandler.handleError<Page<Item>>("getItems", this.createEmptyPage()
        // )
        // ) // TODO: Catch error messages
      );
  }
}
