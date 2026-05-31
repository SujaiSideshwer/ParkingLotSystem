package parkinglot;

import floor.Floor;
import parking.ParkingSpot;
import payment.PaymentService;
import pricing.HourlyPricing;
import ticket.Ticket;
import vehicle.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingLot {
    private static volatile ParkingLot instance;
    private String name;
    private List<Floor> floors;
    private Map<String, Ticket> activeTickets;
    private PaymentService paymentService;

    private ParkingLot(String name){
        this.name = name;
        this.floors = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.paymentService = new PaymentService(new HourlyPricing(50.0));
    }

    public static ParkingLot getInstance(){
        if(instance == null){
            synchronized (ParkingLot.class){
                if(instance == null){
                    instance = new ParkingLot("Main Lot");
                }
            }
        }

        return instance;
    }

    public void addFloor(Floor floor){
        floors.add(floor);
    }

    public synchronized Ticket parkVehicle(Vehicle vehicle){
        for(Floor floor:floors){
            ParkingSpot spot = floor.findAvailableSpot(vehicle);
            if(spot != null){
                spot.assignVehicle(vehicle);
                Ticket ticket = new Ticket(vehicle, spot);
                activeTickets.put(ticket.getTicketId(), ticket);
                return ticket;
            }
        }
        return null;
    }

    public synchronized double releaseSpot(String ticketId){
        Ticket ticket = activeTickets.get(ticketId);
        if(ticket == null) throw new IllegalArgumentException(("Invalid ticket"));

        ticket.markExit();
        double fee = paymentService.calculateFee(ticket);
        paymentService.processPayment(ticket);
        ticket.getSpot().removeVehicle();
        activeTickets.remove(ticketId);
        return fee;
    }

    public boolean isFull(){
        return floors.stream().allMatch(Floor::isFull);
    }

    public String getName() {
        return name;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
