export interface Flight{
    id: number;
    origin: string;
    destination: string;
    price: number;
    departureDate: Date;
    landingDate: Date;
    createdAt: Date;
    duration: number;
    airlineName: string;
}