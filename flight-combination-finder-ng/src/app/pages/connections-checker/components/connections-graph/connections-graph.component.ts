import { Component, Input } from '@angular/core';
import { NgxEchartsDirective, provideEcharts } from 'ngx-echarts';
import { RoutesGraph } from '../../../../model/graph/routes-graph';
import { EChartsOption } from 'echarts';

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

  graph = {
    "nodes": [{
      "id": "0",
      "name": "SDR",
      "symbolSize": 100,
      "x": -200.82776,
      "y": 400.6904,
      "value": 50,
      "category": 0,
      "label": {
        "show": true,
        "formatter": "{b}: {@score}"
      },
    }, {
      "id": "1",
      "name": "STN",
      "symbolSize": 50,
      "x": -212.76357,
      "y": 245.29176,
      "value": 50,
      "category": 0
    }, {
      "id": "2",
      "name": "NUE",
      "symbolSize": 50,
      "x": -379.30386,
      "y": 429.06424,
      "value": 50,
      "category": 0
    }, {
      "id": "3",
      "name": "OVD",
      "symbolSize": 50,
      "x": -332.6012,
      "y": 485.16974,
      "value": 50,
      "category": 0
    },

    ],
    "links": [{
      "source": "0",
      "target": "1",
    }, {
      "source": "1",
      "target": "2",
    }, {
      "source": "1",
      "target": "2",
    }, {
      "source": "3",
      "target": "1",
    },
    {
      "source": 0,
      "target": 1,
      // "symbolSize": [5, 20],
      "label": {
        "show": true,
        "formatter": "OVD -> STN -> SDR"
      },
      "lineStyle": {
        // "width": 5,
      }
    },
    ],
    "categories": [{
      "name": "SDR"
    }, {
      "name": "AGP"
    }, {
      "name": "AGP"
    }, {
      "name": "AGP"
    }, {
      "name": "AGP"
    }
    ]
  };

  ngOnInit(): void {
    console.log(this.graphData);
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
          layout: 'circular',
          data: this.graphData.nodes as any,
          draggable: true,
          links: this.graphData.links?.map(a => {
            return {
              source: a.source,
              target: a.target,
              symbol: ["", "triangle"],
              link: "http://test.com",
              label: {
                show: true,
                formatter: a.routeName,
              }
            }
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
        }
      ]
    };
  }

}
