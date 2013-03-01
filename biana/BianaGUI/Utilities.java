
//import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Utilities{

    public static String convertAttributeParameterMapToList(HashMap<String, HashMap<String, HashMap<String, String>>> containerMap) {
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        for (HashMap<String, HashMap<String, String>> attributeMap : containerMap.values()) {
            buffer.append("[");
            Iterator iter = attributeMap.entrySet().iterator();
            Map.Entry entry;
            while (iter.hasNext()) {
                entry = (Map.Entry) iter.next();
                buffer.append("(\"" + (String) entry.getKey() + "\", [");
                HashMap<String, String> parameterMap = (HashMap<String, String>) entry.getValue();
                if (!parameterMap.isEmpty()) {
                    buffer.append("(\"" + Utilities.join(parameterMap, "\"), (\"", "\", \"") + "\")");
                }
                buffer.append("])");
                if (iter.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append("]");
            i++;
            if (containerMap.values().size() != i) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    public static String join(Map s, String delimiter, String delimiterInner){
	
	StringBuffer buffer = new StringBuffer();
        Iterator iter = s.entrySet().iterator();
        Map.Entry entry;
	if(iter.hasNext()) {
            entry = (Map.Entry) iter.next();
	    buffer.append(entry.getKey());
            buffer.append(delimiterInner);
            buffer.append(entry.getValue());
	    while( iter.hasNext() ){
                entry = (Map.Entry) iter.next();
		buffer.append(delimiter);
		buffer.append(entry.getKey());
                buffer.append(delimiterInner);
                buffer.append(entry.getValue());
	    }
	}
	return buffer.toString();
    }

    
    public static String join(Collection s, String delimiter){
	
	StringBuffer buffer = new StringBuffer();
	Iterator iter = s.iterator();
	if( iter.hasNext() ){
	    buffer.append(iter.next());
	    while( iter.hasNext() ){
		buffer.append(delimiter);
		buffer.append(iter.next());
	    }
	}
	return buffer.toString();
    }

    public static String join(Object[] s, String delimiter){
	
	StringBuffer buffer = new StringBuffer();

	if( s.length>0 ){
	    for( int i=0; i<s.length-1; i++ ){
		buffer.append(s[i]);
		buffer.append(delimiter);
	    }
	    buffer.append(s[s.length-1]);
	}

	return buffer.toString();

    }


}