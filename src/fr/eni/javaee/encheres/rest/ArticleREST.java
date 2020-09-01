package fr.eni.javaee.encheres.rest;

import fr.eni.javaee.encheres.EException;
import fr.eni.javaee.encheres.bll.ArticleManager;
import fr.eni.javaee.encheres.bll.CategorieManager;
import fr.eni.javaee.encheres.bll.UtilisateurManager;
import fr.eni.javaee.encheres.bo.Article;
import fr.eni.javaee.encheres.bo.Categorie;

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
    public Object create(Map<String, Object> article) {
        try {
            Article newArticle = generateNewArticle(article);
            return new ArticleManager().add(newArticle);
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
            @QueryParam("saleIsWon") boolean saleIsWon
    )  {
        try {
            ArticleManager articleManager = new ArticleManager();
            if (categorie.isEmpty()) { categorie = null; }
            List<Article> articles = articleManager.getArticlesLike(userSearch, categorie);
            List<Article> wonArticles = new ArrayList<>();
            if (saleIsWon) {
                int acquereur = (int) request.getSession().getAttribute("noUtilisateur");
                wonArticles = articleManager.filterByAcquereur(articles, acquereur);
            }
            if (saleIsOpen || isCurrentUser) { articles = articleManager.filterByEtat(articles, "En cours"); }
            if (isCurrentUser) {
                int encherisseur = (int) request.getSession().getAttribute("noUtilisateur");
                articles = articleManager.filterByEncherisseur(articles, encherisseur);
            }
            return Stream.of(articles, wonArticles)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
        }
        catch (EException eException) {
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
            @QueryParam("saleIsOver") boolean saleIsOver
    ) {
        try {
            ArticleManager articleManager = new ArticleManager();
            int vendeur = (int) request.getSession().getAttribute("noUtilisateur");
            if (categorie.equals("null")) { categorie = null; }
            List<Article> articles = articleManager.filterByVendeur(articleManager.getArticlesLike(userSearch, categorie), vendeur);
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

    public Article generateNewArticle(Map<String, Object> article) throws EException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Article newArticle = new Article(
                (String) article.get("nomArticle"),
                (String) article.get("description"),
                LocalDateTime.parse((String) article.get("dateDebutEncheres"), formatter),
                LocalDateTime.parse((String) article.get("dateFinEncheres"), formatter),
                new UtilisateurManager().getById((int) article.get("vendeur")));
        if (article.get("categorie") != null) {
            Categorie categorie = new CategorieManager().getById((int) article.get("categorie"));
            newArticle.setCategorie(categorie);
        }
        return newArticle;
    }

}
