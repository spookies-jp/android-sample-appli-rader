package jp.co.spookies.android.appliradar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.location.Location;

/**
 * アプリとサーバーとを媒介
 * 
 */
public class AppliSearcher {
	public static final String serverURL = "http://192.168.1.36:8080/";

	/**
	 * アプリレーダーを起動している人の位置を取得
	 * 
	 * @param location
	 *            自分の位置
	 * @return Friendリスト
	 */
	public static Friend[] getFriends(Location location) {
		if (location == null) {
			return new Friend[0];
		}
		// レスポンス取得
		String data = getResponse(serverURL);
		String[] values = data.split(" ");
		int length = values.length / 2;
		Friend[] friends = new Friend[length];
		// レスポンスのパース
		for (int i = 0; i < length; i++) {
			double latitude, longitude;
			try {
				latitude = Double.parseDouble(values[i * 2]);
				longitude = Double.parseDouble(values[i * 2 + 1]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				latitude = 0;
				longitude = 0;
			}
			friends[i] = new Friend(location, latitude, longitude);
		}
		return friends;
	}

	/**
	 * urlにアクセスする
	 * 
	 * @param url
	 * @return httpレスポンス(body)
	 */
	public static String getResponse(String url) {
		String data = null;
		try {
			// GETアクセス
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));
			StringBuilder builder = new StringBuilder();
			String line;
			// 1行ずつ読み込み
			while ((line = buffer.readLine()) != null) {
				builder.append(line);
			}
			buffer.close();
			data = builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (data == null) {
			data = "";
		}
		return data;
	}
}
