package com.waseet.waseetapp.Utilities;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class DataValidations {

    public Boolean isMobileValid(String mobile, EditText editText) {
        if (TextUtils.isEmpty(mobile)) {
            editText.requestFocus();
            editText.setError("Enter your mobile number");
            return false;
        } else if (Pattern.matches("[a-zA-Z]+", mobile)) {
            editText.requestFocus();
            editText.setError("Mobile no. contains digits only");
            return false;
        }
        return true;
    }

    public Boolean isSpinnerValid(String text, Context context, String message) {
        if (text.equals("Select")) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Boolean isFullNameValid(String firstName, EditText editText) {
        if (TextUtils.isEmpty(firstName)) {
            editText.requestFocus();
            editText.setError("Enter full name");
            return false;
        } else if (firstName.length() < 3) {
            editText.requestFocus();
            editText.setError("Enter full name");
            return false;
        } else if (firstName.matches(".*\\d+.*")) {
            editText.requestFocus();
            editText.setError("Enter your correct name");
            return false;
        }
        return true;
    }

    public Boolean isEmailValid(String email, EditText editText) {
        if (TextUtils.isEmpty(email)) {
            editText.requestFocus();
            editText.setError("Enter your email id");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._-]+[_A-Za-z0-9-]+@[a-z]+\\.[a-z]+")) {
            editText.requestFocus();
            editText.setError("Enter your valid email id");
            return false;
        }
        return true;
    }

    public Boolean isMandatory(String text, EditText editText) {
        if (TextUtils.isEmpty(text)) {
            editText.requestFocus();
            editText.setError("This Field is Mandatory");
            return false;
        }
        return true;
    }

    public Boolean descriptionSize(String text, EditText editText) {
        if (TextUtils.isEmpty(text)) {
            editText.requestFocus();
            editText.setError("This Field is Mandatory");
            return false;
        } else if (text.length() < 25) {
            editText.requestFocus();
            editText.setError("The description field must be at least 20 characters in length.");
            return false;
        }
        return true;
    }

    public Boolean isTvMandatory(String text, TextView textView) {
        if (textView.getText().equals("Select")) {
            textView.requestFocus();
            textView.setError("This Field is Mandatory");
            return false;
        }
        return true;
    }
}