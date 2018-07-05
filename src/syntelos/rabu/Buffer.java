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
import java.io.PrintStream;

/**
 * This interface employs an externalized coordinate space to
 * implement a window / aperture constraint.
 */
public class Buffer
    extends Printer
{
    /**
     * Occasionally optimistic
     */
    public byte[] buffer;
    /**
     * Readable content
     */
    public int length;
    /**
     * Default rate of growth 
     */
    private int growth = 0x100;


    protected Buffer(Printer.Configuration c){
	super(c);
	this.buffer = new byte[0x100];
	this.length = 0;
    }
    protected Buffer(Printer.Configuration c, byte[] b){
	super(c);
	if (null != b && 0 < b.length){
	    this.buffer = b;
	    this.length = b.length;
	}
	else {
	    throw new IllegalArgumentException();
	}
    }


    /**
     * @param q Rate of growth may be aligned to a typical page using
     * {@link #floor(int)} and {@link #ceil(int)}.
     * 
     * @see #floor(int)
     * @see #ceil(int)
     */
    public void grow(int q){

	if (0 >= q){

	    q = this.growth;
	}
	else {

	    this.growth = q;
	}

	byte[] grow = new byte[this.buffer.length + q];

	System.arraycopy(this.buffer,0,grow,0,this.buffer.length);

	this.buffer = grow;
    }
    public int internal(Window w, State s){

	return w.internal(s);
    }
    public int internal(Window w, State s, int external){

	return w.internal(external);
    }
    public int available(Window w, State s){

	int q = w.available(s);
	if (-1 < q){
	    /*
	     * Window constrains availability
	     */
	    return q;
	}
	else {
	    int i = w.internal(s);
	    /*
	     * Buffer constrains availability
	     */
	    return (this.length-i);
	}
    }
    public boolean bounds(Window w, int i){

	if (-1 < i){

	    return (i < this.buffer.length && w.bounds(i));
	}
	else {
	    return false;
	}
    }
    public boolean bounds(Window w, int i, int q){
	/*
	 * [TODO]    Constrain WRITE by WINDOW
	 */
	if (-1 < i && 0 < q){

	    return ((i+q) <= this.buffer.length);
	}
	else {
	    return false;
	}
    }
    /**
     * Read from buffer with effect to {@link State}.
     */
    public int read(Window w, State s){

	int i = this.internal(w,s);
	int q = this.available(w,s);

	if (this.bounds(w,i,q)){

	    s.external += 1;

	    return (this.buffer[i] & 0xFF);
	}
	else {
	    return -1;
	}
    }
    public int read(Window w, State s, byte[] b, int o, int l){

	int i = this.internal(w,s);
	int q = Math.min(l,this.available(w,s));

	if (this.bounds(w,i,q)){

	    s.external += q;

	    System.arraycopy(this.buffer,i,b,o,q);

	    return q;
	}
	else {
	    return -1;
	}
    }
    /**
     * No effect to {@link State}
     */
    public boolean print(Window w, State s){

	return this.print(w,s,System.out);
    }
    public boolean print(Window w, State s, PrintStream out){

	int i = this.internal(w,s);
	int q = this.available(w,s);

	if (this.bounds(w,i,q)){

	    super.print(this.buffer,i,q,out);

	    return true;
	}
	else {
	    return false;
	}
    }
    public boolean reset(Window w, State s){

	return this.seek(w,s,0);
    }
    /**
     * Change {@link State}
     */
    public boolean seek(Window w, State s, int external){

	int i = this.internal(w,s,external);

	if (this.bounds(w,i)){

	    s.external = external;

	    super.seek(external);

	    return true;
	}
	else {
	    return false;
	}
    }
    /**
     * Write to buffer with effect to {@link State}
     */
    public boolean write(Window w, State s, int b){
	/*
	 * [TODO]    Constrain WRITE by WINDOW
	 */
	int i = this.internal(w,s);

	if (0 <= i){

	    if ((i+1) >= this.length){

		if ((i+1) > this.buffer.length){

		    this.grow(-1);
		}

		this.length += 1;
	    }
	    /*
	     */
	    if (this.bounds(w,i)){

		this.buffer[i] = (byte)(b & 0xFF);

		s.external += 1;

		return true;
	    }
	}
	return false;
    }
    public boolean write(Window w, State s, byte[] b, int o, int l){
	/*
	 * [TODO]    Constrain WRITE by WINDOW
	 */
	int i = w.internal(s);

	int q = (i+l);

	if (q > this.buffer.length){

	    this.grow(ceil(q));
	}
	/*
	 */
	if (this.bounds(w,i,l)){

	    System.arraycopy(b,o,this.buffer,i,l);

	    s.external += l;

	    this.length += l;

	    return true;
	}
	else {
	    return false;
	}
    }
    public boolean copy(Window w, State s, InputStream in, int count)
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
	return (0 == count);
    }
    public int copy(Window w, State s, OutputStream out)
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
    public byte[] copy(Window w, State s, int x, int q)
    {
	int i = this.internal(w,s,x);

	if (this.bounds(w,i,q)){

	    byte[] b = new byte[q];
	    {
		System.arraycopy(this.buffer,i,b,0,q);
	    }
	    return b;
	}
	else {
	    return null;
	}
    }
    public int get(Window w, State s, int x){

	int i = this.internal(w,s,x);

	if (this.bounds(w,i)){

	    return (this.buffer[i] & 0xFF);
	}
	else {
	    throw new IllegalArgumentException(String.valueOf(x));
	}
    }
    public boolean set(Window w, State s, int x, int v){

	int i = this.internal(w,s,x);
	int q = this.available(w,s);

	if (this.bounds(w,i)){

	    this.buffer[i] = (byte)(v & 0xFF);

	    return true;
	}
	else {
	    throw new IllegalArgumentException(String.valueOf(x));
	}
    }
    public int indexOf(Window w, State s, int c){

	int x = s.external;
	int i = this.internal(w,s);
	int q = this.available(w,s);

	while (i < this.length){

	    if (c == (this.buffer[i] & 0xFF)){

		return x;
	    }
	    else {
		i++; x++;
	    }
	}
	return -1;
    }
    public String substring(Window w, State s, int o, int l){

	int i = this.internal(w,s,o);
	int q = Math.min(l,this.available(w,s));

	if (0 < q && i < this.length){

	    return new String(this.buffer,0,i,q);
	}
	else {
	    throw new IllegalArgumentException(String.format("offset: %d, length: %d",o,l));
	}
    }
    /**
     * Optimistic rate of growth filter
     */
    public final static int ceil(int q){
	if (0x100 > q)
	    return 0x100;
	else {
	    q &= 0x7FFFFF00;
	    q += 0x100;
	    return q;
	}
    }
    /**
     * Pessimistic rate of growth filter
     */
    public final static int floor(int q){
	if (0x100 > q)
	    return q;
	else if (0x200 < q)
	    return 0x200;
	else
	    return 0x100;
    }
}
