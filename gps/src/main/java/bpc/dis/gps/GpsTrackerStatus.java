package bpc.dis.gps;

public enum GpsTrackerStatus {

    TRACKED(1),
    ACCESSS_DENY(2),
    GPS_IS_OFF(3),
    UNHANDLED(4);

    private final int value;

    GpsTrackerStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}