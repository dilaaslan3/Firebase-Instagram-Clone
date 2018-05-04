package com.dilaaslan.firebaseinstagram;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

//postların paylaşıldığı sayfa
public class PostClass extends ArrayAdapter<String> {
    private final ArrayList<String> useremail;
    private final ArrayList<String> userImage;
    private final ArrayList<String> userComment;
    private final Activity context;

    public PostClass(ArrayList<String> useremail, ArrayList<String> userImage, ArrayList<String> userComment, Activity context) {
        //constructor oluşturuyorum
        super(context,R.layout.custom_view,useremail); // kullanıcı emaili ile alacağım
        this.useremail = useremail;
        this.userImage = userImage;
        this.userComment = userComment;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

    //burada gerekli tanımlamalar yapılarak postlara ulaşılmaya çalışacağım

        return super.getView(position, convertView, parent);
    }
}
