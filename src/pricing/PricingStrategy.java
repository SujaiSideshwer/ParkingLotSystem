package pricing;

public interface PricingStrategy {
    double calculate(long durationMinutes);
}
