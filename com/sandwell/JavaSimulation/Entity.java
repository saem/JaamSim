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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.sandwell.JavaSimulation.InputErrorException;
import com.sandwell.JavaSimulation3D.InputAgent;
import com.sandwell.JavaSimulation3D.Region;

/**
 * Abstract class that encapsulates the methods and data needed to create a
 * simulation object. Encapsulates the basic system objects to achieve discrete
 * event execution.
 */
public class Entity {
	private static long entityCount = 0;
	private static final ArrayList<Entity> allInstances;
	private static final HashMap<String, Entity> namedEntities;

	static Simulation simulation;
	EventManager eventManager;

	private String entityName;
	private String entityInputName; // Name input by user
	private final long entityNumber;

	//public static final int FLAG_TRACE = 0x01; // reserved in case we want to treat tracing like the other flags
	public static final int FLAG_TRACEREQUIRED = 0x02;
	public static final int FLAG_TRACESTATE = 0x04;
	public static final int FLAG_LOCKED = 0x08;
	public static final int FLAG_TRACKEVENTS = 0x10;
	public static final int FLAG_ADDED = 0x20;
	public static final int FLAG_EDITED = 0x40;
	public static final int FLAG_GENERATED = 0x80;
	private int flags;
	protected boolean traceFlag = false;

	/** Current region this entity is in. **/
	protected Region currentRegion;

	private final ArrayList<Input<?>> editableInputs;
	private final HashMap<String, Input<?>> inputMap;

	static {
		allInstances = new ArrayList<Entity>(100);
		namedEntities = new HashMap<String, Entity>(100);
	}

	/**
	 * Constructor for entity initializing members.
	 */
	public Entity() {
		entityNumber = ++entityCount;
		allInstances.add(this);

		flags = 0;

		// Ouch, simulation as Entity hurts here
		if (simulation != null) {
			currentRegion = simulation.getDefaultRegion();
		}

		editableInputs = new ArrayList<Input<?>>();
		inputMap = new HashMap<String, Input<?>>();
	}

	public static ArrayList<? extends Entity> getAll() {
		return allInstances;
	}

	public void earlyInit() {};

	public void startUp() {};

	public void doEnd() {};

	public void kill() {
		allInstances.remove(this);
		if (namedEntities.get(this.getInputName()) == this)
			namedEntities.remove(this.getInputName());
	}

	public static Entity getNamedEntity(String name) {
		return namedEntities.get(name);
	}

	public static long getEntitySequence() {
		long seq = (long)Entity.getAll().size() << 32;
		seq += entityCount;
		return seq;
	}

	static void setSimulation(Simulation sim) {
		simulation = sim;
	}

	public final double getCurrentTime() {
		long internalTime = 0;
		try {
			internalTime = Process.currentTime();
		}
		catch (ErrorException e) {
			internalTime = simulation.eventManager.currentTime();
		}
		return internalTime / Simulation.getSimTimeFactor();
	}

	protected final void addInput(Input<?> in, boolean editable, String... synonyms) {
		if (inputMap.put(in.getKeyword().toUpperCase().intern(), in) != null) {
			System.out.format("WARN: keyword handled twice, %s:%s\n", this.getClass().getName(), in.getKeyword());
		}
		for (String each : synonyms) {
			if (inputMap.put(each.toUpperCase().intern(), in) != null) {
				System.out.format("WARN: keyword handled twice, %s:%s\n", this.getClass().getName(), each);
			}
		}

		// Editable inputs are sorted by category
		if (editable) {

			int index = editableInputs.size();
			for( int i = editableInputs.size() - 1; i >= 0; i-- ) {
				Input<?> ei = editableInputs.get( i );
				if( ei.getCategory().equals( in.getCategory() ) ) {
					index = i+1;
					break;
				}
			}

			editableInputs.add(index, in);
		}
	}

	public Input<?> getInput(String key) {
		return inputMap.get(key.toUpperCase());
	}

	public void resetInputs() {
        Iterator<Entry<String,Input<?>>> iterator = inputMap.entrySet().iterator();
        while(iterator. hasNext()){
            iterator.next().getValue().reset();
        }
	}

	public void setFlag(int flag) {
		flags |= flag;
	}

