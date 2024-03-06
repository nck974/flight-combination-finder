import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, tap } from 'rxjs';
import { Flight } from '../../model/flight';
import { FlightQuery } from '../../model/flight-query';

@Injectable({
  providedIn: 'root'
})
export class FlightsService {

  private url = "http://localhost:8080/flights/test";

  constructor(private httpClient: HttpClient) { }

  // Ge the hours duration as an integer rounded to the top
  private setFlightDuration(flights: Flight[]): Flight[] {
    flights.map(flight => {
      flight.departureDate = new Date(flight.departureDate);
      flight.landingDate = new Date(flight.landingDate);
      let landingHour = flight.landingDate.getHours();
      let departureHour = flight.departureDate.getHours();

      // Normal time difference
      if (departureHour <= landingHour) {
        flight.duration = landingHour - departureHour;
      } 
      // Multi day flights
      else {
        flight.duration = landingHour + 24 - departureHour;
      }

      // Round up if not sharp to the minute
      if (flight.departureDate.getMinutes() > 0) {
        flight.duration = flight.duration + 1;
      }

      return flight;
    })
    return flights;
  }

  getFlights(query: FlightQuery): Observable<Flight[]> {

    let data = JSON.stringify(query);
    const headers = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });

    return this.httpClient.post<Flight[]>(this.url, data, { headers: headers })
      .pipe(
        map((flights) => {
          const mappedFlights: Flight[] = flights.map((flight) => ({ ...flight }));
          return this.setFlightDuration(mappedFlights);
        }),
        tap((flights) => console.log(flights)),
        // catchError(this.errorHandler.handleError<Page<Item>>("getItems", this.createEmptyPage()
        // )
        // ) // TODO: Catch error messages
      );
  }
}
