package com.tahahamdan.develop4android.agrisenceweather;



        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private Context context;
    ArrayList<Weather> weatherArrayList;
    String unitsType;

    public DataAdapter(ArrayList<Weather> weatherlist, String _unitsType,Context current) {
        weatherArrayList= weatherlist;
        unitsType=_unitsType;
        context=current;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.forecast_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        Weather weather = weatherArrayList.get(i);
        viewHolder.dailytime.setText(formateDate(weather.getdtTime()));
        String tempUnits = (unitsType.equals("metric")==true ? "°C" : "°F");
        viewHolder.dailytemp.setText(String.valueOf(weather.getMinTemp())+"/"+String.valueOf(weather.getMaxTemp())+tempUnits);
        viewHolder.dailyDescription.setText(weather.getCondDescr());
        int id= context.getResources().getIdentifier("com.tahahamdan.develop4android.agrisenceweather:drawable/" + weather.condIcon.toLowerCase(), null, null);
        viewHolder.dailyImage.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return weatherArrayList.size();    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dailytime;
        private ImageView dailyImage;
        private TextView dailytemp;
        private TextView dailyDescription;

        public ViewHolder(View view) {
            super(view);
            dailytime = (TextView) view.findViewById(R.id.dailytime);
            dailyImage = (ImageView) view.findViewById(R.id.dailyImage);
            dailytemp = (TextView) view.findViewById(R.id.dailytemp);
            dailyDescription = (TextView) view.findViewById(R.id.dailydescription);
        }
    }

    private String formateDate(String unixString) {
        long unixSeconds = Long.parseLong(unixString);
        Date date = new Date(unixSeconds * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}