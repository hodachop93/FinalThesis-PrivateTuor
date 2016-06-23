package com.hodachop93.hohoda.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hop on 13/04/2016.
 */
public final class ValidateDataUtils {

    /**
     * Checks if is validate mail.
     *
     * @param mail the mail
     * @return true, if is validate mail
     */
    public static boolean isValidateMail(String mail) {
        Pattern pat = Pattern
                .compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pat.matcher(mail);
        return matcher.matches();
    }

    public static boolean isValidUserName(String userName) {
        Pattern pattern = Pattern.compile("^[A-Za-z][a-zA-Z0-9]*([_.]?[0-9a-zA-Z]+)*$");
        return pattern.matcher(userName).matches() && userName.length() <= 45 && userName.length() >= 3;
    }

    public static boolean isValidPassword(String password) {
        // Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$");
        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.{6,})[a-zA-Z0-9]*$");
        return pattern.matcher(password).matches();
    }

    public static boolean isValidHashTag(String name) {
        Pattern pattern = Pattern.compile("^[#]?[a-zA-Z][0-9a-zA-Z]+");
        return pattern.matcher(name).matches();
    }
}
