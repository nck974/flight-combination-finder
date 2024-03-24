import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionsTutorialComponent } from './connections-tutorial.component';

describe('ConnectionsTutorialComponent', () => {
  let component: ConnectionsTutorialComponent;
  let fixture: ComponentFixture<ConnectionsTutorialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectionsTutorialComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConnectionsTutorialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
