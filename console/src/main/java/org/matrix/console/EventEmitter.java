package org.matrix.console;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class EventEmitter<T> {
    private static final String LOG_TAG = "EventEmitter";

    private Set<Listener<T>> mCallbacks;

    public EventEmitter() {
        mCallbacks = new HashSet<>();
    }

    public void register(Listener<T> cb) {
        mCallbacks.add(cb);
    }

    public void unregister(Listener<T> cb) {
        mCallbacks.remove(cb);
    }

    /**
     * Fires all registered callbacks on the UI thread.
     * @param t
     */
    public void fire(final T t) {
        final Set<Listener<T>> callbacks = new HashSet<>(mCallbacks);

        Looper looper = Looper.getMainLooper();
        Handler uiHandler = new Handler(looper);
        uiHandler.post(new Runnable() {
               @Override
               public void run() {
                   for (Listener<T> cb : callbacks) {
                       try {
                           cb.onEventFired(EventEmitter.this, t);
                       } catch (Exception e) {
                           Log.e(LOG_TAG, "Callback threw: " + e.getMessage(), e);
                       }
                   }
               }
           }
        );
    }

    public interface Listener<T> {
        void onEventFired(EventEmitter<T> emitter, T t);
    }
}