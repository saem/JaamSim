/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2011 Ausenco Engineering Canada Inc.
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class TwoKeyInput for storing objects of class V (e.g. Double or DoubleVector),
 * with two mandatory keys of class K1 and K2
 */
public class TwoKeyInput<K1 extends Entity, K2 extends Entity, V> extends Input<V> {

	protected double minValue = Double.NEGATIVE_INFINITY;
	protected double maxValue = Double.POSITIVE_INFINITY;
	private Class<K1> key1Class;
	private Class<K2> key2Class;
	private Class<V> valClass;
	private HashMap<K1,HashMap<K2,V>> hashMap;

	public TwoKeyInput(Class<K1> k1Class, Class<K2> k2Class, Class<V> vClass, String keyword, String cat, V def) {
		super(keyword, cat, def);
		key1Class = k1Class;
		key2Class = k2Class;
		valClass = vClass;
		hashMap = new HashMap<K1,HashMap<K2,V>>();
	}

	public void parse(StringVector input)
	throws InputErrorException {

		// If two entity keys are not provided, set the default value
		Entity ent1 = Input.tryParseEntity( input.get( 0 ), Entity.class );
		Entity ent2 = null;
		if( input.size() > 1 ) {
			ent2 = Input.tryParseEntity( input.get( 1 ), Entity.class );
		}
		if( ent1 == null || ent2 == null ) {
			V defValue = Input.parse( input.subString(0,input.size()-1), valClass, unitString, minValue, maxValue );
			this.setDefaultValue( defValue );
			return;
		}

		// The input is of the form: <Key1> <Key2> <Value>
		// Determine the key(s)
		ArrayList<K1> list = Input.parseEntityList(input.subString(0, 0), key1Class, true);
		ArrayList<K2> list2 = Input.parseEntityList(input.subString(1, 1), key2Class, true);

		// Determine the value
		V val = Input.parse( input.subString(2,input.size()-1), valClass, unitString, minValue, maxValue );

		// Set the value for the given keys
		for( int i = 0; i < list.size(); i++ ) {
			HashMap<K2,V> h1 = hashMap.get( list.get( i ) );
			if( h1 == null ) {
				h1 = new HashMap<K2,V>();
				hashMap.put( list.get( i ), h1 );
			}
			for( int j = 0; j < list2.size(); j++ ) {
				h1.put( list2.get(j), val );
			}
		}
	}

	public void setValidRange(double min, double max) {
		minValue = min;
		maxValue = max;
	}

	public int size() {
		return hashMap.size();
	}

	public V getValue() {
		return null;
	}

	public V getValueFor( K1 k1, K2 k2 ) {
		HashMap<K2,V> h1 = hashMap.get( k1 );
		if( h1 == null ) {
			return this.getDefaultValue();
		}
		else {
			V val = h1.get( k2 );
			if( val == null ) {
				return this.getDefaultValue();
			}
			else {
				return val;
			}
		}
	}

	public boolean containsKey( K1 k1, K2 k2 ) {
		HashMap<K2,V> h1 = hashMap.get( k1 );
		if( h1 == null ) {
			return false;
		}
		else {
			if( h1.get( k2 ) == null ) {
				return false;
			}
			else {
				return true;
			}
		}
	}
}