	public void clearFlag(int flag) {
		flags &= ~flag;
	}

	public boolean testFlag(int flag) {
		return (flags & flag) != 0;
	}

	public void setTraceFlag() {
		traceFlag = true;
	}

	public void clearTraceFlag() {
		traceFlag = false;
	}

	// This is defined for handlers only
	public void validate() throws InputErrorException {}

	/**
	 * Static method to get the eventManager for all entities.
	 */
	public EventManager getEventManager() {
		if (eventManager != null)
			return eventManager;

		return simulation.eventManager;
	}

	/**
	 * Method to return the name of the entity.
	 * Note that the name of the entity may not be the unique identifier used in the namedEntityHashMap; see Entity.toString()
	 */
	public String getName() {
		if (entityName == null)
			return "Entity-" + entityNumber;
		else
			return entityName;
	}

	/**
	 * Method to set the name of the entity.
	 */
	public void setName(String newName) {
		entityName = newName;
	}

	/**
	 * Method to set the name of the entity to prefix+entityNumber.
	 */
	public void setNamePrefix(String prefix) {
		entityName = prefix + entityNumber;
	}

	/**
	 * Method to return the unique identifier of the entity. Used when building Edit tree labels
	 * @return entityName
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Method to set the input name of the entity.
	 */
	public void setInputName(String newName) {
		entityInputName = newName;
		namedEntities.put(newName, this);
	}

	/**
	 * Method to set an alias of the entity.
	 */
	public void setAlias(String newName) {
		namedEntities.put(newName, this);
	}

	/**
	 * Method to remove an alias of the entity.
	 */
	public void removeAlias(String newName) {
		namedEntities.remove(newName);
	}

	/**
	 * Method to get the input name of the entity.
	 */
	public String getInputName() {
		if (entityInputName == null) {
			return this.getName();
		}
		else {
			return entityInputName;
		}
	}

	public final void readInput(StringVector data, String keyword, boolean syntaxOnly, boolean isCfgInput)
	throws InputErrorException {
		Input<?> in = this.getInput(keyword);
		if (in != null) {
//			System.out.format("Parsing using input object:%s\n", in.getKeyword());
			in.parse(data);
		} else {
			this.readData_ForKeyword(data, keyword, syntaxOnly, isCfgInput);
		}
	}

	/**
	 * Interpret the input data in the given buffer of strings corresponding to the given keyword.
	 * Reads keyword from a configuration file:
	 *   TRACE		 - trace flag (0 = off, 1 = on)
	 *  @param data - Vector of Strings containing data to be parsed
	 *  @param keyword - the keyword to determine what the data represents
	 */
	public void readData_ForKeyword(StringVector data, String keyword, boolean syntaxOnly, boolean isCfgInput)
	throws InputErrorException {
		if ("EVENTMANAGER".equalsIgnoreCase(keyword)) {
			EventManager evt = simulation.getDefinedManager(data.get(0));

			if (evt == null) {
				throw new InputErrorException("EventManager %s not defined", data.get(0));
			}
			eventManager = evt;
			return;
		}

		if( "TRACE".equalsIgnoreCase( keyword ) ) {
			Input.assertCount(data, 1);
			boolean trace = Input.parseBoolean(data.get(0));
			if (trace)
				this.setFlag(FLAG_TRACEREQUIRED);
			else
				this.clearFlag(FLAG_TRACEREQUIRED);
			return;
		}
		if( "TRACESTATE".equalsIgnoreCase( keyword ) ) {
			Input.assertCount(data, 1);
			boolean value = Input.parseBoolean(data.get(0));
			if (value)
				this.setFlag(FLAG_TRACESTATE);
			else
				this.clearFlag(FLAG_TRACESTATE);
			return;
		}

		// --------------- LOCK ---------------
		if( "LOCK".equalsIgnoreCase( keyword ) ) {
			Input.assertCount(data, 1);
			boolean value = Input.parseBoolean(data.get(0));

			if (!value)
				throw new InputErrorException("Object cannot be unlocked");

			if (value)
				this.setFlag(FLAG_LOCKED);
			else
				this.clearFlag(FLAG_LOCKED);
			return;
		}

		// --------------- LOCKKEYWORDS ---------------
		if( "LOCKKEYWORDS".equalsIgnoreCase( keyword ) ) {

			// Check that the individual keywords for locking are valid
			for( int i = 0; i < data.size(); i++ ) {
				if( this.getInput( data.get(i) ) == null ) {
					throw new InputErrorException( "LockKeyword " + data.get( i ) + " is not a valid keyword" );
				}
			}
			// Lock the individual keywords for this entity
			for( int i = 0; i < data.size(); i++ ) {
				Input<?> input = this.getInput( data.get(i) );
				input.setLocked( true );
			}
			return;
		}

		throw new InputErrorException( "Invalid keyword " + keyword );
	}


