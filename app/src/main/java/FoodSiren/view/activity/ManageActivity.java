package FoodSiren.view.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.eml_listview_test3.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import FoodSiren.view.dialog.ModifyDialog;
import FoodSiren.view.adapter.FoodAdapter;
import FoodSiren.viewmodel.CategoryViewModel;
import FoodSiren.viewmodel.FoodViewModel;
import FoodSiren.model.data.Category;
import FoodSiren.model.data.Food;
import FoodSiren.util.widget.WidgetView;


public class ManageActivity extends AppCompatActivity {


    private final ViewModelStore viewModelStore = new ViewModelStore();
    public ArrayList<Food> foodList = new ArrayList<>();
    //데이터 복제후 검색에 사용되는 객체
    ArrayList<Food> Copied_foodList_for_Search = new ArrayList<Food>();
    //슬라이드 삭제를 돕는 객체. 크게 중요한 부분은 아니고, 리스트를 슬라이드하면 나오는
    //버튼의 크기 색상을 변경할때 사용된다.
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            // create "Close" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(new ColorDrawable(0xff4093ff));
            // set item width
            openItem.setWidth(200);
            // set item title
            openItem.setTitle("수정");
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(0xffff4040));
            // set item width
            deleteItem.setWidth(200);
            // set item title
            deleteItem.setTitle("삭제");
            // set item title fontsize
            deleteItem.setTitleSize(18);
            // set item title font color
            deleteItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };
    private ArrayList<Category> categoryList;
    private ArrayAdapter<Category> categoryAdapter;
    private FoodAdapter foodAdapter;
    private Spinner spinner_category;
    private EditText editText_for_Search;
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private FoodViewModel foodViewModel;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        Toolbar myToolbar = findViewById(R.id.ManageActivity_tb);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }
        foodViewModel = new ViewModelProvider(this, viewModelFactory).get(FoodViewModel.class);
        categoryViewModel = new ViewModelProvider(this, viewModelFactory).get(CategoryViewModel.class);

        try {
            categoryList = (ArrayList<Category>) categoryViewModel.getAll();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Copied_foodList_for_Search = (ArrayList<Food>) foodViewModel.getAll();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        foodViewModel.getAllUpdatedFoods().observe(this, foods -> {  // foods 는 DB에 들어있는 food 객체의 배열
            Copied_foodList_for_Search.clear();  // foodList를 초기화
            Copied_foodList_for_Search.addAll(foods);  // foodList에 변경된 데이터를 저장
            String categoryText = spinner_category.getSelectedItem().toString();
            String searchText = editText_for_Search.getText().toString();
            Category_And_Food_search(categoryText, searchText);
            foodAdapter.notifyDataSetChanged();
        });

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, WidgetView.class));


        // 툴바 관련
        Toolbar toolbar = findViewById(R.id.ManageActivity_tb);
        setSupportActionBar(toolbar);


        // 카테고리 관련 스피너를 진행
        categoryAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner,
                categoryList);
        spinner_category = findViewById(R.id.ManageActivity_sp_for_category);
        spinner_category.setAdapter(categoryAdapter);
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //스피너 리스너사용이 가능하지만, 푸드내에 카테고리가 지정되어있지 않기에
                //아직은 사용이 불가능함. 푸드내에 카테고리를 지정해주는걸 요함.
                String text = spinner_category.getSelectedItem().toString();
                spinner_search(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Food 객체 생성. 그리고 검색에 사용될 CopyFoodList 생성
        foodList.addAll(Copied_foodList_for_Search);

        // FoodAdapter를 이용하여 Food를 ListView에 Display
        // 추가로 SwipeMenuListView사용을 위해 반드시 build.gradle에
        // dependencies쪽에 >>> compile 'com.baoyz.swipemenulistview:library:1.3.0' 작성해야함.
        // 또한 activity_manage.xml파일내에 listview를 swipemenulistview로 변경함.
        SwipeMenuListView listView = findViewById(R.id.ManageActivity_lv);
        foodAdapter = new FoodAdapter(this, foodList, foodViewModel);
        listView.setAdapter(foodAdapter);


        // 리스트뷰를 옆으로 밀어서 삭제 및 수정하는 기능.
        listView.setMenuCreator(creator);
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
                listView.smoothOpenMenu(position);
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                listView.smoothOpenMenu(position);
            }
        });

        //수정 삭제기능 리스너
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //수정 버튼 클릭시 다이얼로그 생성
                        Food selectedFood = foodAdapter.getItem(position);
                        Intent intent = new Intent(getApplicationContext(), ModifyDialog.class);
                        intent.putExtra("다이얼로그_데이터", Copied_foodList_for_Search.indexOf(selectedFood));
                        startActivityForResult(intent, 0);
                        foodAdapter.notifyDataSetChanged();
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview); // 위젯 내용 업데이트
                        break;
                    case 1:
                        //삭제버튼 클릭시 데이터 삭제
                        Copied_foodList_for_Search.remove(foodAdapter.getItem(position));    // foodList에서 삭제

                        // DB에서 객체 삭제
                        foodViewModel.delete(foodAdapter.getItem(position));

                        foodAdapter.remove(foodAdapter.getItem(position));  // foodList에서 삭제한 객체의 foodAdapter를 제거
                        foodAdapter.notifyDataSetChanged();
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview); // 위젯 내용 업데이트
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }

        });


        //검색기능(Test)
        editText_for_Search = findViewById(R.id.ManageActivity_et_for_search);
        editText_for_Search.addTextChangedListener(new TextWatcher() {

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
                String categoryText = spinner_category.getSelectedItem().toString();
                String searchText = editText_for_Search.getText().toString();
                Category_And_Food_search(categoryText, searchText);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_timpicker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.timePicker:
                //툴바의 아이콘이 할 기능 정의할 것
                Intent intent = new Intent(getApplicationContext(), EditTimeActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModelStore.clear();
    }

    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    // 스피너 작업을 수행하는 메소드(Test)
    public void spinner_search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        foodList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.equals("전체")) {
            foodList.addAll(Copied_foodList_for_Search);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_foodList_for_Search.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                // 여기서 문제가 있는데, 카테고리를 지정을 안해줘서(null) 그런지 카테고리의 equals 부분을 푸드에 넣으니
                // 오류가 발생한다.
                if (Copied_foodList_for_Search.get(i).contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    foodList.add(Copied_foodList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        foodAdapter.notifyDataSetChanged();
    }

    // 카테고리별 검색 작업을 수행하는 메소드(Test)
    public void Category_And_Food_search(String categoryText, String searchText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        foodList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (categoryText.equals("전체")) {
            foodList.addAll(Copied_foodList_for_Search);
            search(searchText);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_foodList_for_Search.size(); i++) {
                if (Copied_foodList_for_Search.get(i).contains(categoryText) && Copied_foodList_for_Search.get(i).contains(searchText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    foodList.add(Copied_foodList_for_Search.get(i));
                } else if (Copied_foodList_for_Search.get(i).contains(categoryText) && searchText.length() == 0) {
                    foodList.add(Copied_foodList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        foodAdapter.notifyDataSetChanged();
    }

    // 검색을 수행하는 메소드(Test)
    public void search(String searchText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        foodList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (searchText.length() == 0) {
            foodList.addAll(Copied_foodList_for_Search);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < Copied_foodList_for_Search.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (Copied_foodList_for_Search.get(i).contains(searchText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    foodList.add(Copied_foodList_for_Search.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 어뎁터를 갱신하여 검색된 데이터를 화면에 보여준다.
        foodAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //갑작스럽게 뒤로가기 버튼을 누를경우에 데이터가 증발해버리는 버그를 위한 메소드.
        foodList.clear();
        foodList.addAll(Copied_foodList_for_Search);
        finish();
    }

    //전달받은 엑티비티의 결과물. 다이얼로그에서 값수정이 다 이루어지므로
    //foodAdapter.notifyDataSetChanged();만 선언한다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //버그중에서, 만약 카테고리검색을 통해 카테고리를 지정해서 카테고리별로 식품이 나오고나서
                //식품내에 카테고리를 변경한다면, 변경이후에 식품의 위치가 다른 카테고리로 옮겨지기 때문에 해당 화면에서
                //사라져야하지만 그렇지 못했던 문제를 위해 한번 더 다시 spinner_search를 진행함.
                String text = spinner_category.getSelectedItem().toString();
                spinner_search(text);
                foodAdapter.notifyDataSetChanged();
            } else {
                foodAdapter.notifyDataSetChanged();
            }
        }
    }
}

