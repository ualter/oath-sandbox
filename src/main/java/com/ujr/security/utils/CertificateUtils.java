package com.ujr.security.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import com.ujr.oath.client.credentials.google.api.GoogleJwtApiHandler;

public class CertificateUtils {
	
	private static StringBuilder BUFFER = new StringBuilder();
	
	public static void main(String[] args) {
		//TEST_LoadTestCertificate();
		TEST_loadScorecardCertificate();
	}
	
	private static void TEST_LoadTestCertificate() {
		loadTestCertificate();
		System.out.println(FLUSH());
	}
	
	private static void TEST_loadScorecardCertificate() {
		Path            certificateFile = CertificateUtils.loadCertificateFile("/br/com/oath/sandbox/jwt/scorecard_public_certificate.pem");
		X509Certificate x509Certificate = CertificateUtils.loadCertificate(certificateFile);
		PublicKey       publicKey       = x509Certificate.getPublicKey();
		System.out.println(FLUSH());
	}
	
	private static void loadTestCertificate() {
		Path pathFile = loadCertificateFileForTest();
		loadCertificate(pathFile);
	}
	
	
	public static X509Certificate loadCertificate(Path pathFile) {
		try {
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			FileInputStream is = new FileInputStream(pathFile.toFile());
			X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
			PublicKey key = cer.getPublicKey();
			out(key.toString());
			return cer;
		} catch (CertificateException | FileNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	}

	private static Path loadCertificateFileForTest()  {
		URL url = GoogleJwtApiHandler.class.getResource("/br/com/security/utils/certificate.pem");
		Path pathFile;
		try {
			pathFile = Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return pathFile;
	}
	
	public static Path loadCertificateFile(String file)  {
		URL url = GoogleJwtApiHandler.class.getResource(file);
		Path pathFile;
		try {
			pathFile = Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return pathFile;
	}
	
	public static void loadWindowsCertificateMyUserStore() {
		try {
			KeyStore msCertStore = KeyStore.getInstance("Windows-MY");
			msCertStore.load(null, null);
			for(Enumeration<String> e = msCertStore.aliases(); e.hasMoreElements();) {
				String alias = e.nextElement();
				Certificate c = msCertStore.getCertificate(alias);
				System.out.println(c);
			}
			//Certificate c = msCertStore.getCertificate("ualter.azambuja@gft.com");
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void out(String s) {
		BUFFER.append(s).append("\n");
	}
	
	public static String FLUSH() {
		String out = BUFFER.toString();
		BUFFER.setLength(0);
		return out;
	}

}
