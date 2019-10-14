/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.ui.editors.featuremodel.actions;

import org.prop4j.analyses.impl.general.evaluation.EvaluatedFeatureModelAnaysis;

import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;
import de.ovgu.featureide.fm.ui.FMUIPlugin;

/**
 * Evaluates the analysis runtime from the open feature model and writes the results into a log.
 *
 * @author Joshua Sprey
 */
public class EvaluationAction extends AFeatureModelAction {

	public static final String ID = "de.ovgu.featureide.evaluation";
	IFeatureModelManager featureModelManager;

	public EvaluationAction(IFeatureModelManager featureModelManager) {
		super("Evaluate this model", ID, featureModelManager);
		this.featureModelManager = featureModelManager;
		setImageDescriptor(FMUIPlugin.getDefault().getImageDescriptor("icons/wordassist_co.gif"));
	}

	@Override
	public void run() {
		final EvaluatedFeatureModelAnaysis analysis = new EvaluatedFeatureModelAnaysis(featureModelManager.getSnapshot());
	}

}