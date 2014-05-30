package jp.co.spookies.android.appliradar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

/**
 * 自分からの相対位置で示されるアプリユーザー
 * 
 */
public class Friend {
	private int x, y;
	private Paint paint;
	public static final float RADIUS = 4.0f;

	/**
	 * コンストラクタ
	 * 
	 * @param centerLocation
	 *            中心となる座標
	 * @param latitude
	 *            緯度
	 * @param longitude
	 *            経度
	 */
	public Friend(Location centerLocation, double latitude, double longitude) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(200, 255, 200));
		x = (int) ((longitude - centerLocation.getLongitude()) * 50000);
		y = (int) ((latitude - centerLocation.getLatitude()) * -100000);
	}

	/**
	 * ユーザーの位置に点を描画
	 * 
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		canvas.drawCircle(x, y, RADIUS, paint);
	}
}
