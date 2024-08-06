/**
 *                       CLASS MODELTREE
 * Despite its name, this class actually extends JScrollPane, and we add the
 * JTree to the JScrollPane.  This class will provide methods to work directly
 * on the JTree.
 */

package org.springsalad.langevinsetup;

import java.util.Enumeration;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public class SystemTree extends JScrollPane {
    
    /* ********** A BUNCH OF STATIC STRINGS TO SET UP THE TREE *****/
    public final static String NEW_MODEL = "New Model";
    public final static String SYSTEM_INFORMATION = "System Information";
    public final static String MOLECULES = "Molecules";
    public final static String REACTIONS = "Reactions";
    public final static String CREATION_DECAY_REACTIONS = "Creation/Decay Reactions";
    public final static String TRANSITION_REACTIONS = "Transition Reactions";
    public final static String ALLOSTERIC_REACTIONS = "Allosteric Reactions";
    public final static String BINDING_REACTIONS = "Binding Reactions";
    public final static String DATA_CONSTRUCTS = "Data Constructs";
    public final static String MOLECULE_COUNTERS = "Molecule Counters";
    public final static String BOND_COUNTERS = "Bond Counters";
    public final static String STATE_COUNTERS = "State Counters";
    public final static String SITE_PROPERTY_COUNTERS = "Site Property Counters";
    public final static String CLUSTER_COUNTERS = "Cluster Counters";
    
    // The tree
    private final JTree tree;
    
    // The tree model
    private final DefaultTreeModel treeModel;
    
    // Root node
    private final DefaultMutableTreeNode rootNode;
    
    public SystemTree(){
        
        rootNode = new DefaultMutableTreeNode(NEW_MODEL);
        createInitialNodes(rootNode);
        
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.setAsksAllowsChildren(true);
        
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        this.setPreferredSize(new Dimension(150,200));
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.setViewportView(tree);
        
    }
    
    private void createInitialNodes(DefaultMutableTreeNode topNode){
        DefaultMutableTreeNode folderNode = null;
        DefaultMutableTreeNode subfolderNode = null;
        
        // Add the "System Information" node
        folderNode = new DefaultMutableTreeNode(SYSTEM_INFORMATION);
        folderNode.setAllowsChildren(false);
        topNode.add(folderNode);
        
        // Add the "Molecules" node
        folderNode = new DefaultMutableTreeNode(MOLECULES);
        folderNode.setAllowsChildren(true);
        topNode.add(folderNode);
        
        // Add the "Reactions" node and its subnodes
        folderNode = new DefaultMutableTreeNode(REACTIONS);
        topNode.add(folderNode);
        subfolderNode = new DefaultMutableTreeNode(CREATION_DECAY_REACTIONS);
        subfolderNode.setAllowsChildren(false);
        folderNode.add(subfolderNode);
        subfolderNode = new DefaultMutableTreeNode(TRANSITION_REACTIONS);
        subfolderNode.setAllowsChildren(true);
        folderNode.add(subfolderNode);
        subfolderNode = new DefaultMutableTreeNode(ALLOSTERIC_REACTIONS);
        subfolderNode.setAllowsChildren(true);
        folderNode.add(subfolderNode);
        subfolderNode = new DefaultMutableTreeNode(BINDING_REACTIONS);
        subfolderNode.setAllowsChildren(true);
        folderNode.add(subfolderNode);
        
        // Add the "Data Constructs" node and its subnodes
        folderNode = new DefaultMutableTreeNode(DATA_CONSTRUCTS);
        topNode.add(folderNode);
        subfolderNode = new DefaultMutableTreeNode(MOLECULE_COUNTERS);
        subfolderNode.setAllowsChildren(false);
        folderNode.add(subfolderNode);
        subfolderNode = new DefaultMutableTreeNode(STATE_COUNTERS);
        subfolderNode.setAllowsChildren(false);
        folderNode.add(subfolderNode);
        subfolderNode = new DefaultMutableTreeNode(BOND_COUNTERS);
        subfolderNode.setAllowsChildren(false);
        folderNode.add(subfolderNode);
        subfolderNode = new DefaultMutableTreeNode(SITE_PROPERTY_COUNTERS);
        subfolderNode.setAllowsChildren(false);
        folderNode.add(subfolderNode);
        subfolderNode = new DefaultMutableTreeNode(CLUSTER_COUNTERS);
        subfolderNode.setAllowsChildren(false);
        folderNode.add(subfolderNode);
    }
    
    public DefaultMutableTreeNode getNode(String nodeString){
        TreeNode node;
        Enumeration<TreeNode> e = rootNode.breadthFirstEnumeration();
        while(e.hasMoreElements()){
            node = e.nextElement();
            if(nodeString.equals(node.toString())){
                return (DefaultMutableTreeNode) node;
            }
        }
        return null;
    }
    
    public void selectNode(String nodeString){
        DefaultMutableTreeNode node = getNode(nodeString);
        if(node != null){
            TreePath treePath = new TreePath(treeModel.getPathToRoot(node));
            tree.scrollPathToVisible(treePath);
            tree.setSelectionPath(treePath);
        }
    }
    
    public void selectRootNode(){
        TreePath treePath = new TreePath(rootNode.getPath());
        tree.scrollPathToVisible(treePath);
        tree.setSelectionPath(treePath);
    }
    
    public void setTitle(String title){
        rootNode.setUserObject(title);
    }
    
    public void addChild(DefaultMutableTreeNode parentNode, Object child){
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    }
    
    public void addMolecule(Molecule molecule){
        DefaultMutableTreeNode parentNode = this.getNode(MOLECULES);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(molecule);
        childNode.setAllowsChildren(false);
        treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    }
    
    public void removeMolecule(Molecule molecule){
        DefaultMutableTreeNode node = this.getNode(molecule.toString());
        if(node != null){
            treeModel.removeNodeFromParent(node);
        }
    }
    
    public void addBindingReaction(BindingReaction reaction){
        DefaultMutableTreeNode parentNode = this.getNode(BINDING_REACTIONS);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(reaction);
        childNode.setAllowsChildren(false);
        treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    }
    
    public void removeBindingReaction(BindingReaction reaction){
        DefaultMutableTreeNode node = this.getNode(reaction.toString());
        if(node != null){
            treeModel.removeNodeFromParent(node);
        }
    }
    
    public void addTransitionReaction(TransitionReaction reaction){
        DefaultMutableTreeNode parentNode = this.getNode(TRANSITION_REACTIONS);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(reaction);
        childNode.setAllowsChildren(false);
        treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    }
    
    public void removeTransitionReaction(TransitionReaction reaction){
        DefaultMutableTreeNode node = this.getNode(reaction.toString());
        if(node != null){
            treeModel.removeNodeFromParent(node);
        }
    }
    
    public void addAllostericReaction(AllostericReaction reaction){
        DefaultMutableTreeNode parentNode = this.getNode(ALLOSTERIC_REACTIONS);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(reaction);
        childNode.setAllowsChildren(false);
        treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    }
    
    public void removeAllostericReaction(AllostericReaction reaction){
        DefaultMutableTreeNode node = this.getNode(reaction.toString());
        if(node != null){
            treeModel.removeNodeFromParent(node);
        }
    }
    
    public JTree getTree(){
        return tree;
    }
    
    public TreeModel getTreeModel(){
        return treeModel;
    }

    public DefaultMutableTreeNode getRootNode(){
        return rootNode;
    }


}
