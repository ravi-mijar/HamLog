package com.hamlog.qso;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hamlog.user.HamOperator;


@Entity
@Table(name="qso")
public class QSO
{
    @Id
    @Column(name="qso_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int qsoID;
    
    public int getQsoID()
    {
        return qsoID;
    }

    public void setQsoID(int qsoID)
    {
        this.qsoID = qsoID;
    }
    @Column(name="qso_date_time")
    private Date qsoDateAndTime;
    
    @Column(name="with_ham")
    private HamOperator qsoWithHam;
    
    @Column(name="tx_power")
    private int qsoTxPower;
    
    @Column(name="rst")
    private int qsoRST;
    
    
    public QSO() {}
    
    public QSO(Date qsoDate, HamOperator withHam, int txPower, int rst)
    {
        super();
        this.qsoWithHam = withHam;
        this.qsoTxPower = txPower;
        this.qsoRST = rst;
        this.qsoDateAndTime = qsoDate;
    }
    
    
    public Date getQsoDateAndTime()
    {
        return qsoDateAndTime;
    }
    public void setQsoDateAndTime(Date qsoDateAndTime)
    {
        this.qsoDateAndTime = qsoDateAndTime;
    }
    public HamOperator getQsoWithHam()
    {
        return qsoWithHam;
    }
    public void setQsoWithHam(HamOperator qsoWithHam)
    {
        this.qsoWithHam = qsoWithHam;
    }
    public int getQsoTxPower()
    {
        return qsoTxPower;
    }
    public void setQsoTxPower(int qsoTxPower)
    {
        this.qsoTxPower = qsoTxPower;
    }
    public int getQsoRST()
    {
        return qsoRST;
    }
    public void setQsoRST(int qsoRST)
    {
        this.qsoRST = qsoRST;
    }
    
    
}
