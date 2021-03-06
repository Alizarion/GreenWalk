package com.project.entities;

import com.project.Helper;

import javax.persistence.*;
import java.util.Date;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(schema= Helper.ENTITIES_CATALOG, name="garbage")
@NamedQueries({
        @NamedQuery(name= WasteGarbage.ALL_GARBAGES,
                hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"),
                query="select wg "
                        + " from WasteGarbage wg ")})
public class WasteGarbage  {

    public static final String ALL_GARBAGES= "ALL_GARBAGES";

    @Id
    @TableGenerator(name="WasteGarbage_SEQ", table="sequence", catalog = Helper.ENTITIES_CATALOG,
            pkColumnName="SEQ_NAME", valueColumnName="SEQ_COUNT")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="WasteGarbage_SEQ")
    @Column
    private Long id;

    @Column
    private Long quantity;

    @ManyToOne
    private Waste waste;

    @ManyToOne
    private Account wasteDeclaring;

    public WasteGarbage( ) {
    }

    public WasteGarbage(Waste waste) {
        this.waste = waste;
        this.quantity = 1L;
    }


    public WasteGarbage(Waste waste,Account wasteDeclaring) {
        this.waste = waste;
        this.wasteDeclaring = wasteDeclaring;
        this.quantity = 1L;
    }

    public Waste getWaste() {
        return waste;
    }

    public void setWaste(Waste waste) {
        this.waste = waste;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getWasteDeclaring() {
        return wasteDeclaring;
    }

    public void setWasteDeclaring(Account wasteDeclaring) {
        this.wasteDeclaring = wasteDeclaring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WasteGarbage)) return false;

        WasteGarbage garbage = (WasteGarbage) o;

        if (waste != null ? !waste.equals(garbage.waste) : garbage.waste != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "{type:'waste',value:" + this.quantity +", name:'"+
                this.waste.getLabelProperty()+"',x:"+((Math.random()*150)+10) +
                ",y:-"+(Math.random()*600) +"}";
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (waste != null ? waste.hashCode() : 0);
        return result;
    }
}
