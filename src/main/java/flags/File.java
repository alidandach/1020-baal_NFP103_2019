package flags;

public enum File {
    DATA_SEPARATOR(0xff, "data separator for file data");

    private String description;
    private int value;

    File(int hex, String des) {
        value = hex;
        description = des;
    }

    public String getValue() {
        return String.format("0x%08X", value);
    }


}
