package payment;

import pricing.PricingStrategy;
import ticket.Ticket;

public class PaymentService {
    private PricingStrategy pricingStrategy;

    public PaymentService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public double calculateFee(Ticket ticket){
        return pricingStrategy.calculate(ticket.getDurationMinutes());
    }

    public boolean processPayment(Ticket ticket){
        double fee = calculateFee(ticket);
        System.out.println("Processing payment of Rs: " + fee +
                " for ticket " + ticket.getTicketId());
        ticket.setPaymentStatus(PaymentStatus.PAID);
        return true;
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }
}
