import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tutorial',
  standalone: true,
  imports: [
    MatButtonModule
  ],
  templateUrl: './tutorial.component.html',
  styleUrl: './tutorial.component.scss'
})
export class TutorialComponent {

  constructor(private router: Router) { }

  onNavigateToCombinations(): void {
    this.router.navigateByUrl("/connections");
  }
}
