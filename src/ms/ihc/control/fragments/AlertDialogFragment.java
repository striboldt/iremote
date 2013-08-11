package ms.ihc.control.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

import ms.ihc.control.viewer.R;

/**
 * Created by mortenstriboldt on 11/08/13.
 */
public class AlertDialogFragment extends DialogFragment {

    public static AlertDialogFragment newInstance(int title) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        // We have only one dialog.
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(R.string.unlicensed_dialog_body)
                .setPositiveButton(R.string.buy_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData( Uri.parse(
                                "market://details?id=" + getActivity().getPackageName()));
                        startActivity(marketIntent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })

                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener(){
                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        getActivity().finish();
                        return true;
                    }
                })
                .create();
    }
}
