package care.dovetail;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

@SuppressLint("SimpleDateFormat")
public class Config {
	public static final Gson GSON = new Gson();

	// Activity Request IDs
	public static final int ACTIVITY_REQUEST_OAUTH = 0;
	public static final int ACTIVITY_CAMERA = 1;
	public static final int ACTIVITY_GALLERY = 2;
	public static final int ACTIVITY_SIGNUP = 2;

	// Notification IDs
	public static final int MESSAGE_NOTIFICATION_ID = 1;
	public static final int WEIGHT_NOTIFICATION_ID = 2;

	public static final String SERVICE_EVENT = "care.dovetail.babymonitor.ServiceEvent";
	public static final String SERVICE_DATA = "care.dovetail.babymonitor.ServiceData";

	public static final String EVENT_TYPE = "SERVICE_EVENT_TYPE";
	public static final String EVENT_TIME = "SERVICE_EVENT_TIME";
	public static final String SENSOR_DATA = "SENSOR_DATA";
	public static final String SENSOR_DATA_HEARTBEAT = "SENSOR_DATA_HEARTBEAT";

	public static final String BT_SENSOR_DATA_CHAR_PREFIX = "1902";

	public static final String API_URL = "http://api.pregnansi.com";
	public static final String USER_URL = API_URL + "/user";
	public static final String RECOVERY_URL = USER_URL + "/recover";
	public static final String PHOTO_UPLOAD_URL = USER_URL + "/photo";
	public static final String CARD_URL = USER_URL + "/card";
	public static final String APPOINTMENT_URL = API_URL + "/appointment";
	public static final String EVENT_URL  = API_URL + "/event";
	public static final String EVENT_CHART_URL  = API_URL + "/event/chart";
	public static final String GROUP_URL = API_URL + "/group";
	public static final String MESSAGE_URL  = API_URL + "/message";
	public static final String SEARCH_URL = API_URL + "/search";
	public static final String POLL_URL = API_URL + "/poll/";

	public static final String USER_PHOTO_URL = PHOTO_UPLOAD_URL + "?uuid=";
	public static final String GROUP_PHOTO_URL = GROUP_URL + "/photo?group_uuid=";

	public static final String GCM_SENDER_ID = "772594394845";

	public static final int EVENT_SYNC_INTERVAL_MILLIS = 60 * 1000;

	public static final int SAMPLE_RATE = 200;

	public static final int UI_UPDATE_INTERVAL_MILLIS = 10000;

	public static final SimpleDateFormat EVENT_TIME_FORMAT =
			new SimpleDateFormat("hh:mm:ssaa, MMM dd yyyy", Locale.US);
	public static final SimpleDateFormat JSON_DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.US);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm");
	public static final SimpleDateFormat MESSAGE_DATE_TIME_FORMAT =
			new SimpleDateFormat("MMM dd, h:mmaa");
	public static final SimpleDateFormat MESSAGE_DATE_FORMAT =
			new SimpleDateFormat("MMM dd");
	public static final SimpleDateFormat MESSAGE_TIME_FORMAT =
			new SimpleDateFormat("h:mmaa");

	public static final String GROUP_ID = "group_uuid";
	public static final String USER_ID = "uuid";

	public static final String WEIGHT_SCALE_NAME = "Samico Scales";

	public static final int REFRESH_TIMEOUT_MILLIS = 3000;

	public static final long GRAPH_DAYS = 7L;

	public static final String BACKGROUND_IMAGES[] = new String[] {
		"https://storage.googleapis.com/dovetail-images/baby1.png",
		"https://storage.googleapis.com/dovetail-images/baby2.png",
		"https://storage.googleapis.com/dovetail-images/baby3.png",
		"https://storage.googleapis.com/dovetail-images/Generic/baby-200760_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/baby-84627_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/brothers-457234_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/cat-649164_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/children-817365_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/flowers-164754_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/friends-640096_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/hands-634866_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/pregnancy-806989_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/summerfield-336672_640.jpg",
		"https://storage.googleapis.com/dovetail-images/Generic/twins-775506_640.jpg" };

	public static final String TODO_ICON =
		"https://storage.googleapis.com/dovetail-images/ic_todo.png";
	public static final String WEIGHT_ICON =
			"https://storage.googleapis.com/dovetail-images/weight.png";

	public static final int CENTER_DECOR[] = new int[] { R.drawable.back_b1, R.drawable.back_b4,
		R.drawable.back_t_br1, R.drawable.back_t_br2, R.drawable.back_tl_br1, R.drawable.back_tr_b1 };
	public static final int BOTTOM_DECOR[] = new int[] { R.drawable.back_b1, R.drawable.back_b2,
		R.drawable.back_b3, R.drawable.back_b4, R.drawable.back_b5, R.drawable.back_t_br1,
		R.drawable.back_t_br2, R.drawable.back_tl_br1, R.drawable.back_tr_b1 };
	public static final int BOTTOM_RIGHT_DECOR[] = new int[] { R.drawable.back_br1,
		R.drawable.back_br2, R.drawable.back_br3, R.drawable.back_br4, R.drawable.back_br5,
		R.drawable.back_br6, R.drawable.back_br7, R.drawable.back_br8, R.drawable.back_br9,
		R.drawable.back_br10 };
	public static final int BOTTOM_LEFT_DECOR[] = new int[] {
		R.drawable.back_bl1, R.drawable.back_bl2 };
	public static final int TOP_RIGHT_DECOR[] = new int[] { R.drawable.back_tr1,
		R.drawable.back_tr2, R.drawable.back_tr3, R.drawable.back_tr4, R.drawable.back_tr5};
}
