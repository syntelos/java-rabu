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
	window ("<offset> <count>","Constrain buffer to window.",Operand.INT,Operand.INT),
	write  ("<file>","Read from buffer.",Operand.STR),
	seek   ("<offset>","Set I/O pointer offset.",Operand.INT),
	reset  ("","Set I/O pointer offset to zero.");


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
    public static class Instruction {

	public final Operator operator;

	public final Object[] operands;

	public final String string;


	public Instruction(Operator c, Object... o){
	    this.operator = c;
	    this.operands = o;
	    this.string = toString(this);
	}


	public boolean proc(Test s){
	    switch(operator){
	    case echo:
		{
		    return s.echo(this);
		}
	    case print:
		{
		    return s.print(this);
		}
	    case read:
		{
		    String o = (String)operands[0];

		    return s.read(this,o);
		}
	    case window:
		{
		    int o = ((Integer)operands[0]).intValue();
		    int c = ((Integer)operands[1]).intValue();
		    return s.window(this,o,c);
		}
	    case write:
		{
		    String o = (String)operands[0];

		    return s.write(this,o);
		}
	    case seek:
		{
		    int o = ((Integer)operands[0]).intValue();

		    return s.seek(this,o);
		}
	    case reset:
		{
		    return s.reset(this);
		}

	    default:
		throw new IllegalStateException(this.operator.name());
	    }
	}


	public String toString(){
	    return this.string;
	}

	private final static String toString(Instruction i){
	    StringBuilder string = new StringBuilder();
	    {
		string.append('(');
		string.append(i.operator.name());

		int count = i.operands.length;
		for (int cc = 0; cc < count; cc++){
		    Object o = i.operands[cc];
		    string.append(' ');
		    string.append(o);
		}
		string.append(')');
	    }
	    return string.toString();
	}
    }

    public static void main(String[] argv){
	int argl = argv.length;

	if (0 < argl){
	    int argc = 0;
	    String arg = null;
	    Test test = new Test();
	    try {
		while (argc < argl){
		    arg = argv[argc++];

		    Operator c = Operator.valueOf(arg);
		    Object[] o = c.operands(argv,argc,argl);
		    {
			argc += o.length;
		    }
		    Instruction i = new Instruction(c,o);

		    if (! i.proc(test)){

			System.exit(1);
		    }
		}

		System.exit(0);
	    }
	    catch (IllegalArgumentException unk){

		unk.printStackTrace();

		err.printf("syntelos.rabu.Test error: unrecognized operator term '%s'.%n",arg);

		System.exit(1);
	    }
	}
	else {
	    usage();
	}
    }


    public RandomAccessData rada;

    public File file;

    public int read = 0, wrote = 0;


    public Test(){
	super();
    }


    protected boolean echo(Instruction i){
	out.println(i);
	/*
	 */
	if (null != this.file)

	    out.printf("%s test file: %s, read: %d, wrote: %d.%n", i, this.file.getPath(), this.read, this.wrote);
	else
	    out.printf("%s test read: %d, wrote: %d.%n", i, this.read, this.wrote);
	/*
	 */
	if (null != rada){

	    Window window = rada.window;

	    Buffer buffer = rada.buffer;

	    State state = rada.state;

	    out.printf("%s rabu window offset: %d, length: %d.%n", i, window.delta, window.length);
	    out.printf("%s rabu buffer length: %d, size: %d.%n", i, buffer.length,buffer.buffer.length);
	    out.printf("%s rabu i/o pointer internal: %d, external: %d.%n", i, window.internal(state), state.external);
	}
	out.println();

	return true;
    }
    protected boolean print(Instruction i){
	/*
	 */
	if (null != this.rada){

	    this.rada.print(out);

	    out.printf("%s %n",i);

	    return true;
	}
	else {
	    out.printf("%s test print missing rabu.%n",i);
	    return false;
	}
    }
    protected boolean read(Instruction i, String arg){
	File file = new File(arg);
	if (file.isFile() && file.canRead()){

	    this.file = file;

	    rada = new RandomAccessData();

	    this.read = rada.read(file);

	    echo(i);

	    if (0 < this.read){

		return true;
	    }
	}
	return false;
    }
    protected boolean window(Instruction i, int x, int c){
	rada = new RandomAccessData(rada,new Window(x,c));

	return echo(i);
    }
    protected boolean write(Instruction i, String arg){
	File file = new File(arg);

	this.file = file;

	this.wrote = rada.write(file);

	echo(i);

	return true;
    }
    protected boolean seek(Instruction i, int ofs){

	rada.seek(ofs);

	echo(i);

	return true;
    }
    protected boolean reset(Instruction i){

	rada.reset();

	echo(i);

	return true;
    }
}
