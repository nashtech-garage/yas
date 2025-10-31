package com.yas.unsafe;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UnsafeExamples {
	// Hardcoded secret
	private static final String JWT_SECRET = "super-secret-key-please-rotate";

	// SQL injection
	public String findUser(String username) throws Exception {
		Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM users WHERE username = '" + username + "'");
		return rs.next() ? rs.getString("email") : null;
	}

	// Insecure deserialization
	public Object loadFromDisk(String path) throws Exception {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
			return ois.readObject();
		}
	}

	// Disable SSL verification
	public String insecureGet(String urlStr) throws Exception {
		trustAllHosts();
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		return conn.getResponseMessage();
	}

	// Cryptography misuse
	public String weakRandomToken() {
		byte[] bytes = new byte[16];
		new java.util.Random().nextBytes(bytes);
		return Base64.getEncoder().encodeToString(bytes);
	}

	private static void trustAllHosts() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
			}
		};
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) { return true; }
		});
	}
}
