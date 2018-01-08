package com.chen.home.chartDemo;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chen.home.R;
import com.chen.home.chartDemo.barchart.BarCardOne;
import com.chen.home.chartDemo.barchart.BarCardThree;
import com.chen.home.chartDemo.barchart.BarCardTwo;
import com.chen.home.chartDemo.linechart.LineCardOne;
import com.chen.home.chartDemo.linechart.LineCardThree;
import com.chen.home.chartDemo.linechart.LineCardTwo;
import com.chen.home.chartDemo.stackedchart.StackedCardOne;
import com.chen.home.chartDemo.stackedchart.StackedCardThree;
import com.chen.home.chartDemo.stackedchart.StackedCardTwo;


class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {

    private final static int NUM_CHARTS = 9;
    private final Context mContext;

    ChartAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart1, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart2, parent, false);
                break;
            case 2:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart3, parent, false);
                break;
            case 3:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart4, parent, false);
                break;
            case 4:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart5, parent, false);
                break;
            case 5:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart6, parent, false);
                break;
            case 6:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart7, parent, false);
                break;
            case 7:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart8, parent, false);
                break;
            case 8:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart9, parent, false);
                break;
            default:
                v = LayoutInflater.from(mContext).inflate(R.layout.chart1, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (position) {
            case 0:
                (new LineCardOne(holder.cardView, mContext)).init();
                break;
            case 1:
                (new LineCardThree(holder.cardView, mContext)).init();
                break;
            case 2:
                (new BarCardOne(holder.cardView, mContext)).init();
                break;
            case 3:
                (new StackedCardThree(holder.cardView, mContext)).init();
                break;
            case 4:
                (new StackedCardOne(holder.cardView)).init();
                break;
            case 5:
                (new BarCardThree(holder.cardView, mContext)).init();
                break;
            case 6:
                (new BarCardTwo(holder.cardView, mContext)).init();
                break;
            case 7:
                (new StackedCardTwo(holder.cardView)).init();
                break;
            case 8:
                (new LineCardTwo(holder.cardView)).init();
                break;
            default:
                (new LineCardOne(holder.cardView, mContext)).init();
                break;
        }
    }

    @Override
    public int getItemCount() {

        return NUM_CHARTS;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final CardView cardView;

        ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.chart_card);
        }
    }
}