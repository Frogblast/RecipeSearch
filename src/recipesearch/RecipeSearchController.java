
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
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
    @FXML
    TextArea descriptionTextArea;
    @FXML
    TextArea ingredientsTextArea;
    @FXML
    TextArea instructionsTextArea;
    @FXML
    ImageView cuisineImageView;
    @FXML
    ImageView difficultyImageView;
    @FXML
    ImageView mainIngredientImageView;
    @FXML
    Label timeLabel;
    @FXML
    Label priceLabel;
    @FXML
    ImageView closeImageView;

    ToggleGroup difficultyToggleGroup;

    RecipeDatabase db = RecipeDatabase.getSharedInstance();

    RecipeBackendController recipeBackendController = new RecipeBackendController();

    private Map<String, RecipeListItem> recipeListItemMap = new HashMap<String, RecipeListItem>();

    private boolean detailedViewIsOpen = false;


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

        populateMainIngredientComboBox();
        populateCuisineComboBox();


    }

    private void populateMainIngredientComboBox() {
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item) {

                                    case "Kött":
                                        iconPath = "RecipeSearch/resources/icon_main_meat.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Fisk":
                                        iconPath = "RecipeSearch/resources/icon_main_fish.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Kyckling":
                                        iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Vegetarisk":
                                        iconPath = "RecipeSearch/resources/icon_main_veg.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                }
                            } catch (NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        mainIngredientComboBox.setButtonCell(cellFactory.call(null));
        mainIngredientComboBox.setCellFactory(cellFactory);
    }

    private void populateCuisineComboBox() {
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item) {

                                    case "Frankrike":
                                        iconPath = "RecipeSearch/resources/icon_flag_france.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Sverige":
                                        iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Indien":
                                        iconPath = "RecipeSearch/resources/icon_flag_india.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Asien":
                                        iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Grekland":
                                        iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Afrika":
                                        iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;

                                }
                            } catch (NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        cuisineComboBox.setButtonCell(cellFactory.call(null));
        cuisineComboBox.setCellFactory(cellFactory);
    }

    @FXML
    public void closeButtonMouseEntered() {
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_hover.png")));
    }

    @FXML
    public void closeButtonMousePressed() {
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_pressed.png")));
    }

    @FXML
    public void closeButtonMouseExited() {
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close.png")));
    }

    @FXML
    public void closeRecipeView() {
        if (detailedViewIsOpen) {
            recipeSearchPane.toFront();
            recipeSearchPane.setOpacity(1);
            detailedViewIsOpen = false;
        } else{
            detailedViewIsOpen = true;
        }
    }

    public void openRecipeView(Recipe recipe) {
        populateRecipeDetailView(recipe);
        recipeDetailPane.toFront();
        recipeSearchPane.setOpacity(0.5);
    }

    private void updateRecipeList() {
        recipeListFlowPane.getChildren().clear();
        List<Recipe> recipeList = recipeBackendController.getRecipes();
        for (Recipe recipe : recipeList) {
            recipeListFlowPane.getChildren().add(recipeListItemMap.get(recipe.getName()));
        }

    }

    private void populateRecipeDetailView(Recipe recipe) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            stringBuilder.append("- ");
            stringBuilder.append(recipe.getIngredients().get(i));
            stringBuilder.append("\n");
        }
        panelImage.setImage(recipe.getFXImage());
        panelLabel.setText(recipe.getName());

        ingredientsTextArea.setText(stringBuilder.toString());
        descriptionTextArea.setText(recipe.getDescription());
        instructionsTextArea.setText(recipe.getInstruction());

        cuisineImageView.setImage(getCuisineImage(recipe.getCuisine()));
        difficultyImageView.setImage(getDifficultyImage(recipe.getDifficulty()));
        mainIngredientImageView.setImage(getMainIngredientImage(recipe.getMainIngredient()));

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


}