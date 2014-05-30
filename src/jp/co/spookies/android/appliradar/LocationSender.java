package jp.co.spookies.android.appliradar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 自分の位置をサーバーに送信し続けるサービス
 * 
 */
public class LocationSender extends Service {
	LocationManager locationManager;
	private int id = -1;
	private Location recentLocation;
	private NotificationManager notificationManager;

	// GPSのリスナー
	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			sendPosition(location);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	// NETWORK LOCATIONのリスナー
	private LocationListener coarseLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			sendPosition(location);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * 座標を送信
	 * 
	 * @param location
	 */
	public synchronized void sendPosition(final Location location) {
		recentLocation = location;
		// httpアクセスは別スレッドに
		new Thread(new Runnable() {
			public void run() {
				if (id < 0) {
					String url = AppliSearcher.serverURL + "regist?"
							+ location.getLatitude() + ","
							+ location.getLongitude() + ","
							+ System.currentTimeMillis();
					try {
						// id未取得ならidを取得
						id = Integer.parseInt(AppliSearcher.getResponse(url));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else {
					String url = AppliSearcher.serverURL + "regist?" + id + ","
							+ location.getLatitude() + ","
							+ location.getLongitude() + ","
							+ System.currentTimeMillis();
					AppliSearcher.getResponse(url);
				}
			}
		}).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	private final ILocationService.Stub binder = new ILocationService.Stub() {
		public Location getRecentLocation() throws RemoteException {
			return recentLocation;
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10 * 1000, 0, locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 10 * 1000, 0,
				coarseLocationListener);
	}

	@Override
	public void onDestroy() {
		notificationManager.cancel(R.string.app_name);
		locationManager.removeUpdates(locationListener);
		locationManager.removeUpdates(coarseLocationListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Notification notification = new Notification(R.drawable.icon01,
				"start", System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, AppliRadarActivity.class), 0);
		notification.setLatestEventInfo(this, getText(R.string.app_name),
				getText(R.string.app_name), pendingIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notificationManager.notify(R.string.app_name, notification);
		return START_STICKY;
	}

}
