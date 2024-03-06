import { ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { FlightQuery, FlightRoute } from '../../../../model/flight-query';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-search-flights-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './search-flights-form.component.html',
  styleUrl: './search-flights-form.component.scss'
})
export class SearchFlightsFormComponent {

  @Output() onSearch = new EventEmitter<FlightQuery>();

  defaultStartDate = new Date();
  defaultEndDate = new Date();

  formQuery = this.formBuilder.group({
    routes: this.formBuilder.array([this.createNewRouteControl("NUE", "STN")]),
    startDate: new FormControl(formatDate(this.defaultStartDate, 'yyyy-MM-dd', 'en')),
    endDate: new FormControl(formatDate(this.defaultEndDate, 'yyyy-MM-dd', 'en')),
  });

  constructor(private formBuilder: FormBuilder, private cdr: ChangeDetectorRef) {
    this.defaultEndDate.setDate(this.defaultEndDate.getDate() + 30);
  }

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
