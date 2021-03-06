/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2002-2011 Ausenco Engineering Canada Inc.
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

import com.sandwell.JavaSimulation3D.InputAgent;

/**
 * Group class - for storing a list of objects
 *
 * For input of the form <object> <keyword> <value>:
 * If the group appears as the object in a line of input, then the keyword and value applies to each member of the group.
 * If the group appears as the value in a line of input, then the list of objects is used as the value.
 */
public class Group extends Entity {
	private static final ArrayList<Group> allInstances;

	private final BooleanInput reportable;

	private Class<?> type;
	private final ArrayList<StringVector> groupKeywordValues;

	protected Vector list; // list of objects in group

	static {
		allInstances = new ArrayList<Group>();
	}

	{
		addEditableKeyword( "List",       "",   "", false, "Key Inputs" );
		addEditableKeyword( "AppendList", "",   "", true,  "Optional Inputs" );

		reportable = new BooleanInput("Reportable", "Optional Inputs", true);
		this.addInput(reportable, true);

		addEditableKeyword( "GroupType",  "",   "", false, "Optional Inputs" );
	}

	public Group() {
		allInstances.add(this);
		list = new Vector( 1, 1 );
		type = null;
		groupKeywordValues = new ArrayList<StringVector>();
	}

	public static ArrayList<Group> getAll() {
		return allInstances;
	}

	public void kill() {
		super.kill();
		allInstances.remove(this);
	}

	// ******************************************************************************************
	// INPUT
	// ******************************************************************************************

	/**
	 * Processes the input data corresponding to the specified keyword. If syntaxOnly is true,
	 * checks input syntax only; otherwise, checks input syntax and process the input values.
	 */
	public void readData_ForKeyword(StringVector data, String keyword, boolean syntaxOnly, boolean isCfgInput)
	throws InputErrorException {


		try {
			if( "List".equalsIgnoreCase( keyword ) ) {
				ArrayList<Entity> temp = Input.parseEntityList(data, Entity.class, true);
				list.clear();
				list.addAll(temp);
				this.checkType();
				return;
			}
			if( "AppendList".equalsIgnoreCase( keyword ) ) {
				int originalListSize = list.size();
				ArrayList<Entity> temp = Input.parseEntityList(data, Entity.class, true);
				for (Entity each : temp) {
					if (!list.contains(each))
						list.add(each);
				}
				this.checkType();
				// set values of appended objects to the group values
				if ( type != null ) {
					for ( int i = originalListSize; i < list.size(); i ++ ) {

						Entity ent = (Entity)list.get( i );
						for ( int j = 0; j < this.getGroupKeywordValues().size(); j++  ) {
							String currentKeyword = this.getGroupKeywordValues().get(j).firstElement();
							StringVector currentData = new StringVector(this.getGroupKeywordValues().get(j));
							currentData.remove( 0 );

							ArrayList<StringVector> splitData = Util.splitStringVectorByBraces( currentData );
							for ( int k = 0; k < splitData.size(); k++ ) {
								ent.readInput(splitData.get(k), currentKeyword, syntaxOnly, isCfgInput);
								ent.updateKeywordValuesForEditBox( currentKeyword, splitData.get( k ) );
							}
						}

					}
				}

				return;
			}

			if( "GroupType".equalsIgnoreCase( keyword ) ) {
				Input.assertCount(data, 1);
				type = Input.parseEntityType(data.get(0));
				this.checkType();
				return;
			}

			// for all other keywords, keep track in keywordValues vector
			StringVector record = new StringVector( data );
			record.insertElementAt(keyword, 0);
			this.getGroupKeywordValues().add( record );

			// If there can never be elements in the group, throw a warning
			if( type == null && list.size() == 0 ) {
				InputAgent.logWarning("The group %s has no elements to apply keyword: %s", this, keyword);
			}

			// For all other keywords, apply the value to each member of the list
			for( int i = 0; i < list.size(); i++ ) {
				Entity ent = (Entity)list.get( i );
				ArrayList<StringVector> splitData = Util.splitStringVectorByBraces(data);
				if( splitData.size() == 0 )
					splitData.add(new StringVector());

				for ( int j = 0; j < splitData.size(); j++ ) {
					ent.readInput(splitData.get(j), keyword, syntaxOnly, isCfgInput);
				}
			}

		}
		catch( Exception e ) {
			InputAgent.logError("Entity: %s Keyword: %s - %s", this.getName(), keyword, e.getMessage());
		}
	}

	private void checkType() {
		if (type == null)
			return;

		for (int i = 0; i < this.getList().size(); i++) {
			if (!type.isInstance(this.getList().get(i)))
				throw new InputErrorException("The Entity: %s is not of Type: %s", this.getList().get(i), type.getSimpleName());
		}
	}

	// ******************************************************************************************
	// ACCESSING
	// ******************************************************************************************

	public Vector getList() {
		return list;
	}

	public boolean isReportable() {
		return reportable.getValue();
	}

	private ArrayList<StringVector> getGroupKeywordValues() {
		return groupKeywordValues;
	}
}
