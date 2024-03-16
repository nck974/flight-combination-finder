import { Component, Input } from '@angular/core';
import { TimeFormatPipe } from '../../../../shared/pipes/time-format.pipe';

@Component({
  selector: 'app-flight-event',
  standalone: true,
  imports: [TimeFormatPipe],
  templateUrl: './flight-event.component.html',
  styleUrl: './flight-event.component.scss'
})
export class FlightEventComponent {
  @Input({ required: true }) backgroundColor!: string;
  @Input({ required: true }) duration!: number;
  @Input({ required: true }) hourIndex!: number;
  @Input({ required: true }) price!: number;
  @Input({ required: true }) departureDate!: Date;
  @Input({ required: true }) landingDate!: Date;
}
