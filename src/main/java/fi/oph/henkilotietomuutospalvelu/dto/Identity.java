package fi.oph.henkilotietomuutospalvelu.dto;

import java.util.Date;

public class Identity {
    private String ssn;
    private String oid;
    private Date vtjsynced;

    public Identity() {}

    public Identity(String ssn, String oid, Date vtjsynced) {
        this.ssn = ssn;
        this.oid = oid;
        this.vtjsynced = vtjsynced;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Date getVtjsynced() {
        return vtjsynced;
    }

    public void setVtjsynced(Date vtjsynced) {
        this.vtjsynced = vtjsynced;
    }

    public String toString() {
        return String.format("<ssn: %s, oid: %s, vtjsynced: %s>", ssn, oid, vtjsynced);
    }
}
