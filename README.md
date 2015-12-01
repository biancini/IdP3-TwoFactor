# Overview

Two Factor authentication module example for Shibboleth IdP v3.
The module doesn't do anything complex, just confront the token provided by the user
during login with a constant token in the idp.properties configuration file.

This module is intended to show how the login process for Shibboleth could be modified to
include custom modules during login.

# Installation

The installation for this module goes as follows:

1. copy all files in the IDP_HOME folder to your $IDP_HOME folder

1. build the webapp so that the new jar developed will be added to the libraries for the
   webapplication by executing the command `$IDP_HOME/bin/build.sh`
   
1. edit idp.properties located at `$IDP_HOME/conf/idp.properties`, adding the following to
   the bottom of the file: 
   ```
   twofactor.token = 123456789
   ```
   
1. edit `conditions-flow.xml` located at `$IDP_HOME/flows/authn/conditions/conditions-flow.xml`,
   adding the following to the top of the `<action-state id="ValidateUsernamePassword">` section:

   ```
   <!-- Enable Two-Factor Authentication -->
   <evaluate expression="ValidateUsernamePassword" />
   <evaluate expression="'twofactor'" />
   <transition on="twofactor" to="TwoFactorAuth" />
   <!-- End Two-Factor Authentication -->
   ```
   
   also, add the following just before the closing `</flow>` tag:
   ```
   <subflow-state id="TwoFactorAuth" subflow="authn/twofactor">
       <input name="calledAsSubflow" value="true" />
       <transition on="proceed" to="proceed" />
   </subflow-state>
   ```
   
1. edit `general-authn.xml` located at `$IDP_HOME/conf/authn/general-authn.xml` to add the
   new authentication context for requesting a token:
   ```
   <bean id="authn/Password" parent="shibboleth.AuthenticationFlow"
        p:passiveAuthenticationSupported="true"
        p:forcedAuthenticationSupported="true">
       <property name="supportedPrincipals">
           <list>
               <bean parent="shibboleth.SAML2AuthnContextClassRef"
                   c:classRef="urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport" />
               <bean parent="shibboleth.SAML2AuthnContextClassRef"
                   c:classRef="urn:oasis:names:tc:SAML:2.0:ac:classes:Password" />
               <bean parent="shibboleth.SAML2AuthnContextClassRef"
                   c:classRef="urn:oasis:names:tc:SAML:2.0:ac:classes:Token" />
               <bean parent="shibboleth.SAML1AuthenticationMethod"
                   c:method="urn:oasis:names:tc:SAML:1.0:am:password" />
           </list>
       </property>
   </bean>
   ```
   
1. restart your application server.