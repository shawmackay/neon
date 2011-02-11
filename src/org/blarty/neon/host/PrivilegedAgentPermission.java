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

package org.jini.projects.neon.host;

import java.security.BasicPermission;
import java.util.ArrayList;
import java.util.List;

/**
 *Allows an agent privileged access to the host system, and to other agent information.
 */
public class PrivilegedAgentPermission extends BasicPermission{

    private List actions;
    public PrivilegedAgentPermission(String name) {
        super(name);
        actions =new ArrayList();
    }

    public PrivilegedAgentPermission(String name, String actions) {
        super(name, actions);

    }



    public String getActions() {
        return actions.toString();
    }

}
