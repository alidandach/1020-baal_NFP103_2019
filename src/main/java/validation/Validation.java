package validation;

import java.util.regex.Pattern;

public enum Validation {
    CONNECT("^-u [a-zA-Z]{4,} -p [a-zA-Z]{5,} -h (([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$"),
    CLIENT("^pc[0-9]{1,}");

    private final Pattern pattern;

    Validation(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return this.pattern;
    }
}
