import { createAction, props } from '@ngrx/store';

import EventDTO from '../models/event.dto';
import Event from '../models/event.model';

export const setEvents = createAction(
  '[Event] Set Events',
  props<{
    events: Event[];
  }>()
);

export const resetEvents = createAction('[Event] Reset Events');

export const fetchEvents = createAction('[Event] Fetch Events');

export const addEventStart = createAction(
  '[Event] Add Event Start',
  props<{ eventDTO: EventDTO }>()
);

export const addEvent = createAction(
  '[Event] Add Event',
  props<{ event: Event }>()
);

export const updateEventStart = createAction(
  '[Event] Update Event Start',
  props<{ id: number }>()
);

export const updateEventFetch = createAction(
  '[Event] Update Event Fetch',
  props<{ eventToUpdate: Event }>()
);

export const updateEvent = createAction(
  '[Event] Update Event',
  props<{ eventDTO: EventDTO }>()
);

export const updateEventFinish = createAction(
  '[Event] Update Event Finish',
  props<{ eventUpdated: Event }>()
);

export const deleteEventStart = createAction(
  '[Event] Delete Event Start',
  props<{ eventToDelete: Event }>()
);

export const deleteEvent = createAction(
  '[Event] Delete Event',
  props<{ eventDeleted: Event }>()
);

export const deleteEventsStart = createAction(
  '[Event] Delete Events Start',
  props<{ eventsToDelete: Event[] }>()
);

export const deleteEvents = createAction(
  '[Event] Delete Events',
  props<{ eventsDeletedIds: number[] }>()
);

export const httpError = createAction(
  '[Event] Http Error',
  props<{
    errorMessages: string[];
  }>()
);

export const startFillingOutForm = createAction(
  '[Event] User Started Filling Out Form'
);

export const changeEndDateOfEvents = createAction(
  '[Event] Change End Date Of Events',
  props<{ endDateOfEvents: Date }>()
);

export const changeMaxRadiusOfEventsDetection = createAction(
  '[Event] Change Max Radius Of Events Detection',
  props<{ maxRadiusOfEventsDetection: number }>()
);
