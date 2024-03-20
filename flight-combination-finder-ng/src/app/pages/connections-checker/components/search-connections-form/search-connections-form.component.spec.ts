import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchConnectionsFormComponent } from './search-connections-form.component';

describe('SearchConnectionsFormComponent', () => {
  let component: SearchConnectionsFormComponent;
  let fixture: ComponentFixture<SearchConnectionsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchConnectionsFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SearchConnectionsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
