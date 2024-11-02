import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatError, MatFormFieldModule, MatHint, MatLabel } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { catchError, debounceTime, distinctUntilChanged, filter, finalize, Observable, of, switchMap } from 'rxjs';
import { Airport } from '../../../model/airport';
import { AirportsService } from '../../services/airports.service';

@Component({
  selector: 'app-search-airport-form-field',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatLabel,
    MatHint,
    MatError,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatAutocompleteModule,
  ],
  templateUrl: './search-airport-form-field.component.html',
  styleUrl: './search-airport-form-field.component.scss'
})
export class SearchAirportFormFieldComponent {

  @Input({ required: true }) label!: string;
  @Input({ required: true }) control!: FormControl;
  isLoading = false;
  foundAirports$?: Observable<Airport[]>;

  constructor(private readonly airportService: AirportsService) { }

  ngOnInit(): void {
    this.foundAirports$ = this.control.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      filter((query: string | null) => query != null && query?.length >= 3),
      switchMap(value => {
        if (typeof value === 'string' && value) {
          this.isLoading = true;
          return this.airportService.searchAirports(value).pipe(
            catchError(_ => {
              return of([]);
            }),
            finalize(() => this.isLoading = false)
          );
        }
        return [];
      })
    )
  }


}
