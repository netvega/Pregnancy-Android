package care.dovetail;

import android.app.Application;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import care.dovetail.api.UserCreate;
import care.dovetail.api.UserRecovery;
import care.dovetail.common.model.ApiResponse;
import care.dovetail.common.model.User;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SignUpActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "SignUpActivity";

	private App app;
	private User recoveredUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		app = (App) getApplication();
		findViewById(R.id.sign_up).setOnClickListener(this);
		findViewById(R.id.recover).setOnClickListener(this);
		findViewById(R.id.submit_code).setOnClickListener(this);
		findViewById(R.id.recover_text).setOnClickListener(this);
	}

	@Override
	public void onResume() {
		app.getSharedPreferences(app.getPackageName(), Application.MODE_PRIVATE)
				.registerOnSharedPreferenceChangeListener(listener);
		int playServicesAvailability = GooglePlayServicesUtil.isGooglePlayServicesAvailable(app);
		switch(playServicesAvailability) {
		case ConnectionResult.SUCCESS:
			break;
		default:
			GooglePlayServicesUtil.getErrorDialog(playServicesAvailability, this, 0,
					new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							Utils.trackEvent(app, TAG, "Error", "Update Cancelled");
						}
					}).show();
			Utils.trackEvent(app, TAG, "Error",
					GooglePlayServicesUtil.getErrorString(playServicesAvailability));
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		app.getSharedPreferences(app.getPackageName(), Application.MODE_PRIVATE)
				.unregisterOnSharedPreferenceChangeListener(listener);
		super.onPause();
	}

	private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			if (app.getUserAuth() != null && app.getUserUUID() != null) {
				finish();
			}
		}
	};

	@Override
	public void onBackPressed() {
		// Do nothing and not let user get out of sign up
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View view) {
		String name = ((TextView) findViewById(R.id.name)).getText().toString();
		String email = ((TextView) findViewById(R.id.email)).getText().toString();

		switch (view.getId()) {
		case R.id.sign_up:
			if (name == null || name.isEmpty() || email == null || email.isEmpty()) {
				break;
			}
			new UserCreate(app) {
				@Override
				protected void onPostExecute(ApiResponse result) {
					super.onPostExecute(result);
					findViewById(R.id.sign_up).setVisibility(View.VISIBLE);
					findViewById(R.id.progress).setVisibility(View.GONE);
					if (app.getUserUUID() != null && app.getUserAuth() != null) {
						finish();
					} else  if (result != null && result.message != null) {
						Toast.makeText(app, result.message, Toast.LENGTH_SHORT).show();
						Utils.trackEvent(app, TAG, "Response", result.message);
					}
				}
			}.execute(Pair.create(UserCreate.PARAM_NAME, name),
					Pair.create(UserCreate.PARAM_EMAIL, email),
        			Pair.create(UserCreate.PARAM_TYPE, "GOOGLE"),
        			Pair.create(UserCreate.PARAM_TOKEN, app.getPushToken()));
			findViewById(R.id.sign_up).setVisibility(View.GONE);
			findViewById(R.id.progress).setVisibility(View.VISIBLE);
			break;
		case R.id.recover:
			if (email == null || email.isEmpty()) {
				break;
			}
			new UserRecovery(app) {
				@Override
				protected void onPostExecute(ApiResponse result) {
					super.onPostExecute(result);
					findViewById(R.id.recover).setVisibility(View.VISIBLE);
					findViewById(R.id.progress).setVisibility(View.GONE);
					if (result != null && result.users != null && result.users.length > 0) {
						// Getting users in this list means we will need a code and UUID
						findViewById(R.id.code).setVisibility(View.VISIBLE);
						findViewById(R.id.submit_code).setVisibility(View.VISIBLE);
						findViewById(R.id.recover).setVisibility(View.GONE);
						findViewById(R.id.submit_code).requestFocus();
						recoveredUser = result.users[0];
					} else  if (result != null && !"OK".equalsIgnoreCase(result.code) &&
							result.message != null) {
						Toast.makeText(app, result.message, Toast.LENGTH_SHORT).show();
						Utils.trackEvent(app, TAG, "Response", result.message);
					}
				}
			}.execute(
					Pair.create(UserRecovery.PARAM_EMAIL, email),
        			Pair.create(UserRecovery.PARAM_TYPE, "GOOGLE"),
        			Pair.create(UserRecovery.PARAM_TOKEN, app.getPushToken()));
			findViewById(R.id.recover).setVisibility(View.GONE);
			findViewById(R.id.progress).setVisibility(View.VISIBLE);
			break;
		case R.id.submit_code:
			String code = ((TextView) findViewById(R.id.code)).getText().toString();
			if (code == null || recoveredUser == null || recoveredUser.uuid == null) {
				break;
			}
			new UserRecovery(app) {
				@Override
				protected void onPostExecute(ApiResponse result) {
					super.onPostExecute(result);
					findViewById(R.id.submit_code).setVisibility(View.VISIBLE);
					findViewById(R.id.progress).setVisibility(View.GONE);
					if (result != null && result.users != null && result.users.length > 0
							&& result.users[0].auth != null) {
						app.setUser(result.users[0]);
						finish();
					} else  if (result != null && result.message != null) {
						Toast.makeText(app, result.message, Toast.LENGTH_SHORT).show();
						Utils.trackEvent(app, TAG, "Response", result.message);
					} else {
						Log.w(TAG, String.format("Could not recover  user"));
						Utils.trackEvent(app, TAG, "Response", "Could not recover  user");
					}
				}
			}.execute(
					Pair.create(UserRecovery.PARAM_UUID, recoveredUser.uuid),
        			Pair.create(UserRecovery.PARAM_CODE, code));
			findViewById(R.id.submit_code).setVisibility(View.GONE);
			findViewById(R.id.progress).setVisibility(View.VISIBLE);
			break;
		case R.id.recover_text:
			findViewById(R.id.recover_text).setVisibility(View.GONE);
			findViewById(R.id.recover).setVisibility(View.VISIBLE);
			findViewById(R.id.name).setVisibility(View.GONE);
			findViewById(R.id.sign_up).setVisibility(View.GONE);
			findViewById(R.id.email_justification).setVisibility(View.GONE);
			break;
		}
	}
}
