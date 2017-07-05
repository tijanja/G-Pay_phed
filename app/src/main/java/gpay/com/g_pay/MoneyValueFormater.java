package gpay.com.g_pay;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;

/**
 * Created by adetunji on 6/22/17.
 */

public class MoneyValueFormater implements TextWatcher
{
    private final WeakReference<EditText> editTextWeakReference;

    public MoneyValueFormater(EditText editText) {
        editTextWeakReference = new WeakReference<EditText>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
       /* String ss = s.toString();
        editText.removeTextChangedListener(this);
        String cleanString = s.toString().replaceAll("[$,.]", "");

        double d = Double.parseDouble(cleanString);
        String formatted = NumberFormat.getCurrencyInstance().format(d);
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);*/
    }

    @Override
    public void afterTextChanged(Editable a)
    {

    }
}
