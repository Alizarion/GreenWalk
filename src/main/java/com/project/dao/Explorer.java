package com.project.dao;

import com.project.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: selim.bensenouci
 * Date: 13/09/13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class Explorer {

    private EntityManager em;

    public Explorer(EntityManager em) {
        this.em = em;
    }

    public List<Person> findAllPersons(){
        return  new ArrayList<Person>(this.em.createNamedQuery(Person.FIND_ALL).
                getResultList());
    }

    public Credential getCredentialByUserName(String userName){
        try{
            Credential credential = (Credential) em.createNamedQuery(
                    Credential.FIND_CREDENTIAL_BY_USERNAME).
                    setParameter("query",userName).
                    getSingleResult();
            // credential.getAccountEagerly();
            credential.getAccount().loadFollowEagerly();
            return credential;
        } catch (NoResultException e){
            return null;
        }
    }


    public Account findAccountByName(String name){
        return (Account) em.createNamedQuery(
                Account.FIND_ACCOUNT_BY_NAME).
                setParameter("name", name).getSingleResult();
    }

    public List<Waste> getAllAvailableWastes(){
        try{
            List<Waste> wastes = new ArrayList<Waste>(em.createNamedQuery(
                    Waste.ALL_WASTES).
                    getResultList());
            return wastes;
        } catch (NoResultException e){
            return null;
        }
    }


    public List<SingleEvent> findLastSingleEvents() {
        try{
            List<SingleEvent> events = new ArrayList<SingleEvent>(em.createNamedQuery(
                    SingleEvent.FIND_ALL).
                    getResultList());
            return events;
        } catch (NoResultException e){
            return null;
        }    }
}
