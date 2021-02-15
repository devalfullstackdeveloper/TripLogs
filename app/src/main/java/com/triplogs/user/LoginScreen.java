package com.triplogs.user;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.triplogs.R;
import com.triplogs.helper.ApiConstant;
import com.triplogs.helper.LogClass;
import com.triplogs.helper.OkHttpWrapper;
import com.triplogs.helper.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Call;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginScreen extends AppCompatActivity {

    private ArrayList permissionsToRequest;
    private  ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    EditText ed1, ed2, ed3, ed4, ed5, ed6;

    MaterialProgressBar materialProgressBar;
    ImageView imgSetting;
    CardView cvContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


         cvContinue = (CardView) findViewById(R.id.cv_continue);

        cvContinue.setEnabled(false);
        cvContinue.setCardBackgroundColor(Color.GRAY);
        cvContinue.setOnClickListener(view -> {


            getConfigApiCall();

        });

        initId();
        onChange();
    }


    private void initId() {
        ed1 = findViewById(R.id.ed_1);
        ed2 = findViewById(R.id.ed_2);
        ed3 = findViewById(R.id.ed_3);
        ed4 = findViewById(R.id.ed_4);
        ed5 = findViewById(R.id.ed_5);
        ed6 = findViewById(R.id.ed_6);
        materialProgressBar = findViewById(R.id.material_design_progressbar);
        imgSetting = findViewById(R.id.img_setting);
    }

    private void onChange() {
        ed1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String txt = ed1.getText().toString();
                if (txt.length() == 0) {

                }
                if (txt.length() == 1) {
                    ed1.clearFocus();
                    ed2.requestFocus();
                    ed2.setCursorVisible(true);

                }
                checkValid();

            }
        });
        ed2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String txt = ed2.getText().toString();
                if (txt.length() == 0) {
                    ed2.clearFocus();
                    ed1.requestFocus();
                    ed1.setCursorVisible(true);

                }
                if (txt.length() == 1) {
                    ed2.clearFocus();
                    ed3.requestFocus();
                    ed3.setCursorVisible(true);

                }
                checkValid();
            }
        });
        ed3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String txt = ed3.getText().toString();
                if (txt.length() == 0) {
                    ed3.clearFocus();
                    ed2.requestFocus();
                    ed2.setCursorVisible(true);

                }
                if (txt.length() == 1) {
                    ed3.clearFocus();
                    ed4.requestFocus();
                    ed4.setCursorVisible(true);

                }
                checkValid();
            }
        });
        ed4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String txt = ed4.getText().toString();
                if (txt.length() == 0) {
                    ed4.clearFocus();
                    ed3.requestFocus();
                    ed3.setCursorVisible(true);
                }
                if (txt.length() == 1) {
                    ed4.clearFocus();
                    ed5.requestFocus();
                    ed5.setCursorVisible(true);
                }
                checkValid();
            }
        });
        ed5.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {

                String txt = ed5.getText().toString();
                if (txt.length() == 0) {
                    ed5.clearFocus();
                    ed4.requestFocus();
                    ed4.setCursorVisible(true);
                }
                if (txt.length() == 1) {
                    ed5.clearFocus();
                    ed6.requestFocus();
                    ed6.setCursorVisible(true);
                }
                checkValid();
            }
        });
        ed6.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {

                String txt = ed6.getText().toString();
                if (txt.length() == 0) {
                    ed6.clearFocus();
                    ed5.requestFocus();
                    ed5.setCursorVisible(true);

                }

                checkValid();
            }
        });
    }

    private void checkValid(){
        if(
                ed1.getText().length()==1
                        && ed2.getText().length()==1
                        && ed3.getText().length()==1
                        && ed4.getText().length()==1
                        && ed5.getText().length()==1
                        && ed6.getText().length()==1
        ){
            cvContinue.setEnabled(true);
            cvContinue.setCardBackgroundColor(getResources().getColor(R.color.sky_blue));
        }else {
            cvContinue.setEnabled(false);
            cvContinue.setCardBackgroundColor(Color.GRAY);
        }
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    (dialog, which) -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginScreen.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void getConfigApiCall() {
        materialProgressBar.setVisibility(View.VISIBLE);
        Map<String, Object> params = new HashMap<>();
        String code = ed1.getText() + "" + ed2.getText() + "" + ed3.getText() + "" + ed4.getText() + "" + ed5.getText() + "" + ed6.getText();
        params.put(ApiConstant.ApiKeys.SECRET_CODE, code);
        params.put(ApiConstant.ApiKeys.PROJECT_ID, "22");
        OkHttpWrapper okHttpWrapper = new OkHttpWrapper();
        String url = ApiConstant.Urls.GET_CONFIG;

        okHttpWrapper.setResponseListener(new OkHttpWrapper.ResponseLickListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogClass.e("onFailure", "error :" + e);
                materialProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Call call, Response response) {

                LogClass.e("onResponse", "response :" + response);
                try {
                    final String myResponse = response.body().string();
                    LogClass.e("onResponse", "body :" + myResponse);
                    LoginScreen.this.runOnUiThread(() -> {
                        materialProgressBar.setVisibility(View.GONE);

                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);

                            if(jsonObject.getBoolean("status")){
                                SharedPrefHelper.getPrefsHelper().setData(SharedPrefHelper.CONFIG_RESPONSE_BODY, myResponse);
                                JSONObject response1 = jsonObject.getJSONObject("response");
                                JSONObject project = response1.getJSONObject("project");
                                JSONArray projectsmeta = project.getJSONArray("projectsmeta");
                                JSONObject version =projectsmeta.getJSONObject(0);


                                LogClass.e("version", "version :" + version);
                                String versionCode = version.getString("metavalue");
                                LogClass.e("version", "version :" + versionCode + "== "+ApiConstant.Versions.CODE);
                                if(versionCode.trim().equals(ApiConstant.Versions.CODE)){

                                Intent i = new Intent(LoginScreen.this, Home.class);
                                startActivity(i);
                                finish();
                                }else {
                                    JSONObject versionInfo =projectsmeta.getJSONObject(1);
                                    String message = versionInfo.getString("metavalue");
                                    String link = project.getString("android_app_url");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
                                    builder.setMessage(message)
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                 //   dialog.dismiss();
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(browserIntent);

                                                }
                                            });

                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }


                               // ApiConstant.Versions.CODE;



                            }else {
                                String message = jsonObject.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
                                builder.setMessage(message)
                                        .setCancelable(true)
                                        .setPositiveButton("Ok", (dialog, id) -> dialog.dismiss());

                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //  progressDialog.dismiss();


                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void checkInterNet(boolean status) {
                materialProgressBar.setVisibility(View.GONE);
                LogClass.e("checkInterNet", "status :" + status);
            }
        });

        okHttpWrapper.postApiCall(url, params, LoginScreen.this);

    }


}

