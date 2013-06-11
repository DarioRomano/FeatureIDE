/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2013  FeatureIDE team, University of Magdeburg, Germany
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
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.core.mpl.signature;

import java.util.LinkedList;

import de.ovgu.featureide.core.mpl.signature.abstr.AbstractRole;
import de.ovgu.featureide.fm.core.Feature;

/**
 * Contains the {@link JavaRoleSignature}s for a feature.
 * 
 * @author Reimar Schroeter
 * @author Sebastian Krieter
 */
public class FeatureRoles extends LinkedList<AbstractRole> {
	private static final long serialVersionUID = 1L;
	private final Feature feature;
	
	public FeatureRoles(Feature feature) {
		this.feature = feature;
	}
	
	public AbstractRole getByName(String className) {
		for (AbstractRole curRole : this) {
			if (curRole.getSignature().getName().equals(className)) {
				return curRole;
			}
		}
		return null;
	}

	public Feature getFeature() {
		return feature;
	}
}