	/**
	 * Informs the editBox of the specified keyword with the specified data.  Is called during readData_ForKeyword cmd.  This does not
	 * update the data in the simulation.
	 * data -> editBox
	 *
	 * @param keyword
	 * @param data
	 */
	public void updateKeywordValuesForEditBox( String keyword, StringVector data ) {
		// Create a list of entities to update in the edit table
		Vector entityList = new Vector( 1, 1 );
		if( this instanceof Group ) {

			// Is the keyword a Group keyword?
			if( this.getInput( keyword ) != null ) {
				entityList.addElement( this );
			}
			else {
				entityList = ((Group)this).getList();
			}
		}
		else {
			entityList.addElement( this );
		}

		// Store the keyword data for use in the edit table
		for( int i = 0; i < entityList.size(); i++ ) {
			Entity ent = (Entity)entityList.get( i );
			Input<?> in = ent.getInput(keyword);

			if (in != null) {
				String str = data.toString();

				// reformat input string to be added to keyword
				// strip out "{}" from data to find value
				if( data.size() > 0 ) {
					if (!(data.get(0).equals("{"))) {
						str = str.replaceAll("[{}]", "");
					} else {
						int strLength = str.length();
						str = "{"+str.substring(3,strLength-3) + "}";
					}
					str = str.replaceAll( "[,]", " " );
					str = str.trim();
				}

				if (( !in.isAppendable() ) || ( data.get(0).equals("{"))) {
					// ent.getKeywordValuesList.setElementAt( str, keywords.indexOfString( keyword ) );
					in.setValueString( str );
					in.setEditedValueString(str);
				}
				else {
					// Takes care of old format, displaying as new format -- appending onto end of record.
					in.setValueString( in.getValueString() + "{ " + str + " } " );
				}
			}
			// The keyword is not on the editable keyword list
			else {
				InputAgent.logWarning("Keyword %s is obsolete. Please replace the Keyword. Refer to the manual for more detail.", keyword);
			}
		}
	}

	public Region getCurrentRegion() {
		return currentRegion;
	}

	public void setRegion(Region newRegion) {
		currentRegion = newRegion;
	}

	/**
	 * Accessor to return information about the entity.
	 */
	public Vector getInfo() {
		Vector info = new Vector( 1, 1 );
		info.addElement( "Name" + "\t" + getName() );
		String[] temp = getClass().getName().split( "[.]" );
		info.addElement( "Type" + "\t" + temp[temp.length - 1] );
		return info;
	}

	private long calculateDelayLength(double waitLength) {
		return Math.round(waitLength * Simulation.getSimTimeFactor());
	}

	public double calculateEventTime(double waitLength) {
		long eventTime = Process.currentTime() + this.calculateDelayLength(waitLength);
		return eventTime / Simulation.getSimTimeFactor();
	}

	public final void startProcess(String methodName, Object... args) {
		Process.start(this, methodName, args);
	}

	public final void startExternalProcess(String methodName, Object... args) {
		getEventManager().startExternalProcess(this, methodName, args);
	}

	public final void scheduleSingleProcess(String methodName, Object... args) {
		getEventManager().scheduleSingleProcess(0, EventManager.PRIO_LASTFIFO, this, methodName, args);
	}

	/**
	 * Wrapper of eventManager.scheduleWait(). Used as a syntax nicity for
	 * calling the wait method.
	 *
	 * @param duration The duration to wait
	 */
	public final void scheduleWait(double duration) {
		long waitLength = calculateDelayLength(duration);
		getEventManager().scheduleWait(waitLength, EventManager.PRIO_DEFAULT, this);
	}

