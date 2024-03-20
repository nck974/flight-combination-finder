import { Component, Input } from '@angular/core';
import { NgxEchartsDirective, provideEcharts } from 'ngx-echarts';
import { RoutesGraph } from '../../../../model/graph/routes-graph';
import { EChartsOption, registerMap } from 'echarts';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-connections-graph',
  standalone: true,
  imports: [NgxEchartsDirective],
  providers: [
    provideEcharts()
  ],
  templateUrl: './connections-graph.component.html',
  styleUrl: './connections-graph.component.scss'
})
export class ConnectionsGraphComponent {
  @Input({ required: true }) graphData!: RoutesGraph;
  options?: EChartsOption;

  constructor(private http: HttpClient) { }

  // TODO: Move toa  separated component
  loadMap(): void {
    this.http.get('assets/maps/world.json').subscribe((mapData: any) => {
      registerMap('world', mapData);
      this.setGraphOptions();
    });
  }

  ngOnInit(): void {
    console.log(this.graphData);
    this.loadMap()
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
          // name: 'SDR -> NUE',
          type: 'graph',
          // map: "world"
          layout: 'none',
          data: this.graphData.nodes?.map(n => {
            return {
              id: n.id.toString(),
              name: n.name,
              // completeName: n.completeName,
              symbolSize: 15,
              value: n.completeName,
              category: n.category,
              x: n.x,
              y: -n.y,
            };
          }),
          draggable: false,
          links: this.graphData.links?.map(l => {
            return {
              source: l.source,
              target: l.target,
              symbol: ["", "triangle"],
              link: "http://test.com",
              label: {
                show: true,
                formatter: l.routeName,
                color: l.color
              },
              lineStyle: {
                color: l.color,
              }
            };
          }),
          categories: this.graphData.categories as any,
          roam: true,
          label: {
            show: true,
            position: 'right',
            //   formatter: '{b}'
          },
          // labelLayout: {
          //   hideOverlap: true
          // },
          // scaleLimit: {
          //   min: 0.4,
          //   max: 2
          // },
          // lineStyle: {
          //   color: 'source',
          // },
          autoCurveness: true,
        },
        {
          type: 'map',
          map: 'world',
          roam: true,
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: true
            }
          },
          itemStyle: {
            borderColor: 'rgba(0,0,0,0.2)'
          }
        }
      ]
    };
  }
}
