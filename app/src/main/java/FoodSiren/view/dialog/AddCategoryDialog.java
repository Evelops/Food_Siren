package FoodSiren.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.eml_listview_test3.R;

public class AddCategoryDialog extends Dialog implements View.OnClickListener {
    private final CategoryDialogClickListener categoryDialogClickListener;
    private Button btnOk, btnCancel;
    private EditText etCategory;

    public AddCategoryDialog(@NonNull Context context, CategoryDialogClickListener categoryDialogClickListener) {
        super(context);
        this.categoryDialogClickListener = categoryDialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_category);

        btnOk = findViewById(R.id.add_category_btn_ok);
        btnCancel = findViewById(R.id.add_category_btn_cancel);
        etCategory = findViewById(R.id.et_add_category);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_category_btn_ok) {
            categoryDialogClickListener.onClick(etCategory.getText().toString());
            dismiss();
        } else if (v.getId() == R.id.add_category_btn_cancel) {
            dismiss();
        }
    }
}
