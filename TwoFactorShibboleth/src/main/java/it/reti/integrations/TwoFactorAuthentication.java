package it.reti.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.RequestedPrincipalContext;

/***
 * Class that implements a two factor authenticator verifying a user token
 * for login.
 * This example class works with Shibboleth IdP 3.0 and is just a simple
 * test class that implements a confrontation with a constant value
 * provided in the idp configuration.
 * 
 * @author Andrea Biancini <andrea.biancini@reti.it>
 */
public class TwoFactorAuthentication {
    private final Logger log = LoggerFactory.getLogger(TwoFactorAuthentication.class);
    
    private final String TOKEN_PRINCIPAL = "urn:oasis:names:tc:SAML:2.0:ac:classes:Token";

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

    private TwoFactorAuthentication(String tokenValue, String username) {
    	this.tokenValue = tokenValue;
        this.username = username;
    }

    /**
     * Method that specified if the two factor authentication with
     * token is enabled or not.
     * @param context the authentication context for the current
     * login session.
     * @return true if the token must be requested, false if not.
     */
    public boolean isTokenEnabled(AuthenticationContext context)
    {
    	if (context == null) {
    		throw new IllegalArgumentException("The authentication context passed is null.");
    	}
    	RequestedPrincipalContext reqContext = context.getSubcontext(RequestedPrincipalContext.class, false);
    	String principalRequested = (reqContext != null) ? reqContext.getMatchingPrincipal().getName() : null;
    	log.info("Logging in using principal: " + ((principalRequested != null) ? principalRequested : "unspecified") + ".");
    	return TOKEN_PRINCIPAL.equals(principalRequested);
    }

    /***
     * Method that verifies the token provided by the user.
     * @param userTokenValue the token provided by the user in the web form. 
     * @return true if the token is correct, false if not.
     */
    public boolean verifyUserToken(String userTokenValue) {
        try {
            if (tokenValue.equals(userTokenValue)) {
                log.info(username + " successfully two-factor authenticated.");
                return true;
            } else {
                log.info(username + " attempted two-factor authentication but wrong token was provided.");
            }
        } catch (Exception e) {
            log.error("An exception occurred while " + username + " attempted a two-factor authentication.", e);
        }
        return false;
    }
}