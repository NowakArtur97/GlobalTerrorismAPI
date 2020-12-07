import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import ErrorResponse from 'src/app/common/models/error-response.model';

import EventService from '../services/event.service';
import * as EventActions from './event.actions';

@Injectable()
export default class EventEffects {
  constructor(private actions$: Actions, private eventService: EventService) {}

  fetchEvents$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.fetchEvents),
      switchMap(() => this.eventService.getAll()),
      map((response) => response.content),
      map((events) => EventActions.setEvents({ events })),
      catchError((errorResponse) => this.handleError(errorResponse.error))
    )
  );

  addEvent$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.addEventStart),
      switchMap(({ eventDTO }) =>
        this.eventService.add(eventDTO).pipe(
          map(
            (event) => EventActions.addEvent({ event }),
            catchError((errorResponse) => this.handleError(errorResponse.error))
          )
        )
      )
    )
  );

  updateEventStart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.updateEventStart),
      switchMap(({ id }) =>
        this.eventService.get(id).pipe(
          map(
            (eventToUpdate) =>
              EventActions.updateEventFetch({
                eventToUpdate,
              }),
            catchError((errorResponse) => this.handleError(errorResponse.error))
          )
        )
      )
    )
  );

  updateEvent$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.updateEvent),
      switchMap(({ eventDTO }) =>
        this.eventService.update(eventDTO).pipe(
          map(
            (eventUpdated) =>
              EventActions.updateEventFinish({
                eventUpdated,
              }),
            catchError((errorResponse) => this.handleError(errorResponse.error))
          )
        )
      )
    )
  );

  deleteEventStart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EventActions.deleteEventStart),
      switchMap(({ eventToDelete }) =>
        this.eventService.delete(eventToDelete.id).pipe(
          map(
            () =>
              EventActions.deleteEvent({
                eventDeleted: eventToDelete,
              }),
            catchError((errorResponse) => this.handleError(errorResponse.error))
          )
        )
      )
    )
  );

  private handleError = (errorResponse: ErrorResponse) => {
    const errorMessages = errorResponse?.errors || ['Unknown error.'];
    return of(
      EventActions.httpError({
        errorMessages,
      })
    );
  };
}
