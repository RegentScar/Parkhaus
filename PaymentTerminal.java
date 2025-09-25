import java.time.Instant;
import java.util.Locale;

public class PaymentTerminal {
    private final String terminalId;
    private final PricingStrategy pricing;

    public PaymentTerminal(String terminalId, PricingStrategy pricing) {
        this.terminalId = terminalId;
        this.pricing = pricing;
    }

    public double pay(Ticket ticket, Instant payTime) {
        if (ticket == null)
            throw new IllegalArgumentException("Ticket darf nicht null sein.");
        if (ticket.isPaid()) {
            System.out.println("[" + terminalId + "] Ticket " + ticket.getId() + " ist bereits bezahlt.");
            return 0.0;
        }
        double amount = pricing.calculatePrice(ticket.getEntryTime(), payTime);
        ticket.markPaid();
        System.out.printf(Locale.GERMANY, "[%s] Zahlung für Ticket #%d: %.2f CHF%n",
                terminalId, ticket.getId(), amount);
        return amount;
    }
}
