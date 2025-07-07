import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotaModificaComponent } from './nota-modifica.component';
import it from '@angular/common/locales/it';
import { describe, beforeEach } from 'node:test';

describe('NotaModifica', () => {
  let component: NotaModificaComponent;
  let fixture: ComponentFixture<NotaModificaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotaModificaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotaModificaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  //it('should create', () => {
   // expect(component).toBeTruthy();
  //});
});
