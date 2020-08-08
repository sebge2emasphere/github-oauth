import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryDetailsConfigComponent } from './repository-details-config.component';

describe('RepositoryDetailsConfigComponent', () => {
  let component: RepositoryDetailsConfigComponent;
  let fixture: ComponentFixture<RepositoryDetailsConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryDetailsConfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
