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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Buffer handling and windowing.  The "read" interface is stateful,
 * having an internal read position (offset pointer).  
 * 
 * The "write" interface will grow the buffer.  This design element
 * needs refinement.  Changing the buffer memory region is a critical
 * operation, which requires representation.
 * 
 * The random access interface (get/set) is stateless.
 */
public class RandomAccessBuffer
    extends Object
{
    /**
     * User constraint translates buffer-external to buffer-internal
     * coordinate space.
     *
     * <pre>
     * internal = (window.delta + external)
     * </pre>
     */
    protected static class Window
	extends Object
    {
	/**
	 * Aperture floor as index (from zero) relative to buffer origin.
	 */
	protected int delta;
	/**
	 * Aperture ceiling as count from {@link #delta}.
	 */
	protected int length;


	Window(){
	    super();
	    this.delta = 0;
	    this.length = 0;
	}
	Window(int ofs, int len){
	    super();
	    if (-1 < ofs && 0 < len){
		this.delta = ofs;
		this.length = len;
	    }
	    else {
		throw new IllegalArgumentException(String.format("ofs %d, len %d",ofs,len));
	    }
	}


	protected int internal(State s){
	    return (delta+s.external);
	}
	protected int internal(int external){
	    return (delta+external);
	}
	protected int available(State s){

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
    }
    /**
     * This interface employs an externalized coordinate space to
     * implement a window / aperture constraint.
     */
    protected static class Buffer
	extends Object
    {
	/**
	 * Occasionally optimistic
	 */
	protected byte[] buffer;
	/**
	 * Readable content
	 */
	protected int length;


	Buffer(){
	    super();
	    this.buffer = new byte[0x100];
	    this.length = 0;
	}
	Buffer(byte[] b){
	    super();
	    if (null != b && 0 < b.length){
		this.buffer = b;
		this.length = b.length;
	    }
	    else {
		throw new IllegalArgumentException();
	    }
	}


	protected void grow(int q){

	    if (0 < q){

		byte[] grow = new byte[this.buffer.length + q];

		System.arraycopy(this.buffer,0,grow,0,this.buffer.length);

		this.buffer = grow;
	    }
	    else {
		throw new IllegalArgumentException();
	    }
	}
	protected int internal(Window w, State s){

	    return w.internal(s);
	}
	protected int internal(Window w, State s, int external){

	    return w.internal(external);
	}
	protected int available(Window w, State s){
	    int i = w.internal(s);
	    int q = w.available(s);
	    if (-1 < q){
		/*
		 * Window constrains availability
		 */
		if ((i+q) < this.length){

		    return q;
		}
		else {

		    throw new IllegalStateException("Window larger than buffer");
		}
	    }
	    else {
		/*
		 * Buffer constrains availability
		 */
		return (this.length-i);
	    }
	}
	protected int read(Window w, State s){

	    int i = this.internal(w,s);
	    int q = this.available(w,s);

	    if (0 < q){

		s.external += 1;

		return this.buffer[i];
	    }
	    else {
		return -1;
	    }
	}
	protected int read(Window w, State s, byte[] b, int o, int l){

	    int i = this.internal(w,s);
	    int q = Math.min(l,this.available(w,s));

	    if (0 < q){

		s.external += q;

		System.arraycopy(this.buffer,i,b,o,q);

		return q;
	    }
	    else {
		return -1;
	    }
	}
	protected boolean seek(Window w, State s, int external){

	    int i = this.internal(w,s,external);

	    if (0 <= i && i < this.length){

		s.external = external;

		return true;
	    }
	    else {
		return false;
	    }
	}
	protected boolean write(Window w, State s, int b){
	    /*
	     * [TODO]    Constrain WRITE by WINDOW
	     */
	    int i = this.internal(w,s);

	    if (0 <= i){

		if ((i+1) >= this.length){

		    if ((i+1) > this.buffer.length){

			this.grow(0x100);
		    }

		    this.length += 1;
		}

		this.buffer[i] = (byte)(b & 0xFF);

		s.external += 1;

		return true;
	    }
	    else {
		return false;
	    }
	}
	protected boolean write(Window w, State s, byte[] b, int o, int l){
	    /*
	     * [TODO]    Constrain WRITE by WINDOW
	     */
	    int i = w.internal(s);

	    if (0 <= i){

		int q = (i+l);

		if (q > this.buffer.length){

		    this.grow(ceil(q));
		}

		System.arraycopy(b,o,this.buffer,i,l);

		s.external += l;

		this.length += l;

		return true;
	    }
	    else {
		return false;
	    }
	}
	protected boolean copy(Window w, State s, InputStream in, int count)
	    throws IOException
	{
	    int z = floor(count);
	    byte[] b = new byte[z];
	    int r;

	    while (0 < count && 0 < (r = in.read(b,0,z))){

		if (!this.write(w,s,b,0,r)){
		    return false;
		}
		else {
		    count -= r;
		    if (z > count){
			z = count;
		    }
		}
	    }
	    return true;
	}
	protected int copy(Window w, State s, OutputStream out)
	    throws IOException
	{
	    int z = floor(this.available(w,s));
	    byte[] b = new byte[z];
	    int r;
	    int c = 0;

	    while (0 < (r = this.read(w,s,b,0,z))){

		out.write(b,0,r);

		c += r;
	    }
	    return c;
	}
	protected int get(Window w, State s, int x){

	    int i = this.internal(w,s,x);
	    int q = this.available(w,s);

	    if (0 < q && -1 < i){

		return this.buffer[i];
	    }
	    else {
		throw new IllegalArgumentException(String.valueOf(x));
	    }
	}
	protected boolean set(Window w, State s, int x, int v){

	    int i = this.internal(w,s,x);
	    int q = this.available(w,s);

	    if (0 < q && -1 < i){

		this.buffer[i] = (byte)(v & 0xff);

		return true;
	    }
	    else {
		throw new IllegalArgumentException(String.valueOf(x));
	    }
	}
	protected int indexOf(Window w, State s, int c){

	    int x = s.external;
	    int i = this.internal(w,s);
	    int q = this.available(w,s);

	    if (-1 < i){

		while (i < q){

		    if (c == (this.buffer[i] & 0xFF)){

			return x;
		    }
		    else {
			i++; x++;
		    }
		}
	    }
	    return -1;
	}
	protected String substring(Window w, State s, int o, int l){

	    int i = this.internal(w,s,o);
	    int q = Math.min(l,this.available(w,s));

	    if (0 < q){

		return new String(this.buffer,0,i,q);
	    }
	    else {
		throw new IllegalArgumentException(String.format("offset: %d, length: %d",o,l));
	    }
	}

	protected final static int ceil(int q){
	    if (0x100 > q)
		return 0x100;
	    else {
		q &= 0x7FFFFF00;
		q += 0x100;
		return q;
	    }
	}
	protected final static int floor(int q){
	    if (0x100 > q)
		return q;
	    else if (0x200 < q)
		return 0x200;
	    else
		return 0x100;
	}
    }
    /**
     * External offset
     */
    protected static class State
	extends Object
    {
	protected int external = 0;
    }


    protected final Window window;

    protected final Buffer buffer;

    protected final State state = new State();


    public RandomAccessBuffer(){
	super();
	this.buffer = new Buffer();
	this.window = new Window();
    }
    public RandomAccessBuffer(byte[] b, int external, int len){
	super();
	this.buffer = new Buffer(b);
	this.window = new Window(external,len);
    }
    public RandomAccessBuffer(RandomAccessBuffer r){
	super();
	this.buffer = r.buffer;
	this.window = r.window;
    }
    public RandomAccessBuffer(RandomAccessBuffer r, Window w){
	super();
	this.buffer = r.buffer;
	this.window = w;
    }


    public int offset(){

	return this.state.external;
    }
    /**
     * Same as {@link #length()}
     */
    public int available(){

	return this.buffer.available(this.window,this.state);
    }
    public boolean seek(int external){

	return this.buffer.seek(this.window,this.state,external);
    }
    public int read(){

	return this.buffer.read(this.window,this.state);
    }
    public int read(byte[] b, int o, int l){

	if (null != b && -1 < o && o < l){

	    return this.buffer.read(this.window,this.state,b,o,l);
	}
	else {
	    throw new IllegalArgumentException();
	}
    }
    public boolean write(int b){

	return this.buffer.write(this.window,this.state,b);
    }
    public boolean write(byte[] b, int o, int l){

	return this.buffer.write(this.window,this.state,b,o,l);
    }
    /**
     * Write to buffer
     */
    public boolean copy(InputStream in, int count)
	throws IOException
    {
	return this.buffer.copy(this.window,this.state,in,count);
    }
    /**
     * Read from buffer
     */
    public int copy(OutputStream out)
	throws IOException
    {
	return this.buffer.copy(this.window,this.state,out);
    }
    /**
     * Same as {@link #available()}.
     * 
     * @see #get(int)
     * @see #set(int,int)
     */
    public int length(){

	return this.buffer.available(this.window,this.state);
    }
    /**
     * Random access constrainted by window and read.
     * 
     * @see #length()
     * @see #set(int,int)
     */
    public int get(int x){

	return this.buffer.get(this.window,this.state,x);
    }
    /**
     * Random access constrainted by window and read.
     * 
     * @see #length()
     * @see #get(int)
     */
    public boolean set(int x, int v){

	return this.buffer.set(this.window,this.state,x,v);
    }
    /**
     * 
     */
    public int indexOf(int c){

	return this.buffer.indexOf(this.window,this.state,c);
    }
    /**
     * 
     */
    public String substring(int o, int l){

	return this.buffer.substring(this.window,this.state,o,l);
    }
}