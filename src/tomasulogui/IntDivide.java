package tomasulogui;

public class IntDivide extends FunctionalUnit {

    public static final int EXEC_CYCLES = 7;

    public IntDivide(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        return stations[station].data1 / stations[station].data2;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
