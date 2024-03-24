import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionsGraphComponent } from './connections-graph.component';

describe('ConnectionsGraphComponent', () => {
  let component: ConnectionsGraphComponent;
  let fixture: ComponentFixture<ConnectionsGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectionsGraphComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConnectionsGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
