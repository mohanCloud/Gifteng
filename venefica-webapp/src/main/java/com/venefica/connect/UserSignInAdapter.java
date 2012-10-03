package com.venefica.connect;

import javax.inject.Inject;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.context.request.NativeWebRequest;

import com.venefica.auth.Token;
import com.venefica.auth.TokenEncryptor;

/**
 * Creates a session for interactions between mobile application and web-services of the application
 * and passes session id as a parameter.
 * 
 * @author Sviatoslav Grebenchukov
 */
public class UserSignInAdapter implements SignInAdapter {

	public final static String PROFILE_URL = "/profile";
	
	private final Log log = LogFactory.getLog(UserSignInAdapter.class);

	@Inject
	private TokenEncryptor tokenEncryptor;

	@Override
	public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
		try {
			Token token = new Token(Long.parseLong(userId));
			String encryptedToken = tokenEncryptor.encrypt(token);

			return URIBuilder.fromUri(PROFILE_URL).queryParam(TOKEN_PARAM, encryptedToken).build()
					.toString();
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	private final static String TOKEN_PARAM = "token";
}
