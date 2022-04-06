package recipesearch;

import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;
import se.chalmers.ait.dat215.lab2.SearchFilter;

import java.util.ArrayList;
import java.util.List;

public class RecipeBackendController {
    private String cuisine;
    private String mainIngredient;
    private String difficulty;
    private int maxPrice;
    private int maxTime;
    private RecipeDatabase recipeDatabase = RecipeDatabase.getSharedInstance();


    public List<Recipe> getRecipes(){
        SearchFilter searchFilter = new SearchFilter(difficulty, maxTime, cuisine,maxPrice, mainIngredient);
        return new ArrayList<Recipe>(recipeDatabase.search(searchFilter));
    }

    public void setCuisine(String cuisine){
        this.cuisine = cuisine;
    }
    public void setMainIngredient(String mainIngredient){
        this.mainIngredient = mainIngredient;
    }
    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }
    public void setMaxPrice(int maxPrice){
        this.maxPrice = maxPrice;
    }
    public void setMaxTime(int maxTime){
        this.maxTime = maxTime;
    }
}
