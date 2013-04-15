package com.romraider.util;

import java.io.File;
import java.util.Vector;

public class VectorUtils {

	public static Vector<File> Walk( String path ) {

        File root = new File( path );
        File[] list = root.listFiles();
        Vector<File> ret = new Vector<File> ();

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                ret.addAll(Walk( f.getAbsolutePath() ));
            }
            else {
                ret.add(f);
            }
        }
        return ret;
    }
	
	public static Vector<File> FilterCI( Vector<File> files, String filter){
		Vector<File> ret = new Vector<File>();
		for(File f : files)
		{
			if(f.getName().toUpperCase().contains(filter.toUpperCase()))
			{
				ret.add(f);
			}
		}
		return ret;
	}
}
