package fr.eni.javaee.encheres.rest;

import fr.eni.javaee.encheres.EException;
import fr.eni.javaee.encheres.bo.Article;
import fr.eni.javaee.encheres.bo.Utilisateur;
import fr.eni.javaee.encheres.dal.jdbc.ArticleJDBCDAOImpl;
import fr.eni.javaee.encheres.dal.jdbc.UtilisateurJDBCDAOImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/utilisateur")
public class APIUtilisateurTest {


    @GET
    @Path("/{identifier: \\d+}")
    public Utilisateur selectById(@PathParam("identifier") int identifier)  {
        try {
            return new UtilisateurJDBCDAOImpl().selectById(identifier);
        } catch (EException eException) {
            eException.printStackTrace();
        }
        return null;
    }
}
