package com.murach.tipcalculator;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;
    private Button   percentUpButton;
    private Button   percentDownButton;
    private Button   saveTipButton;
    private Button   viewHistoryButton;
    private TextView tipTextView;
    private TextView totalTextView;
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    
    // set up preferences
    private SharedPreferences prefs;

    // get db
    TipCalculatorDB db = new TipCalculatorDB(this);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.tipPercentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        saveTipButton = (Button) findViewById(R.id.saveTipButton);
        viewHistoryButton = (Button) findViewById(R.id.viewHistoryButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        saveTipButton.setOnClickListener(this);
        viewHistoryButton.setOnClickListener(this);
        
        // get default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = prefs.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get the instance variables
        billAmountString = prefs.getString("billAmountString", "");
        tipPercent = prefs.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        
        // calculate and display
        calculateAndDisplay();

        //Get all tips
        StringBuilder allTips = new StringBuilder();
        ArrayList<Tip> tips = db.getTips();
        for(Tip t : tips){
           allTips.append("ID: ").append(t.getId())
                   .append(" DATE: ").append(t.getDateStringFormatted())
                   .append(" AMOUNT: ").append(t.getBillAmountFormatted())
                   .append(" PERCENT: ").append(t.getTipPercentFormatted())
                   .append("\n");
        }
        Log.d("ALL TIPS ", allTips.toString());

        //Get latest tip if it exists
        StringBuilder lastTip = new StringBuilder();
        Tip tip = db.getLastTip();
        if(tip != null) {
            lastTip.append("LAST TIP: ").append(tip.getDateStringFormatted());
            Log.d("LAST TIP ", lastTip.toString());
        }

        //Get Average tip percent
        float avg = db.getAvgTipPerc();
        NumberFormat percent = NumberFormat.getPercentInstance();
        Log.d("AVG TIP % ", percent.format(avg));

    }
    
    public void calculateAndDisplay() {
        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount; 
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }
        
        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;
        
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.percentDownButton:
            tipPercent = tipPercent - .01f;
            calculateAndDisplay();
            break;
        case R.id.percentUpButton:
            tipPercent = tipPercent + .01f;
            calculateAndDisplay();
            break;
        case R.id.saveTipButton:
            float billAmount = Float.parseFloat(billAmountString);
            Tip tip = new Tip(db.getTips().size()+1, System.currentTimeMillis(), billAmount, tipPercent);
            db.saveTip(tip);
            setTipPercent(db.getAvgTipPerc());
            NumberFormat percent = NumberFormat.getPercentInstance();
            percentTextView.setText(percent.format(tipPercent));
            Toast.makeText(this, "Tip Saved", Toast.LENGTH_SHORT).show();
            break;
        case R.id.viewHistoryButton:
            startActivity(new Intent(TipCalculatorActivity.this, TipHistoryActivity.class));
            break;
        }
    }

    public void setTipPercent(float tipPercent){
        this.tipPercent = tipPercent;
    }
}