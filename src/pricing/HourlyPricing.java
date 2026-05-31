package pricing;

public class HourlyPricing implements PricingStrategy{
    private double ratePerHour;

    public HourlyPricing(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    @Override
    public double calculate(long durationMinutes) {
        long hours = (long) Math.ceil(durationMinutes / 60.0);
        return hours * ratePerHour;
    }
}
