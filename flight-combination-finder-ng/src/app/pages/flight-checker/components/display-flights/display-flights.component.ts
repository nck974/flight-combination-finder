import { Component, Input, OnInit } from '@angular/core';
import { MatDividerModule } from '@angular/material/divider';
import { Flight } from '../../../../model/flight';
import { FlightQuery } from '../../../../model/flight-query';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { TimeFormatPipe } from '../../../../shared/pipes/time-format.pipe';
import { Route } from '../../../../model/route';
import { DateWeekdayPipe } from '../../../../shared/pipes/date-weekday.pipe';
import { FlightEventComponent } from '../flight-event/flight-event.component';

@Component({
  selector: 'app-display-flights',
  standalone: true,
  imports: [
    DateFormatPipe,
    TimeFormatPipe,
    MatDividerModule,
    DateWeekdayPipe,
    FlightEventComponent
  ],
  templateUrl: './display-flights.component.html',
  styleUrl: './display-flights.component.scss'
})
export class DisplayFlightsComponent implements OnInit {
  @Input() query?: FlightQuery;
  @Input() flights?: Flight[];
  @Input() routes?: Route[];

  dates: Date[] = [];
  flightsByDateAndHour: { [date: string]: { [route: string]: (Flight[] | Route[]) } } = {};
  dayWithFlight: { [date: string]: boolean } = {};
  dayMinHour: { [date: string]: number } = {};
  dayMaxHour: { [date: string]: number } = {};

  ngOnInit(): void {
    if (this.query) {
      console.log("On init:")
      console.log(this.query);
    }
    if (this.flights && this.routes) {
      this.groupFlightsByDateAnHour(this.flights, this.routes);
      this.sortAndFilterDates();
    }
  }

  get nrColumns(): number {
    if (this.query) {
      return this.query.routes.length;
    }
    return 0
  }

  get minScreenWidth(): string {
    const size =
      (this.nrColumns + 1) * 6.5 + // Column
      (this.nrColumns) * 1 + // Gap
      2.5 + // hour
      4 + // date
      4; // padding/margin
    console.log(size);
    return `${size}rem`;
  }

  private initializeDayData(dateTimeKey: string, route: string): void {
    if (!this.flightsByDateAndHour[dateTimeKey]) {
      this.flightsByDateAndHour[dateTimeKey] = {};
    }
    if (!this.flightsByDateAndHour[dateTimeKey][route]) {
      this.flightsByDateAndHour[dateTimeKey][route] = [];
    }
  }

  private setFlightTimeBoundaries(flight: Flight, route: string): void {
    const dateKey = `${flight.departureDate.toDateString()}`;

    let departureHour = flight.departureDate.getHours();
    if ((!this.dayMinHour[dateKey] && this.dayMinHour[dateKey] !== 0) || this.dayMinHour[dateKey] > departureHour) {
      this.dayMinHour[dateKey] = departureHour;
    }

    let landingHour = flight.landingDate.getHours() + 1; // Add one to give room for half hours

    // Handle scenario of multi-day flights
    if (flight.landingDate.getDay() != flight.departureDate.getDay()) {
      const landingDateKey = `${flight.landingDate.toDateString()}`;

      this.dayMinHour[landingDateKey] = 0;
      if (!this.dayMaxHour[landingDateKey] || this.dayMaxHour[landingDateKey] < landingHour) {
        this.dayMaxHour[landingDateKey] = landingHour;
      }
      this.dayMaxHour[dateKey] = 23;
    }
    else if (!this.dayMaxHour[dateKey] || this.dayMaxHour[dateKey] < landingHour) {
      this.dayMaxHour[dateKey] = landingHour;
    }
  }

  private groupFlights(flights: Flight[]): void {
    for (const flight of flights) {

      // Save date
      this.dates.push(flight.departureDate);
      this.dates.push(flight.landingDate);

      // Set keys
      const dateTimeKey = `${flight.departureDate.toDateString()}${flight.departureDate.getHours()}`;
      const route = `${flight.origin}-${flight.destination}`;

      // Initialize data if does not exist
      this.initializeDayData(dateTimeKey, route);

      // Write the limits of min departure and max landing
      this.setFlightTimeBoundaries(flight, route)

      // Save data
      this.flightsByDateAndHour[dateTimeKey][route].push(flight);
      this.dayWithFlight[flight.departureDate.toDateString()] = true;
      // Save also the landing date for multi-day flights
      this.dayWithFlight[flight.landingDate.toDateString()] = true;
    }
  }

  private groupRoutes(routes: Route[]): void {
    for (const availableRoute of routes) {

      // Set keys
      const dateTimeKey = `${availableRoute.departureDate.toDateString()}${availableRoute.departureDate.getHours()}`;
      const route = "summary";

      // Initialize data if does not exist
      this.initializeDayData(dateTimeKey, route);

      // Save data
      this.flightsByDateAndHour[dateTimeKey][route].push(availableRoute as Flight);
    }
  }

  groupFlightsByDateAnHour(flights: Flight[], routes: Route[]): void {
    // Reset fields
    this.flightsByDateAndHour = {};
    this.dayWithFlight = {};

    // Loop through all flights and save the flights
    this.groupFlights(flights);
    this.groupRoutes(routes);
  }


  /// All dates obtained from the landing and departure of the flights have to be sorted and
  /// filtered to have an array of dates which only contains the relevant days to be displayed
  sortAndFilterDates() {
    // Remove the duplicates
    this.dates = this.dates.filter((date, index, self) =>
      index === self.findIndex((d) => (
        d.getDate() === date.getDate() &&
        d.getMonth() === date.getMonth() &&
        d.getFullYear() === date.getFullYear()
      ))
    );

    // Sort the uniqueDates array
    this.dates.sort((a, b) => a.getTime() - b.getTime());
  }


  /// In order not to display all hours which do not have relevant content get the range of all
  /// all hours between the given limits
  getHoursRange(start: number, end: number): number[] {
    return Array.from({ length: end - start + 1 }, (_, index) => start + index);
  }

}
