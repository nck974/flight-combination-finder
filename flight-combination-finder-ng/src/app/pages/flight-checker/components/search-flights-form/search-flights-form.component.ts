import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FlightQuery } from '../../../../model/flight-query';

@Component({
  selector: 'app-search-flights-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-flights-form.component.html',
  styleUrl: './search-flights-form.component.scss'
})
export class SearchFlightsFormComponent {

  @Output() onSearch = new EventEmitter<FlightQuery>();
  formData = {
    origin: 'NUE',
    destination: 'STN',
    startDate: new Date(2024, 2, 1),
    endDate: new Date(2024, 2, 31)
  };

  onSubmit() {
    console.log(this.formData);
    const query: FlightQuery = {
      origin: this.formData.origin,
      destination: this.formData.destination,
      startDate: new Date(this.formData.startDate),
      endDate: new Date(this.formData.endDate),
    };
    console.log("Emitting query...");
    console.log(query);
    this.onSearch.emit(query);
  }
}
