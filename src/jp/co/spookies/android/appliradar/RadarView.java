package jp.co.spookies.android.appliradar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.RemoteException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * アプリレーダーView
 * 
 */
public class RadarView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	private Thread thread;
	private Canvas canvas;
	private Paint paint;
	private int width, height;
	private float contourRadius;
	private RadarAnimation radarAnimation;
	public static final int NUM_CONTOUR = 3;
	private Friend[] friends;
	private Object lockObject = new Object();
	private ILocationService binder;

	public RadarView(Context context) {
		super(context);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(5.0f);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.rgb(0, 127, 0));

		getHolder().addCallback(this);
	}

	public void run() {
		radarAnimation = new RadarAnimation(width / 2, height / 2, height);
		Thread httpThread = null;

		// httpアクセスで情報を取得する
		Runnable httpResponsable = new Runnable() {
			public void run() {
				try {
					Friend[] f = AppliSearcher.getFriends(binder
							.getRecentLocation());
					synchronized (lockObject) {
						friends = f.clone();
					}
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		};
		while (thread != null) {
			doDraw();
			if (httpThread == null || !httpThread.isAlive()) {
				httpThread = new Thread(httpResponsable);
				httpThread.start();
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		width = getWidth();
		height = getHeight();
		contourRadius = height / (NUM_CONTOUR * 2);
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
	}

	private void doDraw() {
		canvas = getHolder().lockCanvas();

		canvas.drawColor(Color.BLACK);

		// 一定距離ごとに円を描く
		for (int i = 0; i < NUM_CONTOUR; i++) {
			canvas.drawCircle(width / 2, height / 2, contourRadius * (i + 1),
					paint);
		}
		if (friends != null) {
			canvas.save();
			canvas.translate(width / 2, height / 2);
			synchronized (lockObject) {
				for (Friend f : friends) {
					f.draw(canvas);
				}
			}
			canvas.restore();
		}
		radarAnimation.draw(canvas);

		getHolder().unlockCanvasAndPost(canvas);
	}

	/**
	 * サービスのbinderを設定
	 * 
	 * @param binder
	 */
	public void setBinder(ILocationService binder) {
		this.binder = binder;
	}

}
