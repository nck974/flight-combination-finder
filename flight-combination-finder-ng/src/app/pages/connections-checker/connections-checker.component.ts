import { Component, OnDestroy } from '@angular/core';
import { ConnectionsService } from '../../shared/services/connections.service';
import { RoutesQuery } from '../../model/routes-query';
import { Subscription, finalize } from 'rxjs';
import { ResponseError } from '../../model/response-error';
import { RoutesGraph } from '../../model/graph/routes-graph';
import { ConnectionsGraphComponent } from './components/connections-graph/connections-graph.component';
import { LoadingSpinnerComponent } from "../../shared/components/loading-spinner/loading-spinner.component";
import { UserMessagesComponent } from "../../shared/components/user-messages/user-messages.component";

@Component({
  selector: 'app-connections-checker',
  standalone: true,
  templateUrl: './connections-checker.component.html',
  styleUrl: './connections-checker.component.scss',
  imports: [ConnectionsGraphComponent, LoadingSpinnerComponent, UserMessagesComponent]
})
export class ConnectionsCheckerComponent implements OnDestroy {
  private connectionsSubscription?: Subscription;
  isLoading = false;
  error?: ResponseError;
  constructor(private connectionsService: ConnectionsService) { }

  connectionsGraph?: RoutesGraph;


  ngOnDestroy(): void {
    this.connectionsSubscription?.unsubscribe();
  }

  onSearchRoutes() {
    const query: RoutesQuery = {
      origin: "NUE",
      destination: "SDR",
      maxNrConnections: 2
    }
    this.isLoading = true;
  //   this.connectionsGraph = {
  //     "nodes": [
  //         {
  //             "id": 0,
  //             "name": "SDR",
  //             "symbolSize": 50.0,
  //             "value": 50.0,
  //             "category": 0
  //         },
  //         {
  //             "id": 1,
  //             "name": "AGP",
  //             "symbolSize": 50.0,
  //             "value": 50.0,
  //             "category": 1
  //         },
  //         {
  //             "id": 2,
  //             "name": "NUE",
  //             "symbolSize": 50.0,
  //             "value": 50.0,
  //             "category": 2
  //         },
  //         {
  //             "id": 3,
  //             "name": "ALC",
  //             "symbolSize": 50.0,
  //             "value": 50.0,
  //             "category": 3
  //         },
  //         {
  //             "id": 4,
  //             "name": "STN",
  //             "symbolSize": 50.0,
  //             "value": 50.0,
  //             "category": 4
  //         },
  //         {
  //             "id": 5,
  //             "name": "SVQ",
  //             "symbolSize": 50.0,
  //             "value": 50.0,
  //             "category": 5
  //         },
  //         {
  //             "id": 6,
  //             "name": "VLC",
  //             "symbolSize": 50.0,
  //             "value": 50.0,
  //             "category": 6
  //         }
  //     ],
  //     "links": [
  //         {
  //             "source": 0,
  //             "target": 1
  //         },
  //         {
  //             "source": 1,
  //             "target": 2
  //         },
  //         {
  //             "source": 0,
  //             "target": 3
  //         },
  //         {
  //             "source": 3,
  //             "target": 2
  //         },
  //         {
  //             "source": 0,
  //             "target": 4
  //         },
  //         {
  //             "source": 4,
  //             "target": 2
  //         },
  //         {
  //             "source": 0,
  //             "target": 5
  //         },
  //         {
  //             "source": 5,
  //             "target": 2
  //         },
  //         {
  //             "source": 0,
  //             "target": 6
  //         },
  //         {
  //             "source": 6,
  //             "target": 2
  //         }
  //     ],
  //     "categories": [
  //         {
  //             "name": "SDR"
  //         },
  //         {
  //             "name": "AGP"
  //         },
  //         {
  //             "name": "NUE"
  //         },
  //         {
  //             "name": "ALC"
  //         },
  //         {
  //             "name": "STN"
  //         },
  //         {
  //             "name": "SVQ"
  //         },
  //         {
  //             "name": "VLC"
  //         }
  //     ]
  // };
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