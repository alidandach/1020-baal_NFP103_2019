package flags;

public enum File {
    DATA_SEPARATOR(0xff,"data separator for file data");

    private int value;
    String description;

    File(int hex, String des) {
        value = hex;
        description = des;
    }

    public String getValue() {
        return String.format("0x%08X", value);
    }


}
