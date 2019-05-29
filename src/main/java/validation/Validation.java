package validation;

import java.util.regex.Pattern;

public enum Validation {
    CONNECT("^-u [a-zA-Z]{4,} -p [a-zA-Z]{4,} -h (([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$"),
    CLIENT("^pc[1-9]{1,}"),
    GROUP("^grp[1-9]{1,}"),
    ID("^0xee[1-9]{1,}"),
    PORT("^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))$");

    private final Pattern pattern;

    Validation(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return this.pattern;
    }
}
