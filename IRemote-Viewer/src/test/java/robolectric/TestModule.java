package robolectric;

import android.content.Context;
import android.test.ActivityTestCase;

import org.junit.Before;
import org.junit.runner.RunWith;

import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.ConnectionManager;




/**
 * Created by mortenstriboldt on 02/02/14.
 */

/*@RunWith(RobolectricTestRunner .class)
public class robolectric.TestModule  {
    private SettingsActivity activity;
    private IhcManager ihcManager;

    @Before
    public void setup() {
        this.activity = Robolectric.buildActivity(SettingsActivity.class).get();
        this.ihcManager = ((ApplicationContext)activity.getApplicationContext()).getIHCConnectionManager();
    }

    @Test
    public void shouldNotBeNull() {
        assertThat(activity).isNotNull();

    }

    @Test
    public void shouldLoginOK() {
        assertThat(activity).isNotNull();
        this.ihcManager.authenticate("admin","ospekos4u!","striboldt.lgnas.com",true);

    }
}*/
