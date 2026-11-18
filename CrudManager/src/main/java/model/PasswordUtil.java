package model;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

	private static final int ITERATIONS = 120000;
	private static final int KEY_LENGTH = 256;

	// Gera o hash no formato iteracoes:salt:hash
	public static String hash(String password) {
		byte[] salt = new byte[16];
		new SecureRandom().nextBytes(salt);

		byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS);

		return ITERATIONS + ":"
				+ Base64.getEncoder().encodeToString(salt) + ":"
				+ Base64.getEncoder().encodeToString(hash);
	}

	// Confere se a senha digitada bate com o hash guardado no banco
	public static boolean verify(String password, String stored) {
		try {
			String[] parts = stored.split(":");
			int iterations = Integer.parseInt(parts[0]);
			byte[] salt = Base64.getDecoder().decode(parts[1]);
			byte[] expected = Base64.getDecoder().decode(parts[2]);

			byte[] actual = pbkdf2(password.toCharArray(), salt, iterations);

			return MessageDigest.isEqual(expected, actual);
		} catch (Exception e) {
			return false;
		}
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
					.generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
	}

	// Para criar o primeiro usuário: rode esta classe e copie o hash impresso.
	// Uso: java model.PasswordUtil minhaSenha
	public static void main(String[] args) {
		String password = args.length > 0 ? args[0] : "123456";
		System.out.println(hash(password));
	}
}
