package ms.ihc.control.Utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import ms.ihc.control.viewer.R;

/**
 * Created by mortenstriboldt on 28/12/15.
 */
public class SnackbarHelper {

    public static void createSnack(Activity activity, String text){
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(R.color.statusBarColor);
        snackbar.show();
    }
}
