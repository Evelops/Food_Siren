package FoodSiren.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.eml_listview_test3.R;

public class MenuDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    private final MenuDialogClickListener menuDialogClickListener;
    private ImageView carrot, broccoli, cabbage, spinach, watermelon, strawberry, bananas, apple, grapes, onion, greenOnion, tomato, potato, cucumber;

    public MenuDialog(@NonNull Context context, MenuDialogClickListener menuDialogClickListener) {
        super(context);
        this.context = context;
        this.menuDialogClickListener = menuDialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu);

        carrot = findViewById(R.id.carrot);
        broccoli = findViewById(R.id.broccoli);
        cabbage = findViewById(R.id.cabbage);
        spinach = findViewById(R.id.spinach);
        watermelon = findViewById(R.id.watermelon);
        strawberry = findViewById(R.id.strawberry);
        bananas = findViewById(R.id.bananas);
        apple = findViewById(R.id.apple);
        grapes = findViewById(R.id.grapes);
        cucumber = findViewById(R.id.cucumber);
        potato = findViewById(R.id.potato);
        tomato = findViewById(R.id.tomato);
        greenOnion = findViewById(R.id.green_onion);
        onion = findViewById(R.id.onion);

        carrot.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_carrot, "당근", 15);
            dismiss();
        });
        broccoli.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_broccoli, "브로콜리", 2);
            dismiss();
        });
        cabbage.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_cabbage, "양배추", 7);
            dismiss();
        });
        spinach.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_spinach, "시금치", 5);
            dismiss();
        });
        watermelon.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_watermelon, "수박", 5);
            dismiss();
        });
        strawberry.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_strawberry, "딸기", 3);
            dismiss();
        });
        bananas.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_bananas, "바나나", 10);
            dismiss();
        });
        apple.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_apple, "사과", 15);
            dismiss();
        });
        grapes.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_grapes, "포도", 15);
            dismiss();
        });
        cucumber.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_cucumber, "오이", 7);
            dismiss();
        });
        onion.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_onion, "양파", 7);
            dismiss();
        });
        greenOnion.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_greenonion, "대파", 15);
            dismiss();
        });
        tomato.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_tomato, "토마토", 14);
            dismiss();
        });
        potato.setOnClickListener(v -> {
            menuDialogClickListener.onClick(R.drawable.ic_potato, "감자", 30);
            dismiss();
        });
    }

    @Override
    public void onClick(View v) {

    }
}
