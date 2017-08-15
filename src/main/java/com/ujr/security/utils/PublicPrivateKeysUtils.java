package com.ujr.security.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class PublicPrivateKeysUtils {
	
	private static StringBuilder BUFFER = new StringBuilder();
	
	public static void main(String[] args) {
		TEST_GetPrivateKeyFromString();
		
	}
	
	public static void TEST_GetPrivateKeyFromString() {
		// 1 
		// extractPrivateKeyFromString("-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDH3ktCPX6Ygr+L\\njyU76T3DIT8lswYONUxRyFwV70/dEhZS/pDJzbr1B698YV78xf20BeOJdJgwP24A\\ntOTxAq7mzijzLnEVjmexFb7+Frbm9/VuQRS0lrCG3tODlgVXJenr6TVW7Rw5vJju\\nE3d/NKf2kLU8A+6ptCg0+hafSSHm2+AgcrJqBEXX7D7sEShAgxGUpWKW6AiRGxXJ\\n26OwEC0NCUnmWTI7KYloFgDO9pAx8V/7y5xLJWRLx79gMYBZ4ZOmvVPYVfDgW0iZ\\nmgoDYesnNHSPpn+jGcDeNNKBUo3fqFS5TzHWNDgThMPIHziPYMToL0F0QvbHNWJu\\n0Vw8xb85AgMBAAECggEAC/d7ZXbQ6E9WKgRC+o05Aib9izbNHOp3bHAehb7jdvBY\\n/ztRro617ATVSER8nCeiMvY+5nrhWWyUaNA4r7t1S5Y6LaRxGq7N9sE/Q1XxBoj/\\n6P8jJVR3/+NCIiwVAfl5qE/SvnOJtjIPrM8XX2zm+TE2pxmL+R1mla47NR2uVqf/\\n2RMFeWKqbhkhalbSO3yctOAOlfXZols8DK+ez1IsqNooISWWb/Hk4N2IPDVKREvf\\n9pFjSgVqvWH7RdiXm6TcafZ7dN0BZg33GFS4WMwE/7MhbPpNRSJx0imRahlrOjLo\\nfjJtmxyRNYThNwAJkld/BdW9SMYLuHcJiZESDXO9HwKBgQDk/7Qxjx8XSOiROvDg\\n8aP/XxI731bx7VCGOZlftyzcQJ38rkGnCE9E7pXYmoEYXbFaFzroW8bI3aSiM1Mz\\nDpfr43kCxNN34o1AeBlTdIKgDsYNvXcbwmYdtctEVJ/BV68Uww7mOqtf69Pt6v/m\\nXuPizcOPPTixyRehTtnQY4evpwKBgQDfb0pA3bRcYUG6gC+vETPVPFRkSLg2xUtu\\nbsQW2u/N1ozPi3MVsMkiNL+tBTWjQSk6+hC/ghwVTq7YDpKXrgTu1204wFYLvAzw\\nzQVc7bjcKh9s7VSHYttim5VXG0vivp+2naOf+ZMyYMViWVaVCx6oWy58T9YaXq64\\nYDJJyKv2HwKBgBEWK0HPZgh8vi2n8jU2koAJffjNr1UZ2fpJ7fHAXy6H+8HQ1sE5\\n04BXNQMWdC93PjZ0qUaRIoH2V8Rqg/i5TAijznGwcf7t/pAi5fDeLqj2sTxxOKPv\\nm2L4H5SXo9vvSPcJnuD627KqjAOilzl/Nw3DQKY9cS+Cy6qTkZkE9CjdAoGAOVPF\\nmnUH5LfzdFWVZnakdO6gvIZH9Y2TeAclerO72XVV9Z5S7drJEGS2VT9D7to2KPKm\\nP2yzpeflRnwesposm9dcJ7Z5nVMngtUnrhs0VhDctUcDbCU6IsfGfm37f3bnZaR2\\nqgIP+VoI6t3/MAiFWi7i83RMe8GVVKsh2/qH3IMCgYEArLzMbIuwoziekNJZ03si\\nmmuJt6h4/VKxH0NQJyYKB/sCUNxqR8XHVh/T4avSphH5rig89OP+mRD3gdBMvJDO\\n6kXuRa7P6Ytbg6hZM4Qm6vijGM/RERp7f2mQJoIv9YP3JyL/rgUNmq76PjrqtTi8\\nHo1ACrRBtnrSBsi1tE/1Uwg=\\n-----END PRIVATE KEY-----\\n");
		// 2
		// extractPrivateKeyFromString("-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDH3ktCPX6Ygr+L\\njyU76T3DIT8lswYONUxRyFwV70/dEhZS/pDJzbr1B698YV78xf20BeOJdJgwP24A\\ntOTxAq7mzijzLnEVjmexFb7+Frbm9/VuQRS0lrCG3tODlgVXJenr6TVW7Rw5vJju\\nE3d/NKf2kLU8A+6ptCg0+hafSSHm2+AgcrJqBEXX7D7sEShAgxGUpWKW6AiRGxXJ\\n26OwEC0NCUnmWTI7KYloFgDO9pAx8V/7y5xLJWRLx79gMYBZ4ZOmvVPYVfDgW0iZ\\nmgoDYesnNHSPpn+jGcDeNNKBUo3fqFS5TzHWNDgThMPIHziPYMToL0F0QvbHNWJu\\n0Vw8xb85AgMBAAECggEAC/d7ZXbQ6E9WKgRC+o05Aib9izbNHOp3bHAehb7jdvBY\\n/ztRro617ATVSER8nCeiMvY+5nrhWWyUaNA4r7t1S5Y6LaRxGq7N9sE/Q1XxBoj/\\n6P8jJVR3/+NCIiwVAfl5qE/SvnOJtjIPrM8XX2zm+TE2pxmL+R1mla47NR2uVqf/\\n2RMFeWKqbhkhalbSO3yctOAOlfXZols8DK+ez1IsqNooISWWb/Hk4N2IPDVKREvf\\n9pFjSgVqvWH7RdiXm6TcafZ7dN0BZg33GFS4WMwE/7MhbPpNRSJx0imRahlrOjLo\\nfjJtmxyRNYThNwAJkld/BdW9SMYLuHcJiZESDXO9HwKBgQDk/7Qxjx8XSOiROvDg\\n8aP/XxI731bx7VCGOZlftyzcQJ38rkGnCE9E7pXYmoEYXbFaFzroW8bI3aSiM1Mz\\nDpfr43kCxNN34o1AeBlTdIKgDsYNvXcbwmYdtctEVJ/BV68Uww7mOqtf69Pt6v/m\\nXuPizcOPPTixyRehTtnQY4evpwKBgQDfb0pA3bRcYUG6gC+vETPVPFRkSLg2xUtu\\nbsQW2u/N1ozPi3MVsMkiNL+tBTWjQSk6+hC/ghwVTq7YDpKXrgTu1204wFYLvAzw\\nzQVc7bjcKh9s7VSHYttim5VXG0vivp+2naOf+ZMyYMViWVaVCx6oWy58T9YaXq64\\nYDJJyKv2HwKBgBEWK0HPZgh8vi2n8jU2koAJffjNr1UZ2fpJ7fHAXy6H+8HQ1sE5\\n04BXNQMWdC93PjZ0qUaRIoH2V8Rqg/i5TAijznGwcf7t/pAi5fDeLqj2sTxxOKPv\\nm2L4H5SXo9vvSPcJnuD627KqjAOilzl/Nw3DQKY9cS+Cy6qTkZkE9CjdAoGAOVPF\\nmnUH5LfzdFWVZnakdO6gvIZH9Y2TeAclerO72XVV9Z5S7drJEGS2VT9D7to2KPKm\\nP2yzpeflRnwesposm9dcJ7Z5nVMngtUnrhs0VhDctUcDbCU6IsfGfm37f3bnZaR2\\nqgIP+VoI6t3/MAiFWi7i83RMe8GVVKsh2/qH3IMCgYEArLzMbIuwoziekNJZ03si\\nmmuJt6h4/VKxH0NQJyYKB/sCUNxqR8XHVh/T4avSphH5rig89OP+mRD3gdBMvJDO\\n6kXuRa7P6Ytbg6hZM4Qm6vijGM/RERp7f2mQJoIv9YP3JyL/rgUNmq76PjrqtTi8\\nHo1ACrRBtnrSBsi1tE/1Uwg=\n-----END PRIVATE KEY-----\n");
		// 3
		extractPrivateKeyFromString("-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDH3ktCPX6Ygr+L\njyU76T3DIT8lswYONUxRyFwV70/dEhZS/pDJzbr1B698YV78xf20BeOJdJgwP24A\ntOTxAq7mzijzLnEVjmexFb7+Frbm9/VuQRS0lrCG3tODlgVXJenr6TVW7Rw5vJju\nE3d/NKf2kLU8A+6ptCg0+hafSSHm2+AgcrJqBEXX7D7sEShAgxGUpWKW6AiRGxXJ\n26OwEC0NCUnmWTI7KYloFgDO9pAx8V/7y5xLJWRLx79gMYBZ4ZOmvVPYVfDgW0iZ\nmgoDYesnNHSPpn+jGcDeNNKBUo3fqFS5TzHWNDgThMPIHziPYMToL0F0QvbHNWJu\n0Vw8xb85AgMBAAECggEAC/d7ZXbQ6E9WKgRC+o05Aib9izbNHOp3bHAehb7jdvBY\n/ztRro617ATVSER8nCeiMvY+5nrhWWyUaNA4r7t1S5Y6LaRxGq7N9sE/Q1XxBoj/\n6P8jJVR3/+NCIiwVAfl5qE/SvnOJtjIPrM8XX2zm+TE2pxmL+R1mla47NR2uVqf/\n2RMFeWKqbhkhalbSO3yctOAOlfXZols8DK+ez1IsqNooISWWb/Hk4N2IPDVKREvf\n9pFjSgVqvWH7RdiXm6TcafZ7dN0BZg33GFS4WMwE/7MhbPpNRSJx0imRahlrOjLo\nfjJtmxyRNYThNwAJkld/BdW9SMYLuHcJiZESDXO9HwKBgQDk/7Qxjx8XSOiROvDg\n8aP/XxI731bx7VCGOZlftyzcQJ38rkGnCE9E7pXYmoEYXbFaFzroW8bI3aSiM1Mz\nDpfr43kCxNN34o1AeBlTdIKgDsYNvXcbwmYdtctEVJ/BV68Uww7mOqtf69Pt6v/m\nXuPizcOPPTixyRehTtnQY4evpwKBgQDfb0pA3bRcYUG6gC+vETPVPFRkSLg2xUtu\nbsQW2u/N1ozPi3MVsMkiNL+tBTWjQSk6+hC/ghwVTq7YDpKXrgTu1204wFYLvAzw\nzQVc7bjcKh9s7VSHYttim5VXG0vivp+2naOf+ZMyYMViWVaVCx6oWy58T9YaXq64\nYDJJyKv2HwKBgBEWK0HPZgh8vi2n8jU2koAJffjNr1UZ2fpJ7fHAXy6H+8HQ1sE5\n04BXNQMWdC93PjZ0qUaRIoH2V8Rqg/i5TAijznGwcf7t/pAi5fDeLqj2sTxxOKPv\nm2L4H5SXo9vvSPcJnuD627KqjAOilzl/Nw3DQKY9cS+Cy6qTkZkE9CjdAoGAOVPF\nmnUH5LfzdFWVZnakdO6gvIZH9Y2TeAclerO72XVV9Z5S7drJEGS2VT9D7to2KPKm\nP2yzpeflRnwesposm9dcJ7Z5nVMngtUnrhs0VhDctUcDbCU6IsfGfm37f3bnZaR2\nqgIP+VoI6t3/MAiFWi7i83RMe8GVVKsh2/qH3IMCgYEArLzMbIuwoziekNJZ03si\nmmuJt6h4/VKxH0NQJyYKB/sCUNxqR8XHVh/T4avSphH5rig89OP+mRD3gdBMvJDO\n6kXuRa7P6Ytbg6hZM4Qm6vijGM/RERp7f2mQJoIv9YP3JyL/rgUNmq76PjrqtTi8\nHo1ACrRBtnrSBsi1tE/1Uwg=\n-----END PRIVATE KEY-----\n");
		
		System.out.println(PublicPrivateKeysUtils.FLUSH());
	}
	
	public static RSAPrivateKey extractPrivateKeyFromString(String key)  {
		String privateKeyPEM = key;
	    try {
			/*
			// 1
			privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\\n", "");
			privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----\\n", "");
			privateKeyPEM = privateKeyPEM.replace("\\n", "");
			*/
	    	/*
	    	// 2
	    	privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
			privateKeyPEM = privateKeyPEM.replace("\n-----END PRIVATE KEY-----\n", "");
			privateKeyPEM = privateKeyPEM.replace("\\n", "");
			*/
	    	// 3
	    	privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
	    	privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
	    	privateKeyPEM = privateKeyPEM.replaceAll("\\n", "");
			byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
			RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
			
			byte[] privateKeyBytes = privKey.getEncoded();
			int columns = 0; 
			for(final byte b : privateKeyBytes) {
				out(Integer.toString(b & 0xFF,2));
				columns++;
				if (columns >= 10) {
					outln("");
					columns = 0;
				}
			}
			
			return privKey;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		} catch (Throwable e) {
			BUFFER.append(privateKeyPEM);
			throw new RuntimeException(e);
		}
	    
	}
	
	public static RSAPublicKey extractPublicKeyFromString(String key)  {
		try {
		    String publicKeyPEM = key;
		    //publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
		    //publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
		    publicKeyPEM = publicKeyPEM.replace("-----BEGIN CERTIFICATE-----\n", "");
		    publicKeyPEM = publicKeyPEM.replace("-----END CERTIFICATE-----", "");
		    publicKeyPEM = publicKeyPEM.replaceAll("\\n", "");
		    byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
		    KeyFactory kf = KeyFactory.getInstance("RSA");
		    //RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new PKCS8EncodedKeySpec(encoded));
		    RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
		    return pubKey;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
	    Signature sign = Signature.getInstance("SHA1withRSA");
	    sign.initSign(privateKey);
	    sign.update(message.getBytes("UTF-8"));
	    return new String(Base64.getEncoder().encodeToString(sign.sign()));
	}


	public static boolean verify(PublicKey publicKey, String message, String signature) throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
	    Signature sign = Signature.getInstance("SHA1withRSA");
	    sign.initVerify(publicKey);
	    sign.update(message.getBytes("UTF-8"));
	    return sign.verify(Base64.getDecoder().decode(signature.getBytes("UTF-8")));
	}

	public static String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
	    Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	    return Base64.getEncoder().encodeToString(cipher.doFinal(rawText.getBytes("UTF-8")));
	}

	public static String decrypt(String cipherText, PrivateKey privateKey) throws IOException, GeneralSecurityException {
	    Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.DECRYPT_MODE, privateKey);
	    return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), "UTF-8");
	}
	
	
	public static void outln(String s) {
		BUFFER.append(s).append("\n");
	}
	
	public static void out(String s) {
		BUFFER.append(s);
	}
	
	public static String FLUSH() {
		String out = BUFFER.toString();
		BUFFER.setLength(0);
		return out;
		
	}

}
