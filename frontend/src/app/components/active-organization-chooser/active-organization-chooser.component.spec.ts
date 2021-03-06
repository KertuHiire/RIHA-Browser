import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveOrganizationChooserComponent } from './active-organization-chooser.component';

describe('ActiveOrganizationChooserComponent', () => {
  let component: ActiveOrganizationChooserComponent;
  let fixture: ComponentFixture<ActiveOrganizationChooserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActiveOrganizationChooserComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActiveOrganizationChooserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
