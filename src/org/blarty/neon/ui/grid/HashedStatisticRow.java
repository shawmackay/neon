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

/*
 *  HashedStatisticRow.java
 *Created on 15 January 2002, 15:22
 */
package org.jini.projects.neon.ui.grid;

import java.util.Iterator;
import java.util.Set;

/**
 *  @author calum
 *@author     calum
 *     05 March 2002
 *@version 0.9community */


public class HashedStatisticRow extends java.util.HashMap implements StatisticRow {
    static final long serialVersionUID = -5190526887789060503L;

    /**
     *  Creates new HashedStatisticRow
     *
     *@since
     */
    public HashedStatisticRow() {
    }


    /**
     *  Sets the item attribute of the HashedStatisticRow object
     *
     *@param  title  The new item value
     *@param  value  The new item value
     *@since
     */
    public void setItem(String title, Object value) {
        put(title, value);
    }


    /**
     *  Gets the item attribute of the HashedStatisticRow object
     *
     *@param  title  Description of Parameter
     *@return        The item value
     *@since
     */
    public Object getItem(String title) {

        return get(title);
    }


    /**
     *  Gets the itemTitles attribute of the HashedStatisticRow object
     *
     *@return    The itemTitles value
     *@since
     */
    public String[] getItemTitles() {
        Set keys = this.keySet();
        String[] titles = new String[this.size()];
        int i = 0;
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            titles[i++] = (String) iter.next();
        }
        return titles;
    }

}

