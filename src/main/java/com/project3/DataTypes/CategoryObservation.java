package com.project3.DataTypes;

import jakarta.persistence.*;

@Entity
public class CategoryObservation extends Observation {

    @ManyToOne
    private Phenomenon phenomenon;

    @Enumerated(EnumType.STRING)
    private Presence presence;

    public Phenomenon getPhenomenon() { return phenomenon; }
    public void setPhenomenon(Phenomenon phenomenon) { this.phenomenon = phenomenon; }

    public Presence getPresence() { return presence; }
    public void setPresence(Presence presence) { this.presence = presence; }
}
