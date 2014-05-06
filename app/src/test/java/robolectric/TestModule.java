package robolectric;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class TestModule {

    @Test
    public void shouldFail() {
        assertTrue(false);
    }
}

/**
 * Created by mortenstriboldt on 02/02/14.
 */
/*
@RunWith(RobolectricTestRunner.class)
public class robolectric.TestModule  {
    private SettingsActivity activity;
    private IhcManager ihcManager;

    @Before
    public void setup() {
        this.activity = Robolectric.buildActivity(SettingsActivity.class).get();
        this.ihcManager = ((ApplicationContext)activity.getApplicationContext()).getInstaceIhcManager();
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
}
*/