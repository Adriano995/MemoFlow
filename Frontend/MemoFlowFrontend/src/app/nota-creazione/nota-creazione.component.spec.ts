import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotaCreazioneComponent } from './nota-creazione.component';

describe('NotaCreazione', () => {
  let component: NotaCreazioneComponent;
  let fixture: ComponentFixture<NotaCreazioneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotaCreazioneComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotaCreazioneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
