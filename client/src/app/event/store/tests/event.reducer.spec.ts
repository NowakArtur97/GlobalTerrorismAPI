import { Dictionary } from '@ngrx/entity';

import EventDTO from '../../models/event.dto';
import Event from '../../models/event.model';
import * as EventActions from '../event.actions';
import eventReducer, {
  EventStoreState,
  selectAllEventsBeforeDate,
  selectAllEventsInRadius,
} from '../event.reducer';

const endDateOfEvents = new Date();
const event1 = {
  id: 6,
  summary: 'summary',
  motive: 'motive',
  date: new Date(1999, 6, 12),
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
  date: new Date(1999, 2, 3),
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
const date =
  event1.date.getFullYear() +
  '-' +
  (event1.date.getMonth() + 1) +
  '-' +
  event1.date.getDate();
const eventDTO: EventDTO = {
  id: 6,
  summary: 'summary',
  motive: 'motive',
  date,
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
const eventsDictionary: Dictionary<Event> = {
  6: event1,
  12: event2,
};

const state: EventStoreState = {
  ids: [],
  entities: {},
  eventToUpdate: null,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  endDateOfEvents,
  maxRadiusOfEventsDetection: null,
  errorMessages: [],
};
const stateWithEvents: EventStoreState = {
  ids: [6, 12],
  entities: eventsDictionary,
  eventToUpdate: null,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  endDateOfEvents,
  maxRadiusOfEventsDetection: null,
  errorMessages: [],
};
const stateWithEventToUpdate: EventStoreState = {
  ids: [],
  entities: {},
  eventToUpdate: event1,
  lastUpdatedEvent: null,
  lastDeletedEvent: null,
  isLoading: false,
  endDateOfEvents,
  maxRadiusOfEventsDetection: null,
  errorMessages: ['ERROR'],
};

describe('eventReducer', () => {
  describe('EventActions.setEvents', () => {
    it('should store events', () => {
      const events: Event[] = [event1, event2];
      const action = EventActions.setEvents({ events });
      const actualState = eventReducer(state, action);
      const expectedState = { ...stateWithEvents };

      expect(actualState).toEqual(expectedState);
    });

    it('should store empty events list', () => {
      const emptyEventsList = [];
      const action = EventActions.setEvents({ events: emptyEventsList });
      const actualState = eventReducer(state, action);
      const expectedState = { ...state, entities: {} };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.resetEvents', () => {
    it('should reset events list', () => {
      const action = EventActions.resetEvents();
      const actualState = eventReducer(stateWithEvents, action);
      const expectedState = { ...state };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.addEventStart', () => {
    it('should start loading and remove error messages', () => {
      const stateWhenAddEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const action = EventActions.addEventStart({ eventDTO });
      const actualState = eventReducer(state, action);
      const expectedState = { ...stateWhenAddEventStart };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.addEvent, remove error messages and stop loading', () => {
    it('should store event', () => {
      const stateWhenAddEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: ['ERROR'],
      };
      const eventsDictionaryWithOneEvent: Dictionary<Event> = {
        6: event1,
      };
      const stateWithOneEvent: EventStoreState = {
        ids: [6],
        entities: eventsDictionaryWithOneEvent,
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const action = EventActions.addEvent({ event: event1 });
      const actualState = eventReducer(stateWhenAddEventStart, action);
      const expectedState = { ...stateWithOneEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEventStart', () => {
    it('should reset previous event to update and remove error messages', () => {
      const action = EventActions.updateEventStart({ id: event1.id });
      const actualState = eventReducer(stateWithEventToUpdate, action);
      const expectedState = { ...state };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEventFetch', () => {
    it('should store event to update', () => {
      const stateWhenUpdateEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: ['ERROR'],
      };
      const action = EventActions.updateEventFetch({ eventToUpdate: event1 });
      const actualState = eventReducer(stateWhenUpdateEventStart, action);
      const expectedState = { ...stateWithEventToUpdate };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEvent', () => {
    it('should remove last updated event and start loading', () => {
      const stateWithLastUpdatedEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: event1,
        lastDeletedEvent: null,
        isLoading: false,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const stateWhenUpdateEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const action = EventActions.updateEvent({ eventDTO });
      const actualState = eventReducer(stateWithLastUpdatedEvent, action);
      const expectedState = { ...stateWhenUpdateEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.updateEventFinish', () => {
    it('should set last updated event, remove event to update, remove error messages and stop loading', () => {
      const event2Updated = {
        id: 12,
        summary: 'summary 2 ver 2',
        motive: 'motive 2 ver 2',
        date: new Date(),
        isPartOfMultipleIncidents: true,
        isSuccessful: false,
        isSuicidal: true,
        target: {
          id: 9,
          target: 'target 2 ver 2',
          countryOfOrigin: { id: 7, name: 'country 2 ver 2' },
        },
        city: {
          id: 10,
          name: 'city 2 ver 2',
          latitude: 10,
          longitude: 20,
          province: {
            id: 8,
            name: 'province 2 ver 2',
            country: { id: 7, name: 'country 2 ver 2' },
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
      const stateWithEventsUpdated: EventStoreState = {
        ids: [6, 12],
        entities: eventsDictionary,
        eventToUpdate: event2Updated,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: ['ERROR'],
      };
      const eventsDictionaryWithUpdatedEvents: Dictionary<Event> = {
        6: event1,
        12: event2Updated,
      };
      const stateWithLastUpdatedEvent: EventStoreState = {
        ids: [6, 12],
        entities: eventsDictionaryWithUpdatedEvents,
        eventToUpdate: null,
        lastUpdatedEvent: event2Updated,
        lastDeletedEvent: null,
        isLoading: false,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const action = EventActions.updateEventFinish({
        eventUpdated: event2Updated,
      });
      const actualState = eventReducer(stateWithEventsUpdated, action);
      const expectedState = { ...stateWithLastUpdatedEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.deleteEventStart', () => {
    it('should remove last deleted event, start loading and remove error massages', () => {
      const stateWithLastDeletedEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: event1,
        isLoading: false,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: ['ERROR'],
      };
      const stateWhenDeleteEventStart: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const action = EventActions.deleteEventStart({ eventToDelete: event1 });
      const actualState = eventReducer(stateWithLastDeletedEvent, action);
      const expectedState = { ...stateWhenDeleteEventStart };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.deleteEvent', () => {
    it('should set last deleted event, remove error messages and stop loading', () => {
      const stateWhenDeleteEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: true,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: ['ERROR'],
      };
      const stateAfterDeletingEvent: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: event1,
        isLoading: false,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const action = EventActions.deleteEvent({ eventDeleted: event1 });
      const actualState = eventReducer(stateWhenDeleteEvent, action);
      const expectedState = { ...stateAfterDeletingEvent };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventActions.changeEndDateOfEvents', () => {
    it('should set new end date of events', () => {
      const newMaxDate = new Date(1997, 0, 1);
      const stateAfterChangingMaxDate: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        endDateOfEvents: newMaxDate,
        maxRadiusOfEventsDetection: null,
        errorMessages: [],
      };
      const action = EventActions.changeEndDateOfEvents({
        endDateOfEvents: newMaxDate,
      });
      const actualState = eventReducer(state, action);
      const expectedState = { ...stateAfterChangingMaxDate };

      expect(actualState).toEqual(expectedState);
    });
  });

  describe('EventsSelectors', () => {
    const event3 = {
      id: 18,
      summary: 'summary 3',
      motive: 'motive 3',
      date: new Date(1999, 2, 2),
      isPartOfMultipleIncidents: false,
      isSuccessful: true,
      isSuicidal: false,
      target: {
        id: 15,
        target: 'target 3',
        countryOfOrigin: { id: 13, name: 'country 3' },
      },
      city: {
        id: 16,
        name: 'city 3',
        latitude: 20,
        longitude: 10,
        province: {
          id: 4,
          name: 'province 3',
          country: { id: 13, name: 'country 3' },
        },
      },
      victim: {
        id: 17,
        totalNumberOfFatalities: 12,
        numberOfPerpetratorsFatalities: 1,
        totalNumberOfInjured: 1,
        numberOfPerpetratorsInjured: 1,
        valueOfPropertyDamage: 2300,
      },
    };
    const event4 = {
      id: 24,
      summary: 'summary 4',
      motive: 'motive 4',
      date: new Date(1998, 5, 11),
      isPartOfMultipleIncidents: true,
      isSuccessful: false,
      isSuicidal: true,
      target: {
        id: 21,
        target: 'target 4',
        countryOfOrigin: { id: 19, name: 'country 4' },
      },
      city: {
        id: 22,
        name: 'city 4',
        latitude: 30,
        longitude: 40,
        province: {
          id: 20,
          name: 'province 4',
          country: { id: 19, name: 'country 4' },
        },
      },
      victim: {
        id: 23,
        totalNumberOfFatalities: 1,
        numberOfPerpetratorsFatalities: 1,
        totalNumberOfInjured: 7,
        numberOfPerpetratorsInjured: 6,
        valueOfPropertyDamage: 5000,
      },
    };

    describe('selectAllEventsBeforeDate', () => {
      it('should select events before max date', () => {
        const expectedMaxDate = new Date(1999, 2, 3);

        const events: Event[] = [event1, event2, event3, event4];
        const expctedEvents: Event[] = [event2, event3, event4];

        expect(
          selectAllEventsBeforeDate.projector(events, expectedMaxDate)
        ).toEqual(expctedEvents);
      });
    });

    describe('selectAllEventsInRadius', () => {
      it('should select events in radius', () => {
        const expectedMaxRadius = 3500000;
        const expectedUserLocation: L.LatLngExpression = [50, 18];

        const eventsBeforeDate: Event[] = [event1, event2, event3, event4];
        const expectedEvents: Event[] = [event1, event3, event4];

        expect(
          selectAllEventsInRadius.projector(
            eventsBeforeDate,
            expectedMaxRadius,
            expectedUserLocation
          )
        ).toEqual(expectedEvents);
      });
    });
  });

  describe('EventActions.startFillingOutForm', () => {
    it('should delete error messages', () => {
      const stateWithErrorMessages: EventStoreState = {
        ids: [],
        entities: {},
        eventToUpdate: null,
        lastUpdatedEvent: null,
        lastDeletedEvent: null,
        isLoading: false,
        endDateOfEvents,
        maxRadiusOfEventsDetection: null,
        errorMessages: ['ERROR'],
      };
      const action = EventActions.startFillingOutForm();
      const actualState = eventReducer(stateWithErrorMessages, action);
      const expectedState = { ...state };

      expect(actualState).toEqual(expectedState);
      expect(actualState.errorMessages).toEqual([]);
      expect(actualState.errorMessages.length).toBe(0);
    });
  });
});
