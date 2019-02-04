package ca.pfv.spmf.test;

import ca.pfv.spmf.algorithms.GenericResults;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FIBNode;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FINode;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FITree;
import ca.pfv.spmf.patterns.Itemsets;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by smazet on 11/10/18.
 */
public class FITreeTest {

    public static final Logger logger = LoggerFactory.getLogger(FITreeTest.class);

    @Test
    public void differentiate() throws Exception {

        int id = 1;

        FITree tree = new FITree();
        tree.setRoot(new FINode(id++,100,1));

        FINode firstChild = new FINode(id++, 80, 2); firstChild.setParent(tree.getRoot());
        FINode secondChild = new FINode(id++, 75, 2); secondChild.setParent(tree.getRoot());
        FINode thirdChild = new FINode(id++, 10, 2); thirdChild.setParent(tree.getRoot());

        FINode firstGrandChild = new FINode(id, 40, 3); firstGrandChild.setParent(firstChild);
        FINode firstDuplicate = new FINode(id++, 6, 3); firstDuplicate.setParent(thirdChild);

        FINode secondGrandChild = new FINode(id, 40, 4); secondGrandChild.setParent(firstGrandChild);
        FINode secondDuplicate = new FINode(id, 6, 4); secondDuplicate.setParent(firstDuplicate);
        FINode otherDiff = new FINode(id++, 50, 3); otherDiff.setParent(secondChild);

        FINode suppDiff = new FINode(id, 6, 5); suppDiff.setParent(secondDuplicate);
        FINode otherDiff2 = new FINode(id++, 50, 4); otherDiff2.setParent(otherDiff);

        FITree reducedTree = tree.reduce();

        logger.info("Reduced tree: "+ reducedTree.toString());

        GenericResults results = reducedTree.differentiate();

        Itemsets diffItemset = results.getItemsets();
        diffItemset.printItemsets(12);

        FITree deltaTree = results.getFITree();

        logger.info("Diff tree: "+deltaTree.toString());

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