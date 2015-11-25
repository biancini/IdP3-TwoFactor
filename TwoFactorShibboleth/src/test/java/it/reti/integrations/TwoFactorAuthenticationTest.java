package it.reti.integrations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TwoFactorAuthenticationTest {
	
	private static final String TOKEN_VALUE = "123456789";
	private static final String USERNAME = "testuser";

	TwoFactorAuthentication twoFactorAuthentication;

    @Before
    public void setUp() {
    	twoFactorAuthentication = TwoFactorAuthentication.instance(TOKEN_VALUE, USERNAME);
    }

    @Test
    public void testNoPublicConstructors() throws ClassNotFoundException {
        Class<?> twoFactorAuthenticationClass = Class.forName("it.reti.integrations.TwoFactorAuthentication");
        assertEquals(0, twoFactorAuthenticationClass.getConstructors().length);
    }

    @Test
    public void testNotSingleton() {
    	TwoFactorAuthentication otherTwoFactorAuthentication = TwoFactorAuthentication.instance(TOKEN_VALUE, USERNAME);
        assertFalse(twoFactorAuthentication == otherTwoFactorAuthentication);
    }

    @Test
    public void testVerifyUserToken_whenResponseValid() {
        assertTrue(twoFactorAuthentication.verifyUserToken(TOKEN_VALUE));
    }

    @Test
    public void testVerifyUserToken_whenResponseBad() {
        String junkToken = "junkresponse";
        assertFalse(twoFactorAuthentication.verifyUserToken(junkToken));
    }

}
