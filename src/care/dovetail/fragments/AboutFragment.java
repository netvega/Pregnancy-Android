package care.dovetail.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import care.dovetail.App;
import care.dovetail.R;

import com.google.android.gms.analytics.HitBuilders;

public class AboutFragment extends Fragment implements OnClickListener {
	private static final String TAG = "AboutFragment";

	private App app;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (App) activity.getApplication();
	}

	@Override
	public void onResume() {
		app.tracker.setScreenName(TAG);
		app.tracker.send(new HitBuilders.ScreenViewBuilder().build());
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_about, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View view) {
	}
}