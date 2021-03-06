import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { MaterialModule } from 'src/app/common/material.module';
import { EventStoreState, selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import CountriesGetResponse from '../models/countries-get-response.model';
import CountryService from '../services/country.service';
import { CountryFormComponent } from './country-form.component';

describe('CountryFormComponent', () => {
  let component: CountryFormComponent;
  let fixture: ComponentFixture<CountryFormComponent>;
  let store: Store<AppStoreState>;
  let countryService: CountryService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CountryFormComponent],
      imports: [
        StoreModule.forRoot({}),
        HttpClientTestingModule,
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [Store],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CountryFormComponent);
    component = fixture.componentInstance;

    store = TestBed.inject(Store);
    countryService = TestBed.inject(CountryService);
  });

  describe('form validation', () => {
    describe('add event', () => {
      beforeEach(() => {
        const state: EventStoreState = {
          ids: [],
          entities: {},
          eventToUpdate: null,
          lastUpdatedEvent: null,
          lastDeletedEvent: null,
          isLoading: false,
          endDateOfEvents: new Date(),
          maxRadiusOfEventsDetection: null,
          errorMessages: [],
        };
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(state);
          }
        });
        const countriesGetResponse: CountriesGetResponse = {
          content: [
            {
              id: 1,
              name: 'country',
            },
            {
              id: 2,
              name: 'country 2',
            },
          ],
        };
        spyOn(countryService, 'getAll').and.returnValue(
          of(countriesGetResponse)
        );

        fixture.detectChanges();
        component.ngOnInit();
      });

      it('which is valid should be valid', () => {
        component.name.setValue('country');

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.name.valid).toBeTruthy();
      });

      it('with empty name should be invalid', () => {
        component.name.setValue('');

        const name = component.name;
        const errors = name.errors;
        expect(errors.notBlank).toBeTruthy();
      });

      it('with blank name should be invalid', () => {
        component.name.setValue('    ');

        const name = component.name;
        const errors = name.errors;
        expect(errors.notBlank).toBeTruthy();
      });
    });

    describe('when update', () => {
      it('valid event should be valid', () => {
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
            numberOfPerpetratorsFatalities: 3,
            totalNumberOfInjured: 14,
            numberOfPerpetratorsInjured: 4,
            valueOfPropertyDamage: 2000,
          },
        };
        const stateWithEventToUpdate: EventStoreState = {
          ids: [],
          entities: {},
          eventToUpdate,
          lastUpdatedEvent: null,
          lastDeletedEvent: null,
          isLoading: false,
          endDateOfEvents: new Date(),
          maxRadiusOfEventsDetection: null,
          errorMessages: [],
        };
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(stateWithEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeTruthy();
        expect(component.name.value).toBe(
          eventToUpdate.city.province.country.name
        );
      });

      it('invalid event should be invalid', () => {
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
            numberOfPerpetratorsFatalities: -3,
            totalNumberOfInjured: -14,
            numberOfPerpetratorsInjured: -4,
            valueOfPropertyDamage: -2000,
          },
        };
        const stateWithInvalidEventToUpdate: EventStoreState = {
          ids: [],
          entities: {},
          eventToUpdate: invalidEventToUpdate,
          lastUpdatedEvent: null,
          lastDeletedEvent: null,
          isLoading: false,
          endDateOfEvents: new Date(),
          maxRadiusOfEventsDetection: null,
          errorMessages: [],
        };
        spyOn(store, 'select').and.callFake((selector) => {
          if (selector === selectEventToUpdate) {
            return of(stateWithInvalidEventToUpdate.eventToUpdate);
          }
        });

        fixture.detectChanges();
        component.ngOnInit();

        expect(component.formGroup.valid).toBeFalsy();
        expect(component.name.valid).toBeFalsy();
      });
    });
  });
});
