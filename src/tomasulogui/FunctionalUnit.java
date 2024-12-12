package tomasulogui;

public abstract class FunctionalUnit implements IssuableUnit {
    PipelineSimulator simulator;
    ReservationStation[] stations = new ReservationStation[2];
    int activeStation = -1;
    int currentCycles = 0;
    int writeValue = 0;
    int writeTag = -1;

    public FunctionalUnit(PipelineSimulator sim) {
        simulator = sim;
    }

    public void squashAll() {
        // todo fill in
    }

    public abstract int calculateResult(int station);

    public abstract int getExecCycles();

    public void snoopCDB(CDB cdb) {
        for (ReservationStation rs : stations) {
            if (rs == null) {
                continue;
            }

            rs.snoop(cdb);
        }
    }

    public void tryWriteBack(CDB cdb) {
        if (cdb.getDataValid() || writeTag < 0) {
            return;
        }

        System.out.println("Writeback to CDB " + this);
        cdb.setDataTag(writeTag);
        cdb.setDataValue(writeValue);
        cdb.setDataValid(true);
        writeTag = -1;

        stations[activeStation] = null;
        activeStation = -1;
        currentCycles = 0;
    }

    public void execCycle(CDB cdb) {
        System.out.println("Exec cycle: "  + this);
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
        System.out.println("Calculated result!");

        writeValue = calculateResult(activeStation);
        writeTag = current.getDestTag();
    }

    @Override
    public boolean canAcceptIssue() {
        return stations[0] == null || stations[1] == null;
    }

    @Override
    public void acceptIssue(IssuedInst inst) {
        boolean station0Avail = stations[0] == null;
        boolean station1Avail = stations[1] == null;

        if (station0Avail) {
            stations[0] = new ReservationStation(simulator);
            stations[0].loadInst(inst);
            return;
        }

        if (station1Avail) {
            stations[1] = new ReservationStation(simulator);
            stations[1].loadInst(inst);
        }
    }
}
