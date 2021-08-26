package FoodSiren.view.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eml_listview_test3.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import FoodSiren.viewmodel.FoodViewModel;
import FoodSiren.model.data.Food;
import FoodSiren.util.widget.WidgetView;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Food> foodList = new ArrayList<>();
    private ImageButton btn_enroll;
    private ImageButton btn_manage;
    private LinearLayout btn_enroll_ll;
    private LinearLayout btn_manage_ll;
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;
    private FoodViewModel foodViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (viewModelFactory == null) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }
        foodViewModel = new ViewModelProvider(this, viewModelFactory).get(FoodViewModel.class);

        try {
            foodList = (ArrayList<Food>) foodViewModel.getAll();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Food food : foodList) {
            try {
                if (food.getNoticed() == false) {
                    if (food.isExpOver()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle(food.getName() + "의 유통기한이 " + food.getDiffDaysCurrentToExp() + "일 지났어요!");
                        dialog.setMessage("\n" + food.getName() + "을(를) 관리 목록에서 삭제할까요?\n");
                        dialog.setPositiveButton("예", (dialog1, which) -> {
                            foodViewModel.delete(food);
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                                    new ComponentName(this, WidgetView.class));
                            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
                            dialog1.dismiss();
                        });

                        dialog.setNegativeButton("아니오", (dialog12, which) -> {
                            food.setNoticed(true);
                            foodViewModel.update(food);
                            dialog12.dismiss();
                        });
                        dialog.show();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        btn_enroll = findViewById(R.id.MainActivity_btn_enroll);
        btn_manage = findViewById(R.id.MainActivity_btn_manage);


        btn_enroll.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EnrollActivity.class);
            startActivity(intent);
        });

        btn_manage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
            startActivity(intent);
        });

        btn_enroll_ll = findViewById(R.id.MainActivity_ll_enroll);
        btn_manage_ll = findViewById(R.id.MainActivity_ll_manage);


        btn_enroll_ll.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EnrollActivity.class);
            startActivity(intent);
        });

        btn_manage_ll.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ManageActivity.class);
            startActivity(intent);
        });
    }
}