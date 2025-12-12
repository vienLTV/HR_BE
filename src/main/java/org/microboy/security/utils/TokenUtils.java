package org.microboy.security.utils;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.microboy.security.enums.Role;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TokenUtils {

	@ConfigProperty(name = "smallrye.jwt.sign.key.location") String privateKey;

	public String generateToken(String accountEmail,
	                                   UUID organizationId,
	                                   Set<Role> roles,
	                                   Long duration,
	                                   String issuer) throws Exception {
		String privateKeyLocation = privateKey;
		PrivateKey privateKey = readPrivateKey(privateKeyLocation);

		JwtClaimsBuilder claimsBuilder = Jwt.claims();
		long currentTimeInSecs = currentTimeInSecs();

		Set<String> groups = roles.stream().map(Role::name).collect(Collectors.toSet());
		claimsBuilder.issuer(issuer);
		claimsBuilder.subject(accountEmail);
		claimsBuilder.issuedAt(currentTimeInSecs);
		claimsBuilder.expiresAt(currentTimeInSecs + duration);
		claimsBuilder.groups(groups);
		claimsBuilder.claim("organizationId", organizationId);

		return claimsBuilder.jws().signatureKeyId(privateKeyLocation).sign(privateKey);
	}

	public PrivateKey readPrivateKey(final String pemResName) throws Exception {
		try (InputStream contentIS = getClass().getResourceAsStream(pemResName)) {
			byte[] tmp = new byte[4096];
			assert contentIS != null;
			int length = contentIS.read(tmp);
			return decodePrivateKey(new String(tmp, 0, length, "UTF-8"));
		}
	}

	public PrivateKey decodePrivateKey(final String pemEncoded) throws Exception {
		byte[] encodedBytes = toEncodedBytes(pemEncoded);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(keySpec);
	}

	public byte[] toEncodedBytes(final String pemEncoded) {
		final String normalizedPem = removeBeginEnd(pemEncoded);
		return Base64.getDecoder().decode(normalizedPem);
	}

	public String removeBeginEnd(String pem) {
		pem = pem.replaceAll("-----BEGIN (.*)-----", "");
		pem = pem.replaceAll("-----END (.*)----", "");
		pem = pem.replaceAll("\r\n", "");
		pem = pem.replaceAll("\n", "");
		return pem.trim();
	}

	public int currentTimeInSecs() {
		long currentTimeMS = System.currentTimeMillis();
		return (int) (currentTimeMS / 1000);
	}
}
