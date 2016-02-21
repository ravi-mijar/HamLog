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
        
    }
    
    public static SessionFactory getSessionFactory()
    {
        if(sessionFactory == null)
            sessionFactory = new Configuration().configure().buildSessionFactory();
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
        HamOperator existingHam = searchHam(withCallSign);
        if(existingHam != null)
        {
            dummy = existingHam;
        }
        else
        {
            dummy.setCountry("India");
            dummy.setHandle(withCallSign);
            
            //find a way to generate another set of text fields on UI to fetch the handle from the user.
            //dummy.setHandle(handle);
        }
        Date dateObj = null;
        dateObj = DateFormat.getDateInstance().parse(date);
        System.out.println(dateObj);
        
        session.beginTransaction();
        session.saveOrUpdate(new QSO(dateObj, dummy, txPower, rst));
        session.getTransaction().commit();
        
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
            ArrayList<HamOperator> qso = new ArrayList<HamOperator>(session.createQuery("from QSO where QSO.qsoWithHam='"+withCallSign+"'").list());
            if(qso.size() == 0)
            {
                System.out.println("No QSOs found with this Ham.");
            }
            else System.out.println(qso);
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
            
            session.persist(newHam);
            
            session.getTransaction().commit();
        }
        else 
        {
            System.out.println("Ham with callsign : " + callSign + " already exists.");
            return false;
        }
        
        return true;
    }
    
    @Command
    public HamOperator searchHam(String callSign)
    {
        HamOperator dummy = new HamOperator(callSign);
        //check whether we already have a HamOperator instance.
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        ArrayList<HamOperator> hams = new ArrayList<HamOperator>(session.createQuery("from HamOperator").list());
        System.out.println("hams:" + hams);
        int pos = hams.indexOf(dummy); 
        System.out.println("pos:" + pos);
        if(pos == 0)
        {
            dummy = hams.get(pos);
            System.out.println("dummy:" + dummy);
        }
        else dummy = null;
        return dummy;
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
        System.out.println("am I here early?");
        if(!HamLog.getSessionFactory().isClosed())
        {
            HamLog.getSessionFactory().getCurrentSession().close();
            HamLog.getSessionFactory().close();
        }
        instance = null;
    }
}
