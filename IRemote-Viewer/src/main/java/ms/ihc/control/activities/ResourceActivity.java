package ms.ihc.control.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ms.ihc.control.viewer.R;


public class ResourceActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resources);
    }


}
