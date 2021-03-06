package care.dovetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.util.Pair;
import care.dovetail.api.EventUploadTask;
import care.dovetail.api.UserUpdate;
import care.dovetail.common.ApiResponseTask;
import care.dovetail.common.model.ApiResponse.Message;
import care.dovetail.common.model.Group;
import care.dovetail.common.model.Mother;
import care.dovetail.common.model.User;
import care.dovetail.messaging.GCMUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class App extends Application {
	static final String TAG = "App";

	public static final String USER_UUID = "USER_UUID";
	public static final String USER_AUTH = "USER_AUTH";
	public static final String USER_PROFILE = "USER_PROFILE";
	public static final String WEIGHT_SCALE_MAC = "WEIGHT_SCALE_MAC";
	public static final String GOOGLE_FIT_ACCOUNT = "GOOGLE_FIT_ACCOUNT";

	public static final String FITNESS_POLL_TIME = "FITNESS_POLL_TIME";
	public static final String EVENT_SYNC_TIME = "EVENT_SYNC_TIME";
	public static final String MESSAGE_SYNC_TIME = "MESSAGE_SYNC_TIME";
	public static final String GROUP_SYNC_TIME = "GROUP_SYNC_TIME";
	public static final String APPOINTMENT_SYNC_TIME = "APPOINTMENT_SYNC_TIME";

	public GoogleAnalytics analytics;
	public Tracker tracker;

	public boolean authInProgress = false;

	public GoogleApiClient apiClient = null;
	public final EventDB events = new EventDB(this);
	public ImageLoader imageLoader;

	private final EventUploadTask eventUploadTask = new EventUploadTask(this);
	private RequestQueue requestQueue;

	private String pushToken;
	private GoogleCloudMessaging gcm;

	private Mother mother;
	public List<Group> groups = new ArrayList<Group>();
	public Map<String, List<Message>> messages = new HashMap<String, List<Message>>();
	public List<User> contacts = new ArrayList<User>();

	@Override
	public void onCreate() {
		super.onCreate();

		// BkgService needed for BTLE devices which are supported JBMR2+
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			startService(new Intent(this, BackgroundService.class));
		}

		analytics = GoogleAnalytics.getInstance(this);
	    analytics.setLocalDispatchPeriod(1800);
	    tracker = analytics.newTracker(R.xml.analytics_global_config);
	    tracker.enableAdvertisingIdCollection(true); // Not sure why this is not in config file.

		String profile =
				getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(USER_PROFILE, null);
		mother = profile != null ? Mother.fromUser(profile) : new Mother();

		requestQueue = Volley.newRequestQueue(this);
		imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
		    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
		    @Override
			public void putBitmap(String url, Bitmap bitmap) {
		        cache.put(url, bitmap);
		    }
		    @Override
			public Bitmap getBitmap(String url) {
		        return cache.get(url);
		    }
		});

		new Timer().scheduleAtFixedRate(eventUploadTask, Config.EVENT_SYNC_INTERVAL_MILLIS,
				Config.EVENT_SYNC_INTERVAL_MILLIS);
	}

	public void setGoogleFitAccount(String account) {
		setStringPref(GOOGLE_FIT_ACCOUNT, account);
	}

	public String getGoogleFitAccount() {
		return getSharedPreferences(
				getPackageName(), MODE_PRIVATE).getString(GOOGLE_FIT_ACCOUNT, null);
	}

	public void setWeightScaleAddress(String macAddress) {
		setStringPref(WEIGHT_SCALE_MAC, macAddress);
	}

	public String getWeightScaleAddress() {
		return getSharedPreferences(
				getPackageName(), MODE_PRIVATE).getString(WEIGHT_SCALE_MAC, null);
	}

	public void setFitnessPollTime(long timeMillis) {
		setLongPref(FITNESS_POLL_TIME, timeMillis);
	}

	public long getFitnessPollTime() {
		return getSharedPreferences(
				getPackageName(), MODE_PRIVATE).getLong(FITNESS_POLL_TIME, 0);
	}

	public void setEventSyncTime(long timeMillis) {
		Log.i(TAG, String.format("Synced up events upto %s",
				Config.MESSAGE_TIME_FORMAT.format(timeMillis)));
		setLongPref(EVENT_SYNC_TIME, timeMillis);
	}

	public long getEventSyncTime() {
		return getSharedPreferences(
				getPackageName(), MODE_PRIVATE).getLong(EVENT_SYNC_TIME, 0);
	}

	public void setMessageSyncTime(long timeMillis) {
		setLongPref(MESSAGE_SYNC_TIME, timeMillis);
	}

	public long getMessageSyncTime() {
		return getSharedPreferences(
				getPackageName(), MODE_PRIVATE).getLong(MESSAGE_SYNC_TIME, 0);
	}

	public void setGroupSyncTime(long timeMillis) {
		setLongPref(GROUP_SYNC_TIME, timeMillis);
		updateContacts();
	}

	public long getGroupSyncTime() {
		return getSharedPreferences(
				getPackageName(), MODE_PRIVATE).getLong(GROUP_SYNC_TIME, 0);
	}

	public void setAppointmentSyncTime(long timeMillis) {
		setLongPref(APPOINTMENT_SYNC_TIME, timeMillis);
	}

	public long getAppointmentSyncTime() {
		return getSharedPreferences(
				getPackageName(), MODE_PRIVATE).getLong(APPOINTMENT_SYNC_TIME, 0);
	}

	public String getUserUUID() {
		return getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(USER_UUID, null);
	}

	public String getUserAuth() {
		return getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(USER_AUTH, null);
	}

	public void setUser(User user) {
		if (user != null) {
			if (user.uuid != null) {
				setStringPref(USER_UUID, user.uuid);
			}
			if (user.auth != null) {
				setStringPref(USER_AUTH, user.auth);
			}
			Log.i(TAG, ApiResponseTask.getAuthHeader(user.uuid, user.auth));
		}
		mother = Mother.fromUser(Config.GSON.toJson(user));
		setStringPref(USER_PROFILE, Config.GSON.toJson(user));
	}

	public Mother getMother() {
		return mother;
	}

	public User getUser(String userId) {
		if (userId == null) {
			return null;
		}
		for (User user : contacts) {
			if (userId.equalsIgnoreCase(user.uuid)) {
				return user;
			}
		}
		return null;
	}

	public Group findUserGroup(User user) {
		for (Group group : groups) {
			if (group != null && group.members != null && group.members.length == 2 &&
					(user.equals(group.members[0]) || user.equals(group.members[1]))) {
				return group;
			}
		}
		return null;
	}

	public String getPushToken() {
		return pushToken;
	}

	@SuppressWarnings("unchecked")
	public void requestPushToken() {
		if (pushToken != null) {
			Log.i(TAG, "Device already registered, registration ID = " + pushToken);
			if (getUserUUID() != null && getUserAuth() != null) {
				new UserUpdate(this).execute(Pair.create(UserUpdate.PARAM_TYPE, "GOOGLE"),
						Pair.create(UserUpdate.PARAM_TOKEN, pushToken));
			}
			return;
		}
	    new AsyncTask<Void, Void, Void>() {
			@Override
	        protected Void doInBackground(Void... params) {
	            try {
	            	if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(App.this);
	                }
	                pushToken = gcm.register(Config.GCM_SENDER_ID);
	                Log.i(TAG, "Device registered, registration ID = " + pushToken);
	                if (getUserUUID() != null && getUserAuth() != null) {
	                	new UserUpdate(App.this).execute(
	                			Pair.create(UserUpdate.PARAM_TYPE, "GOOGLE"),
	                			Pair.create(UserUpdate.PARAM_TOKEN, pushToken));
	                }
	                GCMUtils.storeRegistrationId(App.this, pushToken);
	            } catch (IOException ex) {
	                Log.w(TAG, ex);
	            }
	            return null;
	        }
	    }.execute(null, null, null);
	}

	private void setStringPref(final String pref, String value) {
		final Editor editor = getSharedPreferences(
				getPackageName(), Application.MODE_PRIVATE).edit();
		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... values) {
				if (values != null && values.length > 0) {
					editor.putString(pref, values[0]);
					editor.commit();
				}
				return null;
			}
		}.execute(value);
	}

	private void setLongPref(final String pref, long value) {
		final Editor editor = getSharedPreferences(
				getPackageName(), Application.MODE_PRIVATE).edit();
		new AsyncTask<Long, Void, Void>() {
			@Override
			protected Void doInBackground(Long... values) {
				if (values != null && values.length > 0) {
					editor.putLong(pref, values[0]);
					editor.commit();
				}
				return null;
			}
		}.execute(value);
	}

	public void updateContacts() {
		for (Group group : groups) {
			for (User member : group.members) {
				if (!contacts.contains(member) && !member.equals(mother)) {
					contacts.add(member);
				}
			}
			for (User admin : group.admins) {
				if (!contacts.contains(admin) && !admin.equals(mother)) {
					contacts.add(admin);
				}
			}
		}
	}
}
