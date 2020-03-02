package com.mortex.accenture.task.ui.weather_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mortex.accenture.task.R;
import com.mortex.accenture.task.data.model.GetTempResponse;
import com.mortex.accenture.task.ui.util.Utils;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private MainActivity context;
    private List<GetTempResponse> getTempResponseList = Collections.emptyList();

    public WeatherAdapter(MainActivity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final GetTempResponse getTempResponse = getTempResponseList.get(position);

        String dateAndCity = getTempResponse.getName() + "-" + Utils.getDate(getTempResponse.getDt());
        holder.dateTv.setText(dateAndCity);

        //kelvin to celsius
        String centigrade = ((int) Math.round(getTempResponse.getMain().getTemp() - 273.15)) + "˚";
        holder.tempTv.setText(centigrade);
    }

    @Override
    public int getItemCount() {
        return getTempResponseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void replaceWith(List<GetTempResponse> list) {
        this.getTempResponseList = list;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        GetTempResponse mRecentlyDeletedItem = getTempResponseList.get(position);
        if (mRecentlyDeletedItem.getId() != null) {
            context.removeFromDb(mRecentlyDeletedItem);
        }
        getTempResponseList.remove(mRecentlyDeletedItem);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        TextView dateTv;
        @BindView(R.id.tv_temp)
        TextView tempTv;
        @BindView(R.id.item)
        CardView item;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}