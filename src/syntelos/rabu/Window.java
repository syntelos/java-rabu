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

/**
 * Programmer's window is an abstraction from the file format.
 * 
 * User constraint translates buffer-external to buffer-internal
 * coordinate space.
 *
 * <pre>
 * internal = (window.delta + external)
 * </pre>
 */
public class Window
    extends Object
{
    /**
     * Aperture floor as index (from zero) relative to buffer origin.
     */
    public int delta;
    /**
     * Aperture ceiling as count from {@link #delta}.
     */
    public int length;


    protected Window(){
	super();
	this.delta = 0;
	this.length = 0;
    }
    public Window(int ofs, int len){
	super();
	if (-1 < ofs && 0 < len){
	    this.delta = ofs;
	    this.length = len;
	}
	else {
	    throw new IllegalArgumentException(String.format("ofs %d, len %d",ofs,len));
	}
    }


    public int internal(State s){
	return (delta+s.external);
    }
    public int internal(int external){
	return (delta+external);
    }
    public int available(State s){

	if (0 < length){

	    if (s.external < length){
		/*
		 * Window open
		 */
		return (length-s.external);
	    }
	    else {
		/*
		 * Window closed
		 */
		return 0;
	    }
	}
	else {
	    /*
	     * No window aperture constraint
	     */
	    return -1;
	}
    }
    public boolean bounds(int i){

	if (0 <= this.delta && 0 < this.length){

	    return (this.delta <= i && i < this.length);
	}
	else {
	    return true;
	}
    }
    public boolean bounds(int i, int q){

	if (0 <= this.delta && 0 < this.length){

	    return (this.delta <= i && (i+q) <= this.length);
	}
	else {
	    return true;
	}
    }
}
