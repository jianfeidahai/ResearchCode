/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.measurements.impl;

import au.edu.qut.bpmn.metrics.ComplexityCalculator;
import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.measurements.Measure;
import com.raffaeleconforti.measurements.MeasurementAlgorithm;
import com.raffaeleconforti.wrappers.MiningAlgorithm;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.processtree.ProcessTree;

/**
 * Created by Adriano on 19/10/2016.
 */
public class StructurednessComplexity implements MeasurementAlgorithm {

    @Override
    public boolean isMultimetrics() { return true; }

    @Override
    public Measure computeMeasurement(UIPluginContext pluginContext, XEventClassifier xEventClassifier, ProcessTree processTree, MiningAlgorithm miningAlgorithm, XLog log) {
        return null;
    }

    @Override
    public Measure computeMeasurement(UIPluginContext pluginContext, XEventClassifier xEventClassifier, PetrinetWithMarking petrinetWithMarking, MiningAlgorithm miningAlgorithm, XLog log) {
        Measure measure = new Measure();

        if(petrinetWithMarking == null) return measure;

        try {
            BPMNDiagram bpmn = PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), false);
            ComplexityCalculator cc = new ComplexityCalculator(bpmn);
            measure.addMeasure(getAcronym(), cc.computeStructuredness());
            return measure;
        } catch (Exception e) { return measure; }
    }

    @Override
    public String getMeasurementName() {
        return "Structuredness";
    }

    @Override
    public String getAcronym() {return "struct.";}
}
