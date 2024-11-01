import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatError, MatFormFieldModule, MatHint, MatLabel } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RoutesQuery } from '../../../../model/routes-query';


@Component({
  selector: 'app-search-connections-form',
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
  templateUrl: './search-connections-form.component.html',
  styleUrl: './search-connections-form.component.scss'
})
export class SearchConnectionsFormComponent {

  @Output() onSearch = new EventEmitter<RoutesQuery>();

  formQuery = this.formBuilder.group({
    origin: new FormControl("NUE"),
    destination: new FormControl("SDR"),
  });

  constructor(private readonly formBuilder: FormBuilder) { }

  onSubmit() {
    if (!this.formQuery.valid) {
      return;
    }

    const query: RoutesQuery = {
      origin: this.formQuery.value.origin!,
      destination: this.formQuery.value.destination!,
      maxNrConnections: 2,
    };
    console.debug("Emitting query...");
    console.debug(query);
    this.onSearch.emit(query);
  }
}
