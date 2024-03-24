import { Routes } from '@angular/router';
import { FlightCheckerComponent } from './pages/flight-checker/flight-checker.component';
import { ConnectionsCheckerComponent } from './pages/connections-checker/connections-checker.component';

export const routes: Routes = [
    { path: '', pathMatch: "full", redirectTo: "flights" },
    { path: 'flights', component: FlightCheckerComponent },
    { path: 'connections', component: ConnectionsCheckerComponent },
];
