import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import City from 'src/app/city/models/city.model';
import AppStoreState from 'src/app/store/app.state';
import Victim from 'src/app/victim/models/victim.model';

import Event from '../../event/models/event.model';
import * as EventActions from '../../event/store/event.actions';

@Component({
  selector: 'app-marker-popup',
  templateUrl: './marker-popup.component.html',
  styleUrls: ['./marker-popup.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class MarkerPopupComponent implements OnInit, OnDestroy {
  private updateSubscription$: Subscription;
  errorMessages: string[] = [];

  @Input()
  event: Event;
  @Input()
  city: City;
  @Input()
  victim: Victim;

  constructor(private store: Store<AppStoreState>) {}

  ngOnInit(): void {
    this.updateSubscription$ = this.store
      .select('event')
      .subscribe(({ lastUpdatedEvent, errorMessages }) => {
        if (lastUpdatedEvent?.id === this.event.id) {
          const eventModel = lastUpdatedEvent;
          this.event = eventModel;
          this.city = eventModel.city;
          this.victim = eventModel.victim;
        }
        this.errorMessages = errorMessages;
      });
  }

  ngOnDestroy(): void {
    this.updateSubscription$?.unsubscribe();
  }

  updateEvent(): void {
    this.store.dispatch(EventActions.startFillingOutForm());
    this.store.dispatch(EventActions.updateEventStart({ id: this.event.id }));
  }

  deleteEvent(): void {
    this.store.dispatch(
      EventActions.deleteEventStart({ eventToDelete: this.event })
    );
  }
}
