package com.example.asynctaskwithapiexample.utilities;

import android.os.AsyncTask;
import android.widget.TextView;
import java.io.IOException;

public class AsyncDataLoader extends AsyncTask<String, Void, String> {

    private TextView tvAdditionalData;

    public AsyncDataLoader(TextView tvAdditionalData) {
        this.tvAdditionalData = tvAdditionalData;
    }

    protected String doInBackground(String... params) {
        try {
            return ApiDataReader.getValuesFromApi(params[0]);
        } catch (IOException ex) {
            return String.format("Some error occured => %s", ex.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (tvAdditionalData != null) {
            tvAdditionalData.setText(result);
        }
    }
}