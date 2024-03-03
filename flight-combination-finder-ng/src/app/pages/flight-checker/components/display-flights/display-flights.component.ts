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
  flightsByDateAndHour: { [date: string]: Flight[] } = {};
  dayWithFlight: { [date: string]: boolean } = {};

  ngOnInit(): void {
    if (this.query) {
      console.log("On init:")
      console.log(this.query);
      this.setDatesInRange(this.query);
    }
    if (this.flights) {
      this.groupFlightsByDateAnHour(this.flights);
    }
  }


  groupFlightsByDateAnHour(flights: Flight[]): void {
    // Reset fields
    this.flightsByDateAndHour = {};
    this.dayWithFlight = {};

    // Loop through all flights to setup the form
    for (const flight of flights) {
      const dateTimeKey = `${flight.departureDate.toDateString()}${flight.departureDate.getHours()}`;
      if (!this.flightsByDateAndHour[dateTimeKey]) {
        this.flightsByDateAndHour[dateTimeKey] = [];
      }
      this.flightsByDateAndHour[dateTimeKey].push(flight);
      this.dayWithFlight[flight.departureDate.toDateString()] = true;
      // Save also the landing date for multi-day flights
      this.dayWithFlight[flight.landingDate.toDateString()] = true;
    }
    console.log(this.flightsByDateAndHour)
  }

  setDatesInRange(query: FlightQuery): void {
    this.dates = [];
    let currentDate = query.startDate;
    const endDate = query.endDate;
    while (currentDate.getTime() <= endDate.getTime()) {
      this.dates.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1);
    }
  }

}
