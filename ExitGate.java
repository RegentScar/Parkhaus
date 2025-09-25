public class ExitGate {
    private final String gateId;
    private final ParkingGarage garage;

    public ExitGate(String gateId, ParkingGarage garage) {
        this.gateId = gateId;
        this.garage = garage;
    }

    public boolean tryExit(Ticket ticket) {
        if (ticket == null) {
            System.out.println("Schranke " + gateId + " bleibt geschlossen: kein Ticket vorgelegt.");
            return false;
        }
        if (!ticket.isPaid()) {
            System.out.println("Schranke " + gateId + " bleibt geschlossen: Ticket nicht bezahlt.");
            return false;
        }
        if (ticket.hasExited()) {
            System.out.println("Schranke " + gateId + " bleibt geschlossen: Ticket bereits benutzt.");
            return false;
        }
        boolean ok = garage.registerExit(ticket);
        if (ok) {
            System.out.println("Schranke " + gateId + " öffnet. Gute Fahrt!");
            return true;
        } else {
            System.out.println("Schranke " + gateId + " bleibt geschlossen: System verweigert Ausfahrt.");
            return false;
        }
    }
}
