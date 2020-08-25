package fr.eni.javaee.encheres.dal.jdbc;

import fr.eni.javaee.encheres.EException;
import fr.eni.javaee.encheres.bo.Utilisateur;

import java.util.*;

public class UtilisateurJDBCDAOImpl extends GenericJDBCDAOImpl<Utilisateur> {

    public UtilisateurJDBCDAOImpl() throws EException {
        super();
    }

    @Override
    protected void setIdentifiers() {
        this.identifiers = new ArrayList<>();
        this.identifiers.add("noUtilisateur");
    }

    @Override
    protected void setFields() {
        this.fields = new LinkedHashMap<String, String>() {{
            put("noUtilisateur", "int");
            put("pseudo", "String");
            put("nom", "String");
            put("prenom", "String");
            put("email", "String");
            put("telephone", "String");
            put("rue", "String");
            put("codePostal", "String");
            put("ville", "String");
            put("motDePasse", "String");
            put("credit", "int");
            put("administrateur", "byte");
        }};
    }

    protected Utilisateur getObject() {
        return new Utilisateur();
    }
}