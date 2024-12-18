import { Component, OnDestroy } from '@angular/core';
import { Subscription, finalize } from 'rxjs';
import { Flight } from '../../model/flight';
import { FlightQuery } from '../../model/flight-query';
import { ResponseError } from '../../model/response-error';
import { Route } from '../../model/route';
import { UserMessagesComponent } from '../../shared/components/user-messages/user-messages.component';
import { FlightsService } from '../../shared/services/flights.service';
import { DisplayFlightsComponent } from './components/display-flights/display-flights.component';
import { SearchFlightsFormComponent } from './components/search-flights-form/search-flights-form.component';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TutorialComponent } from './components/tutorial/tutorial.component';
import { environment } from '../../../environment/environment';

@Component({
  selector: 'app-flight-checker',
  standalone: true,
  imports: [
    SearchFlightsFormComponent,
    DisplayFlightsComponent,
    UserMessagesComponent,
    LoadingSpinnerComponent,
    MatButtonModule,
    MatIconModule,
    TutorialComponent
  ],
  templateUrl: './flight-checker.component.html',
  styleUrl: './flight-checker.component.scss'
})
export class FlightCheckerComponent implements OnDestroy {
  private flightSearchSubscription?: Subscription;
  isLoading = false;
  error?: ResponseError;
  query?: FlightQuery = environment.production ? undefined : {
    routes: [
      {
        origin: "NUE",
        destination: "STN",
      },
      {
        origin: "STN",
        destination: "SDR",
      },
    ],
    startDate: new Date(2024, 2, 1),
    endDate: new Date(2024, 2, 3),
  };
  routes?: Route[] = []
  flights?: Flight[] = environment.production ? undefined : [
    {
      id: 1,
      createdAt: new Date(),
      origin: "NUE",
      destination: "STN",
      departureDate: new Date(2024, 2, 1, 7, 30),
      landingDate: new Date(2024, 2, 1, 10, 30),
      price: 25.0,
      duration: 4,
      airlineName: "RYANAIR"
    },
    {
      id: 1,
      createdAt: new Date(),
      origin: "NUE",
      destination: "STN",
      departureDate: new Date(2024, 2, 1, 13, 30),
      landingDate: new Date(2024, 2, 1, 14, 30),
      price: 15.0,
      duration: 2,
      airlineName: "RYANAIR"
    },
    {
      id: 2,
      createdAt: new Date(),
      origin: "STN",
      destination: "SDR",
      departureDate: new Date(2024, 2, 1, 15, 30),
      landingDate: new Date(2024, 2, 1, 17, 30),
      price: 50.0,
      duration: 2,
      airlineName: "VUELING"
    },
  ];

  constructor(private readonly flightService: FlightsService) { }

  ngOnDestroy(): void {
    this.flightSearchSubscription?.unsubscribe();
  }

  onClearResults(): void {
    this.error = undefined;
    this.flights = undefined;
    this.routes = undefined;
  }

  onSearchFlights(query: FlightQuery): void {
    if (!query) {
      return;
    }

    // Reset data
    this.isLoading = true;
    this.query = query;
    this.error = undefined;

    // Make query
    this.flightSearchSubscription = this.flightService.getFlights(query)
      .pipe(
        finalize(() => this.isLoading = false),
      )
      .subscribe(
        {
          next: (response) => {
            this.flights = response.flights;
            this.routes = response.availableRoutes;
          },
          error: (error: ResponseError) => this.error = error
        }
      )
      ;
  }
}
