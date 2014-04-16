/**
 * 
 */
package mobi.wrt.oreader.app.clients.feedly.bo;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import by.istin.android.xcore.model.JSONModel;


public class AuthResponse extends JSONModel {

	protected static final String REFRESH_TOKEN = "refresh_token";
	
	private static final String ID = "id";
	
	private static final String ACCESS_TOKEN = "access_token";

	private static final String EXPIRES_IN = "expires_in";

	private static final String STATE = "state";

	private static final String TOKEN_TYPE = "token_type";

	private static final String PLAN = "plan";

	public String getId() {
		return getString(ID);
	}

	public String getAccessToken() {
		return getString(ACCESS_TOKEN);
	}

	public Long getExpiresIn() {
		return getLong(EXPIRES_IN);
	}

	public String getState() {
		return getString(STATE);
	}

	public String getTokenType() {
		return getString(TOKEN_TYPE);
	}

	public String getRefreshToken() {
		return getString(REFRESH_TOKEN);
	}

	public String getPlan() {
		return getString(PLAN);
	}

	public AuthResponse() {
		super();
	}

	public AuthResponse(JSONObject json) {
		super(json);
	}

	public AuthResponse(Parcel source) {
		super(source);
	}

	public AuthResponse(String json) throws JSONException {
		super(json);
	}

	public static final Creator<AuthResponse> CREATOR = new Creator<AuthResponse>() {

		public AuthResponse createFromParcel(Parcel in) {
			return new AuthResponse(in);
		}

		public AuthResponse[] newArray(int size) {
			return new AuthResponse[size];
		}
	};

	public static ICreator<AuthResponse> MODEL_CREATOR = new ICreator<AuthResponse>() {
		
		@Override
		public AuthResponse create(JSONObject jsonObject) {
			return new AuthResponse(jsonObject);
		}
	};
	
}
