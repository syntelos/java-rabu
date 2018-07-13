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
 * Data location description.
 */
public interface Location
    extends Comparable<Location>
{
    /**
     * Offset classes.
     * 
     * @see Component
     * @see Container
     */
    public enum Offset {
	/**
	 * Offset relative to file origin.
	 */
	FILE,
	/**
	 * Offset relative to file data structure.
	 */
	TAG,
	/**
	 * Offset relative to a point within the file data structure.
	 */
	ELEMENT;
    }

    /**
     * Data location origin.
     */
    public Offset location();
    /**
     * Data location position.
     */
    public long offset();
    /**
     * Data location extent.
     */
    public int length();

}
