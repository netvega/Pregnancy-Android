package care.dovetail.api;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import care.dovetail.App;
import care.dovetail.Config;
import care.dovetail.common.ApiResponseTask;
import care.dovetail.common.model.ApiResponse;

@SuppressWarnings("deprecation")
public class UserGet extends ApiResponseTask {
	private static final String TAG = "UserGet";

	private final App app;

	public UserGet(App app) {
		super(app.getUserUUID(), app.getUserAuth());
		this.app = app;
	}

	@Override
	protected HttpRequestBase makeRequest(Pair<String, String>... params)
			throws UnsupportedEncodingException {
		return new HttpGet(Config.USER_URL);
	}

	@Override
	protected void onPostExecute(ApiResponse result) {
		super.onPostExecute(result);
		if (result != null && !"OK".equalsIgnoreCase(result.code)) {
			Log.e(TAG, result.message);
			Toast.makeText(app, result.message, Toast.LENGTH_LONG).show();
		} else if (result != null && result.users != null && result.users.length > 0) {
			app.setUser(result.users[0]);
		}
	}
}