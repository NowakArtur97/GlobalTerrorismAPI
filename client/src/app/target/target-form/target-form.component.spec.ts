import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { EventStoreState, selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import { TargetFormComponent } from './target-form.component';

describe('TargetFormComponent', () => {
  let component: TargetFormComponent;
  let fixture: ComponentFixture<TargetFormComponent>;
  let store: Store<AppStoreState>;
  const eventToUpdate = {
    id: 6,
    summary: 'summary',
    motive: 'motive',
    date: new Date(),
    isPartOfMultipleIncidents: false,
    isSuccessful: true,
    isSuicidal: false,
    target: {
      id: 3,
      target: 'target',
      countryOfOrigin: { id: 1, name: 'country' },
    },
    city: {
      id: 4,
      name: 'city',
      latitude: 20,
      longitude: 10,
      province: {
        id: 2,
        name: 'province',
        country: { id: 1, name: 'country' },
      },
    },
    victim: {
      id: 5,
      totalNumberOfFatalities: 11,
      numberOfPerpetratorFatalities: 3,
      totalNumberOfInjured: 14,
      numberOfPerpetratorInjured: 4,
      valueOfPropertyDamage: 2000,
    },
  };
  const invalidEventToUpdate = {
    id: 6,
    summary: ' ',
    motive: ' ',
    date: new Date(Date.now() + 100000),
    isPartOfMultipleIncidents: null,
    isSuccessful: null,
    isSuicidal: null,
    target: {
      id: 3,
      target: ' ',
      countryOfOrigin: { id: 1, name: ' ' },
    },
    city: {
      id: 4,
      name: ' ',
      latitude: -2000,
      longitude: -1000,
      province: {
        id: 2,
        name: ' ',
        country: { id: 1, name: ' ' },
      },
    },
    victim: {
      id: 5,
      totalNumberOfFatalities: -11,
      numberOfPerpetratorFatalities: -3,
      totalNumberOfInjured: -14,
      numberOfPerpetratorInjured: -4,
      valueOfPropertyDamage: -2000,
    },
  };
  const initialState: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: null,
    lastUpdatedEvent: null,
    isLoading: false,
  };
  const initialStateWithEventToUpdate: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate,
    lastUpdatedEvent: null,
    isLoading: false,
  };
  const initialStateWithInvalidEventToUpdate: EventStoreState = {
    ids: [],
    entities: {},
    eventToUpdate: invalidEventToUpdate,
    lastUpdatedEvent: null,
    isLoading: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TargetFormComponent],
      imports: [
        StoreModule.forRoot({}),
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TargetFormComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
  });

  describe('form validation', () => {
    describe('when add event', () => {
      beforeEach(() => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(initialState.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();
      });

      it('with empty target should be invalid', () => {
        component.target.setValue('');

        const target = component.target;
        const errors = target.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank target should be invalid', () => {
        component.target.setValue('    ');

        const target = component.target;
        const errors = target.errors;
        expect(errors.notBlank).toBeTruthy();
      });
    });

    describe('when update', () => {
      it('valid event should be valid', () => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(initialStateWithEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.target.value).toBe(eventToUpdate.target.target);
      });

      it('invalid event should be invalid', () => {
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(initialStateWithInvalidEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeFalsy();
        expect(component.target.valid).toBeFalsy();
      });
    });
  });
});
