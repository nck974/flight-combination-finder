import { formatDate } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatError, MatFormFieldModule, MatHint, MatLabel } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { FlightQuery, FlightRoute } from '../../../../model/flight-query';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-search-flights-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatLabel,
    MatHint,
    MatError,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './search-flights-form.component.html',
  styleUrl: './search-flights-form.component.scss'
})
export class SearchFlightsFormComponent implements OnInit, OnDestroy {

  @Output() onSearch = new EventEmitter<FlightQuery>();

  private parametersSubscription?: Subscription;

  private defaultOrigin = "NUE";
  private defaultDestination = "SDR";
  private defaultDateRange = 30 * 24 * 60 * 60 * 1000; // 30 days
  defaultStartDate = new Date();
  defaultEndDate = new Date(this.defaultStartDate.getTime() + this.defaultDateRange);

  formQuery: FormGroup = this.formBuilder.group({
    routes: this.formBuilder.array([]),
    startDate: new FormControl(formatDate(this.defaultStartDate, 'yyyy-MM-dd', 'en')),
    endDate: new FormControl(formatDate(this.defaultEndDate, 'yyyy-MM-dd', 'en')),
  });

  get routesControl() {
    return this.formQuery.get("routes") as FormArray;
  }

  constructor(
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.setUrlParameters();
  }

  ngOnDestroy(): void {
    this.parametersSubscription?.unsubscribe();
  }

  private setUrlParameters(): void {
    this.parametersSubscription = this.route.queryParams.subscribe(params => {
      console.log(params);
      // Angular returns an array or a string depending on the number of parameters
      // therefore it has to be checked first if there are multiple routes in the url
      const originQueryString = params["origin"];
      const destinationQueryString = params["destination"];

      if (!originQueryString || !destinationQueryString) {
        this.setDefaultRoute();
        return;
      }

      const origins: string[] = Array.isArray(originQueryString) ? originQueryString : [originQueryString];
      const destinations: string[] = Array.isArray(destinationQueryString) ? destinationQueryString : [destinationQueryString];

      if (origins.length != destinations.length) {
        this.setDefaultRoute();
        return;
      }

      origins.forEach((origin, index) => {
        this.onAddRoute(origin, destinations[index]);
      });

      // Automatically get results
      const submitSearch = params["submit"];
      if (submitSearch === "true") {
        this.onSubmit();
      }
    });
  }

  private setDefaultRoute() {
    this.onAddRoute(this.defaultOrigin, this.defaultDestination)

  }

  private getAllRoutes(): FlightRoute[] {
    let routes: FlightRoute[] = []
    for (let routeControl of this.routesControl.controls) {
      routes.push(
        {
          origin: routeControl.value.origin,
          destination: routeControl.value.destination
        }
      )
    }
    return routes;
  }

  onAddRoute(origin?: string, destination?: string): void {
    if (!origin) {
      origin = "";
    }
    if (!destination) {
      destination = "";
    }
    this.routesControl.push(
      this.formBuilder.group({
        origin: new FormControl(origin),
        destination: new FormControl(destination)
      })
    );
    // Notify the changes to prevent exception
    this.cdr.detectChanges();
  }

  onRemoveRoute(index: number): void {
    this.routesControl.removeAt(index);
  }

  onSubmit(): void {
    if (!this.formQuery.valid) {
      return;
    }

    const query: FlightQuery = {
      routes: this.getAllRoutes(),
      startDate: new Date(this.formQuery.value.startDate!),
      endDate: new Date(this.formQuery.value.endDate!),
    };
    console.debug("Emitting query...");
    console.debug(query);
    this.onSearch.emit(query);
  }
}
