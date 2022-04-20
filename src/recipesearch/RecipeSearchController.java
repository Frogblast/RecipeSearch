
package recipesearch;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;




public class RecipeSearchController implements Initializable {

    @FXML
    FlowPane recipeListFlowPane;
    @FXML
    ComboBox mainIngredientComboBox;
    @FXML
    ComboBox cuisineComboBox;
    @FXML
    RadioButton allRadioButton;
    @FXML
    RadioButton easyRadioButton;
    @FXML
    RadioButton mediumRadioButton;
    @FXML
    RadioButton hardRadioButton;
    @FXML
    Spinner maxPriceSpinner;
    @FXML
    Slider maxTimeSlider;
    @FXML
    Label minutesLabel;
    @FXML
    Label panelLabel;
    @FXML
    ImageView panelImage;
    @FXML
    Button panelExit;
    @FXML
    AnchorPane recipeDetailPane;
    @FXML
    SplitPane recipeSearchPane;

    ToggleGroup difficultyToggleGroup;

    RecipeDatabase db = RecipeDatabase.getSharedInstance();

    RecipeBackendController recipeBackendController = new RecipeBackendController();

    private Map<String, RecipeListItem> recipeListItemMap = new HashMap<String, RecipeListItem>();


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        for (Recipe recipe : recipeBackendController.getRecipes()) {
            RecipeListItem recipeListItem = new RecipeListItem(recipe, this);
            recipeListItemMap.put(recipe.getName(), recipeListItem);
        }

        updateRecipeList();
        mainIngredientComboBox.getItems().addAll("Visa alla", "Kött", "Fisk", "Kyckling", "Vegetarisk");
        mainIngredientComboBox.getSelectionModel().select("Visa alla");
        mainIngredientComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                recipeBackendController.setMainIngredient(newValue);
                updateRecipeList();
            }
        });

        cuisineComboBox.getItems().addAll("Visa alla", "Frankrike", "Sverige", "Indien", "Asien", "Grekland", "Afrika");
        cuisineComboBox.getSelectionModel().select("Visa alla");
        cuisineComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                recipeBackendController.setCuisine(newValue);
                updateRecipeList();
            }
        });

        difficultyToggleGroup = new ToggleGroup();
        allRadioButton.setToggleGroup(difficultyToggleGroup);
        easyRadioButton.setToggleGroup(difficultyToggleGroup);
        mediumRadioButton.setToggleGroup(difficultyToggleGroup);
        hardRadioButton.setToggleGroup(difficultyToggleGroup);
        allRadioButton.setSelected(true);

        difficultyToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                if (difficultyToggleGroup.getSelectedToggle() != null) {
                    RadioButton selected = (RadioButton) difficultyToggleGroup.getSelectedToggle();
                    recipeBackendController.setDifficulty(selected.getText());
                    updateRecipeList();
                }
            }
        });

        int minValue = 10;
        int maxValue = 250;
        int initialValue = 60;
        int amountToStepBy = 10;

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, initialValue, amountToStepBy);
        maxPriceSpinner.setValueFactory(valueFactory);
        maxPriceSpinner.valueProperty().addListener(new ChangeListener<Integer>() {

            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldValue, Integer newValue) {
                recipeBackendController.setMaxPrice(newValue);
                updateRecipeList();
            }
        });

        maxPriceSpinner.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue) {
                    //focusgained - do nothing
                } else {
                    Integer value = Integer.valueOf(maxPriceSpinner.getEditor().getText());
                    recipeBackendController.setMaxPrice(value);
                    updateRecipeList();
                }

            }


        });

        maxTimeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (newValue != null && !newValue.equals(oldValue) && !maxTimeSlider.isValueChanging()) {
                    int newTime = Math.round(newValue.intValue() / 10) * 10;
                    String displayTime = String.valueOf(newTime);
                    recipeBackendController.setMaxTime(newTime);
                    minutesLabel.setText(displayTime);
                    updateRecipeList();
                }
            }
        });


    }

    @FXML
    public void closeRecipeView(){
        recipeSearchPane.toFront();
    }

    public void openRecipeView(Recipe recipe){
        populateRecipeDetailView(recipe);
        recipeDetailPane.toFront();
    }

    private void updateRecipeList() {
        recipeListFlowPane.getChildren().clear();
        List<Recipe> recipeList = recipeBackendController.getRecipes();
        for (Recipe recipe : recipeList) {
            recipeListFlowPane.getChildren().add(recipeListItemMap.get(recipe.getName()));
        }

    }

    private void populateRecipeDetailView(Recipe recipe){
        panelImage.setImage(recipe.getFXImage());
        panelLabel.setText(recipe.getName());
    }


}