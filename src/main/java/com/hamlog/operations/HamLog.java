package com.hamlog.operations;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import asg.cliche.Command;
import asg.cliche.ShellFactory;

import com.hamlog.qso.QSO;
import com.hamlog.user.HamOperator;

public class HamLog
{
    private static HamLog instance = null;
    private static SessionFactory sessionFactory = null;
    private HamLog()
    {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }
    
    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
    
    public static HamLog getAppInstance() 
    {
        if(instance == null)
        {
            instance = new HamLog();
        }
        return instance;
    }
    
    @Command(description="adds a QSO(call) entry.")
    public boolean addQSO(String withCallSign, String date, int txPower, int rst) throws ParseException
    {
        System.out.println("addQSO");
        System.out.println("withCallSign:" + withCallSign);
        System.out.println("date:" + date);
        System.out.println("txP:" + txPower);
        System.out.println("rst:" + rst);
        
        HamOperator dummy = new HamOperator(withCallSign);
        //check whether we already have a HamOperator instance.
        Session session = getSessionFactory().getCurrentSession();
        /*
         * A org.hibernate.Session begins when the first call to getCurrentSession() is made for the current thread. 
         * It is then bound by Hibernate to the current thread. When the transaction ends, either through commit or 
         * rollback, Hibernate automatically unbinds the org.hibernate.Session from the thread and closes it for you. 
         * If you call getCurrentSession() again, you get a new org.hibernate.Session and can start a new unit of work. 
         */
        session.beginTransaction();
        ArrayList<HamOperator> hams = new ArrayList(session.createQuery("select * from ham_operator").list());
        int pos; 
        if((pos = hams.indexOf(dummy)) != -1)
        {
            dummy = hams.get(pos);
        }
        else
        {
            dummy.setCountry("India");
            dummy.setHandle(withCallSign);
            session.save(dummy);
            
            //find a way to generate another set of text fields on UI to fetch the handle from the user.
            //dummy.setHandle(handle);
        }
        Date dateObj = null;
        dateObj = DateFormat.getDateInstance().parse(date);
        System.out.println(dateObj);
        
        session.save(new QSO(dateObj, dummy, txPower, rst));
        session.close();
        
        return true;
    }
    
    @Command(description="lists the QSOs")
    public boolean listQSOs(String withCallSign)
    {
        System.out.println("listQSO");
        System.out.println("withCallSign:" + withCallSign);
        
        HamOperator existingHam = searchHam(withCallSign);
        if(existingHam == null)
        {
            //error - ham with this call sign doesn't exist.
            System.out.println("No record for this Ham found in the database. Try adding a QSO with him / her first.");
            return true;
        }
        else {
            Session session = getSessionFactory().getCurrentSession();
            session.beginTransaction();
            ArrayList<HamOperator> qso = new ArrayList<HamOperator>(session.createQuery("select * from qso where qso.with_call_sign='"+withCallSign+"'").list());
            if(qso.size() == 0)
            {
                System.out.println("No QSOs found with this Ham.");
            }
            else System.out.println(qso);
            session.close();
        }
        return true;
    }
    
    @Command(description="add a new ham operator")
    public boolean addHamOperator(String callSign, String handle, String country)
    {
        if(searchHam(callSign) == null) 
        {
            Session session = getSessionFactory().getCurrentSession();
            session.beginTransaction();
            HamOperator newHam = new HamOperator(callSign, handle, country);
            session.save(newHam);
            session.close();
        }
        else 
        {
            System.out.println("Ham with callsign : " + callSign + " already exists.");
            return false;
        }
        
        return true;
    }
    
    protected HamOperator searchHam(String callSign)
    {
        HamOperator dummy = new HamOperator(callSign);
        //check whether we already have a HamOperator instance.
        Session session = getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        ArrayList<HamOperator> hams = new ArrayList<HamOperator>(session.createQuery("select from ham_operator").list());
        int pos; 
        if((pos = hams.indexOf(dummy)) != 0)
        {
            dummy = hams.get(pos);
            session.close();
            return dummy;
        }
        session.close();
        return null;
    }

    public static void main(String args[])
    {
        HamLog instance = HamLog.getAppInstance();
        
        try {
            ShellFactory.createConsoleShell("ham-log", "ham-log", instance).commandLoop();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
