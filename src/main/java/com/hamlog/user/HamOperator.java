package com.hamlog.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ham_operator")
public class HamOperator implements Comparable<HamOperator>
{
    @Id
    @Column(name="call_sign")
    private String callSign;
    
    @Column(name="handle")
    private String handle;
    
    @Column(name="country")
    private String country;
    
    public HamOperator() {}
    
    /**
     * This constructor will mostly be used for dummy object creation purpose. 
     * @param callSign
     */
    public HamOperator(String callSign)
    {
        this.callSign = callSign;
        this.handle = null;
        this.country = null;
    }
    
    public HamOperator(String callSign, String handle, String country)
    {
        this.callSign = callSign;
        this.handle = handle;
        this.country = country;
    }
    
    public String getCallSign()
    {
        return callSign;
    }
    public boolean setCallSign(String callSign)
    {
        if(callSign.length() < 4) return false;
        this.callSign = callSign;
        return true;
    }
    public String getHandle()
    {
        return handle;
    }
    public void setHandle(String handle)
    {
        this.handle = handle;
    }
    public String getCountry()
    {
        return country;
    }
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    @Override
    public int compareTo(HamOperator o)
    {
        return (this.callSign.equalsIgnoreCase(o.callSign)) ? 0 : 1;
    }
    
}
