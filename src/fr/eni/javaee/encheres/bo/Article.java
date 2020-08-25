package fr.eni.javaee.encheres.bo;

import java.time.LocalDate;

public class Article {
    private int noArticle, miseAPrix, prixVente;
    private String nomArticle, description, etatVente;
    private LocalDate dateDebutEncheres, dateFinEncheres;
    private Categorie categorie;
    private Utilisateur vendeur, acquereur;
    private boolean retraitEffectue;

    // CONSTRUCTORS

    public Article() {};

    public Article(String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres, Utilisateur vendeur, Categorie categorie) {
        setNomArticle(nomArticle);
        setDescription(description);
        setDateDebutEncheres(dateDebutEncheres == null ? LocalDate.now() : dateDebutEncheres);
        setDateFinEncheres(dateFinEncheres);
        setCategorie(categorie);
        setVendeur(vendeur);
        setRetraitEffectue(false); // Default value.
        setEtatVente();
    }

    public Article(String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres, int miseAPrix, Utilisateur vendeur, Categorie categorie) {
        this(nomArticle, description, dateDebutEncheres, dateFinEncheres, vendeur, categorie);
        setMiseAPrix(miseAPrix);
    }

    public Article(int noArticle, String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres, int miseAPrix, Utilisateur vendeur, Categorie categorie) {
        this(nomArticle, description, dateDebutEncheres, dateFinEncheres, miseAPrix, vendeur, categorie);
        setNoArticle(noArticle);
    }

    public Article (int noArticle, String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres , int miseAPrix, int prixVente, Utilisateur vendeur, Categorie categorie) {
        this(noArticle, nomArticle, description, dateDebutEncheres, dateFinEncheres, miseAPrix, vendeur, categorie);
        setPrixVente(prixVente);
    }

    public Article (int noArticle, String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres , int miseAPrix, int prixVente, Utilisateur vendeur, Utilisateur acquereur, Categorie categorie) {
        this(noArticle, nomArticle, description, dateDebutEncheres, dateFinEncheres, miseAPrix, prixVente, vendeur, categorie);
        setAcquereur(acquereur);
    }

    public Article (int noArticle, String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres , int miseAPrix, int prixVente, Utilisateur vendeur, Utilisateur acquereur, Categorie categorie, boolean retraitEffectue) {
        this(noArticle, nomArticle, description, dateDebutEncheres, dateFinEncheres, miseAPrix, prixVente, vendeur, acquereur, categorie);
        setRetraitEffectue(retraitEffectue);
    }

    public Article (String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres , int miseAPrix, int prixVente, Utilisateur vendeur, Utilisateur acquereur, Categorie categorie) {
        this(nomArticle, description, dateDebutEncheres, dateFinEncheres, vendeur, categorie);
        setMiseAPrix(miseAPrix);
        setPrixVente(prixVente);
        setAcquereur(acquereur);
    }


    // GETTERS & SETTERS

    public int getNoArticle() { return this.noArticle; }

    public void setNoArticle(int noArticle) { this.noArticle = noArticle; }

    public int getMiseAPrix() { return this.miseAPrix; }

    public void setMiseAPrix(int miseAPrix) { this.miseAPrix = miseAPrix; }

    public int getPrixVente() { return this.prixVente; }

    public void setPrixVente(int prixVente) { this.prixVente = prixVente; }

    public String getNomArticle() { return this.nomArticle; }

    public void setNomArticle(String nomArticle) { this.nomArticle = nomArticle; }

    public String getDescription() { return this.description; }

    public void setDescription(String description) { this.description = description; }

    public String getEtatVente() {
        setEtatVente();
        return this.etatVente;
    }

    /**
     * The status of a sale is automatically set according to the dates.
     */
    public void setEtatVente() {
        if (this.dateDebutEncheres.isAfter(LocalDate.now())) { this.etatVente = "Créée"; }
        else if (this.dateFinEncheres.isBefore(LocalDate.now())) {
            this.etatVente = this.retraitEffectue ? "Retrait effectué" : "Enchères terminées";
        } else { this.etatVente = "En cours"; }
    }

    public LocalDate getDateDebutEncheres() { return this.dateDebutEncheres; }

    public void setDateDebutEncheres(LocalDate dateDebutEncheres) { this.dateDebutEncheres = dateDebutEncheres; }

    public LocalDate getDateFinEncheres() { return this.dateFinEncheres; }

    public void setDateFinEncheres(LocalDate dateFinEncheres) { this.dateFinEncheres = dateFinEncheres; }

    public Categorie getCategorie() { return categorie; }

    public int getNoCategorie() { return this.categorie.getNoCategorie(); }

    public void setCategorie(Categorie categorie) { this.categorie = (Categorie) categorie; }

    public Utilisateur getVendeur() { return this.vendeur; }

    public int getNoVendeur() { return this.vendeur.getNoUtilisateur(); }

    public void setVendeur(Utilisateur vendeur) { this.vendeur = (Utilisateur) vendeur; }

    public Utilisateur getAcquereur() { return this.acquereur; }

    public int getNoAcquereur() { return this.acquereur.getNoUtilisateur(); }

    public void setAcquereur(Utilisateur acquereur) { this.acquereur = (Utilisateur) acquereur; }

    public boolean isRetraitEffectue() { return this.retraitEffectue; }

    public void setRetraitEffectue(boolean retraitEffectue) { this.retraitEffectue = retraitEffectue; }
}
