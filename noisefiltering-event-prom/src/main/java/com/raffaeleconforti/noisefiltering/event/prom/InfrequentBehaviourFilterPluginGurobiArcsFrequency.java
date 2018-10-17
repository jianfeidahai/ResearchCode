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

package com.raffaeleconforti.noisefiltering.event.prom;

import com.raffaeleconforti.automaton.Automaton;
import com.raffaeleconforti.automaton.AutomatonFactory;
import com.raffaeleconforti.log.util.LogModifier;
import com.raffaeleconforti.log.util.LogOptimizer;
import com.raffaeleconforti.noisefiltering.event.InfrequentBehaviourFilter;
import com.raffaeleconforti.noisefiltering.event.prom.ui.NoiseFilterUI;
import com.raffaeleconforti.noisefiltering.event.selection.NoiseFilterResult;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * Created by conforti on 7/02/15.
 */

@Plugin(name = "Infrequent Behaviour Filter Gurobi", parameterLabels = {"Log"},//, "PetriNet", "Marking", "Log" },
        returnLabels = {"FilteredLog"},
        returnTypes = {XLog.class})
public class InfrequentBehaviourFilterPluginGurobiArcsFrequency extends InfrequentBehaviourFilterPlugin {

    private final XEventClassifier xEventClassifier = new XEventAndClassifier(new XEventNameClassifier());
    private final AutomatonFactory automatonFactory = new AutomatonFactory(xEventClassifier);
    private InfrequentBehaviourFilter infrequentBehaviourFilter = new InfrequentBehaviourFilter(xEventClassifier);
    private final boolean debug_mode = false;

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@unimelb.edu.au",
            pack = "Infrequent Behaviour Filter Gurobi (raffaele.conforti@unimelb.edu.au)")
//    @PluginVariant(variantLabel = "Infrequent Behaviour Filter LPSolver", requiredParameterLabels = {0})//, 1, 2, 3 })
//    public XLog filterLogLPSolver(final UIPluginContext context, XLog rawlog) {
//        XLog log = rawlog;
//        XFactory factory = new XFactoryNaiveImpl();
//        LogOptimizer logOptimizer = new LogOptimizer(factory);
//        log = logOptimizer.optimizeLog(log);
//
//        XFactoryRegistry.instance().setCurrentDefault(factory);
//        LogModifier logModifier = new LogModifier(factory, XConceptExtension.instance(), XTimeExtension.instance(), logOptimizer);
//        logModifier.insertArtificialStartAndEndEvent(log);
//
//        Automaton<String> automatonOriginal = automatonFactory.generate(log);
//
//        InfrequentBehaviourFilter infrequentBehaviourFilter = new InfrequentBehaviourFilter(xEventClassifier);
//        double[] arcs = infrequentBehaviourFilter.discoverArcs(automatonOriginal, 1.0);
//
//        NoiseFilterUI noiseUI = new NoiseFilterUI();
//        NoiseFilterResult result = noiseUI.showGUI(context, this, arcs, automatonOriginal.getNodes());
//
//        return infrequentBehaviourFilter.filterLog(context, rawlog, result);
//    }

    @PluginVariant(variantLabel = "Infrequent Behaviour Filter Gurobi using Arcs Frequency", requiredParameterLabels = {0})//, 1, 2, 3 })
    public XLog filterLogGurobi(final UIPluginContext context, XLog rawlog) {
        XLog log = rawlog;
        XFactory factory = new XFactoryNaiveImpl();
        LogOptimizer logOptimizer = new LogOptimizer(factory);
        log = logOptimizer.optimizeLog(log);

        XFactoryRegistry.instance().setCurrentDefault(factory);
        LogModifier logModifier = new LogModifier(factory, XConceptExtension.instance(), XTimeExtension.instance(), logOptimizer);
        logModifier.insertArtificialStartAndEndEvent(log);

        Automaton<String> automatonOriginal = automatonFactory.generate(log);


        InfrequentBehaviourFilter infrequentBehaviourFilter = new InfrequentBehaviourFilter(xEventClassifier, true, true, debug_mode, true, false, 0.125, 0.5, false, -1);
        double[] arcs = infrequentBehaviourFilter.discoverArcs(automatonOriginal, 1.0);

        NoiseFilterUI noiseUI = new NoiseFilterUI();
        NoiseFilterResult result = noiseUI.showGUI(context, this, arcs, automatonOriginal.getNodes());

        if (!result.isRemoveTraces() || result.isRemoveNodes()) {
            infrequentBehaviourFilter = new InfrequentBehaviourFilter(xEventClassifier, true, true, debug_mode, result.isRemoveTraces(), result.isRemoveNodes(), 0.125, 0.5, false, -1);
        }

        return infrequentBehaviourFilter.filterLog(context, rawlog, result);
    }

    public XLog filterLog(XLog rawlog) {
        XLog log = rawlog;
        XFactory factory = new XFactoryNaiveImpl();
        LogOptimizer logOptimizer = new LogOptimizer(factory);
        log = logOptimizer.optimizeLog(log);

        LogModifier logModifier = new LogModifier(factory, XConceptExtension.instance(), XTimeExtension.instance(), logOptimizer);
        logModifier.insertArtificialStartAndEndEvent(log);

        return infrequentBehaviourFilter.filterLog(log);
    }

    public double discoverThreshold(double[] arcs, double initialPercentile) {
        return infrequentBehaviourFilter.discoverThreshold(arcs, initialPercentile);
    }

}
