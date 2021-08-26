package FoodSiren.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eml_listview_test3.R;

import FoodSiren.model.data.Recipe;

public class RecipeInfoActivity extends AppCompatActivity {

    private ImageView iv_recipeImage;

    private TextView tv_recipeName;
    private TextView tv_recipeType;
    private TextView tv_recipeMethod;
    private TextView tv_recipeIngredients;
    private TextView tv_recipeManual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        Intent intent = getIntent();
        Recipe recipe = (Recipe) intent.getSerializableExtra("레시피 정보");

        iv_recipeImage = findViewById(R.id.RecipeInfoActivity_iv_recipe_image);

        tv_recipeName = findViewById(R.id.RecipeInfoActivity_tv_recipe_name);
        tv_recipeType = findViewById(R.id.RecipeInfoActivity_tv_recipe_type);
        tv_recipeMethod = findViewById(R.id.RecipeInfoActivity_tv_recipe_method);
        tv_recipeIngredients = findViewById(R.id.RecipeInfoActivity_tv_recipe_ingredients);
        tv_recipeManual = findViewById(R.id.RecipeInfoActivity_tv_recipe_manual);

        tv_recipeIngredients.setMovementMethod(new ScrollingMovementMethod());
        tv_recipeManual.setMovementMethod(new ScrollingMovementMethod());

        Glide.with(this).load(recipe.getImage()).into(iv_recipeImage);

        tv_recipeName.setText(recipe.getName());
        tv_recipeType.setText(recipe.getType());
        tv_recipeMethod.setText(recipe.getMethod());
        tv_recipeIngredients.setText(recipe.getIngredients());

        String manual = "";
        for (int i = 0; i <= 19; i++) {
            if (recipe.getManual().get(i).equals("null")) {
                continue;
            }
            manual = manual + recipe.getManual().get(i) + "\n\n";
        }
        tv_recipeManual.setText(manual);
    }
}