package com.murach.tipcalculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Patrick on 3/8/2016.
 */
public class TipLayout extends RelativeLayout implements View.OnClickListener {

    // define variables for the widgets
    private TextView tipDateTextView;
    private TextView billAmountTextView;
    private TextView tipPercentTextView;
    private Button deleteTipButton;

    private Tip tip;
    private TipCalculatorDB db;
    private Context context;

    public TipLayout(Context context){
        super(context);
    }

    public TipLayout(Context context, Tip tip){
        super(context);

        //Set context and get db object
        this.context = context;
        db = new TipCalculatorDB(context);

        //Inflate the layout
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.adapter_tip_history, this, true);

        //Get references to widgets
        tipDateTextView = (TextView) findViewById(R.id.tipDateTextView);
        billAmountTextView = (TextView) findViewById(R.id.billAmountTextView);
        tipPercentTextView = (TextView) findViewById(R.id.tipPercentTextView);
        deleteTipButton = (Button) findViewById(R.id.deleteTipButton);

        // set the listeners
        deleteTipButton.setOnClickListener(this);
        this.setOnClickListener(this);

        //Set tip data on widgets
        setTips(tip);
    }

    public void setTips(Tip t){
        tip = t;

        tipDateTextView.setText(tip.getDateStringFormatted());
        billAmountTextView.setText(tip.getBillAmountFormatted());
        tipPercentTextView.setText(tip.getTipPercentFormatted());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteTipButton:
                db.deleteTip(tip.getId());

                Intent intent = new Intent(this.context, TipHistoryActivity.class);
                ((Activity)getContext()).finish();
                context.startActivity(intent);
                Log.d("Delete Button ", "PRESSED!");
                break;
        }
    }
}
