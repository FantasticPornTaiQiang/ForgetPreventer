package com.example.forgetpreventer.Utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StrikethroughSpan;

import org.xml.sax.XMLReader;

public class TagHandler implements Html.TagHandler{

    private static final String STRIKETHROUGH = "del";
    private static final String BACKGROUND = "bgcolor";

    //标记类
    private static class Strike {}
    private static class BackgroundColor{}

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (opening) {
            if (tag.equalsIgnoreCase(STRIKETHROUGH)) {
                start(output, new Strike());
            }else if (tag.startsWith(BACKGROUND)){
                start(output, new BackgroundColor());
            }
        } else {
            if (tag.equalsIgnoreCase(STRIKETHROUGH)) {
                end(output, Strike.class, new StrikethroughSpan());
            }else if (tag.startsWith(BACKGROUND)){
                if (tag.substring(7).charAt(0) <= '9')
                    end(output, BackgroundColor.class, new BackgroundColorSpan(Color.parseColor("#"+tag.substring(7))));
                else
                    end(output, BackgroundColor.class, new BackgroundColorSpan(colorHelper(tag.substring(7))));
            }
        }

    }

    private void start(Editable output, Object mark) {
        output.setSpan(mark, output.length(), output.length(), Spanned.SPAN_MARK_MARK);
    }

    private void end(Editable output, Class kind, Object... replaces) {
        Object last = getLast(output, kind);
        int start = output.getSpanStart(last);
        int end = output.length();
        output.removeSpan(last);

        if (start != end) {
            for (Object replace : replaces) {
                output.setSpan(replace, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static Object getLast(Editable text, Class kind) {
        Object[] spans = text.getSpans(0, text.length(), kind);

        if (spans.length == 0) {
            return null;
        } else {
            for (int i = spans.length; i > 0; i--) {
                if (text.getSpanFlags(spans[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return spans[i - 1];
                }
            }

            return null;
        }
    }

    private static int colorHelper(String color){
        switch (color){
            case "black":
                return Color.BLACK;
            case "white":
                return Color.WHITE;
            case "red":
                return Color.RED;
            case "yellow":
                return Color.YELLOW;
            case "green":
                return Color.GREEN;
            case "blue":
                return Color.BLUE;
            case "dkgray":
                return Color.DKGRAY;
            case "gray":
                return Color.GRAY;
            case "cyan":
                return Color.CYAN;
            default:
                break;
        }
        return 0;
    }

}
