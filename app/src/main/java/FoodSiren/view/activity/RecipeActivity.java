package FoodSiren.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eml_listview_test3.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import FoodSiren.view.adapter.RecipeAdapter;
import FoodSiren.model.data.Recipe;

public class RecipeActivity extends AppCompatActivity {

    private static final String IP_ADDRESS = "210.103.56.91";
    private static final String PHP_FILE_NAME = "get_recipe_info_keyword.php";
    private static final String TAG = "phptest";
    private final ArrayList<Recipe> Copied_recipeList_for_Search = new ArrayList<>();
    private ArrayList<Recipe> recipeList;
    private RecipeAdapter recipeAdapter;
    private EditText recipeEditText;
    private TextView recipeTextView;
    private RecyclerView recipeListView;
    private String mJsonString;
    private String foodName;
    private Recipe mRecipe;

    private ArrayAdapter<String> typeSpinnerAdapter;
    private Spinner typeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        foodName = intent.getStringExtra("식자재명");

        recipeEditText = findViewById(R.id.RecipeActivity_et_for_search);
        recipeTextView = findViewById(R.id.RecipeActivity_tv_search_info);
        recipeListView = findViewById(R.id.RecipeActivity_rv_recipe_list);

        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(this, recipeList);

        recipeTextView.setText("'" + foodName + "' 을(를) 포함하는 레시피 검색결과");

        recipeListView.setLayoutManager(new GridLayoutManager(this, 2));
        recipeListView.setAdapter(recipeAdapter);

        GetData task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/" + PHP_FILE_NAME + "?keyword=" + foodName);

        recipeAdapter.notifyDataSetChanged();

        recipeAdapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                mRecipe = recipe;

                Bundle bundle = new Bundle();
                bundle.putSerializable("레시피 정보", recipe);
                Intent intent = new Intent(getApplicationContext(), RecipeInfoActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        ArrayList<String> recipeTypeList = new ArrayList<>();
        recipeTypeList.add("전체");
        recipeTypeList.add("밥");
        recipeTypeList.add("반찬");
        recipeTypeList.add("국&찌개");
        recipeTypeList.add("후식");
        recipeTypeList.add("일품");
        typeSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner, recipeTypeList);
        typeSpinner = findViewById(R.id.RecipeActivity_sp_for_type);
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //스피너 리스너사용이 가능하지만, 푸드내에 카테고리가 지정되어있지 않기에
                //아직은 사용이 불가능함. 푸드내에 카테고리를 지정해주는걸 요함.
                String text = typeSpinner.getSelectedItem().toString();
                useSpinner(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        recipeEditText.addTextChangedListener(new TextWatcher() {

            //오버라이드해야만 하는 빈 함수들. 굳이 구현 필요는 X.
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String typeText = typeSpinner.getSelectedItem().toString();
                String searchText = recipeEditText.getText().toString();
                useSpinnerAndSearch(typeText, searchText);
            }
        });
    }

    private void showResult() {

        String TAG_JSON = "webnautes";
        String TAG_SEQ = "Seq";
        String TAG_NAME = "Name";
        String TAG_METHOD = "Method";
        String TAG_TYPE = "Type";
        String TAG_IMAGE = "Image";
        String TAG_INGREDIENTS = "Ingredients";
        String TAG_MANUAL = "Manual";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String seq = item.getString(TAG_SEQ);
                String name = item.getString(TAG_NAME);
                String method = item.getString(TAG_METHOD);
                String type = item.getString(TAG_TYPE);
                String image = item.getString(TAG_IMAGE);
                String ingredients = item.getString(TAG_INGREDIENTS);

                Recipe recipe = new Recipe();

                recipe.setSeq(seq);
                recipe.setName(name);
                recipe.setMethod(method);
                recipe.setType(type);
                recipe.setImage(image);
                recipe.setIngredients(ingredients);

                for (int j = 1; j <= 9; j++) {
                    recipe.addManual(item.getString(TAG_MANUAL + "0" + j));
                }

                for (int j = 10; j <= 20; j++) {
                    recipe.addManual(item.getString(TAG_MANUAL + "" + j));
                }
                recipeList.add(recipe);
                Copied_recipeList_for_Search.add(recipe);
                recipeAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }
    }

    public void useSpinner(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        recipeList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.equals("전체")) {
            recipeList.addAll(Copied_recipeList_for_Search);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_recipeList_for_Search.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                // 여기서 문제가 있는데, 카테고리를 지정을 안해줘서(null) 그런지 카테고리의 equals 부분을 푸드에 넣으니
                // 오류가 발생한다.
                if (Copied_recipeList_for_Search.get(i).containsByType(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    recipeList.add(Copied_recipeList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        recipeAdapter.notifyDataSetChanged();
    }

    // 카테고리별 검색 작업을 수행하는 메소드(Test)
    public void useSpinnerAndSearch(String categoryText, String searchText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        recipeList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (categoryText.equals("전체")) {
            recipeList.addAll(Copied_recipeList_for_Search);
            search(searchText);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_recipeList_for_Search.size(); i++) {
                if (Copied_recipeList_for_Search.get(i).containsByType(categoryText) && Copied_recipeList_for_Search.get(i).containsByName(searchText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    recipeList.add(Copied_recipeList_for_Search.get(i));
                } else if (Copied_recipeList_for_Search.get(i).containsByType(categoryText) && searchText.length() == 0) {
                    recipeList.add(Copied_recipeList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        recipeAdapter.notifyDataSetChanged();
    }

    // 검색을 수행하는 메소드(Test)
    public void search(String searchText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        recipeList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (searchText.length() == 0) {
            recipeList.addAll(Copied_recipeList_for_Search);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_recipeList_for_Search.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (Copied_recipeList_for_Search.get(i).containsByName(searchText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    recipeList.add(Copied_recipeList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        recipeAdapter.notifyDataSetChanged();
    }

    private class GetData extends AsyncTask<String, Void, String> {

        //        ProgressBar progressBar = new ProgressBar(RecipeActivity.this, null, android.R.attr.progressBarStyleLarge);
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressBar.setVisibility(View.VISIBLE);
            progressDialog = ProgressDialog.show(RecipeActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");
                if (jsonArray.length() == 0) {
                    result = null;
                }
            } catch (Exception e) {

            }

            if (result == null) {
                Toast.makeText(getApplicationContext(), "'" + foodName + "'을 키워드로 갖는 레시피 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                mJsonString = result;
                showResult();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "GetData : Error ", e);

                return null;
            }
        }
    }
}
