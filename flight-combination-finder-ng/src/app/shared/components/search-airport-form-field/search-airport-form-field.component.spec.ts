import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchAirportFormFieldComponent } from './search-airport-form-field.component';

describe('SearchAirportFormFieldComponent', () => {
  let component: SearchAirportFormFieldComponent;
  let fixture: ComponentFixture<SearchAirportFormFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchAirportFormFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchAirportFormFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
