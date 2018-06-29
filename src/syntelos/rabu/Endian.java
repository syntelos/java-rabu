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
 * Byte order in word data I/O.
 * 
 * @see RandomAccessData
 */
public enum Endian {
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
