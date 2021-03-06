import { Component, forwardRef, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, NG_VALIDATORS, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { AbstractFormComponent } from 'src/app/common/components/abstract-form.component';
import CommonValidators from 'src/app/common/validators/common.validator';
import { selectEventToUpdate } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../event/models//event.model';
import City from '../models/city.model';
import CityService from '../services/city.service';

@Component({
  selector: 'app-city-form',
  templateUrl: './city-form.component.html',
  styleUrls: ['./city-form.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CityFormComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CityFormComponent),
      multi: true,
    },
  ],
})
export class CityFormComponent
  extends AbstractFormComponent
  implements OnInit, OnDestroy {
  private citiesSubscription$: Subscription;
  cities: City[] = [];

  constructor(
    protected store: Store<AppStoreState>,
    private cityService: CityService
  ) {
    super(store);
  }
  ngOnInit(): void {
    super.ngOnInit();
    this.citiesSubscription$ = this.cityService
      .getAll()
      .subscribe((citiesResponse) => (this.cities = citiesResponse.content));
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.citiesSubscription$?.unsubscribe();
  }

  initForm(): void {
    let name = '';
    let latitude = 0;
    let longitude = 0;

    this.updateSubscription$.add(
      this.store.select(selectEventToUpdate).subscribe((event: Event) => {
        if (event?.city) {
          const { city } = event;
          name = city.name;
          latitude = city.latitude;
          longitude = city.longitude;
        }
      })
    );

    this.formGroup = new FormGroup({
      name: new FormControl(name, [CommonValidators.notBlank]),
      latitude: new FormControl(latitude, [
        Validators.min(-90),
        Validators.max(90),
        CommonValidators.notBlank,
      ]),
      longitude: new FormControl(longitude, [
        Validators.min(-180),
        Validators.max(180),
        CommonValidators.notBlank,
      ]),
    });
  }

  get name(): AbstractControl {
    return this.formGroup.get('name');
  }

  get latitude(): AbstractControl {
    return this.formGroup.get('latitude');
  }

  get longitude(): AbstractControl {
    return this.formGroup.get('longitude');
  }

  selectCity(event: MatAutocompleteSelectedEvent): void {
    if (!event.option) {
      return;
    }
    const cityName = event.option.value;
    this.name.setValue(cityName);

    const selectedCity = this.cities.find((city) => city.name === cityName);
    this.latitude.setValue(selectedCity.latitude);
    this.longitude.setValue(selectedCity.longitude);
  }
}
