package com.saud.gauchoblog;

import android.graphics.BitmapFactory;

public class BitmapHelper {

	public static WidthHeight calculateResizeWidthAndHeight(int rawWidth, int rawHeight, int reqWidth, int reqHeight) {
		
		WidthHeight wh = new WidthHeight(rawWidth, rawHeight);
		
		if(wh.getWidth() < reqWidth || wh.getHeight() < reqHeight) {
			return wh;
		}
		
		do {
			wh.setWidth(wh.getWidth()/2);
			wh.setHeight(wh.getHeight()/2);
		}while(wh.getWidth() > reqWidth && wh.getHeight()> reqHeight);

		return wh;
	}
	
	public static int calculateInSampleSize(int rawWidth, int rawHeight, int reqWidth, int reqHeight) {
	    int inSampleSize = 1;

	    if (rawHeight > reqHeight || rawWidth > reqWidth) {

	        final int halfHeight = rawHeight / 2;
	        final int halfWidth = rawWidth / 2;

	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;		
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int width = options.outWidth;
	    final int height = options.outHeight;
	    return calculateInSampleSize(width, height, reqWidth, reqHeight);
	}

}