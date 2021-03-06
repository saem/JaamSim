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

public class DoubleInput extends Input<Double> {
	private double minValue;
	private double maxValue;

	public DoubleInput(String key, String cat, Double def) {
		this(key, cat, def, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public DoubleInput(String key, String cat, Double def, double min, double max) {
		super(key, cat, def);
		minValue = min;
		maxValue = max;
	}

	public void parse(StringVector input)
	throws InputErrorException {
		Input.assertCountRange(input, 1, 2);

		// If there are two values, then assume the last one is a unit
		if( input.size() == 2 ) {

			// Determine the units
			Unit unit = Input.parseEntity( input.get( 1 ), Unit.class );

			// Determine the default units
			Unit defaultUnit = Input.tryParseEntity( unitString.replaceAll("[()]", "").trim(), Unit.class );
			if( defaultUnit == null ) {
				throw new InputErrorException( "Could not determine default units " + unitString );
			}

			// Determine the conversion factor from units to default units
			double conversionFactor = unit.getConversionFactorToUnit( defaultUnit );

			// Parse and convert the value
			value = Double.valueOf(Input.parseDouble(input.get(0), minValue, maxValue, conversionFactor));
		}
		else {
			// Parse the value
			value = Double.valueOf(Input.parseDouble(input.get(0), minValue, maxValue));
		}
	}

	public void setValidRange(double min, double max) {
		minValue = min;
		maxValue = max;
	}
}
