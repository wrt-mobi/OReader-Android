/**
 * 
 */
package mobi.wrt.oreader.app.clients.twitter.bo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import by.istin.android.xcore.model.JSONModel;


/**
 * @author IstiN
 * 
 * create date: Feb 19, 2011
 */
public class UserItem extends JSONModel {

	protected static final String UID = "id";
	
	private static final String NAME = "name";
	
	private static final String NICKNAME = "screen_name";

	public static final String TOKEN_SECRET = "token_secret";

	public static final String TOKEN = "token";

	public Long getUid() {
		return getLong(UID);
	}
	
	public String getNickname() {
		return getString(NICKNAME);
	}
	
	public String getName() {
		return getString(NAME);
	}
	
	public String getTokenSecret() {
		return getString(TOKEN_SECRET);
	}

	public String getToken() {
		return getString(TOKEN);
	}

	public UserItem() {
		super();
	}

	public UserItem(JSONObject json) {
		super(json);
	}

	public UserItem(Parcel source) {
		super(source);
	}

	public UserItem(String json) throws JSONException {
		super(json);
	}

	public static final Parcelable.Creator<UserItem> CREATOR = new Parcelable.Creator<UserItem>() {

		public UserItem createFromParcel(Parcel in) {
			return new UserItem(in);
		}

		public UserItem[] newArray(int size) {
			return new UserItem[size];
		}
	};

	public static ICreator<UserItem> MODEL_CREATOR = new ICreator<UserItem>() {
		
		@Override
		public UserItem create(JSONObject jsonObject) {
			return new UserItem(jsonObject);
		}
	};
	
	@Override
	public boolean equals(Object o) {
		return getUid().equals(((UserItem)o).getUid());
	}

}
