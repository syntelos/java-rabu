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


    /**
     * @return User I/O pointer (buffer offset)
     */
    public int offset(){

	return this.state.external;
    }
    /**
     * Same as {@link #length()}
     */
    public int available(){

	return this.buffer.available(this.window,this.state);
    }
    /**
     * Set user I/O pointer {@link State} to zero (origin).
     */
    public boolean reset(){

	return this.buffer.reset(this.window,this.state);
    }
    /**
     * @param external User I/O pointer (buffer offset)
     */
    public boolean seek(int external){

	return this.buffer.seek(this.window,this.state,external);
    }
    /**
     * Employ {@link Window} and {@link State} to return a byte from
     * the {@link Buffer}, incrementing the user I/O pointer.
     */
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
    /**
     * Employ {@link Window} and {@link State} to write a byte to the
     * {@link Buffer}, incrementing the user I/O pointer.
     */
    public boolean write(int b){

	return this.buffer.write(this.window,this.state,b);
    }
    public boolean write(byte[] b, int o, int l){

	return this.buffer.write(this.window,this.state,b,o,l);
    }
    /**
     * Write to buffer without incrementing the user I/O pointer
     * ({@link State}).
     */
    public boolean copy(InputStream in, int count)
	throws IOException
    {
	return this.buffer.copy(this.window,this.state,in,count);
    }
    /**
     * Read from buffer without incrementing the user I/O pointer
     * ({@link State}).
     */
    public int copy(OutputStream out)
	throws IOException
    {
	return this.buffer.copy(this.window,this.state,out);
    }
    /**
     * Read from buffer without incrementing the user I/O pointer
     * ({@link State}).
     */
    public byte[] copy(int x, int q)
    {
	return this.buffer.copy(this.window,this.state,x,q);
    }
    /**
     * Copy buffer to (standard) output using I/O pointer {@link
     * State} with external offsets.  This operation has no effect on
     * the read state.
     */
    public boolean print(){

	return this.buffer.print(this.window,this.state);
    }
    /**
     * Copy buffer to (argument) output using I/O pointer {@link
     * State} with external offsets.  This operation has no effect on
     * the read state.
     */
    public boolean print(PrintStream out){

	return this.buffer.print(this.window,this.state,out);
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
