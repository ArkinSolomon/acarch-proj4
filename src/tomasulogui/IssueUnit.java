package tomasulogui;

public class IssueUnit {
    PipelineSimulator simulator;
    IssuedInst issuedInstruction;
    IssuableUnit fu;

    public IssueUnit(PipelineSimulator sim) {
        simulator = sim;
    }

    public void execCycle() {
        int currPc = simulator.getPC();
        Instruction inst = simulator.getMemory().getInstAtAddr(currPc);

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
            if (issuedInstruction.regSrc1 == 0) {
                issuedInstruction.regSrc1Value = 0;
                issuedInstruction.regSrc1Valid = true;
            } else {
                issuedInstruction.regSrc1Tag = simulator.getROB().getTagForReg(issuedInstruction.regSrc1);
            }
        }

        if (issuedInstruction.regSrc2Used) {
            System.out.println("src 2 used");
            if (issuedInstruction.regSrc2 == 0) {
                issuedInstruction.regSrc2Value = 0;
                issuedInstruction.regSrc2Valid = true;
            } else {
                issuedInstruction.regSrc2Tag = simulator.getROB().getTagForReg(issuedInstruction.regSrc2);
            }
        }

        if (issuedInstruction.regDestUsed) {
            System.out.println("Reg dest: " + issuedInstruction.regDest);
            issuedInstruction.regDestTag = simulator.getROB().getTagForReg(issuedInstruction.regDest);
        }

        if (fu.canAcceptIssue()) {
            simulator.getROB().updateInstForIssue(issuedInstruction);
            fu.acceptIssue(issuedInstruction);
            System.out.println("Instruction accepted");
            simulator.setPC(currPc + 4);
        }
    }
}
