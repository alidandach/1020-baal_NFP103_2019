package validation;

import flags.Identity;

import java.util.regex.Pattern;

public enum Validation {
    CONNECT("^-u [a-zA-Z]{4,} -p [a-zA-Z]{4,} -h (([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$"),
    CLIENT("^pc[1-9]{1,}"),
    GROUP("^grp[1-9]{1,}"),
    ID("^" + Identity.ID.getValue() + "[1-9]{1,}"),
    PORT("^(102[5-9]|10[3-9]\\d|1[1-9]\\d{2}|[2-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$"),
    FILE("^(([a-zA-Z]:)|((\\\\|/){1,2}\\w+)\\$?)((\\\\|/)(\\w[\\w ]*.*))+\\.([a-zA-Z0-9]+)$");

    private final Pattern pattern;

    Validation(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return this.pattern;
    }
}
