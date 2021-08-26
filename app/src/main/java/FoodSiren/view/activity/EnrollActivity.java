package FoodSiren.view.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.bumptech.glide.Glide;
import com.example.eml_listview_test3.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import FoodSiren.model.data.Category;
import FoodSiren.model.data.Food;
import FoodSiren.util.widget.WidgetView;
import FoodSiren.view.dialog.AddCategoryDialog;
import FoodSiren.view.dialog.DelCategoryDialog;
import FoodSiren.view.dialog.MenuDialog;
import FoodSiren.viewmodel.CategoryViewModel;
import FoodSiren.viewmodel.FoodViewModel;

public class EnrollActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_BARCODE_INFO = 2;
    private final ViewModelStore viewModelStore = new ViewModelStore();
    private Spinner categorySpinner;
    private ArrayAdapter<Category> categoryAdapter;
    private Category category;
    private ArrayList<Category> categoryList;
    private String selectedCategory;
    private String mCurrentPhotoPath = null;
    private ImageView btn_Add_Category;
    private ImageView btn_Del_Category;
    private ImageView btn_Add_Shelf_Life;
    private ImageView btn_Camera;
    private ImageView btn_Barcode;
    private ImageView iv_photo;
    private EditText et_barcode;
    private EditText et_foodName;
    private String regDate;
    private String expDate;
    private Button btn_Add;
    private Button btn_Minus;
    private TextView tv_foodCount;
    private Button btn_Save;
    private int count = 1;
    private EditText et_reg_date, et_exp_date;
    private DatePickerDialog.OnDateSetListener callbackMethodRegCalBtn;
    private DatePickerDialog.OnDateSetListener callbackMethodExpCalBtn;
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private FoodViewModel foodViewModel;
    private CategoryViewModel categoryViewModel;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

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

        categoryViewModel.getAllUpdatedCategorys().observe(this, categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
        });

        InitializeView();
        EditTextWatcher();
        InitializeListener();
        SetRecyclerAdapter();
    }

    private void showMenuDialog() {
        MenuDialog menuDialog = new MenuDialog(this, (foodImageID, foodName, position) -> {
            if (regDate != null) {
                int date = Integer.parseInt(regDate) + position;
                mCurrentPhotoPath = Integer.toString(foodImageID);
                Glide.with(this).load(Integer.parseInt(mCurrentPhotoPath)).into(iv_photo);
                et_exp_date.setText(date + "");
                et_foodName.setText(foodName);
            } else {
                Toast.makeText(this, "먼저 " + foodName + "의 구매일자를 입력해 주세요!", Toast.LENGTH_SHORT).show();
            }
        });
        menuDialog.setCanceledOnTouchOutside(true);
        menuDialog.setCancelable(true);
        menuDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        menuDialog.show();
    }

    private void showAddCategoryDialog() {
        AddCategoryDialog categoryDialog = new AddCategoryDialog(this, categoryName -> {
            category = new Category(categoryName);
            categoryViewModel.insert(category);
            Toast.makeText(getApplicationContext(), categoryName + "이(가) 보관 장소에 추가되었습니다.", Toast.LENGTH_SHORT).show();
        });
        categoryDialog.setCanceledOnTouchOutside(true);
        categoryDialog.setCancelable(true);
        categoryDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        categoryDialog.show();
    }

    private void showDelCategoryDialog() {
        DelCategoryDialog categoryDialog = new DelCategoryDialog(this, s -> {
            for (Category c : categoryList) {
                if (c.getName().equals(s)) {
                    categoryViewModel.delete(c);
                    Toast.makeText(getApplicationContext(), c.getName() + "이(가) 보관 장소에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        categoryDialog.setCanceledOnTouchOutside(true);
        categoryDialog.setCancelable(true);
        categoryDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        categoryDialog.show();
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

    private void InitializeView() {
        btn_Camera = findViewById(R.id.EnrollActivity_btn_camera);
        btn_Barcode = findViewById(R.id.EnrollActivity_btn_barcode);
        btn_Add = findViewById(R.id.EnrollActivity_btn_add);
        btn_Minus = findViewById(R.id.EnrollActivity_btn_minus);
        btn_Save = findViewById(R.id.EnrollActivity_btn_save);
        btn_Add_Shelf_Life = findViewById(R.id.btn_add_shelf_life);
        btn_Add_Category = findViewById(R.id.btn_add_category);
        btn_Del_Category = findViewById(R.id.btn_del_category);

        iv_photo = findViewById(R.id.EnrollActivity_iv_photo);
        et_barcode = findViewById(R.id.EnrollActivity_et_bar_code);
        et_foodName = findViewById(R.id.EnrollActivity_et_food_name);
        et_reg_date = findViewById(R.id.EnrollActivity_et_reg_date);
        et_exp_date = findViewById(R.id.EnrollActivity_et_exp_date);
        tv_foodCount = findViewById(R.id.EnrollActivity_tv_count);
        tv_foodCount.setText(count + "");

        btn_Add.setOnClickListener(this);
        btn_Save.setOnClickListener(this);
        btn_Camera.setOnClickListener(this);
        btn_Minus.setOnClickListener(this);
        btn_Barcode.setOnClickListener(this);
        btn_Add_Shelf_Life.setOnClickListener(this);
        btn_Add_Category.setOnClickListener(this);
        btn_Del_Category.setOnClickListener(this);
        qrScan = new IntentIntegrator(this);
    }

    private void EditTextWatcher() {
        et_reg_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regDate = et_reg_date.getText().toString();
                if (regDate.length() == 8) {
                    StringToDate(et_reg_date.getText().toString(), et_reg_date);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_exp_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expDate = et_exp_date.getText().toString();
                if (expDate.length() == 8) {
                    StringToDate(et_exp_date.getText().toString(), et_exp_date);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void InitializeListener() {
        callbackMethodRegCalBtn = (view, year, monthOfYear, dayOfMonth) -> {
            int intMonthOfYear = monthOfYear + 1; // monthOfYear의 범위가 0~11 이므로 +1
            String strMonthOfYear = String.format("%02d", intMonthOfYear);
            String strDayOfMonth = String.format("%02d", dayOfMonth);
            et_reg_date.setText(year + "년" + strMonthOfYear + "월" + strDayOfMonth + "일");
            regDate = et_reg_date.getText().toString();
            if (regDate.length() > 8) {
                regDate = DateToString(regDate);
            }
        };
        callbackMethodExpCalBtn = (view, year, monthOfYear, dayOfMonth) -> {
            int intMonthOfYear = monthOfYear + 1; // monthOfYear의 범위가 0~11 이므로 +1
            String strMonthOfYear = String.format("%02d", intMonthOfYear);
            String strDayOfMonth = String.format("%02d", dayOfMonth);
            et_exp_date.setText(year + "년" + strMonthOfYear + "월" + strDayOfMonth + "일");
            expDate = et_exp_date.getText().toString();
            if (expDate.length() > 8) {
                expDate = DateToString(expDate);
            }
        };
    }


    private void SetRecyclerAdapter() {
        categoryAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner = findViewById(R.id.EnrollActivity_category_spinner_enroll);

        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = categorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void StringToDate(String date, EditText et) {
        try {
            SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMdd");
            Date objDate = fromFormat.parse(date);

            SimpleDateFormat toFormat = new SimpleDateFormat("yyyy년MM월dd일");
            String resultDate = toFormat.format(objDate);

            et.setText(resultDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String DateToString(String parseDate) {
        String[] splitRegByYear = parseDate.split("년");
        String regYear = splitRegByYear[0];
        String regMonthAndDayOfMonth = splitRegByYear[1];

        String[] splitRegByMonth = regMonthAndDayOfMonth.split("월");
        String regMonth = splitRegByMonth[0];
        String regDayOfMonth = splitRegByMonth[1].replace("일", "");

        if (parseDate.length() != 11) {
            if (Integer.parseInt(regMonth) < 10)
                regMonth = "0" + regMonth;

            if (Integer.parseInt(regDayOfMonth) < 10)
                regDayOfMonth = "0" + regDayOfMonth;
        }

        return regYear + regMonth + regDayOfMonth;
    }

    private void ShelfLifeSave() {
        if (et_reg_date.getText().toString().equals("") || et_exp_date.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "날짜를 입력하세요!", Toast.LENGTH_SHORT).show();
        } else {
            String strfoodName = et_foodName.getText().toString();

            // 캘린더로 입력한 경우 년월일 삭제
            regDate = et_reg_date.getText().toString();
            if (regDate.length() > 8) {
                regDate = DateToString(regDate);
            }

            // 캘린더로 입력한 경우 년월일 삭제
            expDate = et_exp_date.getText().toString();
            if (expDate.length() > 8) {
                expDate = DateToString(expDate);
            }

            try {
                String limitReg = regDate.charAt(0) + "" + regDate.charAt(1) + "" + regDate.charAt(2) + "" + regDate.charAt(3);
                String limitExp = expDate.charAt(0) + "" + expDate.charAt(1) + "" + expDate.charAt(2) + "" + expDate.charAt(3);

                if (regDate.length() == 8 && expDate.length() == 8) {
                    if (Integer.parseInt(limitReg) < 2100 && Integer.parseInt(limitReg) > 2020
                            && Integer.parseInt(limitExp) < 2100 && Integer.parseInt(limitExp) > 2020) {
                        if (Integer.parseInt(regDate) >= Integer.parseInt(expDate)) {
                            Toast.makeText(getApplicationContext(), "구매일자와 유통기한을 확인해주세요!", Toast.LENGTH_SHORT).show();
                        } else {

                            String finalRegDate = regDate;
                            String finalExpDate = expDate;

                            Food newFood;
                            if (mCurrentPhotoPath != null) {
                                newFood = new Food(mCurrentPhotoPath, strfoodName, finalRegDate, finalExpDate, count, selectedCategory);
                            } else {
                                newFood = new Food(null, strfoodName, finalRegDate, finalExpDate, count, selectedCategory);
                            }
                            // 객체 추가
                            foodViewModel.insert(newFood);
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                                    new ComponentName(this, WidgetView.class));
                            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
                            Toast.makeText(getApplicationContext(), strfoodName + "이(가) 관리 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "연도를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "연도를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "잘못된 입력 값입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.EnrollActivity_btn_save:
                ShelfLifeSave();
                break;

            case R.id.EnrollActivity_btn_camera:
                getFoodImage();
                break;

            case R.id.EnrollActivity_btn_add:
                count++;
                tv_foodCount.setText(count + "");
                break;

            case R.id.EnrollActivity_btn_minus:
                if (count > 1) {
                    count--;
                    tv_foodCount.setText(count + "");
                }
                break;

            case R.id.EnrollActivity_btn_barcode:
                startbarcode();
                break;

            case R.id.btn_add_shelf_life:
                showMenuDialog();
                break;

            case R.id.btn_add_category:
                showAddCategoryDialog();
                break;

            case R.id.btn_del_category:
                showDelCategoryDialog();
                break;
        }
    }

    public void OnClickHandlerRegCalBtn(View view) {
        Date currentDate = new Date();
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethodRegCalBtn, currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());

        dialog.show();
    }

    public void OnClickHandlerExpCalBtn(View view) {
        Date currentDate = new Date();
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethodExpCalBtn, currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());

        dialog.show();
    }

    public void getFoodImage() {
        Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public void startbarcode() {
        qrScan.setPrompt("Scanning...");
        qrScan.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mCurrentPhotoPath = data.getStringExtra("result");
                Glide.with(this).load(mCurrentPhotoPath).into(iv_photo);
            }
        } else if (requestCode == REQUEST_BARCODE_INFO) {
            if (resultCode == RESULT_OK) {
                String FoodBarcodeName = data.getStringExtra("바코드_식품_이름");
                et_foodName.setText(FoodBarcodeName);
                mCurrentPhotoPath = data.getStringExtra("바코드_식품_사진");
                Glide.with(this).load(mCurrentPhotoPath).into(iv_photo);
            }
        }
        //zxing을 활용한 바코드 스캔을 통해서 json 데이터들을 넘겨받고 가져올것이기 때문에 onActivityResult에서 스캔에 대한 정보를 리턴받아야 함.
        else if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
            } else {
                //qrcode 결과가 있으면
                et_barcode.setText(result.getContents());
                Intent intent = new Intent(getApplicationContext(), BarcodeActivity.class);
                intent.putExtra("바코드_번호", result.getContents());
                startActivityForResult(intent, REQUEST_BARCODE_INFO);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

