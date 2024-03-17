import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionsCheckerComponent } from './connections-checker.component';

describe('ConnectionsCheckerComponent', () => {
  let component: ConnectionsCheckerComponent;
  let fixture: ComponentFixture<ConnectionsCheckerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectionsCheckerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConnectionsCheckerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
