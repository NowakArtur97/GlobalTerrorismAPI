import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from 'src/environments/environment';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthModule } from './auth/auth.module';
import { ChartsDashboardModule } from './charts-dashboard/charts-dashboard.module';
import { CityModule } from './city/city.module';
import { MaterialModule } from './common/material.module';
import { CoreModule } from './core.module';
import { CountryModule } from './country/country.module';
import EventModule from './event/event.module';
import MapModule from './map/map.module';
import { ProvinceModule } from './province/province.module';
import { SharedModule } from './shared/shared.module';
import { TargetModule } from './target/target.module';
import { VictimModule } from './victim/victim.module';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,

    StoreModule.forRoot([]),
    StoreDevtoolsModule.instrument({ logOnly: environment.production }),
    EffectsModule.forRoot([]),

    AppRoutingModule,

    CoreModule,
    EventModule,
    TargetModule,
    CityModule,
    VictimModule,
    ProvinceModule,
    CountryModule,

    MapModule,
    AuthModule,
    ChartsDashboardModule,

    BrowserAnimationsModule,
    MaterialModule,
    SharedModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
