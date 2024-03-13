import { Flight } from "./flight";
import { Route } from "./route";

export interface FlightsResponse{
    flights: Flight[];
    availableRoutes: Route[];
}