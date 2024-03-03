import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlightCheckerComponent } from './flight-checker.component';

describe('FlightCheckerComponent', () => {
  let component: FlightCheckerComponent;
  let fixture: ComponentFixture<FlightCheckerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightCheckerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FlightCheckerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
