package com.triplogs.helper;


import android.content.Context;
import android.net.ConnectivityManager;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * create by jigar 07-01-2021
 */
public class OkHttpWrapper extends OkHttpClient {

    public interface ResponseLickListener {
        void onFailure(Call call, IOException e);

        void onResponse(Call call, Response response);

        void checkInterNet(boolean status);
    }

    ResponseLickListener responseLickListener;

    public void setResponseListener(ResponseLickListener responseLickListener) {
        this.responseLickListener = responseLickListener;
    }

    public void getApiCall(String url, Context context) {
        if (checkInternetConnection(context)) {
            Request request;
            responseLickListener.checkInterNet(true);
            if (SharedPrefHelper.getPrefsHelper().isPrefExists(SharedPrefHelper.PREF_AUTH_TOKEN)) {
                LogClass.e("OkHttpClientWrapper", "Auth-Token :" + SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN));
                request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("X-Auth-Token", SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN))
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

            }
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (responseLickListener != null) {
                        responseLickListener.onFailure(call, e);
                    }else {
                        LogClass.e("responseLickListener","not set");
                    }

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (responseLickListener != null) {
                        responseLickListener.onResponse(call, response);
                    }else {
                        LogClass.e("responseLickListener","not set");
                    }
                }
            });
        } else {
            if (responseLickListener != null) {
                responseLickListener.checkInterNet(false);
            }else {
                LogClass.e("responseLickListener","not set");
            };
        }
    }

    public void postApiCall(String url, Map<String, Object> params, Context context) {

        if (checkInternetConnection(context)) {
            if (params != null) {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    builder.addFormDataPart(param.getKey(), String.valueOf(param.getValue()));
                }
                RequestBody requestBody = builder.build();
                Request request;
                if (SharedPrefHelper.getPrefsHelper().isPrefExists(SharedPrefHelper.PREF_AUTH_TOKEN)) {
                    LogClass.e("postApiCall", "Auth-Token :" + SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN));
                    request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .addHeader("X-Auth-Token", SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN))
                            .build();
                } else {
                    request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                }

                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (responseLickListener != null) {
                            responseLickListener.onFailure(call, e);
                        }else {
                            LogClass.e("responseLickListener","not set");
                        }

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (responseLickListener != null) {
                            responseLickListener.onResponse(call, response);
                        }else {
                            LogClass.e("responseLickListener","not set");
                        }
                    }
                });

            }
        } else {
            if (responseLickListener != null) {
                responseLickListener.checkInterNet(false);
            }else {
                LogClass.e("responseLickListener","not set");
            }
        }
    }

    public void postApiSendFile(String url, Map<String, Object> params, Context context, Map<String, File> files) {

        if (checkInternetConnection(context)) {
            if (params != null) {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    builder.addFormDataPart(param.getKey(), String.valueOf(param.getValue()));
                }

                if (files != null) {
                    for (Map.Entry<String, File> param : files.entrySet()) {
                        builder.addFormDataPart(param.getKey(), param.getValue().getName(),
                                RequestBody.create(MediaType.parse("text/csv"), param.getValue()));
                    }
                }
                RequestBody requestBody = builder.build();
                Request request;
                if (SharedPrefHelper.getPrefsHelper().isPrefExists(SharedPrefHelper.PREF_AUTH_TOKEN)) {
                    LogClass.e("OkHttpClientWrapper", "Auth-Token :" + SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN));
                    request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .addHeader("X-Auth-Token", SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN))
                            .build();
                } else {
                    request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                }

                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (responseLickListener != null) {
                            responseLickListener.onFailure(call, e);
                        }else {
                            LogClass.e("responseLickListener","not set Listener");
                        }

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (responseLickListener != null) {
                            responseLickListener.onResponse(call, response);
                        }else {
                            LogClass.e("responseLickListener","not set Listener");
                        }
                    }
                });

            }
        } else {
            if (responseLickListener != null) {
                responseLickListener.checkInterNet(false);
            }else {
                LogClass.e("responseLickListener","not set Listener");
            }
        }

    }

    public static boolean checkInternetConnection(Context context) {
        if (context != null) {
            ConnectivityManager con_manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return con_manager.getActiveNetworkInfo() != null
                    && con_manager.getActiveNetworkInfo().isAvailable()
                    && con_manager.getActiveNetworkInfo().isConnected();
        }
        return true;
    }


    public void postApiCallJson(String url, Map<String, Object> params, Context context) {

        if (checkInternetConnection(context)) {
            if (params != null) {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    builder.addFormDataPart(param.getKey(), String.valueOf(param.getValue()));
                }
                RequestBody requestBody = builder.build();
                Request request;
                if (SharedPrefHelper.getPrefsHelper().isPrefExists(SharedPrefHelper.PREF_AUTH_TOKEN)) {
                    LogClass.e("postApiCall", "Auth-Token :" + SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN));
                    request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .addHeader("X-Auth-Token", SharedPrefHelper.getPrefsHelper().getPref(SharedPrefHelper.PREF_AUTH_TOKEN))
                            .build();
                } else {
                    request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                }

                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (responseLickListener != null) {
                            responseLickListener.onFailure(call, e);
                        }else {
                            LogClass.e("responseLickListener","not set");
                        }

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (responseLickListener != null) {
                            responseLickListener.onResponse(call, response);
                        }else {
                            LogClass.e("responseLickListener","not set");
                        }
                    }
                });

            }
        } else {
            if (responseLickListener != null) {
                responseLickListener.checkInterNet(false);
            }else {
                LogClass.e("responseLickListener","not set");
            }
        }
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json); // new
        // RequestBody body = RequestBody.create(JSON, json); // old
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json); // new
        // RequestBody body = RequestBody.create(JSON, json); // old
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
