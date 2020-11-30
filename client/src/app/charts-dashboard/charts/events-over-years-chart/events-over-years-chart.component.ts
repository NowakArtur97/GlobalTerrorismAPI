import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ChartDataSets, ChartOptions, ChartPoint, ChartType } from 'chart.js';
import { Subscription } from 'rxjs';
import { selectAllEvents } from 'src/app/event/store/event.reducer';
import AppStoreState from 'src/app/store/app.state';

import Event from '../../../event/models/event.model';

@Component({
  selector: 'app-events-over-years-chart',
  templateUrl: './events-over-years-chart.component.html',
  styleUrls: ['./events-over-years-chart.component.css'],
})
export class EventsOverYearsChartComponent implements OnInit {
  public scatterChartOptions: ChartOptions = {
    responsive: true,
  };

  public scatterChartData: ChartDataSets[] = [
    {
      data: [],
      // data: [{ x: 1, y: 1 }],
      label: 'Events over the years',
      pointRadius: 10,
    },
  ];
  public scatterChartType: ChartType = 'scatter';

  private eventsSubscription$: Subscription;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.eventsSubscription$ = this.store
      .select(selectAllEvents)
      .subscribe((events: Event[]) => {
        const actualYear = new Date().getFullYear();
        let index = 0;
        for (let year = 1970; year <= actualYear; year++) {
          this.scatterChartData[0].data[index] = { x: year, y: 0 };
          index++;
        }

        events.forEach(({ date }) => {
          const year = new Date(date).getFullYear();
          const eventsOverYear: ChartPoint = [
            ...this.scatterChartData[0].data,
          ].find((data: ChartPoint) => data.x === year);
          eventsOverYear.y++;
        });
      });
  }

  ngOnDestroy(): void {
    this.eventsSubscription$?.unsubscribe();
  }
}
