package com.chen.home.chart.animation;

import com.chen.home.chart.model.ChartSet;

import java.util.ArrayList;


/**
 * Interface used by {@link Animation} to interact with {@link com.chen.home.chart.view.ChartView}
 */
public interface ChartAnimationListener {

    /**
     * Callback to let {@link com.chen.home.chart.view.ChartView} know when to invalidate and present new data.
     *
     * @param data Chart data to be used in the next view invalidation.
     * @return True if {@link com.chen.home.chart.view.ChartView} accepts the call, False otherwise.
     */
    boolean onAnimationUpdate(ArrayList<ChartSet> data);
}
