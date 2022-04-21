package recipesearch;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import recipesearch.RecipeSearchController;
import se.chalmers.ait.dat215.lab2.Recipe;

import java.io.IOException;

public class RecipeListItem extends AnchorPane {
    private RecipeSearchController parentController;
    private Recipe recipe;

    @FXML
    Label recipeName;
    @FXML
    ImageView recipeImage;
    @FXML
    ImageView cuisineImage;
    @FXML
    ImageView difficultyImage;
    @FXML
    ImageView mainIngredientImage;
    @FXML
    Label timeLabel;
    @FXML
    Label priceLabel;

    public RecipeListItem(Recipe recipe, RecipeSearchController recipeSearchController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("recipe_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.recipe = recipe;
        this.parentController = recipeSearchController;

        recipeName.setText(recipe.getName());
        recipeImage.setImage(recipe.getFXImage());

        cuisineImage.setImage(getCuisineImage(recipe.getCuisine()));
        difficultyImage.setImage(getDifficultyImage(recipe.getDifficulty()));
        mainIngredientImage.setImage(getMainIngredientImage(recipe.getMainIngredient()));

        timeLabel.setText(recipe.getTime() + " Minuter");
        priceLabel.setText(recipe.getPrice() + " Kr");

    }

    public Image getCuisineImage(String cuisine) {
        String iconPath = "";
        switch (cuisine) {
            case "Sverige":
                iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                break;
            case "Grekland":
                iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                break;
            case "Frankrike":
                iconPath = "RecipeSearch/resources/icon_flag_france.png";
                break;
            case "Indien":
                iconPath = "RecipeSearch/resources/icon_flag_india.png";
                break;
            case "Afrika":
                iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                break;
            case "Asien":
                iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                break;
        }
        return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
    }

    public Image getDifficultyImage(String difficulty) {
        String iconPath = "";
        switch (difficulty) {
            case "Lätt":
                iconPath = "RecipeSearch/resources/icon_difficulty_easy.png";
                break;
            case "Mellan":
                iconPath = "RecipeSearch/resources/icon_difficulty_medium.png";
                break;
            case "Svår":
                iconPath = "RecipeSearch/resources/icon_difficulty_hard.png";
                break;
        }
        return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
    }

    public Image getMainIngredientImage(String mainIngredient) {
        String iconPath = "";
        switch (mainIngredient) {
            case "Kött":
                iconPath = "RecipeSearch/resources/icon_main_beef.png";
                break;
            case "Fisk":
                iconPath = "RecipeSearch/resources/icon_main_fish.png";
                break;
            case "Kyckling":
                iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                break;
            case "Vegetarisk":
                iconPath = "RecipeSearch/resources/icon_main_veg.png";
                break;
        }
        return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
    }

        @FXML
    protected void onClick(Event event){
        parentController.openRecipeView(recipe);
    }
}
