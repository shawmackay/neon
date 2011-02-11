/*
 * neon : org.jini.projects.neon.ui.support
 * 
 * 
 * AgentTreeModel.java
 * Created on 08-Aug-2005
 * 
 * AgentTreeModel
 *
 */

package org.jini.projects.neon.ui.support;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.cxf.common.classloader.FireWallClassLoader;
import org.jini.projects.neon.service.admin.AgentAdmin;
import org.jini.projects.neon.service.admin.AgentDescription;
import org.jini.projects.neon.service.admin.DomainDescription;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * @author calum
 */
public class AgentTreeModel implements TreeModel {

	public static class NamedMap extends TreeMap {
		private String name;

		NamedMap(String name) {
			super();
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	private boolean showAncestors;
	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
	TreeMap rootMap = new NamedMap("Domains");
	private AgentAdmin adminOb;

	public AgentTreeModel(AgentAdmin adminOb) {
		showAncestors = false;
		this.adminOb = adminOb;
		updateDescriptions();

	} // /**

	
	public void updateDescriptions(){
		try {
                    
                        System.out.println("Updating descriptions");
			DomainDescription[] descriptions = adminOb.getDomains();
			
			updateModelFromDescriptions(descriptions);
			for(TreeModelListener l : treeModelListeners)
				l.treeStructureChanged(new TreeModelEvent(this, new Object[]{rootMap}));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void updateDescriptions(TreePath pathUpdated){
		try {
                    
                        System.out.println("Updating descriptions");
			DomainDescription[] descriptions = adminOb.getDomains();
			
			updateModelFromDescriptions(descriptions);
			for(TreeModelListener l : treeModelListeners)
				l.treeNodesRemoved(new TreeModelEvent(this, pathUpdated));
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
	
	
	private void updateModelFromDescriptions(DomainDescription[] descriptions) {
		rootMap.clear();
		for (int i = 0; i < descriptions.length; i++) {
			Map newMap = new NamedMap(descriptions[i].getName());
			rootMap.put(descriptions[i].getName(), newMap);
			for (AgentDescription agentDesc : descriptions[i].getAgents()) {
				String name = agentDesc.getName();
				String[] parts = name.split("\\.");
				Map contextMap = newMap;
				int j = 0;
				for (j = 0; j < parts.length - 1; j++) {
					// System.out.println("Part " + j + ": " + parts[j]);
					if (contextMap.containsKey(parts[j]))
						contextMap = (Map) contextMap.get(parts[j]);
					else {
						Map m = new NamedMap(parts[j]);
						contextMap.put(parts[j], m);
						contextMap = m;
					}
				}
				if (contextMap.containsKey(parts[j])) {
					Map agentMap = (Map) contextMap.get(parts[j]);
					agentMap.put(agentDesc.getIdentity().getID().toString(), agentDesc);
					// System.out.println("Adding " +
					// descriptions[i].getIdentity() + " to existing Map");
				} else {
					TreeMap agentMap = new NamedMap(parts[j]);
					// System.out.println("Creating new TreeMap for " +
					// parts[j]);
					agentMap.put(agentDesc.getIdentity().getID().toString(), agentDesc);
					contextMap.put(parts[j], agentMap);
				}
			}
		}
	}

	// * Used to toggle between show ancestors/show descendant and
	// * to change the root of the tree.
	// */
	// public void showAncestor(boolean b, Object newRoot) {
	// showAncestors = b;
	// Person oldRoot = rootPerson;
	// if (newRoot != null) {
	// rootPerson = (Person)newRoot;
	// }
	// fireTreeStructureChanged(oldRoot);
	// }

	// ////////////// Fire events //////////////////////////////////////////////
	//
	// /**
	// * The only event raised by this model is TreeStructureChanged with the
	// * root as path, i.e. the whole tree has changed.
	// */
	// protected void fireTreeStructureChanged(Object oldRoot) {
	// int len = treeModelListeners.size();
	// TreeModelEvent e = new TreeModelEvent(this,
	// new Object[] {oldRoot});
	// for (int i = 0; i < len; i++) {
	// ((TreeModelListener)treeModelListeners.elementAt(i)).
	// treeStructureChanged(e);
	// }
	// }

	// ////////////// TreeModel interface implementation ///////////////////////

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 */
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.add(l);
		
	}

	/**
	 * Returns the child of parent at index index in the parent's child array.
	 */
	public Object getChild(Object parent, int index) {
		// System.out.println(parent.getClass().getName());
		// System.out.println("Index: " + index + "; Object: " + parent);
		Map p = (Map) parent;
		Iterator iter = p.entrySet().iterator();
		int idx = 0;
		while (iter.hasNext()) {
			Map.Entry entr = (Map.Entry) iter.next();
			if (idx == index) {
				// System.out.println("Returning " + entr.getValue() + " from
				// getChild");
				return entr.getValue();
			}
			idx++;
		}
		// System.out.println("Returning null from getChild(o,idx)");
		return null;
	}

	/**
	 * Returns the number of children of parent.
	 */
	public int getChildCount(Object parent) {
		if(parent instanceof Map){
		Map p = (Map) parent;
		// System.out.println("Returning from getChildCount(o)" +
		// p.keySet().size());
		return p.keySet().size();
		}
		else return 0;
	}

	public void synchronize(){
		for(TreeModelListener l : treeModelListeners)
		
	      l.treeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot()), null, null)); 
	  } 
	
	
	/**
	 * Returns the index of child in parent.
	 */
	public int getIndexOfChild(Object parent, Object child) {
		Map p = (Map) parent;
		Iterator iter = p.keySet().iterator();
		int idx = 0;
		while (iter.hasNext())
			if (child.equals(iter.next()))
				return idx;
			else
				idx++;
		// System.out.println("Returning from getIndexOfChild(o,idx)");
		return 0;
	}

	/**
	 * Returns the root of the tree.
	 */
	public Object getRoot() {
		// System.out.println("Returning from getRoot()");
		return rootMap;
	}

	/**
	 * Returns true if node is a leaf.
	 */
	public boolean isLeaf(Object node) {
		// System.out.println("Node class: " + node.getClass().getName());
		if (node instanceof Map)
			return false;
		else
			return true;
	}

	/**
	 * Removes a listener previously added with addTreeModelListener().
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}

	/**
	 * Messaged when the user has altered the value for the item identified by
	 * path to newValue. Not used by this model.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		// System.out.println("*** valueForPathChanged : " + path + " --> " +
		// newValue);
	}
}
