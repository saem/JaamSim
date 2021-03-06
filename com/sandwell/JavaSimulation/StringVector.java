/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2005 Ausenco Engineering Canada Inc.
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
 */
package com.sandwell.JavaSimulation;

import java.util.Vector;

/**
 * This class stores string values in a Vector using the type-wrapper class
 * String.
 */
public class StringVector extends Vector<String>{
	/**
	 * Construct an empty vector, size 10, size increment 5.
	 */
	public StringVector() {
		this( 10, 5 );
	}

	/**
	 * Construct a copy of the given String Vector
	 */
	public StringVector( StringVector original ) {
		this( original.size(), 1 );

		for( int i = 0; i < original.size(); i++ ) {
			addElement( original.get( i ) );
		}
	}

	/**
	 * Construct a copy of the given Vector
	 */
	public StringVector(com.sandwell.JavaSimulation.Vector original) {
		this(original.size());

		for (int i = 0; i < original.size(); i++) {
			addElement((String)original.get(i));
		}
	}

	/**
	 * Construct an empty vector with the given initial capacity and with its
	 * capacity increment equal to one.
	 */
	public StringVector( int initialCapacity ) {
		this( initialCapacity, 1 );
	}

	/**
	 * Construct an empty vector with the given initial capacity and capacity
	 * increment.
	 */
	public StringVector( int initialCapacity, int capacityIncrement ) {
		super( initialCapacity, capacityIncrement );
	}

	/**
	 * Return a string containing the contents of the StringVector.
	 */
	public String toString() {
		StringBuilder out = new StringBuilder("{");

		for (int i = 0; i < size(); i++) {
			if (i == 0)
				out.append(" ");
			else
				out.append(", ");
			out.append(get(i));
		}
		out.append(" }");
		return out.toString();
	}

	/**
	 * Return a string containing the contents of the vector without a bracket.
	 * This method assumes that each element of the vector is an Entity.
	 */
	public String toStringWOBracket() {

		StringBuilder out = new StringBuilder("");

		for (int i = 0; i < size(); i++) {
			if (i == 0)
				out.append(" ");
			else
				out.append(", ");
			out.append(get(i));
		}

		return out.toString();
	}

	/**
	 * Searches the Vector for the given string ignoring case
	 */
	public boolean containsIgnoreCase( String testValue ) {
		for( int i = 0; i < size(); i++ ) {
			if( get( i ).equalsIgnoreCase( testValue ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add the given string to the end of the vector if it is not already present
	 */
	public void addLastIfNotPresent( String testValue ) {
		if( !this.contains( testValue ) ) {
			addElement( testValue );
		}
	}

	/**
	 * return the inclusive substring from to
	 * @param from
	 * @param to
	 * @return
	 */
	public StringVector subString( int from, int to ) {
		StringVector temp = new StringVector( to - from );
		for( int i = from; i <= to; i++ ) {
			temp.add( this.get(i) );
		}
		return temp;
	}
}
