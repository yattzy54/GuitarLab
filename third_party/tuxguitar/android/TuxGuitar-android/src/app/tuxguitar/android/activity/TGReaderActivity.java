package app.tuxguitar.android.activity;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.os.Bundle;

import app.tuxguitar.android.R;

public class TGReaderActivity extends TGActivity {

	public static final String NAME = "app.tuxguitar.android.activity.TGReaderActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View editingToolbar = findViewById(R.id.main_bottom);
		if (editingToolbar != null) {
			editingToolbar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		// The reader intentionally has no note or measure editing context menu.
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DEL:
			case KeyEvent.KEYCODE_TAB:
			case KeyEvent.KEYCODE_MINUS:
			case KeyEvent.KEYCODE_EQUALS:
			case KeyEvent.KEYCODE_PERIOD:
			case KeyEvent.KEYCODE_0:
			case KeyEvent.KEYCODE_1:
			case KeyEvent.KEYCODE_2:
			case KeyEvent.KEYCODE_3:
			case KeyEvent.KEYCODE_4:
			case KeyEvent.KEYCODE_5:
			case KeyEvent.KEYCODE_6:
			case KeyEvent.KEYCODE_7:
			case KeyEvent.KEYCODE_8:
			case KeyEvent.KEYCODE_9:
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}
}
