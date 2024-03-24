import { Component, OnDestroy } from '@angular/core';
import { ConnectionsService } from '../../shared/services/connections.service';
import { RoutesQuery } from '../../model/routes-query';
import { Subscription, finalize } from 'rxjs';
import { ResponseError } from '../../model/response-error';
import { RoutesGraph } from '../../model/graph/routes-graph';
import { ConnectionsGraphComponent } from './components/connections-graph/connections-graph.component';
import { LoadingSpinnerComponent } from "../../shared/components/loading-spinner/loading-spinner.component";
import { UserMessagesComponent } from "../../shared/components/user-messages/user-messages.component";
import { SearchConnectionsFormComponent } from './components/search-connections-form/search-connections-form.component';

@Component({
  selector: 'app-connections-checker',
  standalone: true,
  templateUrl: './connections-checker.component.html',
  styleUrl: './connections-checker.component.scss',
  imports: [
    ConnectionsGraphComponent,
    LoadingSpinnerComponent,
    UserMessagesComponent,
    SearchConnectionsFormComponent
  ]
})
export class ConnectionsCheckerComponent implements OnDestroy {
  private connectionsSubscription?: Subscription;
  isLoading = false;
  error?: ResponseError;
  constructor(private connectionsService: ConnectionsService) { }

  connectionsGraph?: RoutesGraph = {"nodes":[{"id":0,"name":"NUE","completeName":"NURNBERG","symbolSize":30.0,"value":50.0,"category":0,"x":11.078,"y":49.499},{"id":1,"name":"STN","completeName":"STANSTED","symbolSize":30.0,"value":50.0,"category":1,"x":0.235,"y":51.885},{"id":2,"name":"OVD","completeName":"ASTURIAS","symbolSize":30.0,"value":50.0,"category":2,"x":-6.034,"y":43.563}],"links":[{"source":0,"target":1,"routeName":"NUE->STN->OVD","color":"#7A734A","url":"origin=NUE&destination=STN&origin=STN&destination=OVD"},{"source":1,"target":2,"routeName":"NUE->STN->OVD","color":"#7A734A","url":"origin=NUE&destination=STN&origin=STN&destination=OVD"}],"categories":[{"name":"NUE"},{"name":"STN"},{"name":"OVD"}]};


  ngOnDestroy(): void {
    this.connectionsSubscription?.unsubscribe();
  }

  onSearchRoutes(query: RoutesQuery) {
    this.isLoading = true;
    this.error = undefined;

    // Make query
    this.connectionsSubscription = this.connectionsService.getFlightConnectionsGraph(query)
      .pipe(
        finalize(() => this.isLoading = false),
      )
      .subscribe(
        {
          next: (response) => this.connectionsGraph = response,
          error: (error: ResponseError) => this.error = error
        }
      );
  }
}