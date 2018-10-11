package ca.pfv.spmf.algorithms.frequentpatterns;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPClose;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.algorithms.GenericAlgorithmBase;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPMax;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.AlgoZart;

/**
 * Created by smazet on 09/10/18.
 */
public abstract class AlgorithmBuilder {

    enum AlgoName {
        FPCLOSE,
        FPMAX,
        FPGROWTH,
        ZART
    }

    /**
     * static factories
     *
     * @return
     */
    public static AlgorithmBuilder FPCloseBuilder() {
        return new AlgoFPCloseBuilder();
    }

    public static AlgorithmBuilder FPMaxBuilder() {
        return new AlgoFPMaxBuilder();
    }

    public static AlgorithmBuilder FPGrowthBuilder() {
        return new AlgoFPGrowthBuilder();
    }

    public static AlgorithmBuilder AlgoZartBuilder() {
        return new AlgoZartBuilder();
    }

    /**
     * general builder methods
     */
    public abstract GenericAlgorithmBase build();

    // TODO: parameters in builders
    public AlgorithmBuilder withMinSupport(double minSupportPercentage) {return null;}

    public static class AlgoFPCloseBuilder extends AlgorithmBuilder {

        @Override
        public AlgoFPClose build() {
            return new AlgoFPClose();
        }
    }

    public static class AlgoFPMaxBuilder extends AlgorithmBuilder {

        @Override
        public AlgoFPMax build() {
            return new AlgoFPMax();
        }
    }

    public static class AlgoFPGrowthBuilder extends AlgorithmBuilder {

        @Override
        public AlgoFPGrowth build() {
            return new AlgoFPGrowth();
        }
    }

    public static class AlgoZartBuilder extends AlgorithmBuilder {

        @Override
        public GenericAlgorithmBase build() {
            return new AlgoZart();
        }
    }
}
