package com.sayler.inz.history;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.sayler.inz.data.RoadDataProvider;

public class StatisticFragment extends SherlockFragment {

	private static final String TAG = "StatisticFragment";
	private long roadId;

	private RoadDataProvider roadDataProvider;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// get road id
		roadId = ((RoadActivity) getActivity()).getRoadId();

		//View view = inflater.inflate(R.layout.statistic_fragment, container, false);

		DrawView drawView = new DrawView(getActivity());
		
		return drawView;
	}
	
	
	class DrawView extends View {
        Paint paint = new Paint();

        public DrawView(Context context) {
            super(context);
            paint.setColor(Color.BLUE);
        }
        @Override
        public void onDraw(Canvas canvas) {
             super.onDraw(canvas);
             int wi = canvas.getWidth();
                canvas.drawLine(10, 20, 30, 40, paint);
                canvas.drawLine(20, 10, 50, 20, paint);

        }
}

}
