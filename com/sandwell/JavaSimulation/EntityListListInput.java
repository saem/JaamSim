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

public class EntityListListInput<T extends Entity> extends ListInput<ArrayList<ArrayList<T>>> {
	private Class<T> entClass;
	private boolean unique; // flag to determine if inner lists must be unique or not

	public EntityListListInput(Class<T> aClass, String key, String cat, ArrayList<ArrayList<T>> def) {
		super(key, cat, def);
		entClass = aClass;
		unique = true;
	}

	public void parse(StringVector input)
	throws InputErrorException {

		// Check if number of outer lists violate minCount or maxCount
		ArrayList<StringVector> splitData = Util.splitStringVectorByBraces(input);
		if (splitData.size() < minCount || splitData.size() > maxCount)
			throw new InputErrorException(INP_ERR_RANGECOUNT, minCount, maxCount, input.toString());

		value = Input.parseListOfEntityLists(input, entClass, unique);
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
}
