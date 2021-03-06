import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Store, StoreModule } from '@ngrx/store';
import { of, ReplaySubject, throwError } from 'rxjs';

import EventsGetResponse from '../../models/events-get-response.model';
import EventService from '../../services/event.service';
import * as EventActions from '../event.actions';
import EventEffects from '../event.effects';

describe('EventEffects', () => {
  let eventEffects: EventEffects;
  let actions$: ReplaySubject<any>;
  let eventService: EventService;

  const event = {
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
  const errorResponse = new HttpErrorResponse({
    error: {
      errors: [
        { errors: ['Error message.'], status: 401, timestamp: new Date() },
      ],
    },
    headers: new HttpHeaders('headers'),
    status: 401,
    statusText: 'OK',
    url: 'http://localhost:8080/api/v1',
  });

  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [
        EventEffects,
        Store,
        provideMockActions(() => actions$),
        {
          provide: EventService,
          useValue: jasmine.createSpyObj('eventService', [
            'getAll',
            'get',
            'add',
            'update',
            'delete',
            'deleteAll',
          ]),
        },
      ],
    })
  );

  beforeEach(() => {
    eventEffects = TestBed.inject(EventEffects);
    eventService = TestBed.inject(EventService);
  });

  describe('fetchEvents$', () => {
    const event1 = {
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
    const event2 = {
      id: 12,
      summary: 'summary 2',
      motive: 'motive 2',
      date: new Date(),
      isPartOfMultipleIncidents: true,
      isSuccessful: false,
      isSuicidal: true,
      target: {
        id: 9,
        target: 'target 2',
        countryOfOrigin: { id: 7, name: 'country 2' },
      },
      city: {
        id: 10,
        name: 'city 2',
        latitude: 10,
        longitude: 20,
        province: {
          id: 8,
          name: 'province 2',
          country: { id: 7, name: 'country 2' },
        },
      },
      victim: {
        id: 11,
        totalNumberOfFatalities: 10,
        numberOfPerpetratorsFatalities: 2,
        totalNumberOfInjured: 11,
        numberOfPerpetratorsInjured: 6,
        valueOfPropertyDamage: 7000,
      },
    };
    const mockEvents: EventsGetResponse = {
      content: [event1, event2],
    };

    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(EventActions.fetchEvents);
      (eventService.getAll as jasmine.Spy).and.returnValue(of(mockEvents));
    });

    it('should return a setEvents action', () => {
      eventEffects.fetchEvents$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.setEvents({ events: mockEvents.content })
        );
        expect(eventService.getAll).toHaveBeenCalled();
      });
    });
  });

  describe('addEvent$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(EventActions.addEvent);
    });

    it('should return a addEvent action', () => {
      (eventService.add as jasmine.Spy).and.returnValue(of(event));

      eventEffects.addEvent$.subscribe((resultAction) => {
        expect(resultAction).toEqual(EventActions.addEvent({ event }));
        expect(eventService.add).toHaveBeenCalled();
      });
    });

    it('should return httpError action on failure', () => {
      (eventService.add as jasmine.Spy).and.returnValue(
        throwError(errorResponse)
      );

      eventEffects.addEvent$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.httpError({
            errorMessages: errorResponse.error.errors,
          })
        );
      });
    });
  });

  describe('updateEventStart$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(EventActions.updateEventStart);
    });

    it('should return a updateEventFetch action', () => {
      (eventService.get as jasmine.Spy).and.returnValue(of(event));

      eventEffects.updateEventStart$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.updateEventFetch({ eventToUpdate: event })
        );
        expect(eventService.get).toHaveBeenCalled();
      });
    });

    it('should return httpError action on failure', () => {
      (eventService.get as jasmine.Spy).and.returnValue(
        throwError(errorResponse)
      );

      eventEffects.updateEventStart$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.httpError({
            errorMessages: errorResponse.error.errors,
          })
        );
      });
    });
  });

  describe('updateEvent$', () => {
    const eventUpdated = {
      id: 6,
      summary: 'summary ver 2',
      motive: 'motive',
      date: new Date(),
      isPartOfMultipleIncidents: false,
      isSuccessful: true,
      isSuicidal: false,
      target: {
        id: 3,
        target: 'target ver 2',
        countryOfOrigin: { id: 1, name: 'country ver 2' },
      },
      city: {
        id: 4,
        name: 'city ver 2',
        latitude: 20,
        longitude: 10,
        province: {
          id: 2,
          name: 'province ver 2',
          country: { id: 1, name: 'country ver 2' },
        },
      },
      victim: {
        id: 5,
        totalNumberOfFatalities: 10,
        numberOfPerpetratorsFatalities: 2,
        totalNumberOfInjured: 12,
        numberOfPerpetratorsInjured: 2,
        valueOfPropertyDamage: 2200,
      },
    };

    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(EventActions.updateEvent);
    });

    it('should return a updateEventFinish action', () => {
      (eventService.update as jasmine.Spy).and.returnValue(of(eventUpdated));

      eventEffects.updateEvent$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.updateEventFinish({ eventUpdated })
        );
        expect(eventService.update).toHaveBeenCalled();
      });
    });

    it('should return httpError action on failure', () => {
      (eventService.update as jasmine.Spy).and.returnValue(
        throwError(errorResponse)
      );

      eventEffects.updateEvent$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.httpError({
            errorMessages: errorResponse.error.errors,
          })
        );
      });
    });
  });

  describe('deleteEventStart$', () => {
    const eventDeleted = {
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
        totalNumberOfFatalities: 10,
        numberOfPerpetratorsFatalities: 2,
        totalNumberOfInjured: 12,
        numberOfPerpetratorsInjured: 2,
        valueOfPropertyDamage: 2200,
      },
    };

    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(
        EventActions.deleteEventStart({ eventToDelete: eventDeleted })
      );
    });

    it('should return a deleteEvent action', () => {
      (eventService.delete as jasmine.Spy).and.returnValue(of(null));

      eventEffects.deleteEventStart$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.deleteEvent({ eventDeleted })
        );
        expect(eventService.delete).toHaveBeenCalled();
      });
    });

    it('should return httpError action on failure', () => {
      (eventService.delete as jasmine.Spy).and.returnValue(
        throwError(errorResponse)
      );

      eventEffects.deleteEventStart$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.httpError({
            errorMessages: errorResponse.error.errors,
          })
        );
      });
    });
  });
});
