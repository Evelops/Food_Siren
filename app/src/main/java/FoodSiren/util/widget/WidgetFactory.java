package FoodSiren.util.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.eml_listview_test3.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import FoodSiren.model.data.Food;
import FoodSiren.model.database.FoodSirenDB;
import FoodSiren.viewmodel.FoodDao;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {


    public Context mContext;
    public ArrayList<Food> foodList;
    private FoodSirenDB db;


    public WidgetFactory(Context context) {
        this.mContext = context;
    }

    public void setData() throws ExecutionException, InterruptedException {
        db = FoodSirenDB.getDatabase(mContext);

        // 위젯은 ViewModel을 적용하지 못하여, SelectAsyncTask 를 ViewModel을 거치지 않고 직접 수행
        foodList = (ArrayList<Food>) new SelectAsyncTask(db.foodDao()).execute().get();
    }


    @Override
    public void onCreate() {
        try {
            setData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDataSetChanged() {
        try {
            setData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {

    }


    @Override
    public int getCount() {
        return foodList.size();
    }


    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews listviewWidget = new RemoteViews(mContext.getPackageName(), R.layout.widget_adapter_food);

        if (foodList.get(position).getImagePath() == null) {
            listviewWidget.setImageViewResource(R.id.Widget_FoodAdapter_iv_food_image, (R.drawable.baguette));
        } else {
            String photoPath = foodList.get(position).getImagePath();
            try {
                Bitmap bitmap;
                if (isNumeric(photoPath)) {
                    bitmap = Glide.with(mContext).asBitmap().load(Integer.parseInt(photoPath)).submit(512, 512).get();
                } else {
                    bitmap = Glide.with(mContext).asBitmap().load(photoPath).submit(512, 512).get();
                }
                listviewWidget.setImageViewBitmap(R.id.Widget_FoodAdapter_iv_food_image, bitmap);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        listviewWidget.setTextViewText(R.id.Widget_FoodAdapter_tv_food_name, foodList.get(position).getName());
        listviewWidget.setTextViewText(R.id.Widget_FoodAdapter_tv_food_cnt, foodList.get(position).getCnt() + " 개");
        try {
            listviewWidget.setTextViewText(R.id.Widget_FoodAdapter_tv_reg_to_current, (foodList.get(position).getDiffDaysRegToCurrent() + "일 째"));
            if ((foodList.get(position).isExpOver()) && (foodList.get(position).getDiffDaysCurrentToExp() == 0))
                listviewWidget.setTextViewText(R.id.Widget_FoodAdapter_tv_current_to_exp, (foodList.get(position).getDiffDaysCurrentToExp() + "일 남음"));
            else if (foodList.get(position).isExpOver())
                listviewWidget.setTextViewText(R.id.Widget_FoodAdapter_tv_current_to_exp, (foodList.get(position).getDiffDaysCurrentToExp() + "일 지남"));
            else
                listviewWidget.setTextViewText(R.id.Widget_FoodAdapter_tv_current_to_exp, ((foodList.get(position).getDiffDaysCurrentToExp() + 1) + "일 남음"));
            listviewWidget.setProgressBar(R.id.Widget_FoodAdapter_progress_bar, 100, foodList.get(position).calcProgress(), false);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent fillInIntent = new Intent();
        listviewWidget.setOnClickFillInIntent(R.id.Widget_FoodAdapter_btn_tip, fillInIntent);

        return listviewWidget;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    public Boolean isNumeric(String str) {
        boolean isNumeric = true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                isNumeric = false;
            }
        }

        return isNumeric;
    }

    public static class SelectAsyncTask extends AsyncTask<Food, Void, List<Food>> {
        private final FoodDao mFoodDao;

        public SelectAsyncTask(FoodDao foodDao) {
            this.mFoodDao = foodDao;
        }

        @Override
        protected List<Food> doInBackground(Food... foods) {
            return mFoodDao.getAll();
        }
    }
}