import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserModificaComponent } from './user-modifica.component';

describe('UserModificaComponent', () => {
  let component: UserModificaComponent;
  let fixture: ComponentFixture<UserModificaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserModificaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserModificaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
