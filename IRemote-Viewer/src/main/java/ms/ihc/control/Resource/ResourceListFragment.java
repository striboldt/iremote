package ms.ihc.control.Resource;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.HashMap;

import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.IHCHome;
import ms.ihc.control.viewer.R;


public class ResourceListFragment extends Fragment implements OnItemClickListener {

    private final String TAG = ResourceListFragment.class.getName();

    private Handler mHandler = new Handler();
    private ConnectionManager connectionManager = null;
    private IHCHome home = null;
    public static HashMap<Integer, IHCResource> resourceMap = new HashMap<Integer, IHCResource>();
    public static ResourceAdapter resourceAdapter;

    private ApplicationContext appContext;
    private String selectedLocation;
    private ListView resourceListView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.resources,container,false);

        appContext = (ApplicationContext) getActivity().getApplicationContext();

        selectedLocation = getArguments().getString("location");

        resourceListView = (ListView) view.findViewById(R.id.resourcelist);
        registerForContextMenu(resourceListView);

        resourceListView.setAdapter(resourceAdapter);
        resourceListView.setOnCreateContextMenuListener(this);



        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
