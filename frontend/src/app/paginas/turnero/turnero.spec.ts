import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Turnero } from './turnero';

describe('Turnero', () => {
  let component: Turnero;
  let fixture: ComponentFixture<Turnero>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Turnero]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Turnero);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
