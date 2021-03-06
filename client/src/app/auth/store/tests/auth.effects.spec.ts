import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject, throwError } from 'rxjs';

import AuthResponse from '../../models/auth-response.model';
import LoginData from '../../models/login-data.model';
import RegistrationData from '../../models/registration-data.model';
import User from '../../models/user.model';
import AuthService from '../../services/auth.service';
import * as AuthActions from '../auth.actions';
import AuthEffects from '../auth.effects';

const authResponse: AuthResponse = {
  token: 'secret token',
  expirationTimeInMilliseconds: 36000000,
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

describe('AuthEffects', () => {
  let authEffects: AuthEffects;
  let actions$: ReplaySubject<any>;
  let authService: AuthService;
  let router: Router;

  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        AuthEffects,
        provideMockActions(() => actions$),
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj('authService', [
            'loginUser',
            'registerUser',
            'getUserFromLocalStorage',
            'removeUserFromLocalStorage',
            'saveUserInLocalStorage',
            'setLogoutTimer',
            'clearLogoutTimer',
          ]),
        },
      ],
      imports: [RouterTestingModule],
    })
  );

  beforeEach(() => {
    authEffects = TestBed.inject(AuthEffects);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);

    (authService.setLogoutTimer as jasmine.Spy).and.callThrough();
  });

  describe('loginUser$', () => {
    beforeEach(() => {
      const mockLoginData: LoginData = {
        userNameOrEmail: 'username',
        password: 'password',
      };
      actions$ = new ReplaySubject(1);
      actions$.next(AuthActions.loginUserStart({ loginData: mockLoginData }));
    });

    it('should return an authenticateUserSuccess action on success', () => {
      (authService.loginUser as jasmine.Spy).and.returnValue(of(authResponse));

      authEffects.loginUser$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('[User] Authenticate User Success');
        expect(authService.loginUser).toHaveBeenCalled();
        expect(authService.setLogoutTimer).toHaveBeenCalled();
        expect(authService.saveUserInLocalStorage).toHaveBeenCalled();
      });
    });

    it('should return authenticateUserFailure action on failure', () => {
      (authService.loginUser as jasmine.Spy).and.returnValue(
        throwError(errorResponse)
      );
      (authService.setLogoutTimer as jasmine.Spy).and.callThrough();
      (authService.saveUserInLocalStorage as jasmine.Spy).and.callThrough();

      authEffects.loginUser$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          AuthActions.authenticateUserFailure({
            authErrorMessages: errorResponse.error.errors,
          })
        );
        expect(authService.loginUser).toHaveBeenCalled();
      });
    });
  });

  describe('registerUser$', () => {
    beforeEach(() => {
      const mockRegistrationData: RegistrationData = {
        userName: 'username',
        email: 'email@email.com',
        password: 'password',
        matchingPassword: 'password',
      };
      actions$ = new ReplaySubject(1);
      actions$.next(
        AuthActions.registerUserStart({
          registrationData: mockRegistrationData,
        })
      );
    });

    it('should return an authenticateUserSuccess action on success', () => {
      (authService.registerUser as jasmine.Spy).and.returnValue(
        of(authResponse)
      );
      (authService.setLogoutTimer as jasmine.Spy).and.callThrough();
      (authService.saveUserInLocalStorage as jasmine.Spy).and.callThrough();

      authEffects.registerUser$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('[User] Authenticate User Success');
        expect(authService.registerUser).toHaveBeenCalled();
        expect(authService.setLogoutTimer).toHaveBeenCalled();
        expect(authService.saveUserInLocalStorage).toHaveBeenCalled();
      });
    });

    it('should return an authenticateUserFailure action on failure', () => {
      (authService.registerUser as jasmine.Spy).and.returnValue(
        throwError(errorResponse)
      );

      authEffects.registerUser$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          AuthActions.authenticateUserFailure({
            authErrorMessages: errorResponse.error.errors,
          })
        );
        expect(authService.registerUser).toHaveBeenCalled();
      });
    });
  });

  describe('autoUserLogin$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(AuthActions.autoUserLogin());
    });

    it('should return an authenticateUserSuccess action when user data is stored in local storage', () => {
      const mockUser: User = {
        token: 'secret token',
        expirationDate: new Date(Date.now() + 36000000),
      };
      (authService.getUserFromLocalStorage as jasmine.Spy).and.returnValue(
        of(mockUser)
      );
      authEffects.autoUserLogin$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('[User] Authenticate User Success');
        expect(authService.getUserFromLocalStorage).toHaveBeenCalled();
      });
    });

    it('should return a dummy action when user data is not stored in local storage', () => {
      authEffects.autoUserLogin$.subscribe((resultAction) => {
        expect(resultAction.type).toEqual('DUMMY');
        expect(authService.getUserFromLocalStorage).toHaveBeenCalled();
      });
    });
  });

  describe('logoutUser$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(AuthActions.logoutUser());
    });

    it('should logout user and remove user data from local storage', () => {
      spyOn(router, 'navigate').and.stub();

      authEffects.logoutUser$.subscribe(() => {
        expect(authService.clearLogoutTimer).toHaveBeenCalled();
        expect(authService.removeUserFromLocalStorage).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['/']);
      });
    });
  });
});
