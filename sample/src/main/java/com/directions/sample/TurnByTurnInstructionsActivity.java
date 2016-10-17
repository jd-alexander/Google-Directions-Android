package com.directions.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rsavutiu on 17/10/2016.
 */
public class TurnByTurnInstructionsActivity extends Activity {
    private static final String TAG = "TurnByTurnInstructionsActivity";
    public static String TRAVEL_MODE_EXTRA = "TRAVEL_MODE_EXTRA";
    private Route route;
    private AbstractRouting.TravelMode travelMode;
    @InjectView(R.id.rvSegmentsList)
    RecyclerView rvSegmentsList;
    @InjectView(R.id.title)
    TextView title;
    private SegmentsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_by_turn);
        ButterKnife.inject(this);
        rvSegmentsList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SegmentsAdapter(null);
        rvSegmentsList.setAdapter(mAdapter);
        final Intent startIntent = getIntent();
        if (startIntent.hasExtra(TRAVEL_MODE_EXTRA)) {

            if (MainActivity.getInstance().getRoutesLines()!=null &&
                MainActivity.getInstance().getRoutesLines().size()>0) {
                route = MainActivity.getInstance().getRoutesLines().get(0).second;
                travelMode = (AbstractRouting.TravelMode) startIntent.getSerializableExtra(TRAVEL_MODE_EXTRA);
                title.setText(travelMode.name());
                mAdapter.setSegments(route.getSegments());
                mAdapter.notifyDataSetChanged();
                Logger.getLogger().i(TAG, "Route: " + route.toString());
                Logger.getLogger().i(TAG, "Travel Mode: " + travelMode.name());
            }
        }
    }
}
