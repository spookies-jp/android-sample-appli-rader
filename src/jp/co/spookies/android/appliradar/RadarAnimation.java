package jp.co.spookies.android.appliradar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;

/**
 * レーダーっぽいアニメーションを行うクラス
 * 
 */
public class RadarAnimation {
	private Paint paint;
	private Paint centerPaint;
	private Paint barPaint;
	private Shader shader;
	private int x, y;
	private float radius;
	private float rotation;

	/**
	 * コンストラクタ
	 * 
	 * @param centerX
	 *            レーダーの中心のx座標
	 * @param centerY
	 *            レーダーの中心のy座標
	 * @param radius
	 *            レーダーの半径
	 */
	public RadarAnimation(int centerX, int centerY, int radius) {
		x = centerX;
		y = centerY;
		this.radius = radius;
		paint = new Paint();
		paint.setAntiAlias(true);

		shader = new SweepGradient(x, y, new int[] {
				Color.argb(100, 0, 200, 0), Color.argb(10, 0, 200, 0),
				Color.argb(0, 0, 0, 0) }, null);
		paint.setShader(shader);
		rotation = 0;

		centerPaint = new Paint();
		centerPaint.setAntiAlias(true);
		centerPaint.setColor(Color.RED);
		barPaint = new Paint();
		barPaint.setAntiAlias(true);
		barPaint.setColor(Color.rgb(100, 255, 100));
		barPaint.setStrokeWidth(3.0f);
	}

	public void draw(Canvas canvas) {
		canvas.save();
		canvas.rotate(rotation, x, y);
		canvas.drawCircle(x, y, radius, paint);
		canvas.drawLine(x, y, radius, y, barPaint);
		canvas.drawCircle(x, y, 5.0f, centerPaint);
		canvas.restore();
		rotation -= 2.0f;
		if (rotation < 0) {
			rotation += 360.0f;
		}
	}
}
