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
  flightsByDate: { [date: string]: Flight[] } = {};
  flightsByDateAndHour: { [date: string]: Flight[] } = {};

  ngOnInit(): void {
    if (this.query) {
      console.log("On init:")
      console.log(this.query);
      this.setDatesInRange(this.query);
    }
    if (this.flights) {
      this.groupFlightsByDate(this.flights);
      this.groupFlightsByDateAnHour(this.flights);
    }
  }

  groupFlightsByDate(flights: Flight[]): void {
    for (const flight of flights) {
      const dateKey = flight.departureDate.toDateString();
      if (!this.flightsByDate[dateKey]) {
        this.flightsByDate[dateKey] = [];
      }
      this.flightsByDate[dateKey].push(flight);
    }
    // console.log(this.flightsByDate)
  }

  groupFlightsByDateAnHour(flights: Flight[]): void {
    for (const flight of flights) {
      const dateKey = `${flight.departureDate.toDateString()}${flight.departureDate.getHours()}`;
      if (!this.flightsByDateAndHour[dateKey]) {
        this.flightsByDateAndHour[dateKey] = [];
      }
      this.flightsByDateAndHour[dateKey].push(flight);
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
