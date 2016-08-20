package ms.ihc.control.resource;

import java.util.ArrayList;

import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.ConnectionManager;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ResourceAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ConnectionManager connectionManager;

	private ArrayList<IHCResource> resourceData = new ArrayList<IHCResource>();

	public ResourceAdapter(ApplicationContext context) {
		mInflater = LayoutInflater.from(context);
		this.connectionManager = context.getIHCConnectionManager();
	}

	public void removeItem(int position) {
		resourceData.remove(position);
		notifyDataSetChanged();
	}
	
	public void addItem(final IHCResource resource) {
		resourceData.add(resource);
		notifyDataSetChanged();
	}

	public int getCount() {
		return resourceData.size();
	}

	public Object getItem(int position) {
		return resourceData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		return resourceData.get(position).getDeviceId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		IHCResource resource = resourceData.get(position);
		convertView = resource.getView(mInflater, connectionManager);
		convertView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			}
		});
		convertView.setFocusableInTouchMode(true);
		convertView.setClickable(true);
		//convertView.setBackgroundResource(android.R.drawable.list_selector_background);
		return convertView;
	}

	public static class ViewHolder {
		public Button button1;
		public Button button2;
		public Button button3;
		public Button button4;
		public Button button5;
		public Button button6;
		public Button button7;
		public Button button8;
		public Button button9;
		public Button button10;
		public Button button11;
		public Button button12;
		public TextView position;
		public TextView viewType;
		public TextView deviceName;
		public TextView room;
		public TextView floor;
		public SeekBar seekbar;
		public ImageView favouriteImg;
	}

}
