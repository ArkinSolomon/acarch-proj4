package tomasulogui;

public abstract class FunctionalUnit implements IssuableUnit {
    PipelineSimulator simulator;
    ReservationStation[] stations = new ReservationStation[2];
    int activeStation = -1;
    int currentCycles = 0;

    public FunctionalUnit(PipelineSimulator sim) {
        simulator = sim;
    }

    public void squashAll() {
        // todo fill in
    }

    public abstract int calculateResult(int station);

    public abstract int getExecCycles();

    public void execCycle(CDB cdb) {
        ReservationStation current = null;
        if (activeStation > 0) {
            current = stations[activeStation];
        } else {
            int s;
            for (s = 0; s < stations.length; s++) {
                if (stations[s] != null && stations[s].isReady()) {
                    activeStation = s;
                    current = stations[activeStation];
                }
            }
        }

        if (current == null) {
            return;
        }

        currentCycles++;
        if (currentCycles < getExecCycles()) {
            return;
        }

        cdb.dataTag = current.destTag;
        cdb.setDataValid(true);
        cdb.setDataValue(calculateResult(activeStation));
        System.out.println("Calculated result!");
        stations[activeStation] = null;
        activeStation = -1;
    }

    public boolean acceptIssue(IssuedInst inst) {
        boolean station0Avail = stations[0] == null;
        boolean station1Avail = stations[1] == null;

        if (station0Avail) {
            stations[0] = new ReservationStation(simulator);
            stations[0].loadInst(inst);
            return true;
        }

        if (station1Avail) {
            stations[1] = new ReservationStation(simulator);
            stations[1].loadInst(inst);
            return true;
        }

        return false;
    }
}
