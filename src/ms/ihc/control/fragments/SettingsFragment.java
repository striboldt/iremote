package ms.ihc.control.fragments;

import ms.ihc.control.viewer.R;
import ms.ihc.control.viewer.R.ihc;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsFragment extends Fragment {

	private static final String PREFS_NAME = "IHCSettings";	
	private SharedPreferences settings = null;
	Button next;
	Button previous;
	
	private CompoundButton reloadProjectCheckBox;
	private CompoundButton wanCheckBox;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.settings, container);

		settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		((EditText) view.findViewById(R.ihc.username)).setText(settings.getString("username", ""));
		((EditText) view.findViewById(R.ihc.password)).setText(settings.getString("password", ""));
		((EditText) view.findViewById(R.ihc.lan_ip)).setText(settings.getString("lan_ip", ""));
		((EditText) view.findViewById(R.ihc.wan_ip)).setText(settings.getString("wan_ip", ""));
		
		if (Build.VERSION.SDK_INT >= 14) {
			this.wanCheckBox = (Switch) view.findViewById(R.ihc.WanOnly);
			this.reloadProjectCheckBox = (Switch) view.findViewById(ihc.reloadProject);
		}
		else {
			this.wanCheckBox = (CheckBox) view.findViewById(R.ihc.WanOnly);
			this.reloadProjectCheckBox = (CheckBox) view.findViewById(ihc.reloadProject);		
		}
		
		this.wanCheckBox.setChecked(settings.getBoolean("wanonly", false));
		return view;

	}
	

	


	 
	 
	 





}