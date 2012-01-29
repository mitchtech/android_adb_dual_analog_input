package net.mitchtech.adb;

import java.io.IOException;

import net.mitchtech.adb.dualanaloginput.R;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Server;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

public class DualAnalogInputActivity extends Activity {

	private final String TAG = DualAnalogInputActivity.class.getSimpleName();

	private int mXvalue = 0;
	private int mYvalue = 0;

	Server mServer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		// Create TCP server (based on MicroBridge LightWeight Server)
		try {
			mServer = new Server(4568); // Use ADK port
			mServer.start();
		} catch (IOException e) {
			Log.e(TAG, "Unable to start TCP server", e);
			System.exit(-1);
		}

		mServer.addListener(new AbstractServerListener() {

			@Override
			public void onReceive(org.microbridge.server.Client client, byte[] data) {

				if (data.length < 4)
					return;
				mXvalue = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
				mYvalue = (data[2] & 0xff) | ((data[3] & 0xff) << 8);
				// Any update to UI can not be carried out in a non UI thread
				// like the one used for Server. Hence runOnUIThread is used.
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.i(TAG, "X: " + mXvalue);
						Log.i(TAG, "Y: " + mYvalue);
						new XUpdateTask().execute(mXvalue);
						new YUpdateTask().execute(mYvalue);
					}
				});
			}
		});
	} // End of TCP Server code

	class XUpdateTask extends AsyncTask<Integer, Integer, String> {
		// Called to initiate the background activity
		@Override
		protected String doInBackground(Integer... sensorValue) {
			// Init SeeekBar Widget to display ADC sensor value in SeekBar
			// Max value of SeekBar is set to 1024
			SeekBar xSeekBar = (SeekBar) findViewById(R.id.sbX);
			xSeekBar.setProgress(sensorValue[0]);
			String returnString = String.valueOf(sensorValue[0]);
			return (returnString); // This goes to result
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// Not used in this case
		}

		@Override
		protected void onPostExecute(String result) {
			// Init TextView Widget to display sensor X value
			TextView tvAdcvalue = (TextView) findViewById(R.id.tvX);
			tvAdcvalue.setText(String.valueOf(result));
		}
	}

	class YUpdateTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... sensorValue) {
			// Init SeeekBar Widget to display ADC sensor value in SeekBar
			// Max value of SeekBar is set to 1024
			SeekBar xSeekBar = (SeekBar) findViewById(R.id.sbY);
			xSeekBar.setProgress(sensorValue[0]);
			String returnString = String.valueOf(sensorValue[0]);
			return (returnString); // This goes to result
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// Not used in this case
		}

		@Override
		protected void onPostExecute(String result) {
			// Init TextView Widget to display sensor Y value
			TextView tvAdcvalue = (TextView) findViewById(R.id.tvY);
			tvAdcvalue.setText(String.valueOf(result));
		}
	}

}
