import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { fakeAsync, getTestBed, TestBed, tick } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';
import AppStoreState from 'src/app/store/app.state';

import AuthResponse from '../models/auth-response.model';
import LoginData from '../models/login-data.model';
import RegistrationCheckRequest from '../models/registration-check-request.model';
import RegistrationData from '../models/registration-data.model';
import User from '../models/user.model';
import * as AuthActions from '../store/auth.actions';
import AuthService from './auth.service';

describe('AuthService', () => {
  let injector: TestBed;
  let authService: AuthService;
  let store: Store<AppStoreState>;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), HttpClientTestingModule],
      providers: [AuthService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    store = TestBed.inject(Store);
    authService = injector.inject(AuthService);
    httpMock = injector.inject(HttpTestingController);

    spyOn(store, 'dispatch');
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when login user', () => {
    it('should login user', () => {
      const loginData: LoginData = {
        userNameOrEmail: 'username',
        password: 'password',
      };
      const authResponse: AuthResponse = {
        token: 'token',
        expirationTimeInMilliseconds: 3600000,
      };
      authService.loginUser(loginData).subscribe((res) => {
        expect(res).toEqual(authResponse);
      });

      const req = httpMock.expectOne(`${BASE_URL}/authentication`);
      expect(req.request.method).toBe('POST');
      req.flush(authResponse);
    });
  });

  describe('when register user', () => {
    it('should register user', () => {
      const registrationData: RegistrationData = {
        userName: 'username',
        email: 'email@email.com',
        password: 'pass',
        matchingPassword: 'pass',
      };
      const authResponse: AuthResponse = {
        token: 'token',
        expirationTimeInMilliseconds: 3600000,
      };

      authService.registerUser(registrationData).subscribe((res) => {
        expect(res).toEqual(authResponse);
      });

      const req = httpMock.expectOne(`${BASE_URL}/registration/register`);
      expect(req.request.method).toBe('POST');
      req.flush(authResponse);
    });
  });

  describe('when check user data', () => {
    it('should return user data statuses', () => {
      const dataToCheck: RegistrationCheckRequest = {
        userName: 'username',
        email: 'email@email.com',
      };
      const registrationCheckResponse = {
        isUserNameAvailable: true,
        isEmailAvailable: true,
      };

      authService.checkUserData(dataToCheck).subscribe((res) => {
        expect(res).toEqual(registrationCheckResponse);
      });

      const req = httpMock.expectOne(`${BASE_URL}/registration/checkUserData`);
      expect(req.request.method).toBe('POST');
      req.flush(registrationCheckResponse);
    });
  });

  describe('when get user from local storage', () => {
    it('should get user from local storage when user data is stored in local storage', () => {
      const expirationDate = new Date(Date.now() + 36000000);
      const userData: {
        token: string;
        expirationDate: Date;
      } = { token: 'token', expirationDate };
      const userExpected: User = { token: 'token', expirationDate };

      spyOn(localStorage, 'getItem').and.callFake(() =>
        JSON.stringify(userData)
      );
      const userActual = authService.getUserFromLocalStorage();

      expect(userActual.token).toEqual(userExpected.token);
      expect(userActual.expirationDate).toBeTruthy();
      expect(localStorage.getItem).toHaveBeenCalled();
    });

    it('should return null when user data is not stored in local storage', () => {
      const userActual = authService.getUserFromLocalStorage();

      expect(userActual).toBeNull();
    });
  });

  describe('when remove user from local storage', () => {
    it('should remove user from local storage', () => {
      spyOn(localStorage, 'removeItem').and.callThrough();

      authService.removeUserFromLocalStorage();

      expect(localStorage.removeItem).toHaveBeenCalled();
    });
  });

  describe('when save user in local storage', () => {
    it('should save user in local storage', () => {
      const user: User = {
        token: 'token',
        expirationDate: new Date(Date.now() + 36000000),
      };

      spyOn(localStorage, 'setItem').and.callFake(() => JSON.stringify(user));

      authService.saveUserInLocalStorage(user);

      expect(localStorage.setItem).toHaveBeenCalled();
    });
  });

  describe('when set logout timer', () => {
    it('should dispact logoutUser action after some time', fakeAsync(() => {
      const expirationDate = new Date(Date.now() + 3000);
      spyOn(window, 'setTimeout').and.callThrough();

      authService.setLogoutTimer(expirationDate.toString());

      tick(3000);

      expect(setTimeout).toHaveBeenCalled();
      expect(store.dispatch).toHaveBeenCalledWith(AuthActions.logoutUser());
    }));
  });

  describe('when clear logout timer', () => {
    it('should clear timeout', fakeAsync(() => {
      const expirationDate = new Date(Date.now() + 3000);
      spyOn(window, 'setTimeout').and.callThrough();
      spyOn(window, 'clearTimeout').and.callThrough();

      authService.setLogoutTimer(expirationDate.toString());

      tick(3000);

      authService.clearLogoutTimer();

      expect(clearTimeout).toHaveBeenCalled();
    }));

    it('should not clear timeout if was not setted before', () => {
      spyOn(window, 'clearTimeout').and.callThrough();

      authService.clearLogoutTimer();

      expect(clearTimeout).not.toHaveBeenCalled();
    });
  });
});
