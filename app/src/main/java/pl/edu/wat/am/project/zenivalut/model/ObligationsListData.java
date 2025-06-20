package pl.edu.wat.am.project.zenivalut.model;

import java.util.List;

public class ObligationsListData {
    List<ObligationData> paidObligations;
    List<ObligationData> unpaidObligations;

    public ObligationsListData() {}

    public ObligationsListData(List<ObligationData> paidObligations, List<ObligationData> unpaidObligations) {
        this.paidObligations = paidObligations;
        this.unpaidObligations = unpaidObligations;
    }

    public List<ObligationData> getPaidObligations() {
        return paidObligations;
    }

    public void setPaidObligations(List<ObligationData> paidObligations) {
        this.paidObligations = paidObligations;
    }

    public List<ObligationData> getUnpaidObligations() {
        return unpaidObligations;
    }

    public void setUnpaidObligations(List<ObligationData> unpaidObligations) {
        this.unpaidObligations = unpaidObligations;
    }
}