	/**
	 * Wrapper of eventManager.scheduleWait(). Used as a syntax nicity for
	 * calling the wait method.
	 *
	 * @param duration The duration to wait
	 * @param priority The relative priority of the event scheduled
	 */
	public final void scheduleWait( double duration, int priority ) {
		long waitLength = calculateDelayLength(duration);
		getEventManager().scheduleWait(waitLength, priority, this);
	}

	/**
	 * Schedules an event to happen as the last event at the current time.
	 * Additional calls to scheduleLast will place a new event as the last event.
	 */
	public final void scheduleLastFIFO() {
		getEventManager().scheduleLastFIFO( this );
	}

	/**
	 * Schedules an event to happen as the last event at the current time.
	 * Additional calls to scheduleLast will place a new event as the last event.
	 */
	public final void scheduleLastLIFO() {
		getEventManager().scheduleLastLIFO( this );
	}

	public final void waitUntil() {
		getEventManager().waitUntil(this);
	}

	public final void waitUntilEnded() {
		getEventManager().waitUntilEnded(this);
	}

	/**
	 * Increment clock by the minimum time step
	 */
	public final void scheduleWaitOneTick() {
		getEventManager().scheduleWait(1, EventManager.PRIO_DEFAULT, this);
	}

	public final void interruptThread(Process thread) {
		Process.currentProcess().getEventManager().interrupt(thread);
	}

	public final void terminateThread(Process thread) {
		Process.currentProcess().getEventManager().terminateThread(thread);
	}

	// ******************************************************************************************************
	// EDIT TABLE METHODS
	// ******************************************************************************************************

	public ArrayList<Input<?>> getEditableInputs() {
		return editableInputs;
	}

	/**
	 * Make the given keyword editable from the edit box with Synonyms
	 */
	public void addEditableKeyword( String keyword, String unit, String defaultValue, boolean append, String category, String... synonymsArray ) {

		if( this.getInput( keyword ) == null ) {
			// Create a new input object
			CompatInput in = new CompatInput(this, keyword, category, unit, defaultValue);
			in.setAppendable( append );
			this.addInput(in, true, synonymsArray);
		} else {
			System.out.format("Edited keyword added twice %s:%s%n", this.getClass().getName(), keyword);
		}
	}

	// ******************************************************************************************************
	// TRACING METHODS
	// ******************************************************************************************************

	/**
	 * Track the given subroutine.
	 */
	public void trace(String meth) {
		if (traceFlag) {
			simulation.trace(0, this, meth);
		}
	}

	/**
	 * Track the given subroutine.
	 */
	public void trace(int level, String meth) {
		if (traceFlag) {
			simulation.trace(level, this, meth);
		}
	}

	/**
	 * Track the given subroutine (one line of text).
	 */
	public void trace(String meth, String text1) {
		if (traceFlag) {
			simulation.trace(0, this, meth, text1);
		}
	}

	/**
	 * Track the given subroutine (two lines of text).
	 */
	public void trace(String meth, String text1, String text2) {
		if (traceFlag) {
			simulation.trace(0, this, meth, text1, text2);
		}
	}

	/**
	 * Print an addition line of tracing.
	 */
	public void traceLine(String text) {
		this.trace( 1, text );
	}

	/**
	 * Print an error message.
	 */
	public void error( String meth, String text1, String text2 ) {
		InputAgent.logError("Time:%.5f Entity:%s%n%s%n%s%n%s%n",
							getCurrentTime(), getName(),
							meth, text1, text2);

		// We don't want the model to keep executing, throw an exception and let
		// the higher layers figure out if we should terminate the run or not.
		throw new ErrorException("ERROR: %s", getName());
	}

	/**
	 * Print a warning message.
	 */
	public void warning( String meth, String text1, String text2 ) {
		InputAgent.logWarning("Time:%.5f Entity:%s%n%s%n%s%n%s%n",
				getCurrentTime(), getName(),
				meth, text1, text2);
	}

	public void defineNewEntity(  ) {
		// things that happen when any entity is defined go here
		// overloaded for classes which need this


	}
}
