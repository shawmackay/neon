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
 *  StatisticRow.java
 *Created on 15 January 2002, 14:45
 */
package org.jini.projects.neon.ui.grid;

/**
 *  @author calum
 *@author     calum
 *     05 March 2002
 *@version 0.9community */
public interface StatisticRow extends java.io.Serializable {
    static final long serialVersionUID = 5916032985601091322L;

    /**
     *  Gets the itemTitles attribute of the StatisticRow object
     *
     *@return    The itemTitles value
     *@since
     */
    public String[] getItemTitles();


    /**
     *  Gets the item attribute of the StatisticRow object
     *
     *@param  title  Description of Parameter
     *@return        The item value
     *@since
     */
    public Object getItem(String title);


    /**
     *  Sets the item attribute of the StatisticRow object
     *
     *@param  title  The new item value
     *@param  value  The new item value
     *@since
     */
    public void setItem(String title, Object value);

}

