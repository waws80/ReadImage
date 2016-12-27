package thanatos.readimagefromlocal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import thanatos.readimagefromlocal.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= (ImageView) findViewById(R.id.iv);
    }

    public void camera(View view){
        Utils.getInstance(this).startCamera("12-27");
    }

    public void photo(View view){
        Utils.getInstance(this).startPhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = Utils.getInstance(this).getBitmap(requestCode, data);
        if (bitmap==null)return;
        imageView.setImageBitmap(bitmap);

    }
}
