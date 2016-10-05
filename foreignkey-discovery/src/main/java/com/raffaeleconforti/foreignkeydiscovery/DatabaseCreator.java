package com.raffaeleconforti.foreignkeydiscovery;import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;import com.raffaeleconforti.foreignkeydiscovery.databasestructure.*;import com.raffaeleconforti.foreignkeydiscovery.util.EntityNameExtractor;import com.raffaeleconforti.log.util.LogImporter;import com.raffaeleconforti.log.util.LogOptimizer;import org.eclipse.collections.impl.map.mutable.UnifiedMap;import org.deckfour.xes.extension.std.XConceptExtension;import com.raffaeleconforti.memorylog.XFactoryMemoryImpl;import org.deckfour.xes.model.*;import java.util.*;/** * Created by Raffaele Conforti on 20/10/14. */public class DatabaseCreator {    private Set<Table> setOfTables = new TreeSet<Table>();    private Set<Column> setOfColumns = new TreeSet<Column>();    private XConceptExtension xce = XConceptExtension.instance();    private Map<Object, Object> map = null;    private String conceptName = null;    private String timeStamp = null;    private String resource = null;    private String transition = null;    private LogOptimizer logOptimizer = null;    public static void main(String[] args) throws Exception {        XLog log = LogImporter.importFromFile(new XFactoryMemoryImpl(), "/Volumes/Data/IdeaProjects/ForeignKeyDiscoverer/qut-ut-research.bpmnminer/trunk/BPMNMinerStandaloneProject/OrderToCashLog.xes");        LogOptimizer lo = new LogOptimizer();        lo.optimizeLog(log);        DatabaseCreator dc = new DatabaseCreator(lo);//        dc.generateSetOfTables(log);    }    public DatabaseCreator(LogOptimizer lo) {        map = lo.getReductionMap();        conceptName = (String) map.get("concept:name");        timeStamp = (String) map.get("time:timestamp");        resource = (String) map.get("org:resource");        transition = (String) map.get("lifecycle:transition");    }    /*    This method assumes that the log has been optimized using LogOptimizer     */    public void generateSetOfTables(XLog log, Set<PrimaryKey> primaryKeys) {        UnifiedMap<PrimaryKey, UnifiedMap<String, ArrayList<String>>> traceEventAttributes = new UnifiedMap<PrimaryKey, UnifiedMap<String, ArrayList<String>>>();        for(XTrace trace : log) {            for(PrimaryKey primaryKey : primaryKeys) {                UnifiedMap<String, ArrayList<String>> eventAttributes = null;                if ((eventAttributes = traceEventAttributes.get(primaryKey)) == null) {                    eventAttributes = new UnifiedMap<String, ArrayList<String>>();                    traceEventAttributes.put(primaryKey, eventAttributes);                }                for(XEvent event : trace) {                    boolean add = true;                    for (Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {                        boolean found = false;                        for(Column c : primaryKey.getColumns()) {                            if(c.getColumnName() == entry.getKey()) {                                found = true;                                break;                            }                        }                        if(!found) {                            add = false;                            break;                        }                    }                    if(add) {                        for (Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {                            ArrayList<String> list = null;                            if (entry.getKey() != conceptName && entry.getKey() != timeStamp && entry.getKey() != resource && entry.getKey() != transition) {                                if ((list = eventAttributes.get(entry.getKey())) == null) {                                    list = new ArrayList<String>();                                    eventAttributes.put(entry.getKey(), list);                                }                                XAttribute value = entry.getValue();                                addAttributeToList(list, value);                            }                        }                        for (Map.Entry<String, XAttribute> entry : trace.getAttributes().entrySet()) {                            ArrayList<String> list = null;                            if (entry.getKey() != conceptName && entry.getKey() != timeStamp && entry.getKey() != resource && entry.getKey() != transition) {                                String key = "Trace-" + entry.getKey();                                if ((list = eventAttributes.get(key)) == null) {                                    list = new ArrayList<String>();                                    eventAttributes.put(key, list);                                }                                XAttribute value = entry.getValue();                                addAttributeToList(list, value);                            }                        }                    }                }            }        }        for(Map.Entry<PrimaryKey, UnifiedMap<String, ArrayList<String>>> entry1 : traceEventAttributes.entrySet()) {            Set<Column> columnSet = new TreeSet<Column>();            for(Map.Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet()) {                ArrayList<String> var = entry2.getValue();                ColumnValues columnValues = new ColumnValues(ColumnType.OTHER, var.toArray(new String[var.size()]));                Column column = new Column(entry2.getKey(), columnValues, entry1.getKey().getName());                columnSet.add(column);                setOfColumns.add(column);            }            Table t = new Table(convertPrimaryKeyToString(entry1.getKey()), columnSet);            setOfTables.add(t);        }    }    public void generateSetOfTables(XLog log, List<Entity> primaryKeys) {        UnifiedMap<Entity, UnifiedMap<String, ArrayList<String>>> traceEventAttributes = new UnifiedMap<Entity, UnifiedMap<String, ArrayList<String>>>();        for(XTrace trace : log) {            for(Entity primaryKey : primaryKeys) {                UnifiedMap<String, ArrayList<String>> eventAttributes = null;                if ((eventAttributes = traceEventAttributes.get(primaryKey)) == null) {                    eventAttributes = new UnifiedMap<String, ArrayList<String>>();                    traceEventAttributes.put(primaryKey, eventAttributes);                }                for(XEvent event : trace) {                    boolean add = true;                    for(String s : EntityNameExtractor.getEntityName(primaryKey)) {                        boolean found = false;                        for (Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {                            if(s.equals(entry.getKey()) && !isAttributeNull(entry.getValue())) {                                found = true;                                break;                            }                        }                        if(!found) {                            add = false;                            break;                        }                    }                    if(add) {                        for (Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {                            ArrayList<String> list = null;                            if (entry.getKey() != conceptName && entry.getKey() != timeStamp && entry.getKey() != resource && entry.getKey() != transition) {                                if ((list = eventAttributes.get(entry.getKey())) == null) {                                    list = new ArrayList<String>();                                    eventAttributes.put(entry.getKey(), list);                                }                                XAttribute value = entry.getValue();                                addAttributeToList(list, value);                            }                        }                        for (Map.Entry<String, XAttribute> entry : trace.getAttributes().entrySet()) {                            ArrayList<String> list = null;                            if (entry.getKey() != conceptName && entry.getKey() != timeStamp && entry.getKey() != resource && entry.getKey() != transition) {                                String key = "Trace-" + entry.getKey();                                if ((list = eventAttributes.get(key)) == null) {                                    list = new ArrayList<String>();                                    eventAttributes.put(key, list);                                }                                XAttribute value = entry.getValue();                                addAttributeToList(list, value);                            }                        }                    }                }            }        }        for(Map.Entry<Entity, UnifiedMap<String, ArrayList<String>>> entry1 : traceEventAttributes.entrySet()) {            Set<Column> columnSet = new TreeSet<Column>();            int max = 0;            int min = Integer.MAX_VALUE;            Set<String> entityNames = EntityNameExtractor.getEntityName(entry1.getKey());            if(entityNames.size() == 1) {                for (Map.Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet()) {                    if(entityNames.contains(entry2.getKey())) {                        max = Math.max(max, entry2.getValue().size());                    }                }                for (Map.Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet()) {//                    if (entry2.getValue().size() <= max) {                    ArrayList<String> var = entry2.getValue();                    ColumnValues columnValues = new ColumnValues(ColumnType.OTHER, var.toArray(new String[var.size()]));                    Column column = new Column(entry2.getKey(), columnValues, entry1.getKey().getName());                    //                Column column = new Column(entry2.getKey(), columnValues);                    columnSet.add(column);                    setOfColumns.add(column);//                    }                }            }else {                for (Map.Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet()) {                    if(entityNames.contains(entry2.getKey())) {                        min = Math.min(min, entry2.getValue().size());                    }                }                for (Map.Entry<String, ArrayList<String>> entry2 : entry1.getValue().entrySet()) {//                    if (entry2.getValue().size() >= min) {                    ArrayList<String> var = entry2.getValue();                    ColumnValues columnValues = new ColumnValues(ColumnType.OTHER, var.toArray(new String[var.size()]));                    Column column = new Column(entry2.getKey(), columnValues, entry1.getKey().getName());                    //                Column column = new Column(entry2.getKey(), columnValues);                    columnSet.add(column);                    setOfColumns.add(column);//                    }                }            }            Table t = new Table(entry1.getKey().getName(), columnSet);            setOfTables.add(t);        }    }    private String convertPrimaryKeyToString(PrimaryKey primaryKey) {        StringBuilder sb = new StringBuilder();        sb.append("(");        for(Column c : primaryKey.getColumns()) {            sb.append(c.getColumnName());            sb.append(",");        }        sb.deleteCharAt(sb.length()-1);        sb.append(")");        return  getString(sb.toString());    }    public Set<Table> getSetOfTables() {        return setOfTables;    }    public Set<Column> getSetOfColumns() {        return setOfColumns;    }    private boolean isAttributeNull(XAttribute value) {        String val = null;        if(value instanceof XAttributeLiteral) val = getString(((XAttributeLiteral) value).getValue());        else if(value instanceof XAttributeBoolean) val = getString(Boolean.toString(((XAttributeBoolean) value).getValue()));        else if(value instanceof XAttributeDiscrete) val = getString(Long.toString(((XAttributeDiscrete) value).getValue()));        else if(value instanceof XAttributeContinuous) val = getString(Double.toString(((XAttributeContinuous) value).getValue()));        else if(value instanceof XAttributeTimestamp) val = getString(((XAttributeTimestamp) value).getValue().toString());        return val == null;    }    private void addAttributeToList(ArrayList<String> list, XAttribute value) {        String val = null;        if(value instanceof XAttributeLiteral) val = getString(((XAttributeLiteral) value).getValue());        else if(value instanceof XAttributeBoolean) val = getString(Boolean.toString(((XAttributeBoolean) value).getValue()));        else if(value instanceof XAttributeDiscrete) val = getString(Long.toString(((XAttributeDiscrete) value).getValue()));        else if(value instanceof XAttributeContinuous) val = getString(Double.toString(((XAttributeContinuous) value).getValue()));        else if(value instanceof XAttributeTimestamp) val = getString(((XAttributeTimestamp) value).getValue().toString());        if(val != null) {            list.add(val);        }    }    private String getString(String o) {        String result = null;        if((result = (String) map.get(o)) == null) {            map.put(o, o);            result = o;        }        if(result == null || result.equals("null")) return null;        return result;    }}