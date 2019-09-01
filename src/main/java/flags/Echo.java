package flags;

public enum Echo {
    ECHO_CLIENT(0x6563686fL, "get public key "),
    POSITIVE_ACK(0x706f736974697665L, "positive signal"),
    NEGATIVE_ACK(0x6e65676174697665L, "negative signal"),
    ;

    private String description;
    private long value;

    Echo(long value, String desc) {
        this.value = value;
        this.description = desc;
    }

    public String getValue() {
        return String.format("0x%08X", value);
    }
}
