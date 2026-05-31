package ticket;

import parking.ParkingSpot;
import payment.PaymentStatus;
import vehicle.Vehicle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Ticket {
    private String ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private PaymentStatus paymentStatus;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public long getDurationMinutes(){
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(entryTime, end);
    }

    public void markExit(){
        this.exitTime = LocalDateTime.now();
    }

    public void setPaymentStatus(PaymentStatus s){
        this.paymentStatus = s;
    }

    public String getTicketId(){
        return ticketId;
    }

    public Vehicle getVehicle(){
        return vehicle;
    }

    public ParkingSpot getSpot(){
        return spot;
    }

    public LocalDateTime getEntryTime(){
        return entryTime;
    }

    public LocalDateTime getExitTime(){
        return exitTime;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
}
