package com.chen.algorithm.backtrace;

/**
 * Created by admin on 2018/3/14.
 */

public abstract class BactTrace<TContext, TStrategy> {
    protected void backTrace(TContext context, TStrategy tStrategy) {
        if (finishTarget(context, tStrategy)) {
            process(context);
            return;
        }
        Iterable<TStrategy> strategies = getAvailableStrategy();
        for (TStrategy strategy : strategies) {
            handle();
            mark();
            backTrace(context, tStrategy);
            unMark();
        }

    }

    protected abstract void mark();
    protected abstract void unMark();
    protected abstract void process(TContext context);
    protected abstract void handle();
    protected abstract boolean finishTarget(TContext context, TStrategy strategy);
    protected abstract Iterable<TStrategy> getAvailableStrategy();
}
