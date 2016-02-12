/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.ui.editors.elements;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.ui.editors.IGraphicalConstraint;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeatureModel;

/**
 * Graphical representation of an {@link IConstraint} instance.
 * 
 * @author Sebastian Krieter
 * @author Marcus Pinnecke
 * 
 */
public class GraphicalConstraint implements IGraphicalConstraint {

	protected final IConstraint correspondingConstraint;
	protected final IGraphicalFeatureModel graphicalFeatureModel;

	protected Point location = new Point(0, 0);
	protected Dimension dimension = new Dimension(10, 10);

	public GraphicalConstraint(GraphicalConstraint constraint) {
		correspondingConstraint = constraint.correspondingConstraint;
		graphicalFeatureModel = constraint.graphicalFeatureModel;
		location = constraint.location;
		dimension = constraint.dimension;
	}
	
	public GraphicalConstraint(IConstraint correspondingConstraint, IGraphicalFeatureModel graphicalFeatureModel) {
		this.correspondingConstraint = correspondingConstraint;
		this.graphicalFeatureModel = graphicalFeatureModel;
	}

	@Override
	public IConstraint getObject() {
		return correspondingConstraint;
	}

	@Override
	public GraphicItem getItemType() {
		return GraphicItem.Constraint;
	}

	@Override
	public Point getLocation() {
		return location;
	}

	@Override
	public boolean isFeatureSelected() {
		return correspondingConstraint.isFeatureSelected();
	}

	@Override
	public void setFeatureSelected(boolean selected) {
		correspondingConstraint.setFeatureSelected(selected);
	}

	@Override
	public void setLocation(Point newLocation) {
		location = newLocation;
	}

	@Override
	public Dimension getSize() {
		return dimension;
	}

	@Override
	public void setSize(Dimension size) {
		this.dimension = size;
	}

	@Override
	public IGraphicalFeatureModel getGraphicalModel() {
		return graphicalFeatureModel;
	}

	@Override
	public String getGraphicType() {
		return null;
	}

	@Override
	public GraphicalConstraint clone() {
		return new GraphicalConstraint(this);
	}
}