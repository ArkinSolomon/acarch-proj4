package tomasulogui;

public interface IssuableUnit {
    boolean canAcceptIssue();
    void acceptIssue(IssuedInst issuedInstruction);
}
