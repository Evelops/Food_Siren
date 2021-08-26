package FoodSiren.view.etc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eml_listview_test3.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import FoodSiren.view.activity.MainActivity;

public class FoodSirenIntro extends AppIntro {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static String date_text = "24:00:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // first slider
        addSlide(AppIntroFragment.newInstance("Welcome,Food Siren", "Food Siren과 함께 식자재를 효율적으로 관리하세요."
                , R.drawable.ic_slide1, ContextCompat.getColor(getApplicationContext(), R.color.firstColor)));

        // second slider
        addSlide(AppIntroFragment.newInstance("Food Siren 핵심 기능", "바코드 스캔, 푸시 알림 기능등을 활용하여 쉽게 식자재를 관리할 수 있습니다."
                , R.drawable.ic_slide2, ContextCompat.getColor(getApplicationContext(), R.color.secondColor)));

        // third slider
        addSlide(AppIntroFragment.newInstance("Start Food Siren", "지금 바로 Food Siren을 시작해보세요."
                , R.drawable.ic_slide3, ContextCompat.getColor(getApplicationContext(), R.color.thirdColor)));

        setFadeAnimation();

        //sharedPreferences 를 활용하여 앱을 처음 빌드 했을 때만, 한번 실행되고 이외에는 실행되지 않도록 구성,
        // 앱 내에서 관련 데이터들이 내부 데이터로 저장되고 일회성으로 출력하기 위해서 사용
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences != null) {
            boolean checkShared = sharedPreferences.getBoolean("checkStated", false);
            if (checkShared == true) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));
        editor.putBoolean("checkStated", false).commit();
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));
        editor.putBoolean("checkStated", true).commit();
        finish();
    }
}
