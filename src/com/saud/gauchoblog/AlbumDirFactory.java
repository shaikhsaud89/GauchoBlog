package com.saud.gauchoblog;

import java.io.File;

import android.os.Environment;

public class AlbumDirFactory {

	public static File getAlbumStorageDir(String albumName) {
		return new File(
		  Environment.getExternalStoragePublicDirectory(
		    Environment.DIRECTORY_PICTURES
		  ), 
		  albumName
		);
	}

}
