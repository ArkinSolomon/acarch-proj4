package tomasulogui;

public class IssueUnit {
    PipelineSimulator simulator;
    IssuedInst issuedInstruction;

    public IssueUnit(PipelineSimulator sim) {
        simulator = sim;
    }

    public void execCycle(CDB cdb) {
        int currPc = simulator.getPC();
        Instruction inst = simulator.getMemory().getInstAtAddr(currPc);
        IssuableUnit fu = null;

        // an execution cycle involves:
        // 1. checking if ROB and Reservation Station avail
        // 2. issuing to reservation station, if no structural hazard

        // to issue, we make an IssuedInst, filling in what we know
        // We check the BTB, and put prediction if branch, updating PC
        //     if pred taken, incr PC otherwise
        // We then send this to the ROB, which fills in the data fields
        // We then check the CDB, and see if it is broadcasting data we need,
        //    so that we can forward during issue

        // We then send this to the FU, who stores in reservation station
        switch (inst.getOpcode()) {
            case Instruction.INST_ADD:
            case Instruction.INST_SUB:
            case Instruction.INST_MUL:
            case Instruction.INST_DIV:
            case Instruction.INST_AND:
            case Instruction.INST_OR:
            case Instruction.INST_XOR:
            case Instruction.INST_ADDI:
            case Instruction.INST_ANDI:
            case Instruction.INST_ORI:
            case Instruction.INST_XORI:
            case Instruction.INST_SLL:
            case Instruction.INST_SRL:
            case Instruction.INST_SRA:
                fu = simulator.getALU();
                break;
            case Instruction.INST_J:
            case Instruction.INST_JAL:
            case Instruction.INST_JR:
            case Instruction.INST_JALR:
            case Instruction.INST_BEQ:
            case Instruction.INST_BNE:
            case Instruction.INST_BGEZ:
            case Instruction.INST_BGTZ:
            case Instruction.INST_BLEZ:
            case Instruction.INST_BLTZ:
                fu = simulator.getBranchUnit();
                break;
            case Instruction.INST_LW:
                fu = simulator.getLoader();
                break;
        }

        issuedInstruction = IssuedInst.createIssuedInst(inst);
        issuedInstruction.pc = currPc;

        if (issuedInstruction.regSrc1Used) {
            System.out.println("src 1 used " + issuedInstruction.regSrc1);
            if (issuedInstruction.regSrc1 == 0) {
                issuedInstruction.regSrc1Value = 0;
                issuedInstruction.regSrc1Valid = true;
            } else {
                issuedInstruction.regSrc1Tag = simulator.getROB().getTagForReg(issuedInstruction.regSrc1);

                if (issuedInstruction.regSrc1Tag == -1) {
                    issuedInstruction.regSrc1Value = simulator.getROB().getDataForReg(issuedInstruction.regSrc1);
                    issuedInstruction.regSrc1Valid = true;
                    System.out.println("src1 " + issuedInstruction.regSrc1Value);
                } else if (cdb.getDataValid() && cdb.getDataTag() == issuedInstruction.regSrc1Tag) {
                    issuedInstruction.regSrc1Value = cdb.getDataValue();
                    issuedInstruction.regSrc1Valid = true;
                    issuedInstruction.regSrc1Tag = -1;
                }
            }
        }

        if (issuedInstruction.isImmediate()) {
            issuedInstruction.regSrc2Value = issuedInstruction.getImmediate();
            issuedInstruction.regSrc2Valid = true;
            System.out.println("immediate " + issuedInstruction.regSrc2Value + " " + issuedInstruction.getOpcode());
        } else if (issuedInstruction.regSrc2Used) {
            System.out.println("src 2 used " + issuedInstruction.regSrc2);
            if (issuedInstruction.regSrc2 == 0) {
                issuedInstruction.regSrc2Value = 0;
                issuedInstruction.regSrc2Valid = true;
            } else {
                issuedInstruction.regSrc2Tag = simulator.getROB().getTagForReg(issuedInstruction.regSrc2);
                if (issuedInstruction.regSrc2Tag == -1) {
                    issuedInstruction.regSrc2Value = simulator.getROB().getDataForReg(issuedInstruction.regSrc2);
                    issuedInstruction.regSrc2Valid = true;
                }else if (cdb.getDataValid() && cdb.getDataTag() == issuedInstruction.regSrc2Tag) {
                    issuedInstruction.regSrc2Value = cdb.getDataValue();
                    issuedInstruction.regSrc2Valid = true;
                    issuedInstruction.regSrc2Tag = -1;
                }
            }
        }

        if (issuedInstruction.regDestUsed) {
            issuedInstruction.regDestTag = simulator.getROB().getTagForReg(issuedInstruction.regDest);
        }

        if (fu == null) {
            throw new RuntimeException("Functional unit is null, assign for instruction " + issuedInstruction.getOpcode());
        }

        if (fu.canAcceptIssue()) {
            System.out.println("Instruction can be accepted: " + issuedInstruction.getOpcode());
            simulator.getROB().updateInstForIssue(issuedInstruction);
            fu.acceptIssue(issuedInstruction);
            simulator.setPC(currPc + 4);
        }
    }
}
