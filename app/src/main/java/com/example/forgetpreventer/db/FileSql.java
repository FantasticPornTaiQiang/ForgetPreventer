package com.example.forgetpreventer.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class FileSql extends LitePalSupport {

    private int id;
    private String title;
    private String content;
    private String textContent;
    private String time;
    private String add;
    private String keyStart;
    private String keyEnd;
    @Column(defaultValue = "false")
    private boolean isTop;

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }


    public boolean isTop() {
        return isTop;
    }


    public String getTextContent() {
        return textContent;
    }

    public String getAdd() {
        return add;
    }

    public String getKeyStart() {
        return keyStart;
    }

    public String getKeyEnd() {
        return keyEnd;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setText_content(String textContent) {
        this.textContent = textContent;
    }


    public void setAdd(String add) {
        this.add = add;
    }

    public void setKeyStart(String keyStart) {
        this.keyStart = keyStart;
    }

    public void setKeyEnd(String keyEnd) {
        this.keyEnd = keyEnd;
    }
}


