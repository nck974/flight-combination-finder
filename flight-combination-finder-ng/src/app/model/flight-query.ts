export interface FlightRoute {
    origin: string;
    destination: string;
}

export interface FlightQuery {
    routes: FlightRoute[];
    startDate: Date;
    endDate: Date;
}