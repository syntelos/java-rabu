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

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.File;

/**
 * 
 */
public final class Test {
    /**
     * 
     */
    private static void usage(){
	err.println("Synopsis");
	err.println();
	err.println("    syntelos.rabu.Test <instr>*");
	err.println();
	err.println("Description");
	err.println();
	err.println("    One or more instructions from the following set, space delimited.");
	err.println();

	err.println("Instruction Set");
	err.println();

	for (Operator c: Operator.values()){

	    err.printf("%10s %-20s -- %s%n",c.name(),c.help.operands,c.help.description);
	    err.println();
	}
	System.exit(1);
    }
    /**
     * 
     */
    public static enum Operand {
	STR,
	INT;
    }
    /**
     * 
     */
    public static enum Operator {
	echo   ("","Print state."),
	print  ("","Read from buffer."),
	read   ("<file>","Write to buffer.",Operand.STR),
	window ("<offset> <count>","Constrain buffer to window.",Operand.INT,Operand.INT);


	private final static Object[] NARGS = new Object[]{};


	public final Help help;

	public final Operand[] operands;



	Operator(String ho, String hd, Operand... operands){
	    this.help = new Help(ho,hd);
	    this.operands = operands;
	}


	public Object[] operands(String[] argv, int argc, int argl){
	    int count = this.operands.length;
	    if (0 < count){
		Object[] re = new Object[count];
		if ( (argc+count) <= argl){

		    for (int cc = 0; cc < count; cc++){

			int argx = (argc+cc);
			String arg = argv[argx];
			switch(operands[cc]){
			case STR:
			    re[cc] = arg;
			    break;
			case INT:
			    re[cc] = new Integer(arg);
			    break;
			default:
			    throw new IllegalStateException();
			}
		    }
		    return re;
		}
		else {
		    throw new IllegalArgumentException();
		}
	    }
	    else {
		return NARGS;
	    }
	}

	/**
	 * 
	 */
	public static class Help {
	    public final String operands;
	    public final String description;

	    Help(String o, String d){
		this.operands = o;
		this.description = d;
	    }
	}
    }
    /**
     * 
     */
    public static class State
	extends BufferPrinter
    {

	public RandomAccessFile raf;

	public File file;

	public int read;


	public State(){
	    super();
	}


	protected boolean echo(){
	    /*
	     */
	    if (null != this.file)

		out.printf("(test file: %s, read: %d)%n", this.file.getPath(), this.read);
	    else
		out.printf("(test read: %d)%n", this.read);
	    /*
	     */
	    if (null != raf){

		RandomAccessBuffer.Window window = raf.window;

		RandomAccessBuffer.Buffer buffer = raf.buffer;

		RandomAccessBuffer.State state = raf.state;

		out.printf("(rabu window offset: %d, length: %d)%n", window.delta, window.length);
		out.printf("(rabu buffer length: %d, size: %d)%n", buffer.length,buffer.buffer.length);
		out.printf("(rabu i/o pointer internal: %d, external: %d)%n", window.internal(state), state.external);
	    }
	    return true;
	}
	protected boolean print(){
	    /*
	     */
	    if (null != this.raf){

		if (this.raf.seek(0)){

		    int av = this.raf.available();
		    if (0x100 < av){

			byte[] b = new byte[0x100];

			int c = 0, r;
			while (-1 < (r = this.raf.read(b,0,0x100))){

			    c += r;

			    super.print(b,0,r);

			}
			out.println();
			if (c == av){
			    out.printf("(test print success [read %d/%d])%n",c,av);
			    return true;
			}
			else {
			    out.printf("(test print failure [read %d/%d])%n",c,av);
			    return false;
			}
		    }
		    else if (0 < av){
			byte[] b = new byte[av];

			int r = this.raf.read(b,0,av);

			super.print(b,0,r);

			out.println();

			if (av != r){

			    out.printf("(test print failure [read %d/%d])%n",r,av);
			    return false;
			}
			else {

			    out.printf("(test print success [read %d])%n",av);
			    return true;
			}
		    }
		    else {
			out.println("(test print [available 0] failed)");
			return false;
		    }
		}
		else {
		    out.println("(test print [seek 0] failed)");
		    return false;
		}
	    }
	    else {
		out.println("(test print missing rabu)");
		return false;
	    }
	}
	protected boolean read(String arg){
	    File file = new File(arg);
	    if (file.isFile() && file.canRead()){

		this.file = file;

		raf = new RandomAccessFile();

		this.read = raf.read(file);

		echo();

		if (0 < this.read){

		    super.reset();

		    return true;
		}
	    }
	    return false;
	}
	protected boolean window(int x, int c){
	    raf = new RandomAccessFile(raf,new RandomAccessBuffer.Window(x,c));

	    super.seek(x);

	    return echo();
	}
    }
    /**
     * 
     */
    public static class Instruction {

	public final Operator command;

	public final Object[] operands;


	public Instruction(Operator c, Object... o){
	    this.command = c;
	    this.operands = o;
	}


	public boolean proc(State s){
	    switch(command){
	    case echo:
		{
		    return s.echo();
		}
	    case print:
		{
		    return s.print();
		}
	    case read:
		{
		    String o = (String)operands[0];

		    return s.read(o);
		}
	    case window:
		{
		    int o = ((Integer)operands[0]).intValue();
		    int c = ((Integer)operands[1]).intValue();
		    return s.window(o,c);
		}
	    default:
		throw new IllegalStateException(this.command.name());
	    }
	}
    }

    public static void main(String[] argv){
	int argl = argv.length;

	if (0 < argl){
	    int argc = 0;
	    String arg = null;
	    State state = new State();
	    try {
		while (argc < argl){
		    arg = argv[argc++];

		    Operator c = Operator.valueOf(arg);
		    Object[] o = c.operands(argv,argc,argl);
		    {
			argc += o.length;
		    }
		    Instruction i = new Instruction(c,o);

		    if (! i.proc(state)){

			System.exit(1);
		    }
		}

		System.exit(0);
	    }
	    catch (IllegalArgumentException unk){

		unk.printStackTrace();

		err.printf("syntelos.rabu.Test error: unrecognized command term '%s'.%n",arg);

		System.exit(1);
	    }
	}
	else {
	    usage();
	}
    }
}
