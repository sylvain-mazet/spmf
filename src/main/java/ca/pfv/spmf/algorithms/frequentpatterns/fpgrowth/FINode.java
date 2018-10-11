package ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smazet on 09/10/18.
 *
 * This FINode class used to be the MFINode class dedicated to maximal itemsets
 * Now CFITree inherits from FITree,
 * but CFINode and MFINode have merged into FINode
 */
public class FINode {
    int itemID = -1;  // item id
    int counter = 1;  // frequency counter  (a.k.a. support)
    int level;  // at which level in the MFI tree this node appears

    // the parent node of that node or null if it is the root
    FINode parent = null;
    // the child nodes of that node
    List<FINode> children = new ArrayList<FINode>();

    FINode nodeLink = null; // link to next node with the same item id (for the header table).

    public int getItemID() {
        return itemID;
    }

    public int getCounter() {
        return counter;
    }

    public int getLevel() {
        return level;
    }

    public FINode getParent() {
        return parent;
    }

    public List<FINode> getChildren() {
        return children;
    }
    /**
     * Return the immediate child of this node having a given ID.
     * If there is no such child, return null;
     */
    FINode getChildWithID(int id) {
        // for each child node
        for(FINode child : children){
            // if the id is the one that we are looking for
            if(child.itemID == id){
                // return that node
                return child;
            }
        }
        // if not found, return null
        return null;
    }

    /**
     * Method for getting a string representation of this tree
     * (to be used for debugging purposes).
     * @param indent indentation
     * @return a string
     */
    public String toString(String indent) {
        StringBuilder output = new StringBuilder();
        output.append(""+ itemID);
        output.append(" (count="+ counter);
        output.append(" level="+ level);
        output.append(")\n");
        String newIndent = indent + "   ";
        for (FINode child : children) {
            output.append(newIndent+ child.toString(newIndent));
        }
        return output.toString();
    }

}
