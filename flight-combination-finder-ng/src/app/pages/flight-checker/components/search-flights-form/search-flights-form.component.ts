import { formatDate } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatError, MatFormFieldModule, MatHint, MatLabel } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { FlightQuery, FlightRoute } from '../../../../model/flight-query';

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
export class SearchFlightsFormComponent {

  @Output() onSearch = new EventEmitter<FlightQuery>();

  private defaultDateRange = 30 * 24 * 60 * 60 * 1000; // 30n days
  defaultStartDate = new Date();
  defaultEndDate = new Date(this.defaultStartDate.getTime() + this.defaultDateRange);

  formQuery = this.formBuilder.group({
    routes: this.formBuilder.array([this.createNewRouteControl("NUE", "STN")]),
    startDate: new FormControl(formatDate(this.defaultStartDate, 'yyyy-MM-dd', 'en')),
    endDate: new FormControl(formatDate(this.defaultEndDate, 'yyyy-MM-dd', 'en')),
  });

  constructor(private formBuilder: FormBuilder, private cdr: ChangeDetectorRef) { }

  get routesControl() {
    return this.formQuery.get("routes") as FormArray;
  }

  private createNewRouteControl(origin?: string, destination?: string): FormGroup {
    return this.formBuilder.group({
      origin: new FormControl(origin),
      destination: new FormControl(destination)
    });
  }

  onAddRoute(): void {
    this.routesControl.push(
      this.formBuilder.group({
        origin: new FormControl(""),
        destination: new FormControl("")
      })
    );
    this.cdr.detectChanges();
  }

  onRemoveRoute(index: number): void {
    this.routesControl.removeAt(index);
  }


  onSubmit() {
    console.log(this.formQuery);
    let routes: FlightRoute[] = []
    for (let routeControl of this.routesControl.controls) {
      routes.push(
        {
          origin: routeControl.value.origin,
          destination: routeControl.value.destination
        }
      )
    }

    const query: FlightQuery = {
      routes: routes,
      startDate: new Date(this.formQuery.value.startDate!),
      endDate: new Date(this.formQuery.value.endDate!),
    };
    console.log("Emitting query...");
    console.log(query);
    this.onSearch.emit(query);
  }
}
