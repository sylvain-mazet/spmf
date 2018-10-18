package ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth;

import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.patterns.AbstractItemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetArrayImplWithCount;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.ItemsetForDelta;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by smazet on 09/10/18.
 */
public class FITree {

    static final Logger logger = LoggerFactory.getLogger(FITree.class);

    // List of pairs (item, frequency) of the header table
    Map<Integer, FINode> mapItemNodes = new HashMap<>();

    // Map that indicates the last node for each item using the node links
    // key: item   value: an fp tree node
    Map<Integer, FINode> mapItemLastNode = new HashMap<>();

    // root of the tree
    FINode root = null; // null node

    // last added itemset
    FINode lastAddedItemsetNode = null;

    public void setRoot(FINode root) {
        this.root = root;
    }

    public FINode getRoot() {
        return root;
    }

    /**
     * Add an itemset to the (M or C)FI-Tree
     * @param itemset the itemset
     * @param itemsetLength the length of the itemset
     * @param support the support of the itemset
     */
    public void addItemset(int[] itemset, int itemsetLength, int support) {

        FINode currentNode = root;
        // For each item in the itemset
        for(int i=0; i < itemsetLength; i++){
            int item = itemset[i];

            // look if there is a node already in the FP-Tree
            FINode child = currentNode.getChildWithID(item);
            if(child == null){
                // there is no node, we create a new one
                FINode newNode = new FINode();
                newNode.itemID = item;
                newNode.parent = currentNode;
                // remember at which level in the tree that node appears
                newNode.level = i+1;
                newNode.counter = support; // NEW BY PHILIPPE 2015
                // we link the new node to its parent
                currentNode.children.add(newNode);

                // we take this node as the current node for the next for loop iteration
                currentNode = newNode;

                // We update the header table.
                // We check if there is already a node with this id in the header table
                fixNodeLinks(item, newNode);
            }else{
                // there is a node already, we update it
//				MFI used to not do this: child.counter++;

                // CFI used to do this:
                child.counter = Math.max(support, child.counter);

                currentNode = child;
            }
        }

//		 SET THE SUPPORT OF THE MFI (the last item)
//		currentNode.counter = support;

        // remember that this is the last added itemset
        lastAddedItemsetNode = currentNode;
    }

    /**
     * Method to fix the node link for an item after inserting a new node.
     * @param item  the item of the new node
     * @param newNode the new node thas has been inserted.
     */
    private void fixNodeLinks(Integer item, FINode newNode) {
        // get the latest node in the tree with this item
        FINode lastNode = mapItemLastNode.get(item);
        if(lastNode != null) {
            // if not null, then we add the new node to the node link of the last node
            lastNode.nodeLink = newNode;
        }
        // Finally, we set the new node as the last node
        mapItemLastNode.put(item, newNode);

        FINode headernode = mapItemNodes.get(item);
        if(headernode == null){  // there is not
            mapItemNodes.put(item, newNode);
        }
    }

    /**
     * The versions with int arrays is for maximal itemsets
     */

    /**
     * Perform the subset test to see if an itemset is a subset of an already
     * found MFI
     * @param headWithP the itemset to be tested
     * @return true if the itemset is not a subset of an already found MFI.
     */
    public boolean passSubsetChecking(List<Integer> headWithP) {

        // Find the node list for the last item of the itemset
        Integer lastItem = headWithP.get(headWithP.size()-1);

        // OPTIMIZATION:
        // We first check against the last added itemset
        if(lastAddedItemsetNode != null) {
            boolean isSubset = issASubsetOfPrefixPath(headWithP, lastAddedItemsetNode);
            // if the itemset is a subset of the last added itemset, we do not need to check further
            if(isSubset) {
                return false;
            }
        }

        // OTHERWISE, WE NEED TO COMPARE "headwithP" with all the patterns in the MFI-tree.
        FINode node = mapItemNodes.get(lastItem);
        // if that last item is not yet in the MFI-tree, it means that "itemset" is not a subset
        // of some itemset already in the tree
        if(node == null) {
            return true;
        }
        // we will loop over each node by following node links
        do {
            // for a node, we will check if "headwithP" is a subset of the path ending at node
            boolean isSubset = issASubsetOfPrefixPath(headWithP, node);
            // if it is a subset, then "headWithP" is in the MFI-tree, we return false
            if(isSubset) {
                return false;
            }
            // go to the next itemset to test
            node = node.nodeLink;
        }while(node != null);

        // the itemset is not in the MFI-TREE.  Itemset passed the test!
        return true;
    }

