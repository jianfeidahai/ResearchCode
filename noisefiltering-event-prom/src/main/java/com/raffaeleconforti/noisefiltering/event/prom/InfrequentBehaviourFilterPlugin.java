package com.raffaeleconforti.noisefiltering.event.prom;

import com.raffaeleconforti.automaton.Automaton;
import com.raffaeleconforti.automaton.AutomatonFactory;
import com.raffaeleconforti.log.util.LogModifier;
import com.raffaeleconforti.log.util.LogOptimizer;
import com.raffaeleconforti.memorylog.XFactoryMemoryImpl;
import com.raffaeleconforti.noisefiltering.event.InfrequentBehaviourFilter;
import com.raffaeleconforti.noisefiltering.event.selection.NoiseFilterResult;
import com.raffaeleconforti.noisefiltering.event.prom.ui.NoiseFilterUI;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import com.raffaeleconforti.memorylog.XFactoryMemoryImpl;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * Created by conforti on 7/02/15.
 */

@Plugin(name = "Infrequent Behaviour Filter", parameterLabels = {"Log"},//, "PetriNet", "Marking", "Log" },
        returnLabels = {"FilteredLog"},
        returnTypes = {XLog.class})
public class InfrequentBehaviourFilterPlugin {

    private final XEventClassifier xEventClassifier = new XEventAndClassifier(new XEventNameClassifier());
    private final AutomatonFactory automatonFactory = new AutomatonFactory(xEventClassifier);
    private final InfrequentBehaviourFilter infrequentBehaviourFilter = new InfrequentBehaviourFilter(xEventClassifier);

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@qut.edu.au",
            pack = "Infrequent Behaviour Filter (raffaele.conforti@qut.edu.au)")
    @PluginVariant(variantLabel = "Infrequent Behaviour Filter", requiredParameterLabels = {0})//, 1, 2, 3 })
    public XLog filterLog(final UIPluginContext context, XLog rawlog) {
        XLog log = rawlog;
        LogOptimizer logOptimizer = new LogOptimizer();
        log = logOptimizer.optimizeLog(log);

        XFactory memory = new XFactoryMemoryImpl();
        XFactoryRegistry.instance().setCurrentDefault(memory);
        LogModifier logModifier = new LogModifier(memory, XConceptExtension.instance(), XTimeExtension.instance(), logOptimizer);
        logModifier.insertArtificialStartAndEndEvent(log);

        Automaton<String> automatonOriginal = automatonFactory.generate(log);

        double[] arcs = infrequentBehaviourFilter.discoverArcs(automatonOriginal, 1.0);

        NoiseFilterUI noiseUI = new NoiseFilterUI();
        NoiseFilterResult result = noiseUI.showGUI(context, this, arcs, automatonOriginal.getNodes());

        return infrequentBehaviourFilter.filterLog(context, rawlog, result);
    }

    public XLog filterLog(XLog rawlog) {
        XLog log = rawlog;
        LogOptimizer logOptimizer = new LogOptimizer();
        log = logOptimizer.optimizeLog(log);

        LogModifier logModifier = new LogModifier(new XFactoryMemoryImpl(), XConceptExtension.instance(), XTimeExtension.instance(), logOptimizer);
        logModifier.insertArtificialStartAndEndEvent(log);

        return infrequentBehaviourFilter.filterLog(log);
    }

    public double discoverThreshold(double[] arcs, double initialPercentile) {
        return infrequentBehaviourFilter.discoverThreshold(arcs, initialPercentile);
    }

}
