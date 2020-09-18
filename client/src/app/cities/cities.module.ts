import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { StoreModule } from '@ngrx/store';

import { CitiesComponent } from './cities.component';
import citiesReducer from './store/cities.reducer';

@NgModule({
  declarations: [CitiesComponent],
  imports: [
    HttpClientModule,
    StoreModule.forFeature('cities', citiesReducer),
    LeafletModule,
  ],
  exports: [CitiesComponent],
})
export default class CitiesModule {}
