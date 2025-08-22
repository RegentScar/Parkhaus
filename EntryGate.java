public class EntryGate {
    private final String gateId;
    private final ParkingGarage garage;

    public EntryGate(String gateId, ParkingGarage garage) {
        this.gateId = gateId;
        this.garage = garage;
    }

    public Ticket requestEntry() {
        Ticket t = garage.issueTicketOrNull();
        if (t != null) {
            System.out.println("Schranke " + gateId + " öffnet. Ticket #" + t.getId() + " ausgegeben.");
            return t;
        } else {
            System.out.println("Schranke " + gateId + " bleibt geschlossen: keine freien Plätze.");
            return null;
        }
    }
}
