package Analyzer;

/**
 * Created by root on 8/6/17.
 */


import java.util.*;

import soot.*;
import soot.options.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.*;

public class GuaranteedDefs
{
    protected Map<Unit, List> unitToGuaranteedDefs;

    public GuaranteedDefs(UnitGraph graph)
    {
        if(Options.v().verbose())
            G.v().out.println("[" + graph.getBody().getMethod().getName() +
                    "]     Constructing GuaranteedDefs...");

        Analyzer.GuaranteedDefsAnalysis  analysis = new  Analyzer.GuaranteedDefsAnalysis(graph);

        // build map
        {
            unitToGuaranteedDefs = new HashMap<Unit, List>(graph.size() * 2 + 1, 0.7f);
            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext()){
                Unit s = (Unit) unitIt.next();
                FlowSet set = (FlowSet) analysis.getFlowBefore(s);
                unitToGuaranteedDefs.put
                        (s, Collections.unmodifiableList(set.toList()));
            }
        }
    }

    /**
     * Returns a list of locals guaranteed to be defined at (just
     * before) program point <tt>s</tt>.
     **/
    public List getGuaranteedDefs(Unit s)
    {
        return unitToGuaranteedDefs.get(s);
    }
}

