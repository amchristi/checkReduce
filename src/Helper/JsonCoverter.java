package Helper;

import Reducer.MethodInfo;
import Reducer.TestMethodInfo;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import soot.G;
import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.jimple.internal.JimpleLocal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by root on 10/8/17.
 */
public class JsonCoverter {
    public JSONObject ConvertTestMethodInfo(TestMethodInfo tm, String key){
        JSONObject jsonTestMethodInfo = new JSONObject();
        jsonTestMethodInfo.put("methodRef",tm.methodRef.toString());
        JSONArray jsonAssertRefs = new JSONArray();
        Iterator<SootMethodRef> it =  tm.assertRefs.iterator();
        while(it.hasNext()){
            SootMethodRef sm = it.next();
            jsonAssertRefs.add(sm.toString());
        }
        jsonTestMethodInfo.put("assertRefs",jsonAssertRefs);
        JSONArray jsonMethodDetails = new JSONArray();
        Iterator<MethodInfo> itMethodInfo = tm.methodDetails.iterator();
        while(itMethodInfo.hasNext()){
            MethodInfo m = itMethodInfo.next();
            JSONObject jsonM = ConvertMethodInfo(m);
            jsonMethodDetails.add(jsonM);

        }
        jsonTestMethodInfo.put("methodDetails",jsonMethodDetails);
        Debugger.log(jsonTestMethodInfo.toJSONString());
        return jsonTestMethodInfo;


    }

    public JSONObject ConvertMethodInfo(MethodInfo methodInfo){

        JSONObject jsonMethodInfo = new JSONObject();
        jsonMethodInfo.put("methodName",methodInfo.methodName);
        JSONArray jsonClassVariableRefs = new JSONArray();
        Iterator<SootFieldRef> itCV = methodInfo.classVaraibleRefs.iterator();
        while(itCV.hasNext()){
            jsonClassVariableRefs.add(itCV.next().toString());
        }
        JSONArray jsonLocalVaraibleRefs = new JSONArray();
        Iterator<JimpleLocal> itLV = methodInfo.localVariableRefs.iterator();
        while(itLV.hasNext()){
            jsonLocalVaraibleRefs.add(itLV.next().toString());
        }
        jsonMethodInfo.put("classVaraibleRefs",jsonClassVariableRefs);
        jsonMethodInfo.put("localVariableRefs",jsonLocalVaraibleRefs);

        return jsonMethodInfo;

    }

    public String convert(HashMap<String,HashSet<TestMethodInfo>> testMethodAssertMap){

        JSONArray jsonTestMethodAssertMap = new JSONArray();
        // (key,value) pairs.


        for (String s :testMethodAssertMap.keySet()             ) {
            HashSet<TestMethodInfo> testMethods = testMethodAssertMap.get(s);
            Iterator<TestMethodInfo> it = testMethods.iterator();
            while(it.hasNext()){
                JSONObject obj = ConvertTestMethodInfo(it.next(),s);
                jsonTestMethodAssertMap.add(obj);
            }
        }




        return jsonTestMethodAssertMap.toJSONString();

    }
}
