import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ParkingGarage {
    private final int capacity;
    private final PricingStrategy pricing;
    private int occupied = 0;
    private int ticketCounter = 0;

    private final Map<Integer, Ticket> activeTickets = new HashMap<>();

    public ParkingGarage(int capacity, PricingStrategy pricing) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Kapazität > 0 erforderlich.");
        this.capacity = capacity;
        this.pricing = pricing;
    }

    public int getFreeSpots() {
        return capacity - occupied;
    }

    /** Gibt ein Ticket zurück oder null, wenn voll. */
    Ticket issueTicketOrNull() {
        if (occupied >= capacity)
            return null;
        int id = ++ticketCounter;
        Ticket t = new Ticket(id, Instant.now());
        activeTickets.put(id, t);
        occupied++;
        return t;
    }

    /** Prüft & registriert die Ausfahrt. */
    public boolean registerExit(Ticket ticket) {
        if (ticket == null)
            return false;
        Ticket stored = activeTickets.get(ticket.getId());
        if (stored == null)
            return false;
        if (!stored.isPaid() || stored.hasExited())
            return false;

        stored.markExited();
        activeTickets.remove(stored.getId());
        occupied = Math.max(0, occupied - 1);
        return true;
    }
}
