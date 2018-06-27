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
import java.io.OutputStream;
import java.io.IOException;
import java.io.EOFException;


/**
 * Data copy over {@link RandomAccessBuffer rabu} defines endianness
 * for multi-byte word ordering.
 */
public class RandomAccessData
    extends RandomAccessBuffer
{
    /**
     * Byte order in word data I/O.
     */
    public static enum Endian {
	/**
	 * Least significant byte, first.
	 */
	LE,
	/**
	 * Most significant byte, first.
	 */
	BE;


	public int uint16(byte[] m, int o){

	    int a = (m[o] & 0xFF);
	    int b = (m[o+1] & 0xFF);

	    switch(this){
	    case LE:
		return (((b & 0xFF) << 8)|(a & 0xFF));

	    case BE:
		return (((a & 0xFF) << 8)|(b & 0xFF));

	    default:
		throw new InternalError(this.name());
	    }
	}
	public int sint32(byte[] m, int o){

	    int a = (m[o] & 0xFF);
	    int b = (m[o+1] & 0xFF);
	    int c = (m[o+2] & 0xFF);
	    int d = (m[o+3] & 0xFF);

	    switch(this){
	    case LE:
		return (((d & 0xFF) << 24)|((c & 0xFF) << 16)|((b & 0xFF) << 8)|(a & 0xFF));

	    case BE:
		return (((a & 0xFF) << 24)|((b & 0xFF) << 16)|((c & 0xFF) << 8)|(d & 0xFF));

	    default:
		throw new InternalError(this.name());
	    }
	}

    }

    /**
     * Multi-byte word ordering employed by this class.
     */
    public final Endian endian;


    public RandomAccessData(Endian e){
	super();
	if (null != e){
	    this.endian = e;
	}
	else {
	    throw new IllegalArgumentException();
	}
    }
    public RandomAccessData(Endian e, byte[] b, int x, int l){
	super(b,x,l);
	if (null != e){
	    this.endian = e;
	}
	else {
	    throw new IllegalArgumentException();
	}
    }
    public RandomAccessData(Endian e, RandomAccessBuffer r){
	super(r);
	if (null != e){
	    this.endian = e;
	}
	else {
	    throw new IllegalArgumentException();
	}
    }
    public RandomAccessData(Endian e, RandomAccessBuffer r, Window w){
	super(r,w);
	if (null != e){
	    this.endian = e;
	}
	else {
	    throw new IllegalArgumentException();
	}
    }


    /**
     * @param ofs Offset of data byte
     * @return Indexed data byte
     */
    public int uint8(int ofs){

	return super.get(ofs);
    }
    /**
     * Write the argument value to the argument output as an eight bit
     * byte.  Return the subject value.
     */
    public int uint8(int a, OutputStream out) throws IOException {

	if (-1 < a & 0xFF >= a){

	    out.write(a);

	    return a;
	}
	else {
	    throw new IllegalArgumentException(String.valueOf(a));
	}
    }
    /**
     * Read one byte from the input argument, and write to the buffer.
     * Return the subject value.
     */
    public int uint8(InputStream in) throws IOException {
	int a = in.read();
	if (-1 < a){

	    super.write(a);

	    return a;
	}
	else {
	    throw new EOFException();
	}
    }
    /**
     * Read one byte from the buffer, and write to the output
     * argument.  Return the subject value.
     */
    public int uint8(OutputStream out) throws IOException {
	int a = super.read();
	if (-1 < a){

	    out.write(a);

	    return a;
	}
	else {
	    throw new EOFException();
	}
    }
    /**
     * @param ofs Offset of data
     * @return Indexed data
     */
    public int uint16(int ofs){

	int a = super.get(ofs);
	int b = super.get(ofs+1);

	switch(this.endian){
	case LE:
	    return (((b & 0xFF) << 8)|(a & 0xFF));

	case BE:
	    return (((a & 0xFF) << 8)|(b & 0xFF));

	default:
	    throw new InternalError(this.endian.name());
	}
    }
    /**
     * Write the argument value to the argument output as an unsigned
     * sixteen bit integer.  Return the subject value.
     */
    public int uint16(int v, OutputStream out) throws IOException {

	if (-1 < v && 0xFFFF >= v){

	    int a = ((v & 0xFF00) >>> 8);
	    int b = (v & 0xFF);

	    switch(this.endian){
	    case LE:
		out.write(b);
		out.write(a);
		return v;
	    case BE:
		out.write(a);
		out.write(b);
		return v;
	    default:
		throw new InternalError(this.endian.name());
	    }
	}
	else {
	    throw new IllegalArgumentException(String.valueOf(v));
	}
    }
    /**
     * Read two bytes from the input argument, and write to the
     * buffer.  Return the subject value.
     */
    public int uint16(InputStream in) throws IOException {
	int a = in.read();
	int b = in.read();
	if (-1 < a && -1 < b){
	    int v = 0;
	    switch(this.endian){
	    case LE:
		v = (((b & 0xFF) << 8)|(a & 0xFF));
		super.write(b);
		super.write(a);
		return v;
	    case BE:
		v = (((a & 0xFF) << 8)|(b & 0xFF));
		super.write(a);
		super.write(b);
		return v;
	    default:
		throw new InternalError(this.endian.name());
	    }
	}
	else {
	    throw new EOFException();
	}
    }
    /**
     * Read two bytes from the buffer, and write to the output
     * argument.  Return the subject value.
     */
    public int uint16(OutputStream out) throws IOException {
	int a = super.read();
	int b = super.read();
	if (-1 < a && -1 < b){
	    int v = 0;
	    switch(this.endian){
	    case LE:
		v = (((b & 0xFF) << 8)|(a & 0xFF));
		out.write(b);
		out.write(a);
		return v;
	    case BE:
		v = (((a & 0xFF) << 8)|(b & 0xFF));
		out.write(a);
		out.write(b);
		return v;
	    default:
		throw new InternalError(this.endian.name());
	    }
	}
	else {
	    throw new EOFException();
	}
    }
    /**
     * @param ofs Offset of data 
     * @return Indexed data 
     */
    public int sint32(int ofs){

	int a = super.get(ofs);
	int b = super.get(ofs+1);
	int c = super.get(ofs+2);
	int d = super.get(ofs+3);
	switch(this.endian){
	case LE:
	    return (((d & 0xFF) << 24)|((c & 0xFF) << 16)|((b & 0xFF) << 8)|(a & 0xFF));

	case BE:
	    return (((a & 0xFF) << 24)|((b & 0xFF) << 16)|((c & 0xFF) << 8)|(d & 0xFF));

	default:
	    throw new InternalError(this.endian.name());
	}
    }
    /**
     * Write the argument value to the argument output as a signed,
     * thirty two bit integer.  Return the subject value.
     */
    public int sint32(int v, OutputStream out) throws IOException {
	int a = ((v & 0xFF000000) >>> 24);
	int b = ((v & 0x00FF0000) >>> 16);
	int c = ((v & 0x0000FF00) >>> 8);
	int d = (v & 0x000000FF);

	switch(this.endian){
	case LE:
	    out.write(d);
	    out.write(c);
	    out.write(b);
	    out.write(a);
	    return v;
	case BE:
	    out.write(a);
	    out.write(b);
	    out.write(c);
	    out.write(d);
	    return v;
	default:
	    throw new InternalError(this.endian.name());
	}
    }
    /**
     * Read four bytes from the input argument, and write to the
     * buffer.  Return the subject value.
     */
    public int sint32(InputStream in) throws IOException {
	int a = in.read();
	int b = in.read();
	int c = in.read();
	int d = in.read();
	if (-1 < a && -1 < b && -1 < c && -1 < d){
	    int v = 0;
	    switch(this.endian){
	    case LE:
		v = (((d & 0xFF) << 24)|((c & 0xFF) << 16)|((b & 0xFF) << 8)|(a & 0xFF));
		super.write(d);
		super.write(c);
		super.write(b);
		super.write(a);
		return v;
	    case BE:
		v = (((a & 0xFF) << 24)|((b & 0xFF) << 16)|((c & 0xFF) << 8)|(d & 0xFF));
		super.write(a);
		super.write(b);
		super.write(c);
		super.write(d);
		return v;
	    default:
		throw new InternalError(this.endian.name());
	    }
	}
	else {
	    throw new EOFException();
	}
    }
    /**
     * Read four bytes from the buffer, and write to the output
     * argument.  Return the subject value.
     */
    public int sint32(OutputStream out) throws IOException {
	int a = super.read();
	int b = super.read();
	int c = super.read();
	int d = super.read();
	if (-1 < a && -1 < b && -1 < c && -1 < d){
	    int v = 0;
	    switch(this.endian){
	    case LE:
		v = (((d & 0xFF) << 24)|((c & 0xFF) << 16)|((b & 0xFF) << 8)|(a & 0xFF));
		out.write(d);
		out.write(c);
		out.write(b);
		out.write(a);
		return v;
	    case BE:
		v = (((a & 0xFF) << 24)|((b & 0xFF) << 16)|((c & 0xFF) << 8)|(d & 0xFF));
		out.write(a);
		out.write(b);
		out.write(c);
		out.write(d);
		return v;
	    default:
		throw new InternalError(this.endian.name());
	    }
	}
	else {
	    throw new EOFException();
	}
    }
    /**
     * @param ofs Offset of data 
     * @return Indexed data 
     */
    public long sint64(int ofs) throws IOException {
	int a = super.get(ofs);
	int b = super.get(ofs+1);
	int c = super.get(ofs+2);
	int d = super.get(ofs+3);
	int e = super.get(ofs+4);
	int f = super.get(ofs+5);
	int g = super.get(ofs+6);
	int h = super.get(ofs+7);

	switch(this.endian){
	case LE:
	    return (((h & 0xFFL) << 56)|((g & 0xFFL) << 48)|((f & 0xFFL) << 40)|((e & 0xFFL) << 32)|
		    ((d & 0xFFL) << 24)|((c & 0xFFL) << 16)|((b & 0xFFL) << 8)|(a & 0xFFL));

	case BE:
	    return (((a & 0xFFL) << 56)|((b & 0xFFL) << 48)|((c & 0xFFL) << 40)|((d & 0xFFL) << 32)|
		    ((e & 0xFFL) << 24)|((f & 0xFFL) << 16)|((g & 0xFFL) << 8)|(h & 0xFFL));

	default:
	    throw new InternalError(this.endian.name());
	}
    }
    /**
     * Write the argument value to the argument output as a signed,
     * sixty four bit integer.  Return the subject value.
     */
    public long sint64(long v, OutputStream out) throws IOException {
	int a = (int)((v & 0xFF00000000000000L) >>> 56);
	int b = (int)((v & 0x00FF000000000000L) >>> 48);
	int c = (int)((v & 0x0000FF0000000000L) >>> 40);
	int d = (int)((v & 0x000000FF00000000L) >>> 32);
	int e = (int)((v & 0x00000000FF000000L) >>> 24);
	int f = (int)((v & 0x0000000000FF0000L) >>> 16);
	int g = (int)((v & 0x000000000000FF00L) >>> 8);
	int h = (int)(v & 0x00000000000000FFL);

	switch(this.endian){
	case LE:
	    out.write(h);
	    out.write(g);
	    out.write(f);
	    out.write(e);
	    out.write(d);
	    out.write(c);
	    out.write(b);
	    out.write(a);
	    return v;
	case BE:
	    out.write(a);
	    out.write(b);
	    out.write(c);
	    out.write(d);
	    out.write(e);
	    out.write(f);
	    out.write(g);
	    out.write(h);
	    return v;
	default:
	    throw new InternalError(this.endian.name());
	}
    }
    /**
     * Read eight bytes from the input argument, and write to the
     * buffer.  Return the subject value.
     */
    public long sint64(InputStream in) throws IOException {
	int a = in.read();
	int b = in.read();
	int c = in.read();
	int d = in.read();
	int e = in.read();
	int f = in.read();
	int g = in.read();
	int h = in.read();
	if (-1 < a && -1 < b && -1 < c && -1 < d && -1 < e && -1 < f && -1 < g && -1 < h){
	    long v = 0;
	    switch(this.endian){
	    case LE:
		v = (((h & 0xFFL) << 56)|((g & 0xFFL) << 48)|((f & 0xFFL) << 40)|((e & 0xFFL) << 32)|
		     ((d & 0xFFL) << 24)|((c & 0xFFL) << 16)|((b & 0xFFL) << 8)|(a & 0xFFL));
		super.write(h);
		super.write(g);
		super.write(f);
		super.write(e);
		super.write(d);
		super.write(c);
		super.write(b);
		super.write(a);
		return v;
	    case BE:
		v = (((a & 0xFFL) << 56)|((b & 0xFFL) << 48)|((c & 0xFFL) << 40)|((d & 0xFFL) << 32)|
		     ((e & 0xFFL) << 24)|((f & 0xFFL) << 16)|((g & 0xFFL) << 8)|(h & 0xFFL));
		super.write(a);
		super.write(b);
		super.write(c);
		super.write(d);
		super.write(e);
		super.write(f);
		super.write(g);
		super.write(h);
		return v;
	    default:
		throw new InternalError(this.endian.name());
	    }
	}
	else {
	    throw new EOFException();
	}
    }
    /**
     * Read eight bytes from the buffer, and write to the output
     * argument.  Return the subject value.
     */
    public long sint64(OutputStream out) throws IOException {
	int a = super.read();
	int b = super.read();
	int c = super.read();
	int d = super.read();
	int e = super.read();
	int f = super.read();
	int g = super.read();
	int h = super.read();
	if (-1 < a && -1 < b && -1 < c && -1 < d && -1 < e && -1 < f && -1 < g && -1 < h){
	    long v = 0;
	    switch(this.endian){
	    case LE:
		v = (((h & 0xFFL) << 56)|((g & 0xFFL) << 48)|((f & 0xFFL) << 40)|((e & 0xFFL) << 32)|
		     ((d & 0xFFL) << 24)|((c & 0xFFL) << 16)|((b & 0xFFL) << 8)|(a & 0xFFL));
		out.write(h);
		out.write(g);
		out.write(f);
		out.write(e);
		out.write(d);
		out.write(c);
		out.write(b);
		out.write(a);
		return v;
	    case BE:
		v = (((a & 0xFFL) << 56)|((b & 0xFFL) << 48)|((c & 0xFFL) << 40)|((d & 0xFFL) << 32)|
		     ((e & 0xFFL) << 24)|((f & 0xFFL) << 16)|((g & 0xFFL) << 8)|(h & 0xFFL));
		out.write(a);
		out.write(b);
		out.write(c);
		out.write(d);
		out.write(e);
		out.write(f);
		out.write(g);
		out.write(h);
		return v;
	    default:
		throw new InternalError(this.endian.name());
	    }
	}
	else {
	    throw new EOFException();
	}
    }

}
