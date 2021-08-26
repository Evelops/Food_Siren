package FoodSiren.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import FoodSiren.model.database.FoodSirenDB;
import FoodSiren.model.data.Category;

// View(Activity)에게 Model(Data)를 전달하는 클래스
// Activity가 직접 DB에 접근해서 Data를 뽑아오는 것은 Activity와 같은 View가 맡는 일이 아니기 때문에(View는 오로지 화면 구성에만 집중) 이를 ViewModel이 맡아서 처리한다.

public class CategoryViewModel extends AndroidViewModel {
    private final FoodSirenDB db;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        db = FoodSirenDB.getDatabase(application);  // DB 생성, 싱글톤 패턴
    }

    public LiveData<List<Category>> getAllUpdatedCategorys() {  // DB에 있는 모든 걸 가져옴, LiveData이므로 B가 갱신되면 새로 가져옴
        return db.categoryDao().getAllUpdatedCategories();
    }

    public List<Category> getAll() throws ExecutionException, InterruptedException {  // DB에 있는 모든 걸 가져옴(1회성)
        return new SelectAsyncTask(db.categoryDao()).execute().get();
    }

    // 매개변수로 넘기는 Category 객체를 DB 추가
    public void insert(Category category) {
        new InsertAsyncTask(db.categoryDao()).execute(category);
    }

    // 매개변수로 넘기는 Category 객체와 같은 객체를 DB에서 찾아서 업데이트
    public void update(Category category) {
        new UpdateAsyncTask(db.categoryDao()).execute(category);
    }  // 

    // 매개변수로 넘기는 Category 객체와 같은 객체를 DB에서 찾아서 삭제
    public void delete(Category category) {
        new DeleteAsyncTask(db.categoryDao()).execute(category);
    }

    // getAll()을 수행하는 비동기문, DB에 접근하는 건 시간이 오래걸릴 수 있으므로 비동기적으로 처리해야 함.
    public static class SelectAsyncTask extends AsyncTask<Category, Void, List<Category>> {
        private final CategoryDao mCategoryDao;

        public SelectAsyncTask(CategoryDao categoryDao) {
            this.mCategoryDao = categoryDao;
        }

        @Override
        protected List<Category> doInBackground(Category... categorys) {
            return mCategoryDao.getAll();
        }
    }

    // insert()을 수행하는 비동기문
    public static class InsertAsyncTask extends AsyncTask<Category, Void, Void> {
        private final CategoryDao mCategoryDao;

        public InsertAsyncTask(CategoryDao categoryDao) {
            this.mCategoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categorys) {
            mCategoryDao.insert(categorys[0]);
            return null;
        }
    }

    // update()을 수행하는 비동기문
    public static class UpdateAsyncTask extends AsyncTask<Category, Void, Void> {
        private final CategoryDao mCategoryDao;

        public UpdateAsyncTask(CategoryDao categoryDao) {
            this.mCategoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categorys) {
            mCategoryDao.update(categorys[0]);
            return null;
        }
    }

    // delete()을 수행하는 비동기문
    public static class DeleteAsyncTask extends AsyncTask<Category, Void, Void> {
        private final CategoryDao mCategoryDao;

        public DeleteAsyncTask(CategoryDao categoryDao) {
            this.mCategoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... categorys) {
            mCategoryDao.delete(categorys[0]);
            return null;
        }
    }
}
