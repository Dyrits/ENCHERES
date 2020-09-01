package fr.eni.javaee.encheres.rest;

import fr.eni.javaee.encheres.EException;
import fr.eni.javaee.encheres.bll.ArticleManager;
import fr.eni.javaee.encheres.bll.CategorieManager;
import fr.eni.javaee.encheres.bll.RetraitManager;
import fr.eni.javaee.encheres.bll.UtilisateurManager;
import fr.eni.javaee.encheres.bo.Article;
import fr.eni.javaee.encheres.bo.Retrait;
import fr.eni.javaee.encheres.bo.Utilisateur;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/article")
public class ArticleREST {
    @Context
    private HttpServletRequest request;

    @GET
    @Path("/{identifier: \\d+}")
    public Object selectById(@PathParam("identifier") int identifier)  {
        try { return new ArticleManager().getById(identifier); }
        catch (EException eException) {
            eException.printStackTrace();
            return eException;
        }
    }

    @POST
    @Path("/new")
    public Object create(Map<String, Object> data, @CookieParam("CookieIDUtilisateur") String identifier) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            int noVendeur = identifier != null ?
                    Integer.parseInt(identifier) :
                    (int) request.getSession().getAttribute("noUtilisateur");
            Utilisateur vendeur = new UtilisateurManager().getById(noVendeur);
            Article newArticle = new Article(
                    (String) data.get("nomArticle"),
                    (String) data.get("description"),
                    LocalDateTime.parse((String) data.get("dateDebutEncheres"), formatter),
                    LocalDateTime.parse((String) data.get("dateFinEncheres"), formatter),
                    (int) data.get("miseAPrix"),
                    vendeur,
                    data.get("categorie") != null ? new CategorieManager().getById((int) data.get("categorie")) : null
            );
            Article article = new ArticleManager().add(newArticle);
            // The instance of retrait is automatically added. It is updated if the address data have been modified.
            updateRetrait(vendeur, article, data);
            return article;
        } catch (EException eException) {
            eException.printStackTrace();
            return eException;
        }
    }

    @GET
    @Path("/buy")
    public Object searchArticles(
            @QueryParam("userSearch") String userSearch,
            @QueryParam("categorie") String categorie,
            @QueryParam("saleIsOpen") boolean saleIsOpen,
            @QueryParam("isCurrentUser") boolean isCurrentUser,
            @QueryParam("saleIsWon") boolean saleIsWon,
            @CookieParam("CookieIDUtilisateur") String identifier
    )  {
        try {
            ArticleManager articleManager = new ArticleManager();
            if (categorie.isEmpty()) { categorie = null; }
            List<Article> articles = articleManager.getArticlesLike(userSearch, categorie);
            List<Article> wonArticles = new ArrayList<>();
            if (saleIsOpen || isCurrentUser || saleIsWon) {
                int noUtilisateur = identifier != null ?
                        Integer.parseInt(identifier) :
                        (int) request.getSession().getAttribute("noUtilisateur");
                if (saleIsWon) { wonArticles = articleManager.filterByAcquereur(articles, noUtilisateur); }
                if (saleIsOpen || isCurrentUser) { articles = articleManager.filterByEtat(articles, "En cours"); }
                if (isCurrentUser) { articles = articleManager.filterByEncherisseur(articles, noUtilisateur); }
            }
            return Stream.of(articles, wonArticles)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (EException eException) {
            eException.printStackTrace();
            return eException;
        }
    }

    @GET
    @Path("/sell")
    public Object searchSales(
            @QueryParam("userSearch") String userSearch,
            @QueryParam("categorie") String categorie,
            @QueryParam("saleIsOpen") boolean saleIsOpen,
            @QueryParam("saleIsCreated") boolean saleIsCreated,
            @QueryParam("saleIsOver") boolean saleIsOver,
            @CookieParam("CookieIDUtilisateur") String identifier
    ) {
        try {
            ArticleManager articleManager = new ArticleManager();
            int noVendeur = identifier != null ?
                    Integer.parseInt(identifier) :
                    (int) request.getSession().getAttribute("noUtilisateur");
            if (categorie.equals("null")) { categorie = null; }
            List<Article> articles = articleManager.filterByVendeur(articleManager.getArticlesLike(userSearch, categorie), noVendeur);
            List<Article> openArticles = new ArrayList<>();
            List<Article> createdArticles = new ArrayList<>();
            if (saleIsOpen) { openArticles = articleManager.filterByEtat(articles, "En cours"); }
            if (saleIsCreated) { createdArticles = articleManager.filterByEtat(articles,"Créée"); }
            if (saleIsOver) { articles = articleManager.filterByIsOver(articles); }
            return Stream.of(articles, openArticles, createdArticles)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (EException eException) {
            eException.printStackTrace();
            return eException;
        }
    }

    @GET
    @Path("/refresh")
    public Object refresh() {
        try {
            ArticleManager articleManager = new ArticleManager();
            articleManager.setAllArticlesObtenus();
            return articleManager.getTimeUntilNextEnd();
        } catch (EException eException) {
            eException.printStackTrace();
            return eException;
        }
    }

    private void updateRetrait(Utilisateur vendeur, Article article, Map<String, Object> data) throws EException {
        String rue, codePostal, ville;
        boolean modifyRue = !(rue = (String) data.get("rue")).equals(vendeur.getRue());
        boolean modifyCodePostal = !(codePostal = (String) data.get("codePostal")).equals(vendeur.getCodePostal());
        boolean modifyVille = !(ville = (String) data.get("ville")).equals(vendeur.getVille());
        if (modifyRue || modifyCodePostal || modifyVille) {
            RetraitManager retraitManager = new RetraitManager();
            Retrait retrait = retraitManager.getById(article.getNoArticle());
            retrait.setRue(rue);
            retrait.setCodePostal(codePostal);
            retrait.setVille(ville);
            retraitManager.update(retrait);
        }
    }

}
