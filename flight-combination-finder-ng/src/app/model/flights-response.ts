import { Flight } from "./flight";

export interface FlightsResponse{
    flights: Flight[];
    availableRoutes: Flight[];
}