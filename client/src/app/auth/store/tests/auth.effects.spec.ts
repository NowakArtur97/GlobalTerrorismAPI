import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject, throwError } from 'rxjs';
import ErrorResponse from 'src/app/shared/models/ErrorResponse';

import AuthResponse from '../../models/AuthResponseData';
import LoginData from '../../models/LoginData';
import User from '../../models/User';
import AuthService from '../../services/auth.service';
import * as AuthActions from '../auth.actions';
import AuthEffects from '../auth.effects';

const mockLoginData = new LoginData('username', 'password');
const mockUserData = new AuthResponse('secret token');
const mockUser = new User('secret token');
const mockErrorResponse = new HttpErrorResponse({
  error: {
    errors: [new ErrorResponse(['Error message.'], 401, new Date())],
  },
  headers: new HttpHeaders('headers'),
  status: 401,
  statusText: 'OK',
  url: 'http://localhost:8080/api/v1',
});

describe('AuthEffects', () => {
  let authEffects: AuthEffects;
  let actions$: ReplaySubject<any>;
  let authService: AuthService;

  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        AuthEffects,
        provideMockActions(() => actions$),
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj('authService', ['loginUser']),
        },
      ],
    })
  );

  beforeEach(() => {
    authEffects = TestBed.get(AuthEffects);
    authService = TestBed.get(AuthService);
    (authService.loginUser as jasmine.Spy).and.returnValue(of(mockUser));
  });

  describe('loginUser$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(AuthActions.loginUserStart({ loginData: mockLoginData }));
    });

    it('should return a authenticateUserSuccess action', () => {
      authEffects.loginUser$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          AuthActions.authenticateUserSuccess({ user: mockUser })
        );
      });
    });

    it('should return authenticateUserFailure action on failure', () => {
      (authService.loginUser as jasmine.Spy).and.returnValue(
        throwError(mockErrorResponse)
      );

      authEffects.loginUser$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          AuthActions.authenticateUserFailure({
            authErrorMessages: mockErrorResponse.error.errors,
          })
        );
      });
    });
  });
});
