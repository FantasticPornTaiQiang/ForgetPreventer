package com.example.forgetpreventer;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.forgetpreventer.Utils.ColorPickerDialog;
import com.example.forgetpreventer.Utils.FileSql;
import com.example.forgetpreventer.Utils.OnColorChangedListener;
import com.example.forgetpreventer.Utils.RichText;

public class EditActivity extends AppCompatActivity {

    private static final int PHOTO_ALBUM = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int IMAGE = 1;
    private static final int CAMERA = 2;

    private RichText richText;
    private TextView confirm;

    private int start;
    private int end;
    private static int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        richText = (RichText)findViewById(R.id.rich_text);
        confirm = findViewById(R.id.finishText);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileSql sql = new FileSql();
                sql.setId(id);
                sql.setContent(richText.toHtml());
                sql.save();
                id++;
            }
        });


        initFunctions();

    }



    private void initFunctions(){
        initBold();
        initItalic();
        initUnderline();
        initStrikethrough();
        initSetTextColor();
        initSetBackgroundColor();
        initInsertPicture();
        initRedo();
        initUndo();
      //  Intent intent = getIntent();
     //   id = intent.getIntExtra("id",-1);
       // richText.fromHtml(intent.getStringExtra("content"));

    }

    private void initBold(){
        Button boldButton = (Button) findViewById(R.id.bold);

        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richText.setFormatBold(!richText.contains(RichText.FORMAT_BOLD));
            }
        });

        boldButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditActivity.this, "bold", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initItalic(){
        Button italicButton = (Button) findViewById(R.id.italic);

        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richText.setFormatItalic(!richText.contains(RichText.FORMAT_ITALIC));
            }
        });

        italicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditActivity.this, "italic", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initUnderline(){
        Button underlineButton = (Button) findViewById(R.id.underline);

        underlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richText.setFormatUnderlined(!richText.contains(RichText.FORMAT_UNDERLINED));
            }
        });

        underlineButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditActivity.this, "underline", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initStrikethrough(){
        Button strikethroughButton = (Button) findViewById(R.id.strikethrough);

        strikethroughButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richText.setFormatStrikethrough(!richText.contains(RichText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethroughButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditActivity.this, "strikethrough", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void initSetTextColor(){

        Button textColorButton = findViewById(R.id.textcolor);

        textColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = richText.getSelectionStart();
                end = richText.getSelectionEnd();
                if (start == end){
                    return;
                }
                int [] colors = new int[]{Color.YELLOW,Color.BLACK,Color.BLUE,Color.GRAY,
                        Color.GREEN,Color.CYAN,Color.RED,Color.DKGRAY, Color.LTGRAY,Color.MAGENTA,
                        Color.rgb(100,22,33),Color.rgb(82,182,2), Color.rgb(122,32,12),Color.rgb(82,12,2),
                        Color.rgb(89,23,200),Color.rgb(13,222,23), Color.rgb(222,22,2),Color.rgb(2,22,222)};


                ColorPickerDialog dialog =
                        new ColorPickerDialog(EditActivity.this, colors)
                                .setDismissAfterClick(true)
                                .setTitle("颜色选择")
                                .setCheckedColor(Color.BLACK)
                                .setOnColorChangedListener(new OnColorChangedListener() {
                                    @Override
                                    public void onColorChanged(int foregroundColor) {
                                        richText.setTextColor(foregroundColor, start, end);
                                    }})
                                .build(6)
                                .show();

            }
        });
    }

    private void initSetBackgroundColor(){

        Button backgroundColorButton = findViewById(R.id.backgroundcolor);

        backgroundColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = richText.getSelectionStart();
                end = richText.getSelectionEnd();
                if (start == end){
                    return;
                }
                int [] colors = new int[]{Color.YELLOW,Color.BLACK,Color.BLUE,Color.GRAY,
                        Color.GREEN,Color.CYAN,Color.RED,Color.DKGRAY, Color.LTGRAY,Color.MAGENTA,
                        Color.rgb(100,22,33),Color.rgb(82,182,2), Color.rgb(122,32,12),Color.rgb(82,12,2),
                        Color.rgb(89,23,200),Color.rgb(13,222,23), Color.rgb(222,22,2),Color.rgb(2,22,222)};


                ColorPickerDialog dialog =
                        new ColorPickerDialog(EditActivity.this, colors)
                                .setDismissAfterClick(true)
                                .setTitle("颜色选择")
                                .setCheckedColor(Color.BLACK)
                                .setOnColorChangedListener(new OnColorChangedListener() {
                                    @Override
                                    public void onColorChanged(int backgroundColor) {
                                        richText.setBackgroundColor(backgroundColor, start, end);
                                    }})
                                .build(6)
                                .show();

            }
        });
    }

    private void getPhotoAlbumImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE);
    }

    private void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case IMAGE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    String imagePath = c.getString(columnIndex);
                    bitmap = BitmapFactory.decodeFile(imagePath);
                    richText.insertPicture(bitmap, start, end);
                    c.close();
                    break;
                case CAMERA:
                    Bundle bundle = data.getExtras();
                    bitmap = (Bitmap) bundle.get("data");
                    richText.insertPicture(bitmap, start, end);
            }
        }

    }

    @Override//当还没有权限时，向用户申请权限，用户同意或拒绝都回调此函数
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PHOTO_ALBUM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhotoAlbumImage();
                } else {
                    Toast.makeText(this, "你拒绝了访问相册", Toast.LENGTH_SHORT).show();
                }
                break;

            case TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "你拒绝了访问相机", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initInsertPicture(){
        Button insertPictureButton = findViewById(R.id.insert_picture);

        insertPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override//点击“插入图片”
            public void onClick(View view) {
                start = richText.getSelectionStart();
                end = richText.getSelectionEnd();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setTitle("请选择方式");
                builder.setPositiveButton("相册选取", new DialogInterface.OnClickListener() {
                    @Override//点击对话框的“相册选取”按钮
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if(ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EditActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_ALBUM);
                        } else {
                            getPhotoAlbumImage();
                        }
                    }
                });
                builder.setNegativeButton("拍照选取", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                            ActivityCompat.requestPermissions(EditActivity.this, new String[]{ Manifest.permission.CAMERA}, TAKE_PHOTO);
                        } else {
                            takePhoto();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void initRedo(){
        Button redoButton = findViewById(R.id.redo);

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richText.redo();
            }
        });
    }

    private void initUndo(){
        Button undoButton = findViewById(R.id.undo);

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richText.undo();
            }
        });
    }



}
