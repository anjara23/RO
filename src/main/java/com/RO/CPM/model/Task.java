package com.RO.CPM.model;

import jakarta.validation.constraints.*;
import java.util.List;
public class Task {

    @NotBlank(message ="Le nom de la tâche est requis")
    private String nom;

    @Min(value = 1, message = "La durée doit être au moins 1")
    private int duree;

    private List<String> preced;
    private List<String> succ;

    private int dateTot;
    private int dateTard;
    private int marge;

    public Task() {}

    public Task(String nom, int duree, List<String> preced) {
        this.nom = nom;
        this.duree = duree;
        this.preced = preced;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom;}

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public List<String> getPreced() { return preced; }
    public void setPreced(List<String> preced) { this.preced = preced; }

    public List<String> getSucc() { return succ; }
    public void setSucc(List<String> succ) { this.succ = succ; }

    public int getDateTot() { return dateTot; }
    public void setDateTot(int dateTot) { this.dateTot = dateTot; }

    public int getDateTard() { return dateTard; }
    public void setDateTard(int dateTard) { this.dateTard = dateTard;}

    public int getMarge() { return marge; }
    public void setMarge(int marge) { this.marge = marge; }
}
