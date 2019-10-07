package com.example.forgetpreventer.Utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;


public class HtmlParser {


    public static Spanned fromHtml(String source) {
        return Html.fromHtml(source, null, new TagHandler());
    }

    public static String toHtml(Spanned text) {
        StringBuilder out = new StringBuilder();
        withinHtml(out, text);
        return out.toString();
    }

    private static void withinHtml(StringBuilder out, Spanned text) {
        int next;

        for (int i = 0; i < text.length(); i = next) {
            next = text.nextSpanTransition(i, text.length(), ParagraphStyle.class);
            withinContent(out, text, i, next);
        }
    }


    private static void withinContent(StringBuilder out, Spanned text, int start, int end) {
        int next;

        for (int i = start; i < end; i = next) {
            next = TextUtils.indexOf(text, '\n', i, end);
            if (next < 0) {
                next = end;
            }
            int nl = 0;
            while (next < end && text.charAt(next) == '\n') {
                next++;
                nl++;
            }
            withinParagraph(out, text, i, next - nl);
            for (int n = 0 ;n < nl; n++)
                out.append("<br>");
        }
    }

    private static void withinParagraph(StringBuilder out, Spanned text, int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, CharacterStyle.class);
            CharacterStyle[] spans = text.getSpans(i, next, CharacterStyle.class);
            //头
            for (int j = 0; j < spans.length; j++) {
                if (spans[j] instanceof StyleSpan) {
                    int style = ((StyleSpan) spans[j]).getStyle();

                    if ((style & Typeface.BOLD) != 0) {
                        out.append("<b>");
                    }

                    if ((style & Typeface.ITALIC) != 0) {
                        out.append("<i>");
                    }
                }

                if (spans[j] instanceof UnderlineSpan) {
                    out.append("<u>");
                }

                if (spans[j] instanceof StrikethroughSpan) {
                    out.append("<del>");
                }

                if (spans[j] instanceof ImageSpan) {
                    out.append("<img src=\"");
                    out.append(((ImageSpan) spans[j]).getSource());
                    out.append("\">");

                    i = next;
                }
                if (spans[j] instanceof ForegroundColorSpan){
                    out.append("<font color=\"");
                    out.append(getColor(((ForegroundColorSpan)spans[j]).getForegroundColor()));
                    out.append("\">");
                }
                if (spans[j] instanceof BackgroundColorSpan){
                    out.append("<bgcolor=\"");
                    out.append(getColor(((BackgroundColorSpan)spans[j]).getBackgroundColor()));
                    out.append("\">");
                }

            }
//            out.append("<br>");
            withinStyle(out, text, i, next);
            //尾
            for (int j = spans.length - 1; j >= 0; j--) {

                if (spans[j] instanceof StrikethroughSpan) {
                    out.append("</del>");
                }

                if (spans[j] instanceof UnderlineSpan) {
                    out.append("</u>");
                }

                if (spans[j] instanceof StyleSpan) {
                    int style = ((StyleSpan) spans[j]).getStyle();

                    if ((style & Typeface.BOLD) != 0) {
                        out.append("</b>");
                    }

                    if ((style & Typeface.ITALIC) != 0) {
                        out.append("</i>");
                    }
                }
                if (spans[j] instanceof ForegroundColorSpan){
                    out.append("</font>");
                }
                if (spans[j] instanceof BackgroundColorSpan){
                    out.append("</bgcolor=\"");
                    out.append(getColor(((BackgroundColorSpan)spans[j]).getBackgroundColor()));
                    out.append("\">");
                }

            }
        }

    }
    private static String getColor(int color){
        int a = Color.parseColor("#548B54");
        int b = Color.parseColor("#912CEE");
        int c = Color.parseColor("#90EE90");
        int d = Color.parseColor("#EEE9BF");
        int e = Color.parseColor("#4B0082");
        int f = Color.parseColor("#0000CD");
        int g = Color.parseColor("#CD9B1D");
        int h = Color.parseColor("#B03060");
        int i = Color.parseColor("#DB7093");

        switch (color){
            case Color.BLACK:
                return "black";
            case Color.WHITE:
                return "white";
            case Color.RED:
                return "red";
            case Color.YELLOW:
                return "yellow";
            case Color.GREEN:
                return "green";
            case Color.BLUE:
                return "blue";
            case Color.DKGRAY:
                return "dkgray";
            case Color.GRAY:
                return "gray";
            case Color.CYAN:
                return "cyan";
            default:
                break;
        }
        if (color == a)
            return "#548B54";
        else if (color == b)
            return "#912CEE";
        else if (color == c)
            return "#90EE90";
        else if (color == d)
            return "#EEE9BF";
        else if (color == e)
            return "#4B0082";
        else if (color == f)
            return "#0000CD";
        else if (color == g)
            return "#CD9B1D";
        else if (color == h)
            return "#B03060";
        else if (color == i)
            return "#DB7093";

        return "#FFFFFF";
    }

    private static void withinStyle(StringBuilder out, CharSequence text, int start, int end) {
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c >= 0xD800 && c <= 0xDFFF) {
                if (c < 0xDC00 && i + 1 < end) {
                    char d = text.charAt(i + 1);
                    if (d >= 0xDC00 && d <= 0xDFFF) {
                        i++;
                        int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                        out.append("&#").append(codepoint).append(";");
                    }
                }
            } else if (c > 0x7E || c < ' ') {
                out.append("&#").append((int) c).append(";");
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }

                out.append(' ');
            } else {
                out.append(c);
            }
        }
    }

}
