package com.atsexp.fly.adapters;

import java.io.File;

import com.atsexp.fly.utils.Bookmarks;
import com.atsexp.fly.R;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BookmarksAdapter extends SimpleCursorAdapter {

	private static String[] fromColumns = { Bookmarks.NAME, Bookmarks.PATH };
	private static int[] toViews = { R.id.title, R.id.path };
	private Cursor mCursor;
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private int mTitleIndex, mPathIndex;

	public BookmarksAdapter(Context context, Cursor c) {
		super(context, R.layout.item_bookmark, c, fromColumns, toViews, 0);
		this.mLayoutInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.mCursor = c;
		this.mTitleIndex = c.getColumnIndexOrThrow(Bookmarks.NAME);
		this.mPathIndex = c.getColumnIndexOrThrow(Bookmarks.PATH);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (mCursor.moveToPosition(position)) {
			ViewHolder viewHolder;

			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_bookmark,
						parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.title.setText(mCursor.getString(mTitleIndex));
			viewHolder.path.setText(mCursor.getString(mPathIndex));
			viewHolder.remove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Uri deleteUri = ContentUris.withAppendedId(
							Bookmarks.CONTENT_URI, getItemId(position));
					mContext.getContentResolver().delete(deleteUri, null, null);
					update(mCursor);
				}
			});
		}
		return convertView;
	}

	public void createBookmark(File file) {
		Cursor c = mContext.getContentResolver().query(Bookmarks.CONTENT_URI,
				new String[] { Bookmarks._ID }, Bookmarks.PATH + "=?",
				new String[] { file.getPath() }, null);

		if (!c.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(Bookmarks.NAME, file.getName());
			values.put(Bookmarks.PATH, file.getPath());
			mContext.getContentResolver().insert(Bookmarks.CONTENT_URI, values);
			Toast.makeText(mContext, R.string.bookmarkadded, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(mContext, R.string.bookmarkexist, Toast.LENGTH_SHORT)
					.show();
		}

		update(mCursor);
	}

	@SuppressWarnings("deprecation")
	private void update(Cursor c) {
		c.requery();
		notifyDataSetChanged();
	}

	private class ViewHolder {
		ImageButton remove;
		TextView title;
		TextView path;

		ViewHolder(View v) {
			title = (TextView) v.findViewById(R.id.title);
			path = (TextView) v.findViewById(R.id.path);
			remove = (ImageButton) v.findViewById(R.id.imageButton_remove);
		}
	}
}