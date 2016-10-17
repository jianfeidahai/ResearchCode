package com.raffaeleconforti.wrapper.impl;

import au.edu.qut.structuring.StructuringService;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.wrapper.LogPreprocessing;
import com.raffaeleconforti.wrapper.MiningAlgorithm;
import com.raffaeleconforti.wrapper.PetrinetWithMarking;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.bpmnminer.prom.BPMNMinerPlugin;

/**
 * Created by conforti on 3/12/15.
 */
@Plugin(name = "BPMNMiner Wrapper", parameterLabels = {"Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class BPMNMinerAlgorithmWrapper implements MiningAlgorithm {

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@qut.edu.au",
            pack = "Noise Filtering")
    @PluginVariant(variantLabel = "BPMNMiner Wrapper", requiredParameterLabels = {0})
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log) {
        return minePetrinet(context, log, false);
    }

    @Override
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log, boolean structure) {
        LogPreprocessing logPreprocessing = new LogPreprocessing();
        log = logPreprocessing.preprocessLog(context, log);

        BPMNMinerPlugin bpmnMinerPlugin = new BPMNMinerPlugin();
        BPMNDiagram diagram = bpmnMinerPlugin.mineBPMNModel(context, log);

        if (structure) {
            try {
                StructuringService structuring = new StructuringService();
                diagram = structuring.structureDiagram(diagram);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BPMNToPetriNetConverter.convert(diagram);

        Object[] objects = BPMNToPetriNetConverter.convert(diagram);
        logPreprocessing.removedAddedElements((Petrinet) objects[0]);

        context.addConnection(new InitialMarkingConnection((Petrinet) objects[0], (Marking) objects[1]));
        context.addConnection(new FinalMarkingConnection((Petrinet) objects[0], (Marking) objects[2]));
        return new PetrinetWithMarking((Petrinet) objects[0], (Marking) objects[1], (Marking) objects[2]);
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "BPMNMiner";
    }
}
