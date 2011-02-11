/*
 * Copyright 2005 neon.jini.org project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.jini.projects.neon.util;

import java.lang.reflect.Field;

import net.jini.core.entry.Entry;

/**
 * Handles matching of an set of entries against a set of template entries
 */
public class MetaMatcher {

    /**
     * Checks whether one set of entries is matched to a second set of entries
     * such that template Set is equivalent to, or a subset of, match Set and
     * each entry within the template set has an equivalent within the matchSet.
     * 
     * @param matchSet
     *            an array of entries representing the entries that need to be
     *            matched
     * @param templateSet
     *            an array of entries to match against.
     * @return whether the given set matches the template set
     */
    public boolean match(Entry[] matchSet, Entry[] templateSet) {

        boolean assignableCheck = false;        
        int nummatched = 0;
        if (matchSet.length >= templateSet.length) {
            for (int checkloop = 0; checkloop < matchSet.length; checkloop++) {
                for (int innerloop = 0; innerloop < templateSet.length; innerloop++)
                    if (matchSet[checkloop].getClass().isInstance(templateSet[innerloop])) {
                        assignableCheck = true;
                        Field[] entryData = matchSet[checkloop].getClass().getFields();
                        try {
                            if (!checkFields(entryData, matchSet[checkloop], templateSet[innerloop])) {
                                System.out.println("Fields don't match");
                                return false;
                            } else
                                nummatched++;
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
            }
        } else {            
            return false;
        }
        if (nummatched != templateSet.length) {
            return false;
        }
return true;
    }

    private boolean checkFields(Field[] fields, Entry toMatch, Entry template) throws IllegalAccessException {
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            Object templateValue = f.get(template);
            // System.out.println("Checking " + f.getName());
            if (templateValue == null) {
                // System.out.println("TemplateVal is null..skipping");
                continue;
            }
            Object matchValue = f.get(toMatch);
            if (matchValue != null) {
                if (templateValue.getClass().equals(matchValue.getClass())) {
                    if (templateValue.equals(matchValue)) {
                        // System.out.println("Template Field is matched:(T):" +
                        // templateValue + ", (M)" + matchValue);
                    } else
                        return false;
                } else
                    return false;
            } else
                return false;
        }
        return true;
    }
}
