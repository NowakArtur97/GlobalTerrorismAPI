import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';

import AuthResponse from '../models/auth-response.model';
import LoginData from '../models/login-data.model';
import RegistrationData from '../models/registration-data.model';
import User from '../models/user.model';
import AuthService from './auth.service';

describe('AuthService', () => {
  let injector: TestBed;
  let authService: AuthService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    authService = injector.inject(AuthService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when login user', () => {
    it('should login user', () => {
      const loginData = new LoginData('username', 'password');
      const authResponse = new AuthResponse('token');

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
      const registrationData = new RegistrationData(
        'username',
        'email@email.com',
        'pass',
        'pass'
      );
      const authResponse = new AuthResponse('token');

      authService.registerUser(registrationData).subscribe((res) => {
        expect(res).toEqual(authResponse);
      });

      const req = httpMock.expectOne(`${BASE_URL}/registration/register`);
      expect(req.request.method).toBe('POST');
      req.flush(authResponse);
    });
  });

  describe('when get user from local storage', () => {
    it('should get user from local storage when user data is stored in local storage', () => {
      const userData: {
        _token: string;
      } = { _token: 'secret token' };
      const userExpected = new User('secret token');

      spyOn(localStorage, 'getItem').and.callFake(() =>
        JSON.stringify(userData)
      );
      const userActual = authService.getUserFromLocalStorage();

      expect(userActual).toEqual(userExpected);
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
      const user = new User('secret token');

      spyOn(localStorage, 'setItem').and.callFake(() => JSON.stringify(user));

      authService.saveUserInLocalStorage(user);

      expect(localStorage.setItem).toHaveBeenCalled();
    });
  });
});
