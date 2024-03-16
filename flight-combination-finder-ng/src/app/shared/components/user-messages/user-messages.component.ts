import { Component, Input, OnInit } from '@angular/core';
import { ResponseError } from '../../../model/response-error';

@Component({
  selector: 'app-user-messages',
  standalone: true,
  imports: [],
  templateUrl: './user-messages.component.html',
  styleUrl: './user-messages.component.scss'
})
export class UserMessagesComponent implements OnInit {
  @Input() error?: ResponseError;
  ngOnInit(): void {
    if (this.error){
      console.log(this.error);
    }
  }
}
