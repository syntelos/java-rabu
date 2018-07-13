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

import java.io.IOException;
import java.io.PrintStream;

/**
 * Data tree leaf.
 * 
 * A component of an octet stream has an offset and length which
 * define its location.  
 * 
 * A component may contain children as the result of parsing and
 * editing the data at its location.  In this case, the output of an
 * octet stream from the data tree has layers.  The parsing and
 * editing of the octet stream produces data buffers, parsers, and
 * editors to contribute to output.
 * 
 * @see Container
 */
public interface Component
    extends Location
{
    /**
     * 
     */
    public void read(LocationInputStream in)
	throws IOException;
    /**
     * 
     */
    public void write(LocationOutputStream out)
	throws IOException;
    /**
     * Unstructured line printer may call {@link Printer}.
     * 
     * @param out Output target (device)
     */
    public void println(PrintStream out);
    /**
     * Structured line printer with tree depth indent.
     * 
     * @param depth Tree depth from zero is an indent operator
     * @param out Output target (device)
     */
    public void println(int depth, PrintStream out);
}
