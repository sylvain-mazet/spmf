package ca.pfv.spmf.test;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FIBNode;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FIBTree;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FINode;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FITree;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by smazet on 11/10/18.
 */
public class FITreeTest {
    @Test
    public void differentiate() throws Exception {

        int id = 1;

        FITree tree = new FITree();
        tree.setRoot(new FINode(id++,100,10));

        FINode firstChild = new FINode(id++, 80, 20); firstChild.setParent(tree.getRoot());
        FINode secondChild = new FINode(id++, 75, 30); secondChild.setParent(tree.getRoot());
        FINode thirdChild = new FINode(id++, 10, 40); thirdChild.setParent(tree.getRoot());

        FINode firstGrandChild = new FINode(id++, 40, 200); firstGrandChild.setParent(firstChild);

        FIBTree deltaTree = tree.differentiate();

        System.out.println(deltaTree.toString());

        FIBNode newRoot = (FIBNode) deltaTree.getRoot();

        FIBNode newFirstChild = (FIBNode)newRoot.getChildren().get(0);
        assertsForNode(newFirstChild,0.2, 0.25, 1.0, 0.5);

        FIBNode newFirstGrandChild = (FIBNode) newFirstChild.getChildren().get(0);
        assertsForNode(newFirstGrandChild, 0.5, 1.0, 9.0, 0.9);

        FIBNode newSecondChild = (FIBNode) newRoot.getChildren().get(1);
        assertsForNode(newSecondChild, 0.25, 1.0/3.0, 2.0, 2./3.);

        FIBNode newThirdChild = (FIBNode) newRoot.getChildren().get(2);
        assertsForNode(newThirdChild, 0.9, 9.0, 3.0, 0.75);
    }

    private void assertsForNode(FIBNode node, double deltaSupportIn, double deltaSupportOut, double deltaLengthIn, double deltaLengthOut) {
        Assert.assertTrue(node.getDeltaSupportIn()==deltaSupportIn); // ****
        Assert.assertTrue(node.getDeltaSupportOut()==deltaSupportOut);
        Assert.assertTrue(node.getDeltaLengthIn()==deltaLengthIn);
        Assert.assertTrue(node.getDeltaLengthOut()==deltaLengthOut); // ****
    }

}