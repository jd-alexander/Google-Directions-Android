package com.directions.sample;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.route.Segment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rsavutiu on 13/10/2016.
 */
public class SegmentsAdapter extends RecyclerView.Adapter<SegmentsAdapter.ViewHolder> {

    private List<Segment> segments = new ArrayList<>();

    public SegmentsAdapter(List<Segment> segments) {
        this.segments = segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.turn_by_turn_instruction_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Segment item = segments.get(position);
        String maneuver = item.getManeuver();
        if (!TextUtils.isEmpty(maneuver)) {
            for (GoogleManeuvers googleManeuver : GoogleManeuvers.values()) {
                if (maneuver.equals(googleManeuver.maneuverName)) {
                    holder.ivIcon.setImageDrawable(SampleApp.getInstance().getResources()
                            .getDrawable(googleManeuver.drawable));
                    break;
                }
            }
        }
        holder.tvInstruction.setText(item.getInstruction());
        holder.tvDistance.setText(Double.toString(item.getDistance()));
    }

    @Override
    public int getItemCount() {
        return (segments!=null)?segments.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        //we only have 1 view type so far.
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.tvInstruction)
        public TextView tvInstruction;
        @InjectView(R.id.tvDistance)
        public TextView tvDistance;
        @InjectView(R.id.ivIcon)
        public ImageView ivIcon;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public enum GoogleManeuvers {
        TURN_SHARP_LEFT("turn-sharp-left", R.drawable.ic_arrow_back_white_48dp),
        UTURN_RIGHT("uturn-right", R.drawable.ic_arrow_forward_white_48dp),
        TURN_SLIGHT_RIGHT("turn-slight-right", R.drawable.ic_arrow_forward_white_48dp),
        MERGE("merge", R.drawable.ic_arrow_upward_white_48dp),
        ROUNDABOUT_LEFT("roundabout-left", R.drawable.ic_arrow_back_white_48dp),
        ROUNDABOUT_RIGHT("roundabout-right", R.drawable.ic_arrow_forward_white_48dp),
        UTURN_LEFT("uturn-left", R.drawable.ic_arrow_back_white_48dp),
        TURN_SLIGHT_LEFT("turn-slight-left", R.drawable.ic_arrow_back_white_48dp),
        TURN_LEFT("turn-left", R.drawable.ic_arrow_back_white_48dp),
        RAMP_RIGHT("ramp-right", R.drawable.ic_arrow_forward_white_48dp),
        TURN_RIGHT("turn-right", R.drawable.ic_arrow_forward_white_48dp),
        FORK_RIGHT("fork-right", R.drawable.ic_arrow_forward_white_48dp),
        STRAIGHT("straight", R.drawable.ic_arrow_upward_white_48dp),
        FORK_LEFT("fork-left", R.drawable.ic_arrow_back_white_48dp),
        FERRY_TRAIN("ferry-train", R.drawable.icon_content_oepnv_faehre),
        TURN_SHARP_RIGHT("turn-sharp-right", R.drawable.ic_arrow_forward_white_48dp),
        RAMP_LEFT("ramp-left", R.drawable.ic_arrow_back_white_48dp),
        FERRY("ferry", R.drawable.icon_content_oepnv_faehre);
        private final int drawable;
        private final String maneuverName;

        private GoogleManeuvers(String maneuverName, int drawable) {
            this.maneuverName = maneuverName;
            this.drawable = drawable;
        }
    };
}
