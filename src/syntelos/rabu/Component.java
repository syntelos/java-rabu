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
 * Data structure inspection operator may call {@link BufferPrinter}.
 */
public interface Component {
    /**
     * Unstructured line printer 
     * 
     * @param out Output target (device)
     */
    public void println(PrintStream out);
    /**
     * Structured line printer with tree depth indent
     * 
     * @param depth Tree depth from zero is an indent operator
     * @param out Output target (device)
     */
    public void println(int depth, PrintStream out);
}
