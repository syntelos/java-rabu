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
import java.util.Arrays;

/**
 * Data tree branch.
 * 
 * A component may contain children as the result of parsing and
 * editing the data at its location.  In this case, the output of an
 * octet stream from the data tree has layers.  The parsing and
 * editing of the octet stream produces data buffers, parsers, and
 * editors to contribute to output.
 * 
 * A container implements the reading and writing of octet streams by
 * the use of components to perform parsing and editing.  The
 * overlapping, intersecting layers of buffers, parsers, and editors
 * that contribute to output are managed by a container to write a
 * valid octet stream.
 * 
 * @see Component
 */
public interface Container
    extends Component
{
}
