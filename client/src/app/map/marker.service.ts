import { Injectable } from '@angular/core';
import * as L from 'leaflet';

import Event from '../event/models/event.model';

@Injectable({ providedIn: 'root' })
export default class MarkerService {
  private createMarkerPopup(event: Event): string {
    const { victim } = event;
    return `<div>
    <p>Summary: ${event.summary}</p>
    <p>Motive: ${event.motive}</p>
    <p>Date: ${event.date}</p>
    <p>Total number of fatalities: ${victim.totalNumberOfFatalities}</p>
    <p>Total number of injured: ${victim.totalNumberOfInjured}</p>
    </div>
    `;
  }

  private scaleRadius = (value: number, maxValue: number): number =>
    20 * value * maxValue;

  showCircleMarkers(events: Event[] = [], map: L.Map): L.CircleMarker[] {
    const maxValue = Math.max(
      ...events.map(
        (event) =>
          event.victim.totalNumberOfFatalities +
          event.victim.totalNumberOfInjured
      ),
      0
    );
    const markers: L.CircleMarker[] = [];
    for (const event of events) {
      const { city, victim } = event;
      const circle = L.circleMarker([city.latitude, city.longitude], {
        radius: this.scaleRadius(
          victim.totalNumberOfFatalities + victim.totalNumberOfInjured,
          maxValue
        ),
      })
        .addTo(map)
        .bindPopup(this.createMarkerPopup(event));
      markers.push(circle);
    }
    return markers;
  }
}
