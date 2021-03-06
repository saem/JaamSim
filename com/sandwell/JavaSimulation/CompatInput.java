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

public class CompatInput extends Input<String> {
	Entity target;

	public CompatInput(Entity target, String key, String cat, String units, String def) {
		super(key, cat, units, def);
		this.target = target;
	}

	public void parse(StringVector input)
	throws InputErrorException {
		target.readData_ForKeyword(input, this.getKeyword(), false, true);
	}
}
