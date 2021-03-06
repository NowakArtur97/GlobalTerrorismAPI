import { Component } from '@angular/core';
import { Label } from 'ng2-charts';

import Event from '../../../../event/models/event.model';
import { AbstractVictimsChartComponent } from '../abstract-victims-chart.component';

@Component({
  selector: 'app-fatal-victims-chart',
  templateUrl: './fatal-victims-chart.component.html',
  styleUrls: ['./fatal-victims-chart.component.css'],
})
export class FatalVictimsChartComponent extends AbstractVictimsChartComponent {
  pieChartLabels: Label[] = [
    'Number of perpetrators fatalities',
    'Number of civilians fatalities',
  ];

  private numberOfPerpetratorsFatalities = 0;
  private numberOfCiviliansFatalities = 0;

  protected populateChart(events: Event[]): void {
    this.numberOfCiviliansFatalities = 0;
    this.numberOfPerpetratorsFatalities = 0;

    events.forEach(({ victim }) => {
      this.numberOfPerpetratorsFatalities +=
        victim.numberOfPerpetratorsFatalities;
      this.numberOfCiviliansFatalities +=
        victim.totalNumberOfFatalities - victim.numberOfPerpetratorsFatalities;
    });

    this.pieChartData[0] = this.numberOfPerpetratorsFatalities;
    this.pieChartData[1] = this.numberOfCiviliansFatalities;
  }
}
