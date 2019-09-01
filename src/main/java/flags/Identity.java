package flags;

public enum Identity {
    ID(0Xee, "id of client");

    private String description;
    private int value;

    Identity(int hex, String des) {
        value = hex;
        description = des;
    }

    public String getValue() {
        return String.format("0x%08X", value);
    }
}
