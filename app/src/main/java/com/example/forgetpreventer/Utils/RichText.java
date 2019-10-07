package com.example.forgetpreventer.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.R.style;

import com.example.forgetpreventer.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class RichText extends EditText implements TextWatcher {
    public static final int FORMAT_BOLD = 1;//粗体
    public static final int FORMAT_ITALIC = 2;//斜体
    public static final int FORMAT_UNDERLINED = 3;//下划线
    public static final int FORMAT_STRIKETHROUGH = 4;//删除线

    private boolean historyEnable = true;
    private int historySize = 100;

    private List<String> historyList = new LinkedList();
    private boolean historyWorking = false;
    private int historyCursor = 0;
    private SpannableStringBuilder inputBefore;
    private Editable inputLast;
    private boolean textChanged;


    public RichText(Context context) {
        super(context);
        this.init(null);
    }

    public RichText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public RichText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.RichText);
        this.historyEnable = array.getBoolean(R.styleable.RichText_historyEnable, true);
        this.historySize = array.getInt(R.styleable.RichText_historySize, 100);
        array.recycle();
        if (this.historyEnable && this.historySize <= 0) {
            throw new IllegalArgumentException("historySize must > 0");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.addTextChangedListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeTextChangedListener(this);
    }

    public void setFormatBold(boolean valid) {
        if(this.getSelectionStart() < this.getSelectionEnd()){
            if (valid) {
                this.styleValid(FORMAT_BOLD, this.getSelectionStart(), this.getSelectionEnd());
            } else {
                this.styleInvalid(FORMAT_BOLD, this.getSelectionStart(), this.getSelectionEnd());
            }

            if(historyEnable && !historyWorking){
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }
    }

    public void setFormatItalic(boolean valid) {
        if(this.getSelectionStart() < this.getSelectionEnd()){
            if (valid) {
                this.styleValid(FORMAT_ITALIC, this.getSelectionStart(), this.getSelectionEnd());
            } else {
                this.styleInvalid(FORMAT_ITALIC, this.getSelectionStart(), this.getSelectionEnd());
            }

            if(historyEnable && !historyWorking){
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }
    }

    //选中文本只要有没有Style的地方，就添加Style
    protected void styleValid(int style, int start, int end) {
        switch(style) {
            case 0:
            case 1:
            case 2:
            case 3:
                if (start < end) {
                    getEditableText().setSpan(new StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            default:
        }
    }
    //选中文本全部有Style时，删除Style
    protected void styleInvalid(int style, int start, int end) {
        switch(style) {
            case 0:
            case 1:
            case 2:
            case 3:
                if (start >= end) {
                    return;
                } else {
                    StyleSpan[] spans = this.getEditableText().getSpans(start, end, StyleSpan.class);
                    List<Part> list = new ArrayList();

                    for(int i = 0; i < spans.length; i++) {
                        StyleSpan span = spans[i];
                        if (span.getStyle() == style) {
                            list.add(new Part(this.getEditableText().getSpanStart(span), this.getEditableText().getSpanEnd(span)));
                            this.getEditableText().removeSpan(span);
                        }
                    }
                    //start是选中的区域的start，part.getStart()是该Span对象创建时的start，若part.getStart() < start，则说明多删了，要把选中范围外的span还原
                    //当使用getSpans获取某段文本A的span时，会返回一个目标span格式的span数组，文本A包含几个span，数组里就会有几个span。比如“1234567890”这段文本包含“123”“4567”和“890”这三段来自不同span的文本。当我们调用getSpans函数来获取“345678”这段文本格式时，就会返回三个span，这三个span分别是“123”“4567”和“890”的span，因此在循环中调用removeSpan移除文本格式时，就会修改整个“1234567890”的文本格式，必须在之后对“345678”范围外的字符加以还原，才能得到想要的结果。
                    for (Part part : list){
                        if (part.isValid()) {
                            if (part.getStart() < start) {
                                styleValid(style, part.getStart(), start);
                            }

                            if (part.getEnd() > end) {
                                this.styleValid(style, end, part.getEnd());
                            }
                        }
                    }

                    return;
                }
            default:
        }
    }
    //选中文本全部有Style才返回true
    protected boolean containStyle(int style, int start, int end) {
        switch(style) {
            case 0:
            case 1:
            case 2:
            case 3:
                if (start >= end) {
                    return false;
                } else {
                    StringBuilder builder = new StringBuilder();
                    //提取选中文本的第i到i+1个字符，比较其是否和Style相等，相等则压入StringBuilder，最后比较StringBuilder中的字符和选中文本是否一样即可
                    for(int i = start; i < end; i++) {
                        StyleSpan[] spans = this.getEditableText().getSpans(i, i + 1, StyleSpan.class);
                        for(int j = 0; j < spans.length; j++) {
                            if (spans[j].getStyle() == style) {
                                builder.append(this.getEditableText().subSequence(i, i + 1).toString());
                                break;
                            }
                        }
                    }

                    return this.getEditableText().subSequence(start, end).toString().equals(builder.toString());
                }
            default:
                return false;
        }
    }

    public void setFormatUnderlined(boolean valid) {
        if(this.getSelectionStart() < this.getSelectionEnd()){
            if (valid) {
                underlineValid(this.getSelectionStart(), this.getSelectionEnd());
            } else {
                underlineInvalid(this.getSelectionStart(), this.getSelectionEnd());
            }

            if(historyEnable && !historyWorking){
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }
    }

    protected void underlineValid(int start, int end) {
        if (start < end) {
            getEditableText().setSpan(new UnderlineSpan(), start, end, 33);
        }
    }

    protected void underlineInvalid(int start, int end) {
        if (start < end) {
            UnderlineSpan[] underlineSpans = this.getEditableText().getSpans(start, end, UnderlineSpan.class);
            List<Part> list = new ArrayList();

            for(int i$ = 0; i$ < underlineSpans.length; ++i$) {
                UnderlineSpan span = underlineSpans[i$];
                list.add(new Part(this.getEditableText().getSpanStart(span), this.getEditableText().getSpanEnd(span)));
                this.getEditableText().removeSpan(span);
            }

            for (Part part : list){
                if (part.isValid()) {
                    if (part.getStart() < start) {
                        underlineValid(part.getStart(), start);
                    }

                    if (part.getEnd() > end) {
                        underlineValid(end, part.getEnd());
                    }
                }
            }

        }
    }

    protected boolean containUnderline(int start, int end) {
        if (start >= end) {
            return false;
        } else {
            StringBuilder builder = new StringBuilder();

            for(int i = start; i < end; ++i) {
                if (((UnderlineSpan[])this.getEditableText().getSpans(i, i + 1, UnderlineSpan.class)).length > 0) {
                    builder.append(this.getEditableText().subSequence(i, i + 1).toString());
                }
            }

            return this.getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
    }

    public void setFormatStrikethrough(boolean valid) {
        if(this.getSelectionStart() < this.getSelectionEnd()){
            if (valid) {
                this.strikethroughValid(this.getSelectionStart(), this.getSelectionEnd());
            } else {
                this.strikethroughInvalid(this.getSelectionStart(), this.getSelectionEnd());
            }
            if(historyEnable && !historyWorking){
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }
    }

    protected void strikethroughValid(int start, int end) {
        if (start < end) {
            this.getEditableText().setSpan(new StrikethroughSpan(), start, end, 33);
        }
    }

    protected void strikethroughInvalid(int start, int end) {
        if (start < end) {
            StrikethroughSpan[] strikethroughSpans = this.getEditableText().getSpans(start, end, StrikethroughSpan.class);
            List<Part> list = new ArrayList();

            for(int i = 0; i < strikethroughSpans.length; i++) {
                list.add(new Part(this.getEditableText().getSpanStart(strikethroughSpans[i]), this.getEditableText().getSpanEnd(strikethroughSpans[i])));
                this.getEditableText().removeSpan(strikethroughSpans[i]);
            }

            for (Part part : list){
                if (part.isValid()) {
                    if (part.getStart() < start) {
                        this.strikethroughValid(part.getStart(), start);
                    }

                    if (part.getEnd() > end) {
                        this.strikethroughValid(end, part.getEnd());
                    }
                }
            }

        }
    }

    protected boolean containStrikethrough(int start, int end) {
        if (start >= end) {
            return false;
        } else {
            StringBuilder builder = new StringBuilder();

            for(int i = start; i < end; ++i) {
                if ((this.getEditableText().getSpans(i, i + 1, StrikethroughSpan.class)).length > 0) {
                    builder.append(this.getEditableText().subSequence(i, i + 1).toString());
                }
            }

            return this.getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
    }

    public boolean contains(int format){
        switch (format){
            case FORMAT_BOLD:
                return containStyle(FORMAT_BOLD,getSelectionStart(),getSelectionEnd());
            case FORMAT_ITALIC:
                return containStyle(FORMAT_ITALIC,getSelectionStart(),getSelectionEnd());
            case FORMAT_UNDERLINED:
                return containUnderline(getSelectionStart(),getSelectionEnd());
            case FORMAT_STRIKETHROUGH:
                return containStrikethrough(getSelectionStart(),getSelectionEnd());



            default:
                return false;
        }
    }

    public void setTextColor(int textColor, int start, int end){
        if(start < end){
            this.getEditableText().setSpan(new ForegroundColorSpan(textColor), start, end, 33);
            if(historyEnable && !historyWorking){
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }


    }

    public void setBackgroundColor(int backgroundColor, int start, int end){
        if(start < end){
            this.getEditableText().setSpan(new BackgroundColorSpan(backgroundColor), start, end, 33);
            if(historyEnable && !historyWorking){
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }
    }

    public void insertPicture(Bitmap bitmap, int start, int end){
        if(this.getSelectionStart() <= this.getSelectionEnd()){
            if(start == end){
                this.getEditableText().insert(start," ");
                this.getEditableText().setSpan(new ImageSpan(getContext(), bitmap), getSelectionStart() - 1, getSelectionStart(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if(start < end){
                this.getEditableText().setSpan(new ImageSpan(getContext(), bitmap), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if(historyEnable && !historyWorking){
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        if (this.historyEnable && !this.historyWorking) {
            this.inputBefore = new SpannableStringBuilder(text);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable text) {
        if (historyEnable && !historyWorking) {
            inputLast = new SpannableStringBuilder(text);
            if (!text.toString().equals(inputBefore.toString())) {
                if (historyList.size() >= historySize) {
                    historyList.remove(0);
                }
                historyList.add(toHtml());
                historyCursor = historyList.size();
            }
        }
        textChanged = true;
    }

    public void redo() {
        if (this.redoValid()) {
            this.historyWorking = true;
            if (this.historyCursor >= this.historyList.size() - 1) {
                this.historyCursor = this.historyList.size();
                this.setText(this.inputLast);
            } else {
                ++this.historyCursor;
                this.setText((CharSequence)this.historyList.get(this.historyCursor));
            }

            this.setSelection(this.getEditableText().length());
            this.historyWorking = false;
        }
    }

    public void undo() {
        if (this.undoValid()) {
            this.historyWorking = true;
            if(historyCursor == historyList.size()) historyCursor -= 2;
            else --this.historyCursor;
            if(historyCursor < 0) return;
            fromHtml(historyList.get(historyCursor));
            setSelection(getEditableText().length());
            this.historyWorking = false;
        }
    }

    protected boolean redoValid() {
        if (this.historyEnable && this.historySize > 0 && this.historyList.size() > 0 && !this.historyWorking) {
            return this.historyCursor < this.historyList.size() - 1 || this.historyCursor >= this.historyList.size() - 1 && this.inputLast != null;
        } else {
            return false;
        }
    }

    protected boolean undoValid() {
        if (this.historyEnable && this.historySize > 0 && !this.historyWorking) {
            return this.historyList.size() > 0 && this.historyCursor > 0;
        } else {
            return false;
        }
    }

    public String toHtml() {
        return HtmlParser.toHtml(getEditableText());
    }
    public void fromHtml(String source) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(HtmlParser.fromHtml(source));
        setText(builder);
    }

    public boolean getTextIsChange(){
        return textChanged;
    }
    public void setTextIsChange(boolean textIsChange){
        this.textChanged = textIsChange;
    }


    public void clearHistory() {
        if (this.historyList != null) {
            this.historyList.clear();
        }

    }



    public static class Operation{
        public static final int APPEND_TEXT = 0;
        public static final int SUBTRACT_TEXT = 1;
        public static final int SET_FORMAT_BOLD = 2;
        public static final int SET_FORMAT_ITALIC = 3;
        public static final int SET_FORMAT_UNDERLINED = 4;
        public static final int SET_FORMAT_STRIKETHROUGH = 5;
        public static final int SET_FOREGROUND_COLOR = 6;
        public static final int SET_BACKGROUND_COLOR = 7;
        public static final int INSERT_PICTURE = 8;
        public static final int INSERT_NOTE = 9;
    }

}
