package ms.ihc.control.resource.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import ms.ihc.control.fragments.BaseFragment;
import ms.ihc.control.resource.ResourceAdapter;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.viewer.IHCHome;
import ms.ihc.control.viewer.IHCLocation;
import ms.ihc.control.viewer.R;


public class ResourceListFragment extends BaseFragment implements OnItemClickListener {

    private final String TAG = ResourceListFragment.class.getName();
    ResourceAdapter resourceAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.generic_listview,container,false);

        String location  = getActivity().getIntent().getStringExtra("location");

        ListView resourceListView = (ListView) view.findViewById(R.id.listview);
        resourceAdapter = new ResourceAdapter(getApplicationContext());


        Log.v(TAG,"Loading resources");
        IHCHome ihcHome = getApplicationContext().getIHCHome();
        if(ihcHome != null) {
            for (IHCLocation ihcLocation : ihcHome.getLocations()) {
                if (ihcLocation.getName().equals(location)) {
                    for (IHCResource ihcResource : ihcLocation.getResources()) {
                        ihcResource.setLocation("");
                        resourceAdapter.addItem(ihcResource);
                    }
                }
            }


            resourceListView.setAdapter(resourceAdapter);
            /*registerForContextMenu(resourceListView);
            resourceListView.setOnCreateContextMenuListener(this);*/
        }

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onResourceChanged(){
        resourceAdapter.notifyDataSetChanged();
    }
}
