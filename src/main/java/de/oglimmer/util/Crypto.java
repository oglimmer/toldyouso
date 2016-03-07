package de.oglimmer.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomUtils;
import org.mindrot.jbcrypt.BCrypt;

import lombok.SneakyThrows;

public enum Crypto {
	INSTANCE;

	public String calcPasswordHash(String plainText) {
		return BCrypt.hashpw(plainText, BCrypt.gensalt());
	}

	public boolean calcPasswordHash(String plaintext, String hashed) {
		return BCrypt.checkpw(plaintext, hashed);
	}

	public String createFactPassword(String loginPassword, byte[] initVec) {
		byte[] key = RandomUtils.nextBytes(16);
		Service service = new Service(loginPassword, initVec);
		return service.encrypt(service.enc(key));
	}

	public String cryptFact(String fact, String password, String factPassword, byte[] initVector) {
		Service service = new Service(password, initVector);
		String decrypted = service.decrypt(factPassword);
		byte[] key = service.dec(decrypted);
		Service service2 = new Service(key, initVector);
		return service2.encrypt(fact);
	}

	public String decryptFact(String encryptedFact, String password, String factPassword, byte[] initVector) {
		Service service = new Service(password, initVector);
		String decrypted = service.decrypt(factPassword);
		byte[] key = service.dec(decrypted);
		Service service2 = new Service(key, initVector);
		return service2.decrypt(encryptedFact);
	}

	static class Service {

		private Cipher deCipher;
		private Cipher enCipher;
		private SecretKeySpec key;
		private IvParameterSpec ivSpec;

		public Service(byte[] keyPhrase, byte[] initVector) {
			try {
				ivSpec = new IvParameterSpec(initVector);
				key = new SecretKeySpec(keyPhrase, "AES");
				deCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				enCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			}
		}

		@SneakyThrows(value = UnsupportedEncodingException.class)
		public Service(String keyPhrase, byte[] initVector) {
			try {
				ivSpec = new IvParameterSpec(initVector);
				key = new SecretKeySpec(hashTo128bit(keyPhrase), "AES");
				deCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				enCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			}
		}

		private byte[] hashTo128bit(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
			byte[] key = (str).getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			return key;
		}

		@SneakyThrows(value = { InvalidKeyException.class, InvalidAlgorithmParameterException.class,
				IllegalBlockSizeException.class, BadPaddingException.class })
		public String encrypt(String obj) {
			byte[] input = obj.getBytes();
			enCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			return enc(enCipher.doFinal(input));
		}

		@SneakyThrows(value = { InvalidKeyException.class, InvalidAlgorithmParameterException.class,
				IllegalBlockSizeException.class, BadPaddingException.class })
		public String decrypt(String encrypted) {
			deCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			return new String(deCipher.doFinal(dec(encrypted)));
		}

		private String enc(byte[] b) {
			return Base64.getEncoder().encodeToString(b);
		}

		private byte[] dec(String str) {
			return Base64.getDecoder().decode(str);
		}

	}

}
