import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayFlightsComponent } from './display-flights.component';

describe('DisplayFlightsComponent', () => {
  let component: DisplayFlightsComponent;
  let fixture: ComponentFixture<DisplayFlightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisplayFlightsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DisplayFlightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
