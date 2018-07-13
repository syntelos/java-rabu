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

import java.io.Closeable;
import java.io.IOException;

/**
 * 
 * @see Component
 * @see Container
 */
public interface LocationInputStream
    extends Closeable
{

    public long offset();

    public void unread(byte[] buf);

    public int read() throws IOException;

    public int read(byte b[], int o, int l) throws IOException ;

    public long skip(long n) throws IOException ;

    public int available() throws IOException ;

    public void close() throws IOException ;

    public void mark(int r);

    public void reset();

    public boolean markSupported();

}
