import { Component, Input, OnInit, Output } from '@angular/core';
import { NgxEchartsDirective, provideEcharts } from 'ngx-echarts';
import { RoutesGraph } from '../../../../model/graph/routes-graph';
import { EChartsOption, registerMap } from 'echarts';
import { finalize } from 'rxjs';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { MapsService } from '../../../../shared/services/maps.service';

@Component({
  selector: 'app-connections-graph',
  standalone: true,
  imports: [NgxEchartsDirective, LoadingSpinnerComponent],
  providers: [
    provideEcharts()
  ],
  templateUrl: './connections-graph.component.html',
  styleUrl: './connections-graph.component.scss'
})
export class ConnectionsGraphComponent implements OnInit {
  @Input({ required: true }) graphData!: RoutesGraph;
  @Output() isLoading: boolean = false;
  options?: EChartsOption;

  constructor(private mapService: MapsService) { }


  ngOnInit(): void {
    this.loadMap()
  }


  loadMap(): void {
    this.isLoading = true;
    this.mapService.getWorldMap().pipe(
      finalize(() => this.isLoading = false)
    ).subscribe((mapData: any) => {
      registerMap('world', mapData);
      this.setGraphOptions();
    });
  }

  private setGraphOptions() {
    this.options = {
      responsive: true,
      tooltip: {},
      legend: [
        {
          data: this.graphData.categories!.map(a => a.name as string)
        }
      ],
      series: [
        {
          type: 'graph',
          coordinateSystem: 'geo',
          layout: 'none',
          roam: true,
          label: {
            show: true,
            position: 'right',
          },
          autoCurveness: true, // Allow  multiple connections between the same nodes
          draggable: false,
          // Set airports in the map
          data: this.graphData.nodes?.map(n => {
            return {
              id: n.id.toString(),
              name: n.name,
              symbolSize: 15,
              value: [n.x, n.y],
              category: n.category,
              tooltip: n.completeName,
              emphasis: {
                label: {
                  show: true,
                  formatter: n.completeName
                },

              },
            };
          }),
          // Set connections between maps
          links: this.graphData.links?.map(l => {
            return {
              source: l.source,
              target: l.target,
              symbol: ["", "triangle"],
              label: {
                show: true,
                formatter: l.routeName,
                color: l.color
              },
              lineStyle: {
                color: l.color,
              },
              tooltip: {
                show: true,
                showDelay: 1000,
                hideDelay: 5000,
                appendToBody: true,
                extraCssText: 'pointer-events: auto!important',
                formatter: `<a target="_blank" rel="noopener noreferrer" href="/flights?${l.url}&submit=true">${l.routeName}</a>`
              },
              emphasis: {
                label: {
                  show: true,
                  formatter: l.routeName
                },
                lineStyle: {
                  width: 10
                }
              },
            };
          }),
          // Set the clickable labels on top of the map
          categories: this.graphData.categories as any,
        },
      ],
      // Configure the world map
      geo: [
        {
          map: 'world',
          roam: true,
          label: {
            show: false,
          },
          tooltip: {
            show: false,
          },
          emphasis: {
            label: {
              show: false,
            },
            disabled: true
          },
        }
      ],
    };
  }
}
