/*
 * RandomAccessBuffer
 * Copyright (C) 2018, John Pritchard, Syntelos
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package syntelos.rabu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * File handling over {@link RandomAccessBuffer rabu} copies file to buffer.
 */
public class RandomAccessFile
    extends RandomAccessBuffer
{


    public RandomAccessFile(){
	super();
    }
    public RandomAccessFile(byte[] b, int x, int l){
	super(b,x,l);
    }
    public RandomAccessFile(RandomAccessBuffer r){
	super(r);
    }
    public RandomAccessFile(RandomAccessBuffer r, Window w){
	super(r,w);
    }


    public int read(File file){
	int c = 0;
	FileInputStream fin = null;
	try {
	    fin = new FileInputStream(file);
	    byte[] b = new byte[0x200];
	    int r;

	    while (0 < (r = fin.read(b,0,0x200))){

		c += r;

		if (!this.write(b,0,r)){

		    throw new IllegalStateException("buffer write failed.");
		}
	    }
	    return c;
	}
	catch (IOException iox){
	    throw new IllegalArgumentException(file.getPath(),iox);
	}
	finally {
	    if (null != fin){
		try {
		    fin.close();
		}
		catch (Throwable t){
		}
	    }
	}
    }
    public int write(File file){

	FileOutputStream fout = null;
	try {
	    fout = new FileOutputStream(file);

	    byte[] b = new byte[0x200];
	    int r, c = 0;

	    while (0 < (r = this.read(b,0,0x200))){

		c += r;

		fout.write(b,0,r);

	    }
	    return c;
	}
	catch (IOException iox){
	    throw new IllegalArgumentException(file.getPath(),iox);
	}
	finally {
	    if (null != fout){
		try {
		    fout.close();
		}
		catch (Throwable t){
		}
	    }
	}
    }
}
