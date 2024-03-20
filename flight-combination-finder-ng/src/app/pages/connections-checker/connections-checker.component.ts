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

  connectionsGraph?: RoutesGraph = {"nodes":[{"id":0,"name":"NUE","completeName":"NURNBERG","symbolSize":30.0,"value":50.0,"category":0,"x":11.078,"y":49.499},{"id":1,"name":"AGP","completeName":"MALAGA","symbolSize":30.0,"value":50.0,"category":1,"x":-4.499,"y":36.674},{"id":2,"name":"SDR","completeName":"SANTANDER","symbolSize":30.0,"value":50.0,"category":2,"x":-3.82,"y":43.427},{"id":3,"name":"ALC","completeName":"ALICANTE","symbolSize":30.0,"value":50.0,"category":3,"x":-0.558,"y":38.282},{"id":4,"name":"STN","completeName":"STANSTED","symbolSize":30.0,"value":50.0,"category":4,"x":0.235,"y":51.885},{"id":5,"name":"SVQ","completeName":"SEVILLA","symbolSize":30.0,"value":50.0,"category":5,"x":-5.893,"y":37.418},{"id":6,"name":"VLC","completeName":"VALENCIA","symbolSize":30.0,"value":50.0,"category":6,"x":-0.481,"y":39.489}],"links":[{"source":0,"target":1,"routeName":"NUE->AGP->SDR","color":"#9FFB95"},{"source":1,"target":2,"routeName":"NUE->AGP->SDR","color":"#9FFB95"},{"source":0,"target":3,"routeName":"NUE->ALC->SDR","color":"#5E341C"},{"source":3,"target":2,"routeName":"NUE->ALC->SDR","color":"#5E341C"},{"source":0,"target":4,"routeName":"NUE->STN->SDR","color":"#0D881A"},{"source":4,"target":2,"routeName":"NUE->STN->SDR","color":"#0D881A"},{"source":0,"target":5,"routeName":"NUE->SVQ->SDR","color":"#B253F6"},{"source":5,"target":2,"routeName":"NUE->SVQ->SDR","color":"#B253F6"},{"source":0,"target":6,"routeName":"NUE->VLC->SDR","color":"#3947BB"},{"source":6,"target":2,"routeName":"NUE->VLC->SDR","color":"#3947BB"}],"categories":[{"name":"NUE"},{"name":"AGP"},{"name":"SDR"},{"name":"ALC"},{"name":"STN"},{"name":"SVQ"},{"name":"VLC"}]};
  // connectionsGraph?: RoutesGraph;


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