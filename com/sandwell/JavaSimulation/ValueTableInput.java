/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2010-2011 Ausenco Engineering Canada Inc.
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
import com.sandwell.JavaSimulation.ValueTable;

public class ValueTableInput<T extends Entity> extends Input<ValueTable<T>> {

	private double minValue = Double.NEGATIVE_INFINITY;
	private double maxValue = Double.POSITIVE_INFINITY;
	protected int[] validCounts;
	private Class<T> entClass;

	public ValueTableInput(Class<T> aClass, String key, String cat, ValueTable<T> def) {
		super(key, cat, def);
		entClass = aClass;
		validCounts = new int[] { 1, 2, 3, 12, 13, 14 };

		// initialize value to hold the default
		value = new ValueTable<T>(def.getDefault().getCurrentValue());
	}

	public void parse(StringVector input)
	throws InputErrorException {
		Input.assertCount(input, validCounts);

		// Default for all objects that are not explicitly stated
		Entity ent = Input.tryParseEntity( input.get( 0 ), Entity.class );
		if( ent == null || ent instanceof ProbabilityDistribution) {
			TimeValue defTime;

			// If there are 2 or 13 entries, assume the last entry is a unit
			if( input.size() == 2 || input.size() == 13 ) {

				// Determine the units
				Unit unit = Input.parseEntity( input.get( input.size()- 1), Unit.class );

				// Determine the default units
				Unit defaultUnit = Input.tryParseEntity( unitString.replaceAll("[()]", "").trim(), Unit.class );
				if( defaultUnit == null ) {
					throw new InputErrorException( "Could not determine default units " + unitString );
				}

				// Determine the conversion factor to the default units
				double conversionFactor = unit.getConversionFactorToUnit( defaultUnit );

				// Parse and convert the values
				StringVector temp = input.subString(0,input.size()-2);
				defTime = Input.parseTimeValue(temp, minValue, maxValue, conversionFactor);
			}
			else {
				// Parse the values
				defTime = Input.parseTimeValue(input, minValue, maxValue);
			}
			value.setDefault( defTime );
			return;
		}

		// Object/Value pair
		TimeValue val;
		ArrayList<T> list = Input.parseEntityList(input.subString(0, 0), entClass, true);

		// If there are 3 or 14 entries, assume the last entry is a unit
		if( input.size() == 3 || input.size() == 14 ) {

			// Determine the units
			Unit unit = Input.parseEntity( input.get( input.size()- 1), Unit.class );

			// Determine the default units
			Unit defaultUnit = Input.tryParseEntity( unitString.replaceAll("[()]", "").trim(), Unit.class );
			if( defaultUnit == null ) {
				throw new InputErrorException( "Could not determine default units " + unitString );
			}

			// Determine the conversion factor to the default units
			double conversionFactor = unit.getConversionFactorToUnit( defaultUnit );

			// Parse and convert the values
			StringVector temp = input.subString(1,input.size()-2);
			val = Input.parseTimeValue(temp, minValue, maxValue, conversionFactor);
		}
		else {
			// Parse the values
			StringVector temp = input.subString(1,input.size()-1);
			val = Input.parseTimeValue(temp, minValue, maxValue);
		}

		for( T each : list ) {
			value.put( each, val );
		}
	}

	public void setValidRange(double min, double max) {
		minValue = min;
		maxValue = max;
	}

	public void setValidCounts(int... list) {
		validCounts = list;
	}
}
