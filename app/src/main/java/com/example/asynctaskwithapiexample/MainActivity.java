package com.example.asynctaskwithapiexample;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asynctaskwithapiexample.utilities.ApiDataReader;
import com.example.asynctaskwithapiexample.utilities.AsyncDataLoader;
import com.example.asynctaskwithapiexample.utilities.Constants;
import com.example.asynctaskwithapiexample.parsers.BankHolidaysParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private ListView lvItems;
    private TextView tvStatus;
    private ArrayAdapter<String> listAdapter;
    private Switch swUseAsyncTask;
    private Spinner spinnerCurrency;

    private TextView tvAdditionalData;
    private AsyncDataLoader asyncDataLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.lvItems = findViewById(R.id.lv_items);
        this.tvStatus = findViewById(R.id.tv_status);
        this.swUseAsyncTask = findViewById(R.id.sw_use_async_task);
        this.spinnerCurrency = findViewById(R.id.spinner_currencies);
        this.listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        this.lvItems.setAdapter(this.listAdapter);
        this.tvStatus=findViewById(R.id.tv_status);
        this.tvAdditionalData=findViewById(R.id.tv_additional_data);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCurrency.setAdapter(adapter);

        asyncDataLoader= new AsyncDataLoader(this.tvAdditionalData);
    }

    public void onBtnGetDataClick(View view) {
        this.tvStatus.setText(R.string.loading_data);
        if(this.swUseAsyncTask.isChecked()){
            getDataByAsyncTask();
            Toast.makeText(this, R.string.msg_using_async_task, Toast.LENGTH_LONG).show();
        }
        else{
            getDataByThread();
            Toast.makeText(this, R.string.msg_using_thread, Toast.LENGTH_LONG).show();
        }
    }

    public void getDataByAsyncTask() {
        // Currency selected by the user
        final String selectedCurrency = spinnerCurrency.getSelectedItem().toString();
        new AsyncDataLoader(this.tvAdditionalData) {
            @Override
            public void onPostExecute(String result) {
                if(result !=null && !result.isEmpty()){
                    String filteredResult = filterResultByCurrency(result, selectedCurrency);
                    tvStatus.setText(getString(R.string.data_loaded) + filteredResult);
                } else {
                    tvStatus.setText(R.string.error_loading_data);
                }
            }
        }.execute(Constants.BANK_HOLIDAYS_API_URL);
    }

    private String extractAdditionalData(List<String> results){
        StringBuilder additionalData=new StringBuilder();
        for (String result : results) {
            additionalData.append("Papildoma informacija...").append("\n");
        }
       return additionalData.toString();
    }

    public void getDataByThread() {
        this.tvStatus.setText(R.string.loading_data);
        Runnable getDataAndDisplayRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = ApiDataReader.getValuesFromApi(Constants.BANK_HOLIDAYS_API_URL);
                    final String selectedCurrency = spinnerCurrency.getSelectedItem().toString();

                    String filteredResult = filterResultByCurrency(result, selectedCurrency);
                    Runnable updateUIRunnable = new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText(getString(R.string.data_loaded) + filteredResult);
                        }
                    };
                    runOnUiThread(updateUIRunnable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(getDataAndDisplayRunnable);
        thread.start();
    }

    private String filterResultByCurrency(String inputResult, String selectedCurrency) {
        StringBuilder filteredResult=new StringBuilder();

        String[] lines=inputResult.split("\n");

        for (String line : lines) {
            if (line.contains(selectedCurrency)) {
                filteredResult.append(line).append("\n");
            }
        }
        return filteredResult.toString();
    }

}