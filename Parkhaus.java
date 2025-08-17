import java.time.*;
import java.util.*;

public class Parkhaus {

    public static void main(String[] args) throws InterruptedException {
        // ==== Setup ====
        PricingStrategy pricing = new PerMinutePricing(0.10); // 10 Rappen/Minute
        ParkingGarage garage = new ParkingGarage(10, pricing); // 10 Plätze

        EntryGate entryGate = new EntryGate("E1", garage);
        ExitGate exitGateA = new ExitGate("A1", garage);
        ExitGate exitGateB = new ExitGate("A2", garage);

        PaymentTerminal terminalFloor1 = new PaymentTerminal("Kasse-1", pricing);

        // Simulation
        System.out.println("Freie Plätze zu Beginn: " + garage.getFreeSpots());
        Ticket t1 = entryGate.requestEntry();
        System.out.println("Ticket t1 = " + t1);

        Ticket t2 = entryGate.requestEntry();
        System.out.println("Ticket t2 = " + t2);

        System.out.println("Freie Plätze nach 2 Einfahrten: " + garage.getFreeSpots());

        // Warte etwas, um Parkzeit zu simulieren
        Thread.sleep(3000); // 3 Sek.

        // Bezahlen von Ticket 1 an Kasse
        terminalFloor1.pay(t1, Instant.now());

        // Versuche Ausfahrt mit unbezahltem Ticket 2 -> sollte NICHT öffnen
        boolean out2fail = exitGateA.tryExit(t2);
        System.out.println("Ausfahrt t2 (unbezahlt): " + (out2fail ? "geöffnet" : "verweigert"));

        // Bezahle Ticket 2
        terminalFloor1.pay(t2, Instant.now());

        // Ausfahrt über Gate A und Gate B (zwei Ausfahrten)
        boolean out1 = exitGateA.tryExit(t1);
        boolean out2 = exitGateB.tryExit(t2);
        System.out.println("Ausfahrt t1 über A1: " + (out1 ? "geöffnet" : "verweigert"));
        System.out.println("Ausfahrt t2 über A2: " + (out2 ? "geöffnet" : "verweigert"));

        System.out.println("Freie Plätze am Ende: " + garage.getFreeSpots());
    }
}

/**
 * Hält nur Ticket-bezogene Daten/Zustand.
 * Kapselung: Felder privat, Zugriff über Methoden.
 */
class Ticket {
    private final int id;
    private final Instant entryTime;
    private boolean paid;
    private boolean exited;

    Ticket(int id, Instant entryTime) {
        this.id = id;
        this.entryTime = entryTime;
        this.paid = false;
        this.exited = false;
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

    public void markPaid() {
        this.paid = true;
    }

    public void markExited() {
        this.exited = true;
    }

    @Override
    public String toString() {
        return "Ticket#" + id + " [paid=" + paid + ", exited=" + exited + "]";
    }
}

/**
 * PaymentTerminal hängt von diesem Interface ab.
 * Neue Preislogiken lassen sich hinzufügen, ohne bestehende Klassen zu ändern.
 */
interface PricingStrategy {
    /**
     * Berechnet den zu zahlenden Betrag für die Parkdauer von entryTime bis
     * payTime.
     */
    double calculatePrice(Instant entryTime, Instant payTime);
}

/**
 * Minutenpreis, auf volle Minuten aufgerundet (mind. 1 Minute).
 */
class PerMinutePricing implements PricingStrategy {
    private final double pricePerMinute;

    public PerMinutePricing(double pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }

    @Override
    public double calculatePrice(Instant entryTime, Instant payTime) {
        long minutes = Duration.between(entryTime, payTime).toMinutes();
        long payableMinutes = Math.max(1, minutes); // min. 1 Minute
        return payableMinutes * pricePerMinute;
    }
}

/**
 * Basisklasse für Einfahrts- und Ausfahrtschranken.
 */
abstract class Gate {
    private final String gateId;

    protected Gate(String gateId) {
        this.gateId = gateId;
    }

    public String getGateId() {
        return gateId;
    }

    protected void open() {
        System.out.println("Schranke " + gateId + " öffnet.");
    }

    protected void deny(String reason) {
        System.out.println("Schranke " + gateId + " bleibt geschlossen: " + reason);
    }
}

/**
 * Verantwortlich für den Einlassprozess.
 */
class EntryGate extends Gate {
    private final ParkingGarage garage;

    public EntryGate(String gateId, ParkingGarage garage) {
        super(gateId);
        this.garage = garage;
    }

    /**
     * Fahrer drückt "Ticket ziehen". Gibt Ticket aus, wenn Plätze frei sind,
     * Schranke öffnet dann.
     */
    public Ticket requestEntry() {
        Optional<Ticket> maybe = garage.tryIssueTicket();
        if (maybe.isPresent()) {
            open();
            return maybe.get();
        } else {
            deny("Keine freien Plätze.");
            return null;
        }
    }
}

/**
 * Verantwortlich für die Ausfahrtprüfung: Ticket muss bezahlt sein und noch
 * nicht benutzt.
 */
class ExitGate extends Gate {
    private final ParkingGarage garage;

    public ExitGate(String gateId, ParkingGarage garage) {
        super(gateId);
        this.garage = garage;
    }

    /**
     * Versucht Ausfahrt. Öffnet nur bei bezahltem, gültigem Ticket.
     */
    public boolean tryExit(Ticket ticket) {
        if (ticket == null) {
            deny("Kein Ticket vorgelegt.");
            return false;
        }
        if (!ticket.isPaid()) {
            deny("Ticket ist nicht bezahlt.");
            return false;
        }
        if (ticket.hasExited()) {
            deny("Ticket bereits benutzt.");
            return false;
        }
        boolean ok = garage.registerExit(ticket);
        if (ok) {
            open();
            return true;
        } else {
            deny("System verweigert Ausfahrt.");
            return false;
        }
    }
}