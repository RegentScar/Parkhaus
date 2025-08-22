import java.time.*;

public class Parkhaus {

    public static void main(String[] args) throws InterruptedException {
        // ==== Setup ====
        PricingStrategy pricing = new PerMinutePricing(0.10); // 10 Rappen/Minute
        ParkingGarage garage = new ParkingGarage(10, pricing); // 10 Plätze

        EntryGate entryGate = new EntryGate("E1", garage);
        ExitGate exitGateA = new ExitGate("A1", garage);
        ExitGate exitGateB = new ExitGate("A2", garage);

        PaymentTerminal kasse = new PaymentTerminal("Kasse-1", pricing);

        // ==== Simulation ====
        System.out.println("Freie Plätze zu Beginn: " + garage.getFreeSpots());

        Ticket t1 = entryGate.requestEntry();
        System.out.println("Ticket t1 = " + t1);

        Ticket t2 = entryGate.requestEntry();
        System.out.println("Ticket t2 = " + t2);

        System.out.println("Freie Plätze nach 2 Einfahrten: " + garage.getFreeSpots());

        // Parkzeit simulieren
        Thread.sleep(3000); // 3 Sek.

        // Bezahlen von Ticket 1
        kasse.pay(t1, Instant.now());

        // Ausfahrt mit unbezahltem Ticket 2 -> verweigern
        boolean out2fail = exitGateA.tryExit(t2);
        System.out.println("Ausfahrt t2 (unbezahlt): " + (out2fail ? "geöffnet" : "verweigert"));

        // Ticket 2 bezahlen
        kasse.pay(t2, Instant.now());

        // Ausfahrten
        boolean out1 = exitGateA.tryExit(t1);
        boolean out2 = exitGateB.tryExit(t2);
        System.out.println("Ausfahrt t1 über A1: " + (out1 ? "geöffnet" : "verweigert"));
        System.out.println("Ausfahrt t2 über A2: " + (out2 ? "geöffnet" : "verweigert"));

        System.out.println("Freie Plätze am Ende: " + garage.getFreeSpots());
    }
}
