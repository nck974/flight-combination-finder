import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeFormat',
  standalone: true
})
export class TimeFormatPipe implements PipeTransform {

  transform(value: Date): any {
    const datePipe = new DatePipe('en-US');
    return datePipe.transform(value, 'HH:mm');
  }

}
