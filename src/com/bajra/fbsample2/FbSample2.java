package com.bajra.fbsample2;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.Session.StatusCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FbSample2 extends Activity {

	// static final String APP_ID = "307234779396415"; //APP_ID of
	// graphApiSampleActivity
	// static final String APP_ID="651980534843727"; //MyTesting
	static final String APP_ID = "703649406335382"; // MyApp1
	Session session;

	Button btnShare;
	private boolean pendingRequest;
	static final String PENDING_REQUEST_BUNDLE_KEY = "com.bajra.fbsample2:PendingRequest";
	static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fb_sample2);
		btnShare = (Button) findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickHandler(v);
			}
		});

		this.session = createSession();
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (this.session.onActivityResult(this, requestCode, resultCode, data)
				&& pendingRequest && this.session.getState().isOpened()) {
			share_it();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		pendingRequest = savedInstanceState.getBoolean(
				PENDING_REQUEST_BUNDLE_KEY, pendingRequest);

	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private Session createSession() {
		Session session = Session.getActiveSession();
		if (session == null || session.getState().isClosed()) {
			session = new Session.Builder(getApplicationContext())
					.setApplicationId(APP_ID).build();
			Session.setActiveSession(session);
		}
		return session;
	}

	public void clickHandler(View v) {
		if (session.isOpened())
			share_it();
		else {
			StatusCallback callback = new StatusCallback() {

				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					// TODO Auto-generated method stub
					if (exception != null) {
						Toast.makeText(getApplicationContext(),
								exception.getMessage(), Toast.LENGTH_SHORT)
								.show();
						FbSample2.this.session = createSession();
					}
				}
			};
			pendingRequest = true;
			this.session.openForRead(new Session.OpenRequest(this)
					.setCallback(callback));
		}
	}

	public void share_it() {
		// Toast.makeText(getApplicationContext(), "Share it now" ,
		// Toast.LENGTH_SHORT).show();
		if (session != null) {
			List<String> permission = session.getPermissions();
			System.out
					.println("no of permission it has = " + permission.size());
			for (int i = 0; i < permission.size(); i++) {
				System.out.println("Permission " + i + " => "
						+ permission.get(i));
			}
			if (permission.size() == 0) {
				pendingRequest = true;
				Session.NewPermissionsRequest publishPermission = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(publishPermission);
				// return;
			} else {
				// i have the premission now
				Bundle postParam = new Bundle();
//				 postParam.putString("name", "Ram_name");
//				 postParam.putString("caption", "My_caption ");
//				 postParam.putString("message",
//				 "I am what the message look like");
//				 postParam.putString("description", "I am your decription");
//				 postParam.putString("link",
//				 "https://developers.facebook.com/android");
//				 postParam.putString("picture",
//				 "http://walpopular.com/wp-content/uploads/2013/12/love-birds-2.jpg");

				postParam.putString("name",
						((EditText) findViewById(R.id.etName)).getText()
								.toString());
				postParam.putString("caption",
						((EditText) findViewById(R.id.etCaption)).getText()
								.toString());
				postParam.putString("message",
						"I am what the message look like");
				postParam.putString("description",
						((EditText) findViewById(R.id.etDescription)).getText()
								.toString());
				postParam.putString("link",
						((EditText) findViewById(R.id.etLink)).getText()
								.toString());
				postParam.putString("picture",
						((EditText) findViewById(R.id.etPic)).getText()
								.toString());

				Request.Callback callback = new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						// TODO Auto-generated method stub
						String postId = null;

						try {
							JSONObject graphObject = response.getGraphObject()
									.getInnerJSONObject();

							postId = graphObject.getString("id");
						} catch (Exception ex) {
							System.out
									.println("Error Occured fetching grapho objecyt "
											+ ex.getMessage());
						}

					}
				};

				Request request = new Request(session, "me/feed", postParam,
						HttpMethod.POST, callback);
				RequestAsyncTask reqTask = new RequestAsyncTask(request);
				reqTask.execute();

			}

		}

	}

}

class RequestCallBack implements Request.Callback {

		@Override
		public void onCompleted(Response response) {
			// TODO Auto-generated method stub
			String postId = null;

			try {
				JSONObject graphObject = response.getGraphObject()
						.getInnerJSONObject();

				postId = graphObject.getString("id");
			} catch (Exception ex) {
				System.out
						.println("Error Occured fetching grapho objecyt "
								+ ex.getMessage());
			}

		}
	
}