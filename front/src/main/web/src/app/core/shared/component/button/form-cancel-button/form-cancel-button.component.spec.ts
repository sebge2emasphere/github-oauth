import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormCancelButtonComponent } from './form-cancel-button.component';

describe('FormCancelButtonComponent', () => {
  let component: FormCancelButtonComponent;
  let fixture: ComponentFixture<FormCancelButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FormCancelButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormCancelButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
