import { TestBed } from '@angular/core/testing';
import { AirportsService } from './airports.service';


describe('AirportsService', () => {
  let service: AirportsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AirportsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
