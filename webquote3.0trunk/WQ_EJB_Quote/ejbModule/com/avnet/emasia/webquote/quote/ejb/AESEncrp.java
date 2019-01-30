package com.avnet.emasia.webquote.quote.ejb;

import java.io.IOException;

/**
 * @author Lin, Tough(901518)
 * Created on 2013-3-4
 */

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.avnet.emasia.webquote.exception.WebQuoteException;

import sun.misc.*;

public class AESEncrp {

	private static final String ALGO = "AES";
	private static final byte[] KEYVALUE = new byte[] { 'Q', 'u', 'o', 't', 'e', 'b', 'e', 'w', 'x', 'd', 'y', 's', 'l',
			'd', 'x', 't' };

	public static String encrypt(String data) throws WebQuoteException {
		Key key = generateKey();
		String encryptedValue;
		Cipher c;
		try {
			c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encVal = c.doFinal(data.getBytes());
			encryptedValue = new BASE64Encoder().encode(encVal);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new WebQuoteException(e);
		}
		return encryptedValue;
	}

	public static String decrypt(String encryptedData) throws WebQuoteException {
		Key key = generateKey();
		String decryptedValue = null;
		Cipher c;
		try {
			c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		decryptedValue = new String(decValue);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException e) {
			throw new WebQuoteException(e);
		}
		return decryptedValue;
	}

	private static Key generateKey() {
		Key key = new SecretKeySpec(KEYVALUE, ALGO);
		return key;
	}

	public static void main(String args[]) throws WebQuoteException {
		System.out.println(encrypt("uViX7#"));
		System.out.println(decrypt("90wPMDCBHVqDThYPU/f6Mw=="));
		System.out.println(encrypt("Web@vnet01"));
	}

}
