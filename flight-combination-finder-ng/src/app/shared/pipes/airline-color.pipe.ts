import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'airlineColor',
  standalone: true
})
export class AirlineColorPipe implements PipeTransform {

  transform(airlineName: string | undefined): string {
    if (airlineName == "VUELING") {
      return "#FFCC00";
    }
    if (airlineName == "RYANAIR") {
      return "#0d49c0";
    }
    return "#1c1a6c";
  }
}
