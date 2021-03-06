package care.dovetail.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import care.dovetail.Config;

public class Mother extends User {
	private static final String TAG = "Mother";
	public static final long  WEEK_MILLIS = 7L * 24L * 60L * 60L * 1000L;

	public static final String  FEATURE_DUE_DATE_MILLIS = "DUE_DATE_MILLIS";

	public long dueDateMillis;
	public List<Baby> babies;

	public static class Baby {
		public String name;
		public Gender gender;

		public Baby() {}

		public Baby(String name, Gender gender) {
			this.name = name;
			this.gender = gender;
		}
	}

	public int getPregnancyWeek() {
		return (int)(40L - ((dueDateMillis - System.currentTimeMillis()) / WEEK_MILLIS));
	}

	public User toUser() {
		if (features == null) {
			features = new HashMap<String, String>();
		} else {
			features.clear();
		}
		features.put(FEATURE_DUE_DATE_MILLIS, Long.toString(dueDateMillis));
		for (int i = 0; babies != null && i < babies.size(); i++) {
			features.put("BABY_" + i, Config.GSON.toJson(babies.get(i)));
		}
		return this;
	}

	public static Mother fromUser(String user) {
		Mother mother = Config.GSON.fromJson(user, Mother.class);
		if (mother == null || mother.features == null) {
			return mother;
		}
		String dueDate = mother.features.get(FEATURE_DUE_DATE_MILLIS);
		if (dueDate != null) {
			try {
				mother.dueDateMillis = Long.parseLong(dueDate);
			} catch (Exception ex) {
				Log.w(TAG, ex);
			}
		}
		mother.babies = new ArrayList<Baby>();
		for (String key : mother.features.keySet()) {
			if (key.startsWith("BABY_")) {
				Baby baby = Config.GSON.fromJson(mother.features.get(key), Baby.class);
				mother.babies.add(Integer.parseInt(key.replace("BABY_", "")), baby);
			}
		}
		return mother;
	}
}
