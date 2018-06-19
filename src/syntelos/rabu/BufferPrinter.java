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

import java.io.PrintStream;

/**
 * Following the "octal dump" (unix "od") concept into {@link Test}.
 */
public class BufferPrinter {
    /**
     * An ASCII named character table.
     */
    public static enum ASCII {
	NUL(0x00),
	SOH(0x01),
	STX(0x02),
	ETX(0x03),
	EOT(0x04),
	ENQ(0x05),
	ACK(0x06),
	BEL(0x07),
	BS(0x08),
	HT(0x09),
	LF(0x0A),
	VT(0x0B),
	FF(0x0C),
	CR(0x0D),
	SO(0x0E),
	SI(0x0F),
	DLE(0x10),
	DC1(0x11),
	DC2(0x12),
	DC3(0x13),
	DC4(0x14),
	NAK(0x15),
	SYN(0x16),
	ETB(0x17),
	CAN(0x18),
	EM(0x19),
	SUB(0x1A),
	ESC(0x1B),
	FS(0x1C),
	GS(0x1D),
	RS(0x1E),
	US(0x1F),
	SP(0x20),
	DEL(0x7F);


	public final int code;


	ASCII(int code){
	    this.code = code;
	}


	public final static ASCII valueOf(int code){
	    switch(code){
	    case 0x00: return NUL;
	    case 0x01: return SOH;
	    case 0x02: return STX;
	    case 0x03: return ETX;
	    case 0x04: return EOT;
	    case 0x05: return ENQ;
	    case 0x06: return ACK;
	    case 0x07: return BEL;
	    case 0x08: return BS;
	    case 0x09: return HT;
	    case 0x0A: return LF;
	    case 0x0B: return VT;
	    case 0x0C: return FF;
	    case 0x0D: return CR;
	    case 0x0E: return SO;
	    case 0x0F: return SI;
	    case 0x10: return DLE;
	    case 0x11: return DC1;
	    case 0x12: return DC2;
	    case 0x13: return DC3;
	    case 0x14: return DC4;
	    case 0x15: return NAK;
	    case 0x16: return SYN;
	    case 0x17: return ETB;
	    case 0x18: return CAN;
	    case 0x19: return EM;
	    case 0x1A: return SUB;
	    case 0x1B: return ESC;
	    case 0x1C: return FS;
	    case 0x1D: return GS;
	    case 0x1E: return RS;
	    case 0x1F: return US;
	    case 0x20: return SP;
	    case 0x7F: return DEL;
	    default:   return null;
	    }
	}
    }
    /**
     * Work in progress
     */
    public static enum Format {
	ASC  (Format.Operand.NONE),
	HEX  (Format.Operand.SIZE);


	/**
	 * Work in progress
	 */
	public static enum Operand {
	    NONE (-1),
	    SIZE (0);

	    public final int floor;

	    Operand(int floor){
		this.floor = floor;
	    }

	    public boolean accept(int value){
		return (floor < value);
	    }
	    public boolean is(Operand opnd){
		return (this == opnd);
	    }
	}


	public final Operand operand;

	Format(Operand operand){
	    this.operand = operand;
	}
    }



    public final Format format;

    public final int size;

    private int p = 0;


    public BufferPrinter(){
	this(Format.ASC);
    }
    public BufferPrinter(Format f){
	super();
	if (null != f && Format.Operand.NONE.is(f.operand)){

	    this.format = f;

	    this.size = Format.Operand.NONE.floor;
	}
	else {
	    throw new IllegalArgumentException();
	}
    }
    public BufferPrinter(Format f, int size){
	super();
	if (null != f && Format.Operand.SIZE.is(f.operand) && Format.Operand.SIZE.accept(size)){

	    this.format = f;

	    this.size = size;
	}
	else {
	    throw new IllegalArgumentException();
	}
    }


    public void reset(){
	this.p = 0;
    }
    public void seek(int p){
	this.p = p;
    }
    public void print(byte[] b, int o, int l){

	this.print(b,o,l,System.out);
    }
    public void print(byte[] b, int o, int l, PrintStream out){

	int c = 0;

	while (o < l){

	    out.printf("%07d",p);

	    for (c = 0; c < 20 && o < l; c++,p++,o++){

		byte bb = b[o];

		switch(this.format){

		case ASC:
		    ASCII as = ASCII.valueOf(bb);
		    if (null != as){

			out.printf(" %3s",as.name());
		    }
		    else if (0x20 < bb && 0x7f > bb){

			out.printf(" %3c",bb);
		    }
		    else {

			out.printf(" %03X",bb);
		    }
		    break;

		case HEX:
		    out.printf(" %02X",bb);
		    break;

		default:
		    throw new IllegalStateException(this.format.name());
		}
	    }

	    out.println();
	}

	out.printf("%07d%n",p);
    }
}
