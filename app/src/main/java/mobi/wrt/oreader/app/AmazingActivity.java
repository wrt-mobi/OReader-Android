package mobi.wrt.oreader.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import mobi.wrt.oreader.app.clients.db.ClientEntity;


public class AmazingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amazing);
        ImageLoader.getInstance().displayImage(getIntent().getStringExtra(ClientEntity.ICON), (ImageView)findViewById(R.id.photo));
    }

}
