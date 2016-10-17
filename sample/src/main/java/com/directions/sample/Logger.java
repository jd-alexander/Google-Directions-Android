package com.directions.sample;

import android.util.Log;

/** 27 Sep 2013
 * Radu Savutiu,,
 * @author Radu Savutiu
*/
public class Logger {
    private static Logger _singleton;

    public static Logger getLogger() {
        if (_singleton==null) {
            _singleton = new Logger();
        }
        return _singleton;
    }

	public void i(String TAG, String msg) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "^" + msg);
		}
	}
	public void i(String TAG, String msg, Throwable thr) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "^" + msg, thr);
		}
	}
	public void d(String TAG, String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "^" + msg);
		}
	}
	public static void d(String TAG, String msg, Throwable thr) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "^" + msg, thr);
		}
	}
	public void e(String TAG, String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(TAG, "^" + msg);
		}
	}
	public void e(String TAG, String msg, Throwable thr) {
		if (BuildConfig.DEBUG) {
			Log.e(TAG, "^" + msg, thr);
		}
	}
	public void v(String TAG, String msg) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "^" + msg);
		}
	}
	public void v(String TAG, String msg, Throwable thr) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "^" + msg, thr);
		}
	}
	public void w(String TAG, String msg) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "^" + msg);
		}
	}
	public void w(String TAG, String msg, Throwable thr) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "^" + msg, thr);
		}
	}
}