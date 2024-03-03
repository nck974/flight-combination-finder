import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FlightCheckerComponent } from './pages/flight-checker/flight-checker.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FlightCheckerComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'flight-combination-finder-ng';
}
