package com.sr.datasync;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    static boolean isValidMail(String email){
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
}
