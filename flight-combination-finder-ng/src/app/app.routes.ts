import { Routes } from '@angular/router';
import { FlightCheckerComponent } from './pages/flight-checker/flight-checker.component';
import { ConnectionsCheckerComponent } from './pages/connections-checker/connections-checker.component';

export const routes: Routes = [
    { path: '', component: FlightCheckerComponent },
    { path: 'connections', component: ConnectionsCheckerComponent },
];
