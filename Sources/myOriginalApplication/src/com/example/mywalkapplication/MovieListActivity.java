/**************************
 * 撮影したムービーファイルの一覧を表示
 */
package com.example.mywalkapplication;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MovieListActivity extends CommonActivity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//XMLレイアウトの読み込み
		//setContentView(R.layout.movie_play);
		
		LinearLayout ll_file_list = new LinearLayout(this);
		ll_file_list.setOrientation(LinearLayout.VERTICAL);
		//読み込みディレクトリ
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
		//ディレクトリ名を表示
		TextView tv_message = new TextView(this);
		tv_message.setText(mediaStorageDir.toString());
		ll_file_list.addView(tv_message);
		//ディレクトリからファイルを全件取得
		File[] files = mediaStorageDir.listFiles();
		Arrays.sort(files, new FileSort());
		for(File f : files)
		{
			if(f.isFile() || f.isDirectory())
			{
				try
				{
					//１ファイルごとにボタンを作成
					Button bt = new Button(this);
					bt.setText(f.getName());
					bt.setTag(f.getName());
					bt.setMinimumWidth(400);
					bt.setOnClickListener(new MoviePlayButtonClickListener());
					ll_file_list.addView(bt);
				}
				catch(Exception e){}
			}
		}
		//上記のボタン一覧を画面に表示
		LinearLayout ll_root = new LinearLayout(this);
		//スクロール可能にする
		ScrollView sv_scroll = new ScrollView(this);
		sv_scroll.addView(ll_file_list);
		ll_root.addView(sv_scroll);
		setContentView(ll_root);
		/*
		Button bt_movie = (Button)findViewById(R.id.bt_movie);
		bt_movie.setTag(BUTTON_TAG_MOVIE);
		bt_movie.setOnClickListener(new ButtonClickListener());
		*/
	}
	
	/*************
	 * ファイルのソートを行う
	 * @author m_sugawara
	 *
	 */
	static class FileSort implements Comparator<File> {
		public int compare(File src, File target) {
		    //int diff = src.getName().compareTo(target.getName());//昇順
		    int diff = target.getName().compareTo(src.getName());//降順
		    return diff;
		}
    }
	
	public class MoviePlayButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//選択されたファイルの名前を取得
			String tag = v.getTag().toString();

			Intent intent = new Intent(MovieListActivity.this, MoviePlayActivity.class);
			intent.putExtra("FILE_NAME", tag);
			startActivity(intent);
		}
		
	}

}
