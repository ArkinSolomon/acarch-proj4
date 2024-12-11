package tomasulogui;

public class ReservationStation {
    PipelineSimulator simulator;

    int tag1;
    int tag2;
    int data1;
    int data2;
    boolean data1Valid = false;
    boolean data2Valid = false;
    // destTag doubles as branch tag
    int destTag;
    IssuedInst.INST_TYPE function = IssuedInst.INST_TYPE.NOP;

    // following just for branches
    int addressTag;
    boolean addressValid = false;
    int address;
    boolean predictedTaken = false;

    public ReservationStation(PipelineSimulator sim) {
        simulator = sim;
    }

    public int getDestTag() {
        return destTag;
    }

    public int getData1() {
        return data1;
    }

    public int getData2() {
        return data2;
    }

    public boolean isPredictedTaken() {
        return predictedTaken;
    }

    public IssuedInst.INST_TYPE getFunction() {
        return function;
    }

    public void snoop(CDB cdb) {
        // TODO - add code to snoop on CDB each cycle
    }

    public boolean isReady() {
        return data1Valid && data2Valid;
    }

    public void clear() {
        function = IssuedInst.INST_TYPE.NOP;
    }

    public void loadInst(IssuedInst inst) {
        tag1 = inst.regSrc1Tag;
        tag2 = inst.regSrc2Tag;
        destTag = inst.regDestTag;
        data1 = data2 = 0;
        data1Valid = inst.regSrc1Valid;
        data2Valid = inst.regSrc2Valid;

        if (inst.isImmediate()) {
            data2 = inst.getImmediate();
            data2Valid = true;
        }

        function = inst.opcode;
    }
}
