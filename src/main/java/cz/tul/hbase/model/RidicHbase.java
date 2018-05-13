package cz.tul.hbase.model;

public class RidicHbase {
    private String crp;
    private String rowId;
    private String jmeno;

    public RidicHbase(String crp, String rowId, String jmeno) {
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

    public String getJmeno() {
        return jmeno;
    }

}
