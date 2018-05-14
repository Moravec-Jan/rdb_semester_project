package cz.tul.hbase.model;

public class DriverHbase {
    private String crp;
    private String rowId;
    private String jmeno;

    public DriverHbase(String crp, String rowId, String jmeno) {
        this.crp = crp;
        this.rowId = rowId;
        this.jmeno = jmeno;
    }

    public String getCrp() {
        return crp;
    }

    public void setCrp(String crp) {
        this.crp = crp;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getName() {
        return jmeno;
    }

}
