import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap } from 'rxjs/operators';

import CitiesGetResponse from '../models/cities-get-response.model';
import * as CitiesActions from './cities.actions';

@Injectable()
export default class CitiesEffects {
  constructor(private actions$: Actions, private httpClient: HttpClient) {}

  fetchCities$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CitiesActions.fetchCitites),
      switchMap(() => {
        const headers = new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTYwMDQ3MDYzNiwiaWF0IjoxNjAwNDM0NjM2fQ.ETbkXCn1wRD6gDG0lWnwIb9Zji9jWyPmkZ6HRcpHpX8`,
        });

        return this.httpClient.get<CitiesGetResponse>(
          'http://localhost:8080/api/v1/cities?page=0&size=50',
          { headers: headers }
        );
      }),
      map((response) => {
        return response.content;
      }),
      map((cities) => CitiesActions.setCities({ cities }))
    )
  );
}