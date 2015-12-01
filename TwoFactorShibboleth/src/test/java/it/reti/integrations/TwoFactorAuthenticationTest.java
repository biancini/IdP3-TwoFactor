package it.reti.integrations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.RequestedPrincipalContext;

public class TwoFactorAuthenticationTest {
	
	private static final String TOKEN_VALUE = "123456789";
	private static final String USERNAME = "testuser";
	
	private static final String PASSWORD_PRINCIPAL = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";
	private static final String TOKEN_PRINCIPAL = "urn:oasis:names:tc:SAML:2.0:ac:classes:Token";

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
    
    @Test(expected=IllegalArgumentException.class)
    public void testIsTokenEnabled_whenNoContext() {
    	twoFactorAuthentication.isTokenEnabled(null);
    }
    
    @Test
    public void testIsTokenEnabled_whenNullPrincipal() {
    	RequestedPrincipalContext principalContext = Mockito.mock(RequestedPrincipalContext.class);
    	when(principalContext.getMatchingPrincipal()).thenReturn(null);
    	AuthenticationContext context = new AuthenticationContext();
    	context.addSubcontext(principalContext);
    	boolean enabled = twoFactorAuthentication.isTokenEnabled(context);
    	assertFalse(enabled);
    }
    
    @Test
    public void testIsTokenEnabled_whenPasswordPrincipal() {
    	Principal principal = Mockito.mock(Principal.class);
    	when(principal.getName()).thenReturn(PASSWORD_PRINCIPAL);
    	RequestedPrincipalContext principalContext = new RequestedPrincipalContext();
    	principalContext.setMatchingPrincipal(principal);
    	AuthenticationContext context = new AuthenticationContext();
    	context.addSubcontext(principalContext);
    	boolean enabled = twoFactorAuthentication.isTokenEnabled(context);
    	assertFalse(enabled);
    }
    
    @Test
    public void testIsTokenEnabled_whenTokenPrincipal() {
    	Principal principal = Mockito.mock(Principal.class);
    	when(principal.getName()).thenReturn(TOKEN_PRINCIPAL);
    	RequestedPrincipalContext principalContext = new RequestedPrincipalContext();
    	principalContext.setMatchingPrincipal(principal);
    	AuthenticationContext context = new AuthenticationContext();
    	context.addSubcontext(principalContext);
    	boolean enabled = twoFactorAuthentication.isTokenEnabled(context);
    	assertTrue(enabled);
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
