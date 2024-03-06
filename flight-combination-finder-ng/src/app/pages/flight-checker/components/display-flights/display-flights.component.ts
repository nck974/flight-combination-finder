import { Component, Input, OnInit } from '@angular/core';
import { Flight } from '../../../../model/flight';
import { FlightQuery } from '../../../../model/flight-query';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { TimeFormatPipe } from '../../../../shared/pipes/time-format.pipe';

@Component({
  selector: 'app-display-flights',
  standalone: true,
  imports: [DateFormatPipe, TimeFormatPipe],
  templateUrl: './display-flights.component.html',
  styleUrl: './display-flights.component.scss'
})
export class DisplayFlightsComponent implements OnInit {
  @Input() query?: FlightQuery;
  @Input() flights?: Flight[];

  dates: Date[] = [];
  hours = Array.from({ length: 24 }, (_, index) => `${index}`);
  flightsByDateAndHour: { [date: string]: { [route: string]: Flight[] } } = {};
  dayWithFlight: { [date: string]: boolean } = {};
  dayMinHour: { [date: string]: number } = {};
  dayMaxHour: { [date: string]: number } = {};

  ngOnInit(): void {
    if (this.query) {
      console.log("On init:")
      console.log(this.query);
    }
    if (this.flights) {
      this.groupFlightsByDateAnHour(this.flights);
      this.sortAndFilterDates();
    }
  }

  get nrColumns(): number {
    if (this.query) {
      return this.query.routes.length;
    }
    return 0
  }

  groupFlightsByDateAnHour(flights: Flight[]): void {
    // Reset fields
    this.flightsByDateAndHour = {};
    this.dayWithFlight = {};

    // Loop through all flights and save the flights
    for (const flight of flights) {

      // Save date
      this.dates.push(flight.departureDate);
      this.dates.push(flight.landingDate);

      // Set keys
      const dateTimeKey = `${flight.departureDate.toDateString()}${flight.departureDate.getHours()}`;
      const dateKey = `${flight.departureDate.toDateString()}`;
      const route = `${flight.origin}-${flight.destination}`;

      // Initialize data
      if (!this.flightsByDateAndHour[dateTimeKey]) {
        this.flightsByDateAndHour[dateTimeKey] = {};
      }
      if (!this.flightsByDateAndHour[dateTimeKey][route]) {
        this.flightsByDateAndHour[dateTimeKey][route] = [];
      }

      let departureHour = flight.departureDate.getHours();
      if (!this.dayMinHour[dateKey] || this.dayMinHour[dateTimeKey] > departureHour) {
        this.dayMinHour[dateKey] = departureHour
      }

      let landingHour = flight.landingDate.getHours() + 1; // Add one to give room for half hours

      // Handle scenario of multi-day flights
      if (flight.landingDate.getDay() != flight.departureDate.getDay()) {
        console.log(`Setting with ${flight.landingDate.toDateString()} hour ${landingHour}`);

        this.dayMinHour[flight.landingDate.toDateString()] = 0;
        if (!this.dayMaxHour[flight.landingDate.toDateString()] || this.dayMaxHour[flight.landingDate.toDateString()] < landingHour) {
          this.dayMaxHour[flight.landingDate.toDateString()] = landingHour;
        }
        this.dayMaxHour[dateKey] = 23;
      }
      else if (!this.dayMaxHour[dateKey] || this.dayMaxHour[dateKey] < landingHour) {
        console.log(`Setting with ${dateKey} max-hour ${landingHour}`);
        this.dayMaxHour[dateKey] = landingHour;
      } else {
        console.log(`Not setting in ${dateKey} (${route}) max-hour ${landingHour}`);

      }

      // Save data
      this.flightsByDateAndHour[dateTimeKey][route].push(flight);
      this.dayWithFlight[flight.departureDate.toDateString()] = true;
      // Save also the landing date for multi-day flights
      this.dayWithFlight[flight.landingDate.toDateString()] = true;
    }
    console.log("this.flightsByDateAndHour");
    console.log(this.flightsByDateAndHour)
    console.log("this.dayMinHour");
    console.log(this.dayMinHour)
    console.log("this.dayMaxHour");
    console.log(this.dayMaxHour)
  }


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


  getHoursRange(start: number, end: number): number[] {
    return Array.from({ length: end - start + 1 }, (_, index) => start + index);
  }

}