    /**
     * Check if the itemset headwithP is contained in the path ending at "node" in the MFI-tree
     * @param headWithP the itemset headwithP
     * @param node  the node
     * @return true if "headwithP" is contained in the path ending at "node" in the MFI-Tree. Otherwise, false.
     */
    protected boolean issASubsetOfPrefixPath(List<Integer> headWithP, FINode node) {
        // optimization proposed in the fpmax* paper: if there is less than itemset node in that branch,
        // we don't need to check it
        if(node.level >= headWithP.size()) {
            // check if "itemset" is contained in the prefix path ending at "node"
            // We will start comparing from the parent of "node" in the prefix path since
            // the last item of itemset is "node".
            FINode nodeToCheck = node;
            int positionInItemset = headWithP.size()-1;
            int itemToLookFor = headWithP.get(positionInItemset);
            // for each item in itemset
            do {
                if(nodeToCheck.itemID == itemToLookFor) {
                    positionInItemset--;
                    // we found the itemset completely, so the subset check test is failed
                    if(positionInItemset <0) {
                        return true;
                    }
                    itemToLookFor = headWithP.get(positionInItemset);
                }
                nodeToCheck = nodeToCheck.parent;
            }while(nodeToCheck != null);
        }
        return false;
    }


    /**
     * The version with int arrays is for closed itemsets
     */


    /**
     * Perform the subset test to see if an itemset is a subset of an already
     * found CFI with the same support
     * @param headWithP the itemset to be tested
     * @param headWithPLength the last position to be considered in headWithP
     * @param headWithPSupport the support of the itemset headwithP
     * @return true if the itemset is not a subset of an already found CFI.
     */
    public boolean passSubsetChecking(int[] headWithP, int headWithPLength, int headWithPSupport) {
        // OPTIMIZATION:
        // We first check against the last added itemset
        // If there is a last added itemset and it has the same support
        if (lastAddedItemsetNode != null
                && lastAddedItemsetNode.counter == headWithPSupport) {
            boolean isSubset = issASubsetOfPrefixPath(headWithP, headWithPLength, lastAddedItemsetNode);
            // if the itemset is a subset of the last added itemset, we do not need to check further
            if(isSubset) {
                return false;
            }
        }


        // Find the node list for the first item of the itemset
        Integer firstITem = headWithP[headWithP.length-1];

        // OTHERWISE, WE NEED TO COMPARE "headwithP" with all the patterns in the CFI-tree.
        FINode node = mapItemNodes.get(firstITem);
        // if that last item is not yet in the CFI-tree, it means that "itemset" is not a subset
        // of some itemset already in the tree
        if(node == null) {
            return true;
        }
        // we will loop over each node by following node links
        do {
            // for a node, we will check if "headwithP" is a subset of the path ending at node
            boolean isSubset = node.counter == headWithPSupport
                    && issASubsetOfPrefixPath(headWithP, headWithPLength, node);
            // if it is a subset, then "headWithP" is in the CFI-tree, we return false
            if(isSubset) {
                return false;
            }
            // go to the next itemset to test
            node = node.nodeLink;
        }while(node != null);

        // the itemset is not in the CFI-TREE.  Itemset passed the test!
        return true;
    }

    /**
     * Check if the itemset headwithP is contained in the path ending at "node" in the CFI-tree
     * and have the same support
     * @param headWithP the itemset headwithP
     * @param headWithPLength the last position to be considered in headWithP
     * @param node  the node
     * @return true if "headwithP" is contained in the path ending at "node" in the CFI-Tree and has the same support.
     * Otherwise, false.
     */
    protected boolean issASubsetOfPrefixPath(int[] headWithP, int headWithPLength,FINode node) {
        // optimization proposed in the fpmax* paper: if there is less than itemset node in that branch,
        // we don't need to check it
        if(node.level >= headWithPLength) {
            // check if "itemset" is contained in the prefix path ending at "node"
            // We will start comparing from the parent of "node" in the prefix path since
            // the last item of itemset is "node".
            FINode nodeToCheck = node;
            int positionInItemset = headWithP.length-1;
            int itemToLookFor = headWithP[positionInItemset];
            // for each item in itemset
            do {
                if(nodeToCheck.itemID == itemToLookFor) {
                    positionInItemset--;
                    // we found the itemset completely, so the subset check test is failed
                    if(positionInItemset < 0) {
                        return true;
                    }
                    itemToLookFor = headWithP[positionInItemset];
                }
                nodeToCheck = nodeToCheck.parent;
            }while(nodeToCheck != null);
        }
        return false;
    }


    @Override
    /**
     * Method for getting a string representation of the tree
     * (to be used for debugging purposes).
     * @return a string
     */
    public String toString() {
        return "M"+root.toString("");
    }

