package com.dailynews.lin.dailynews.Utils;
import android.util.Log;
public class LogUtils {
	private static boolean debag = true;
	public static void d(Object obj, String msg){
		if (debag) {
			Log.d(obj.getClass().getSimpleName(), msg);
		}
	}
}
