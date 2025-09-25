import java.time.Duration;
import java.time.Instant;

public class PerMinutePricing implements PricingStrategy {
    private final double pricePerMinute;

    public PerMinutePricing(double pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }

    @Override
    public double calculatePrice(Instant entryTime, Instant payTime) {
        long minutes = Duration.between(entryTime, payTime).toMinutes();
        long payableMinutes = Math.max(1, minutes); // mind. 1 Minute
        return payableMinutes * pricePerMinute;
    }
}
