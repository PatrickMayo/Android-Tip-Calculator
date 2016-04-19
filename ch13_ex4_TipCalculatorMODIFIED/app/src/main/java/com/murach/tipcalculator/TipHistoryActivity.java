package com.murach.tipcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;


/**
 * Created by Patrick on 3/7/2016.
 */
public class TipHistoryActivity extends Activity{

    private ListView tipListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_history);
        // get references to the widgets
        tipListView = (ListView) findViewById(R.id.tipsListView);

        //Get db
        TipCalculatorDB db = new TipCalculatorDB(this);

        //Set adapter
        ListAdapter adapter = new TipListAdapter(this, db.getTips());
        tipListView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        TipCalculatorDB db = new TipCalculatorDB(this);
        ListAdapter adapter = new TipListAdapter(this, db.getTips());
        tipListView.setAdapter(adapter);
    }
}
