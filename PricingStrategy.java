import java.time.Instant;

public interface PricingStrategy {
    double calculatePrice(Instant entryTime, Instant payTime);
}
