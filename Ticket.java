import java.time.Instant;

public class Ticket {
    private final int id;
    private final Instant entryTime;
    private boolean paid;
    private boolean exited;

    Ticket(int id, Instant entryTime) {
        this.id = id;
        this.entryTime = entryTime;
    }

    public int getId() {
        return id;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean hasExited() {
        return exited;
    }

    void markPaid() {
        this.paid = true;
    }

    void markExited() {
        this.exited = true;
    }

    @Override
    public String toString() {
        return "Ticket#" + id + " [paid=" + paid + ", exited=" + exited + "]";
    }
}
