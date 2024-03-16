import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlightEventComponent } from './flight-event.component';

describe('FlightEventComponent', () => {
  let component: FlightEventComponent;
  let fixture: ComponentFixture<FlightEventComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightEventComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FlightEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
