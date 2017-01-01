package com.mobop.michael_david.stufftracker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by david on 17/11/16.
 */
public class StuffItemsListFragment extends Fragment {

    public static final int FRAGMENT_ID  = 1;
    public static final int ACTION_ID_START_FILTER_FRAGMENT  = 0;
    public static final int ACTION_ID_REFRESH_LIST  = 1;

    private static final String TAG = StuffItemsListFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    OnFragmentInteractionListener mListener;
    StuffTrackerManager stuffTrackerManager;


    private RecyclerItemClickListener.OnItemClickListener stuffListListener
            = new RecyclerItemClickListener.OnItemClickListener() {

        public void onItemClick(View v, int position) {
            System.gc();

            Log.d(TAG, "onItemClick: ItemClicked" + position);

        }
    };

    public StuffItemsListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.list_fragment, container, false);

        Log.d(TAG, "Bug stufftrackermanager: " + stuffTrackerManager);
        Log.d(TAG, "Bug savedInstanceState: " + savedInstanceState);
        if((stuffTrackerManager != null) && (savedInstanceState == null)){
            mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            Toolbar toolbar = (Toolbar) view.findViewById(R.id.main_menu_toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new RecyclerViewAdapter(stuffTrackerManager);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), stuffListListener));

            mAdapter.notifyDataSetChanged();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setStuffTrackerManager(StuffTrackerManager stuffTrackerManager){
        this.stuffTrackerManager = stuffTrackerManager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                // TODO : Start the new activity with the filter menu and implement return object parcelable
                // Signal MainActivity to start filter fragment
                mListener.onFragmentQuit(FRAGMENT_ID, 0);
                return true;

            case R.id.action_refresh:
                //TODO : Refresh list elements with the StuffTrackerManager
                mListener.onFragmentQuit(FRAGMENT_ID, 1);
                mRecyclerView.getRecycledViewPool().clear();
                mAdapter.notifyDataSetChanged();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                mListener = (OnFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener"); }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

}
