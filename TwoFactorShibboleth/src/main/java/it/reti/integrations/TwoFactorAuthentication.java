package it.reti.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoFactorAuthentication {
    private final Logger log = LoggerFactory.getLogger(TwoFactorAuthentication.class);

    // This variable will be used for test to permit a check against a constant token.
    private final String tokenValue;
    
    private final String username;

    public static TwoFactorAuthentication instance(String tokenValue, String username) {
    	if (tokenValue == null) {
            throw new IllegalArgumentException("Null value not allowed for tokenValue");
        }
        if (username == null) {
            throw new IllegalArgumentException("Null value not allowed for username");
        }
        return new TwoFactorAuthentication(tokenValue, username);
    }

    private TwoFactorAuthentication() {
    	this.tokenValue = null;
        this.username = null;
    }

    private TwoFactorAuthentication(String tokenValue, String username) {
    	this.tokenValue = tokenValue;
        this.username = username;
    }

    public boolean verifyUserToken(String userTokenValue) {
        try {
            if (tokenValue.equals(userTokenValue)) {
                log.info(username + " successfully two-factor authenticated.");
                return true;
            } else {
                log.info(username + " attempted two-factor authentication but wrong token was provided.");
                log.info("to be removed token: " + tokenValue + " passed token: " + userTokenValue);
            }
        } catch (Exception e) {
            log.error("An exception occurred while " + username + " attempted a two-factor authentication.", e);
        }
        return false;
    }
}