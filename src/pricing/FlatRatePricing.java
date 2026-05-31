package pricing;

public class FlatRatePricing implements PricingStrategy{
    private double flatRate;

    public FlatRatePricing(double flatRate) {
        this.flatRate = flatRate;
    }

    @Override
    public double calculate(long durationMinutes) {
        return flatRate;
    }
}
