import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateWeekday',
  standalone: true
})
export class DateWeekdayPipe implements PipeTransform {

  transform(value: Date, ...args: any[]): any {
    const datePipe = new DatePipe('en-US');
    return datePipe.transform(value, 'EEE');
  }
}
