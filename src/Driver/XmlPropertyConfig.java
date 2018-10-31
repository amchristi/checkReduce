package Driver;

import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.options.Options;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 9/25/17.
 */
public class XmlPropertyConfig implements IInfoflowConfig {
    @Override
    public void setSootOptions(Options options) {
        // explicitly include packages for shorter runtime:
        List<String> includeList = new LinkedList<String>();
        includeList.add("java.lang.*");
        includeList.add("java.util.*");
        includeList.add("java.io.*");
        includeList.add("sun.misc.*");
        includeList.add("java.net.*");
        includeList.add("javax.servlet.*");
        includeList.add("javax.crypto.*");
        includeList.add("junit.framework.*");

        includeList.add("android.*");
        includeList.add("org.apache.http.*");

        includeList.add("com.osustar.*");

        //includeList.add("de.test.*");
        includeList.add("soot.*");
        //includeList.add("com.example.*");
        //includeList.add("libcore.icu.*");
        //includeList.add("securibench.*");
        includeList.add("abcd.*");
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        options.set_include(includeList);
        options.set_output_format(Options.output_format_none);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("cg.spark", "string-constants:true");
        Options.v().set_ignore_classpath_errors(true);
    }

}
