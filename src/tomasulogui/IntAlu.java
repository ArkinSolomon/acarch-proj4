package tomasulogui;

public class IntAlu extends FunctionalUnit{
  public static final int EXEC_CYCLES = 1;

  public IntAlu(PipelineSimulator sim) {
    super(sim);
  }


  public int calculateResult(int station) {
    int result = 0;
    switch (stations[station].function) {
      case ADD:
        result = stations[station].data1 + stations[station].data2;
        break;
      case SUB:
        result = stations[station].data1 - stations[station].data2;
        break;
      case AND:
        result = stations[station].data1 & stations[station].data2;
        break;
      case OR:
        result = stations[station].data1 | stations[station].data2;
        break;
      case XOR:
        result = stations[station].data1 ^ stations[station].data2;
        break;
      case ADDI:
        result = stations[station].data1 + stations[station].data2;
        break;
      case ANDI:
        result = stations[station].data1 & stations[station].data2;
        break;
      case ORI:
        result = stations[station].data1 | stations[station].data2;
        break;
      case XORI:
        result = stations[station].data1 ^ stations[station].data2;
        break;
      case SLL:
        result = stations[station].data1 << stations[station].data2;
        break;
      case SRL:
        result = stations[station].data1 >> stations[station].data2;
        break;
      case SRA:
        result = stations[station].data1 >>> stations[station].data2;
        break;
    }
    return result;
  }

  public int getExecCycles() {
    return EXEC_CYCLES;
  }
}
