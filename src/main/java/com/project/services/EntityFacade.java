package com.project.services;

import com.project.comingsoon.MailingList;
import com.project.dao.Explorer;
import com.project.entities.*;
import com.project.entities.notifications.Notification;
import com.project.services.mail.MailSender;
import com.project.services.mail.RegisterEmail;
import com.project.services.mail.SMTPEmailProvider;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Entity facade Service used for fonctional application
 * @author Selim.Bensenouci
 */
@Named(value = EntityFacade.EF_NAME)
@Stateless
@TransactionAttribute
public class EntityFacade implements Serializable{

    private final static Logger LOG = Logger.getLogger(EntityFacade.class);

    public static final String EF_NAME = "entityFacade";

    @PersistenceContext
    EntityManager em;

    private Explorer explorer;

    @PostConstruct
    protected void postConstruct() {
        LOG.debug("PostConstruct " + this);
        this.explorer = new Explorer(this.em);
    }


    /**
     * Method to merge person with the PersistantContext
     * @param person to merge
     * @return
     */
    public Person createPerson(Person person){
        return this.em.merge(person);
    }


    /**
     * Method to delete person from the dataBase
     * @param person to delete
     */
    public void deletePerson(Person person){
        this.em.remove(person);
    }

    /**
     * Simple method to find person from this PersistantContext
     * @param id of the wanted person
     */
    public void findPerson(Long id){
        this.em.find(Person.class,id);
    }

    public Account findAccountById(Long id){
        return   this.em.find(Account.class, id);
    }


    public List<Person> findAllPersons(){
        return this.explorer.findAllPersons();
    }

    public Account getActiveUser(){
        Account account = null;
        if(FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null){
            Credential credential = getCredentialByUserName(FacesContext.
                    getCurrentInstance().getExternalContext().getUserPrincipal().getName());
            account = credential.getAccount();
        }
        return account;
    }


    public Account findAccountByEmail(String email){
        try{
            return (Account) em.createNamedQuery(
                    Account.FIND_ACCOUNT_BY_EMAIL).
                    setParameter("email", email).getSingleResult();
        }
        catch (NoResultException e ){
            return null;
        }
    }

    public void createAccount(Account account){


        account.getCredential().setKeyGen(account.getFirstName().hashCode()
                + '-' + account.getLastName().hashCode() +
                "-" + account.getCredential().getUserName().hashCode());
        account.getCredential().setState(CredentialsState.P);
        em.persist(account);
        account = em.merge(account);
        em.flush();
        em.clear();

        //TODO change for production Email
        EmailCounter emailCounter =  new EmailCounter(account.getEmailAddress(),
                "inscription",new Date());

        mergeEmailCounter(emailCounter);
        MailSender sender = new MailSender(new SMTPEmailProvider());
        sender.sendEmail(new RegisterEmail(account));
    }

    public Credential getCredentialByUserName(String userName){
        return   this.explorer.getCredentialByUserName(userName);
    }

    public Account findAccountByName(String name){
        return this.explorer.findAccountByName(name);
    }

    public Account mergeAccount(Account account){

        return em.merge(account);
    }


    public void mergeEmailCounter(EmailCounter emailCounter){
        em.merge(emailCounter);
    }

    public ImageContent addNewTemporaryImageContent(ImageContent content) {
        content = em.merge(content);
        em.flush();
        em.clear();
        return content;

    }

    public UploadedFile getUploadedFileById(Long id){
        return em.find(UploadedFile.class, id);

    }

    public void updateUploadedFile(UploadedFile contentFile) {
        em.merge(contentFile);
    }


    public void registerFromComingSoon(MailingList mailingList){
        em.merge(mailingList);
    }

    public List<Waste> getAllAvailableWastes(){
        return this.explorer.getAllAvailableWastes();
    }

    public void addNewSingleEvent(SingleEvent selectedEvent) {
        this.em.flush();
        this.em.clear();
        this.em.merge(selectedEvent);
    }

    public Event mergeEvent(Event event) {
        this.em.flush();
        this.em.clear();
        event = this.em.merge(event);
        this.em.refresh(event);
        return event;
    }

    public List<SingleEvent> findLastSingleEvents() {
        return this.explorer.findLastSingleEvents();
    }

    public List<GroupEvent> findLastGroupEvents() {
        return this.explorer.findLastGroupEvents();
    }


    public Event findEventById(long id) {
        return em.find(Event.class,id);
    }

    public void mergeComment(Comment newComment) {
        newComment=em.merge(newComment);
        notify(newComment.pushNotifications());
    }

    public List<Account> searchMembers(String query) {
        return this.explorer.findAccountByKeyWord(query);
    }

    public void submitGroupEvent(GroupEvent event) {
        event =  this.em.merge(event);
        notify(event.pushNotifications());

    }

    public void notify(List<Notification> notifications){
        for (Notification notification : notifications){
            this.em.merge(notification);
        }
    }
}