    /**
     * Build another FITree from this one: collapse branches when support does not change.
     */
    public FITree reduce() {

        FITree reducedTree = new FITree();
        reducedTree.setRoot(new FICNode(0, new ItemsetArrayImplWithCount(new int[]{})));

        int id = 1;
        recurseReduce(id,  (FICNode)reducedTree.getRoot(), this.getRoot());

        return reducedTree;
    }

    private int recurseReduce(int id, FICNode reducedRoot, FINode root) {
        if (root.getChildren().isEmpty()) {
            ItemsetArrayImplWithCount itemSet = buildItemset(root);
            FICNode newNode = new FICNode( id++, itemSet);
            newNode.setParent(reducedRoot);
            return id;
        }

        if (root.getChildren().size()==1) {
            FINode child = root.getChildren().get(0);
            if (child.getCounter() == root.getCounter()) {
                id = recurseReduce(id, reducedRoot, child);
            }
            else {
                ItemsetArrayImplWithCount itemSet = buildItemset(root);
                FICNode newNode = new FICNode(id++, itemSet);
                newNode.setParent(reducedRoot);
                id = recurseReduce(id, newNode, child);
            }
        }
        else {

            ItemsetArrayImplWithCount itemSet = buildItemset(root);
            FICNode newNode = new FICNode(id++, itemSet);
            newNode.setParent(reducedRoot);

            for (FINode child : root.getChildren()) {
                id = recurseReduce(id, newNode, child);
            }
        }

        return id;
    }

    private ItemsetArrayImplWithCount buildItemset(FINode node) {
        List<Integer> items = new ArrayList<>(node.getLevel());
        items.add(node.getItemID());
        FINode iterator = node;
        while (iterator.getParent()!=null) {
            iterator = iterator.getParent();
            items.add(iterator.getItemID());
        }
        return new ItemsetArrayImplWithCount(items,node.getCounter());
    }

    /**
     * Build another FITree from this one: the nodes of the new tree
     * are the edges of ourselves.
     * The nodes are not the same since they contain statistics of the edge.
     */
    public GenericResults differentiate() {
        //FIBTree deltaTree = new FIBTree();
        //deltaTree.setRoot(new FIBNode(0, new ItemsetArrayImplWithCount(new int []{})));

        Itemsets itemsets = new Itemsets("Differentiated tree");

        int idStart = 1;
        recurseDifferentiate(idStart,itemsets, root);

        return itemsets;
    }

    private int recurseDifferentiate(int id, Itemsets itemsets, FINode root) {
        Iterator<FINode> childrenIterator = root.getChildren().iterator();

        while (childrenIterator.hasNext()) {
            FICNode child = (FICNode)childrenIterator.next();

            // I want nodes only in this child
            Set<Integer> childSet = new HashSet<>();
            int level = child.level;
            for (int item : child.getItemset().getItems()) {
                childSet.add(item);
            }
            for (int item : ((FICNode) root).getItemset().getItems()) {
                if (childSet.remove(item)) {
                    level--;
                }
            }

            ItemsetForDelta newItemset = new ItemsetForDelta( childSet );
            newItemset.setAbsoluteSupport(child.getItemset().getAbsoluteSupport());

            // we check if this itemset has already been seen
            GenericResults.ListOfItemset itemsetsAtLevel = null;
            if (itemsets.getLevels().size() > level) {
                itemsetsAtLevel = itemsets.getLevels().get(level);
                int hashCode = Arrays.hashCode(newItemset.getItems());
                Iterator<AbstractItemset> itemsetAtLevel = itemsetsAtLevel.iterator();
                boolean alreadyThere = false;
                while (itemsetAtLevel.hasNext()) {
                    AbstractItemset itemsetSameLevel = itemsetAtLevel.next();
                    if (hashCode == Arrays.hashCode(itemsetSameLevel.getItems())) {
                        alreadyThere = true;
                        logger.debug("Found same itemsets "+itemsetAtLevel.toString()+" VS "+newItemset.toString());
                    }
                }
                // if not, add it
                if (!alreadyThere) {
                    itemsets.addItemset(newItemset, level);
                }
            } else {
                // no itemset at this level yet
                itemsets.addItemset(newItemset, level);
            }

            newItemset.setDeltaSupportIn((double) (root.counter - child.counter) / root.counter);
            newItemset.setDeltaSupportOut((double) (root.counter - child.counter) / child.counter);
            newItemset.setDeltaLengthIn((double) (child.level - root.level) / root.level);
            newItemset.setDeltaLengthOut((double) (child.level - root.level) / child.level);
            newItemset.setLift(child.level - root.level);
            newItemset.setCoverage(root.counter - child.counter);

            id = recurseDifferentiate(id, itemsets,child);

        }

        return id;

    }

}
