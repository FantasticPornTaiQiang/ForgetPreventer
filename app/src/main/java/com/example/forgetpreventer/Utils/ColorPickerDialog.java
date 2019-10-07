package com.example.forgetpreventer.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import java.util.LinkedList;
import java.util.List;

public class ColorPickerDialog implements View.OnClickListener {

    private int rowCount;
    private int colCount;
    private int buttonCount;
    private int defaultPadding  = 20;
    private int [] mColors;

    private List<ColorButton> colorButtonList = new LinkedList<>();

    private ColorButton currentButton;
    private AlertDialog dialog;
    private String title = "颜色选择";
    private Boolean isDismissAfterClick = true;
    private LinearLayout rootLayout;
    private Context mContext;
    private OnColorChangedListener listener;



    public ColorPickerDialog(Context context ,int[] colors) {
        this(context,colors,colors[0]);
    }

    public ColorPickerDialog(Context context,  int[] colors, int checkedColor) {

        this.mContext = context;
        this.mColors = colors;
        buttonCount = colors.length;
        defaultPadding = dip2px(mContext,defaultPadding);
        setCheckedColor(checkedColor);

    }

    public ColorPickerDialog build(int widthCount){

        colCount = widthCount;

        rowCount = (buttonCount -1)/widthCount + 1;

        rootLayout = new LinearLayout(mContext);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout [] linearLayouts = new LinearLayout[rowCount];

        for(int i = 0 ; i < rowCount ; i++){
            linearLayouts[i] = new LinearLayout(mContext);
            linearLayouts[i].setOrientation(LinearLayout.HORIZONTAL);
            linearLayouts[i].setPadding(defaultPadding,defaultPadding/2,0,defaultPadding/2);
            rootLayout.addView(linearLayouts[i]);
        }

        for(int i = 0 ; i < buttonCount ; i++){
            ColorButton colorButton = new ColorButton(mContext,mColors[i]);
            colorButton.setOnClickListener(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0,0,defaultPadding,0);
            linearLayouts[i/widthCount].addView(colorButton,lp);
            colorButtonList.add(colorButton);
        }
        return this;
    }


    public ColorPickerDialog show(){


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        dialog = builder.create();
        dialog.setTitle(getTitle());
        dialog.setView(rootLayout);
        dialog.show();
        dialog.getWindow().setLayout((dip2px(mContext,30)+defaultPadding)*colCount+defaultPadding*3,dip2px(mContext,100+30*rowCount)+defaultPadding*rowCount);
        return this;
    }


    @Override
    public void onClick(View v) {
        if(listener != null){
            ColorButton colorButton = (ColorButton) v;
            if(colorButton.isChecked() == false){
                if(currentButton != null){
                    currentButton.setChecked(false);
                }
                colorButton.setChecked(true);
                listener.onColorChanged(colorButton.getmColor());
                currentButton = colorButton;
                if(isDismissAfterClick && dialog != null){
                    dialog.dismiss();
                }
            }
        }
    }
    public ColorPickerDialog setCheckedColor(int color){
        if(currentButton != null && color == currentButton.getmColor()){
            return this;
        }
        for(ColorButton colorButton: colorButtonList){
            if(color == colorButton.getmColor()){
                if(currentButton != null){
                    currentButton.setChecked(false);
                }
                colorButton.setChecked(true);
                listener.onColorChanged(colorButton.getmColor());
                currentButton = colorButton;
            }
        }
        return this;
    }

    public ColorPickerDialog setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
        return this;
    }

    public ColorPickerDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }


    public ColorPickerDialog setDismissAfterClick(Boolean dismissAfterClick) {
        isDismissAfterClick = dismissAfterClick;
        return this;
    }
    private  static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
