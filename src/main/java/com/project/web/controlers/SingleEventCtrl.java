package com.project.web.controlers;

import com.project.entities.*;
import com.project.services.EntityFacade;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author selim@openlinux.fr.
 */
@ManagedBean
@ViewScoped
public class SingleEventCtrl implements Serializable {

    private final static Logger LOG = Logger.getLogger(SingleEventCtrl.class);

    @Inject
    EntityFacade facade;

    private SingleEvent selectedEvent;

    private List<ImageContent> imageContents = new ArrayList<ImageContent>();

    @Inject
    SessionAttributeCtrl sessionAttribute;

    private List<Waste> wasteList;

    private List<WasteGarbage> wasteGarbages;

    private WasteGarbage selectedWasteGarbage;

    private Account userAccount;

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("lang");

    private Waste selectedWaste;

    @PostConstruct
    private void postInit(){
        LOG.info("SingleEventCtrl : PostConstruct");
        FacesContext.getCurrentInstance().getViewRoot().getAttributes().put(
                EntityFacade.EF_NAME, this.facade);
        this.selectedEvent = new SingleEvent();
        this.userAccount = facade.findActiveUserWithSigneEventList();
        this.selectedEvent.setOwner(this.userAccount);
        this.wasteList = this.facade.getAllAvailableWastes();
    }

    public SingleEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(SingleEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public void initNewSingleEvent(){
        this.selectedEvent = new SingleEvent();
        this.selectedEvent.setOwner(this.userAccount);
    }


    public void fileUploadListenerImageContent(FileUploadEvent event)
            throws JAXBException, IOException, CloneNotSupportedException {
        ImageContent tempImageContent = new ImageContent() ;

        tempImageContent.setImage(null);
        if(tempImageContent.getId() == null){
            tempImageContent = facade.addNewTemporaryImageContent(tempImageContent);
        }

        ImageContentFile file = new ImageContentFile(event.getFile().getFileName());

        tempImageContent.setImage(file);
        tempImageContent = facade.addNewTemporaryImageContent(tempImageContent);
        tempImageContent.getFile().writeFile(event.getFile().getInputstream());
        this.imageContents.add(tempImageContent);
    }

    public List<ImageContent> getImageContents() {
        return imageContents;
    }

    public void setImageContents(List<ImageContent> imageContents) {
        this.imageContents = imageContents;
    }

    public void handleFileUpload(FileUploadEvent event) {

        FacesMessage msg = new FacesMessage("Succesful",
                event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<Waste> getWasteList() {
        return wasteList;
    }

    public void setWasteList(List<Waste> wasteList) {
        this.wasteList = wasteList;
    }

    public Waste getSelectedWaste() {
        return selectedWaste;
    }

    public Account getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Account userAccount) {
        this.userAccount = userAccount;
    }

    public void setSelectedWaste(Waste selectedWaste) {
        this.selectedWaste = selectedWaste;
    }

    public void addGarbage(){
        this.selectedWasteGarbage = new WasteGarbage(this.selectedWaste,this.userAccount);
    }

    public void addGarbageToEvent(){
        this.selectedEvent.addNewGarbage(this.selectedWasteGarbage);
      //  this.selectedWasteGarbage = null;
        final RequestContext requestContext =
                RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("isValid", true);
    }


    public WasteGarbage getSelectedWasteGarbage() {
        return selectedWasteGarbage;
    }

    public void setSelectedWasteGarbage(WasteGarbage selectedWasteGarbage) {
        this.selectedWasteGarbage = selectedWasteGarbage;
    }

    public void submitEvent(){
        this.selectedEvent.addAllContents(new HashSet<Content>(this.imageContents));
        final RequestContext requestContext =
                RequestContext.getCurrentInstance();
        if (this.selectedEvent.getGarbageAsList().size()==0){
            FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error",
                            resourceBundle.getString("form.single.event.error.no.wastes-"+
                                    sessionAttribute.getSelectedLang().getKey()));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            requestContext.addCallbackParam("isValid", false);
        }  else {
            facade.addNewSingleEvent(this.selectedEvent);
            requestContext.addCallbackParam("isValid", true);
        }


    }
}
