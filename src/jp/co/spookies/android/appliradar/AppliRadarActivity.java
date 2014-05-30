package jp.co.spookies.android.appliradar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

public class AppliRadarActivity extends Activity {
	private RadarView view;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "kill").setIcon(android.R.drawable.ic_delete);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(this, LocationSender.class);
			unbindService(connection);
			stopService(intent);
			finish();
		}
		return true;
	}

	private ILocationService binder;
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = ILocationService.Stub.asInterface(service);
			view.setBinder(binder);
		}

		public void onServiceDisconnected(ComponentName name) {
			binder = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(AppliRadarActivity.this, LocationSender.class);
		startService(intent);
		view = new RadarView(this);

		bindService(intent, connection, BIND_AUTO_CREATE);
		setContentView(view);
	}
}