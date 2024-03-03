import { Component, OnDestroy } from '@angular/core';
import { FlightsService } from '../../shared/services/flights.service';
import { SearchFlightsFormComponent } from './components/search-flights-form/search-flights-form.component';
import { DisplayFlightsComponent } from './components/display-flights/display-flights.component';
import { Flight } from '../../model/flight';
import { FlightQuery } from '../../model/flight-query';
import { Subscription, finalize } from 'rxjs';

@Component({
  selector: 'app-flight-checker',
  standalone: true,
  imports: [SearchFlightsFormComponent, DisplayFlightsComponent],
  templateUrl: './flight-checker.component.html',
  styleUrl: './flight-checker.component.scss'
})
export class FlightCheckerComponent implements OnDestroy {
  private flightSearchSubscription?: Subscription;
  isLoading = false;
  query?: FlightQuery = {
    origin: "STD",
    destination: "NUE",
    startDate: new Date(2024, 2, 1),
    endDate: new Date(2024, 2, 3),
  };
  flights?: Flight[] = [
    {
      id: 1,
      createdAt: new Date(),
      origin: "STD",
      destination: "NUE",
      departureDate: new Date(2024, 2, 1, 7, 30),
      landingDate: new Date(2024, 2, 1, 10, 30),
      price: 25.0,
      duration: 3,
    },
    {
      id: 1,
      createdAt: new Date(),
      origin: "STD",
      destination: "NUE",
      departureDate: new Date(2024, 2, 1, 13, 30),
      landingDate: new Date(2024, 2, 1, 14, 30),
      price: 15.0,
      duration: 4,
    },
    {
      id: 2,
      createdAt: new Date(),
      origin: "STD",
      destination: "NUE",
      departureDate: new Date(2024, 2, 1, 15, 30),
      landingDate: new Date(2024, 2, 1, 17, 30),
      price: 50.0,
      duration: 1,
    },
  ];

  getDatesInRange(query: FlightQuery): Date[] {
    const dates = [];
    const currentDate = query.startDate;
    while (currentDate <= query.endDate) {
      dates.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1);
    }
    return dates;
  }

  constructor(private flightService: FlightsService) { }

  ngOnDestroy(): void {
    this.flightSearchSubscription?.unsubscribe();
  }

  onSearchFlights(query: FlightQuery): void {
    if (!query) {
      return;
    }
    this.isLoading = true;
    this.query = query;
    this.flightSearchSubscription = this.flightService.getFlights(query)
      .pipe(
        finalize(() => this.isLoading = false)
      )
      .subscribe(
        (flights) => {
          console.log("Displaying new flights...");
          this.flights = flights;
        }
      );
  }
}
