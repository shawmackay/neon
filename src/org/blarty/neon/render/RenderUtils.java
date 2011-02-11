package org.jini.projects.neon.render;

import java.util.Map;
import java.util.TreeMap;

public class RenderUtils {
        public static Map<String, String> chopQueryString(String query, boolean keepEmptyValues) {
                TreeMap<String, String> map = new TreeMap<String, String>();
                String[] pairs = query.split("&|;");
                for (String s : pairs) {
                        String[] pair = s.split("=");
                        if (pair.length == 1) {
                                if (keepEmptyValues)
                                        map.put(pair[0], null);
                        }else
                                map.put(pair[0], pair[1]);
                }
                return map;
        }

        public static void main(String[] args) {
                System.out.println(chopQueryString("field1=a&field2=b;field4=;field3=c", false));

        }
}
