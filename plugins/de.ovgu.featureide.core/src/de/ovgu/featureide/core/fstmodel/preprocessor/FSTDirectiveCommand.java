/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.core.fstmodel.preprocessor;

/**
 * Definition of directive kinds.
 * 
 * @author Jens Meinicke
 */
public enum FSTDirectiveCommand {
	IF (false), 
	IF_NOT (false), 
	IFDEF (false), 
	IFNDEF (false), 
	ELIF (true), 
	ELIFDEF (true), 
	ELIFNDEF (true), 
	ELSE (true), 
	ELSE_NOT (true), 
	CONDITION (false), //Check if one line statement 
	DEFINE (false),  //Check if one line statement
	UNDEFINE (false), //Check if one line statement
	CALL (false); //Check if one line statement
	
	private boolean oneLineStatement;
	
	private FSTDirectiveCommand(boolean isOneLineStatement) {
		this.oneLineStatement = isOneLineStatement;
	}
	
	public boolean isOneLineStatement() {
		return oneLineStatement;
	}
}
