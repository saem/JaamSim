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

import com.sandwell.JavaSimulation3D.Clock;

public class TimeValue {
	private final double dVal;
	private final DoubleVector monthVal;
	private final ProbabilityDistribution probVal;
	private final ArrayList<ProbabilityDistribution> monthProbVal;
	private double currentValue;

	public TimeValue(double val) {
		dVal = val;
		currentValue = dVal;
		monthVal = null;
		probVal = null;
		monthProbVal = null;
	}

	public TimeValue(DoubleVector month) {
		dVal = Double.NaN;
		monthVal = month;
		currentValue = monthVal.get(0);
		probVal = null;
		monthProbVal = null;
	}

	public TimeValue(ProbabilityDistribution prob) {
		dVal = Double.NaN;
		monthVal = null;
		probVal = prob;
		currentValue = 0.0;
		monthProbVal = null;
	}

	public TimeValue(ArrayList<ProbabilityDistribution> probList) {
		dVal = Double.NaN;
		monthVal = null;
		probVal = null;
		monthProbVal = probList;
	}

	public double getNextValueForTime(double time) {

		if (!Double.isNaN(dVal))
			currentValue = dVal;

		if (monthVal != null)
			currentValue = monthVal.get(Clock.getMonthIndex(time));

		if (probVal != null)
			currentValue = probVal.nextValue();

		if(monthProbVal != null)
			currentValue = monthProbVal.get(Clock.getMonthIndex(time)).nextValue();

		return currentValue;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public double getExpectedValueForTime(double time) {
		if (!Double.isNaN(dVal))
			return currentValue;

		if (monthVal != null)
			return currentValue;

		if(probVal != null)
			return probVal.getExpectedValue();
		return monthProbVal.get(Clock.getMonthIndex(time)).getExpectedValue();
	}

	public void initialize() {

		if( probVal != null ) {
			probVal.initialize();
		}
		if( monthProbVal != null) {
			for(ProbabilityDistribution each: monthProbVal) {
				each.initialize();
			}
		}

		// Initialize the current value
		if (!Double.isNaN(dVal))
			currentValue = dVal;

		if (monthVal != null)
			currentValue = monthVal.get(Clock.getMonthIndex(0));

		if (probVal != null)
			currentValue = probVal.getExpectedValue();
	}

	public String toString() {
		if (!Double.isNaN(dVal))
			return Double.toString(dVal);

		if (monthVal != null)
			return monthVal.toString();

		if(probVal != null)
			return probVal.toString();
		return monthProbVal.toString();
	}
}