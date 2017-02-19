package bk.devoxx17.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Sidney on 19/02/2017.
 */

public final class PasswordGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextPassword() {
        return new BigInteger(130, random).toString(32);
    }
}