package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ModelClass.DateDayModelClass;
import dto.Datedto;
import dto.Timedto;
import ws.wolfsoft.rapid.GeyserFiveActivity;
import ws.wolfsoft.rapid.R;


public class TimeRecycleAdapter extends RecyclerView.Adapter<TimeRecycleAdapter.MyViewHolder> {

	public interface OnTimeSelected {
		void onSelect();
	}

    private Context context;
    private List<Timedto> list = new ArrayList<>();
	private OnTimeSelected listener;

    public TimeRecycleAdapter(Context context) {
        this.context = context;
    }
	
	public void submitList(List<Timedto> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public void addListener(OnTimeSelected l) {
		this.listener = l;
	}
	
	public void clearSelections() {
		for (Timedto td : list) {
			td.setSelected(false);
		}
		notifyDataSetChanged();
	}
	
    @Override
    public TimeRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_list, parent, false);
        return new TimeRecycleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Timedto dto = list.get(position);

        holder.time.setText(dto.getTime());
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Timedto td : list) {
					td.setSelected(false);
				}
				dto.setSelected(true);
				if(listener != null) listener.onSelect();
                notifyDataSetChanged();
            }
        });

        if (dto.isSelected()) {
            holder.time.setTextColor(Color.parseColor("#6685ff"));
            holder.hours.setTextColor(Color.parseColor("#6685ff"));
            holder.linear.setBackgroundResource(R.drawable.blue_dateday_rect);
        } else {
            holder.time.setTextColor(Color.parseColor("#8f909e"));
            holder.hours.setTextColor(Color.parseColor("#8f909e"));
            holder.linear.setBackgroundResource(R.drawable.gray_dateday_rect);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

	public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView time, hours;
        LinearLayout linear;

        public MyViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.time);
            hours = (TextView) view.findViewById(R.id.hours);
            linear = (LinearLayout) view.findViewById(R.id.linear);
        }
    }
}


