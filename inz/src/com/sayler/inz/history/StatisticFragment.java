package com.sayler.inz.history;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidplot.Plot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.sayler.inz.R;
import com.sayler.inz.data.RoadDataProvider;
import com.sayler.inz.database.DBSqliteOpenHelper;
import com.sayler.inz.database.DaoHelper;
import com.sayler.inz.database.model.Road;
import com.sayler.inz.database.model.Track;

public class StatisticFragment extends SherlockFragment {

	private static final String TAG = "StatisticFragment";
	private long roadId;

	private RoadDataProvider roadDataProvider;

	private XYPlot plot;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// get road id
		roadId = ((RoadActivity) getActivity()).getRoadId();

		// orm
		DaoHelper.setOpenHelper(getActivity().getApplicationContext(),
				DBSqliteOpenHelper.class);

		// data provider
		roadDataProvider = new RoadDataProvider();

		// view
		View view = inflater.inflate(R.layout.statistic_fragment, container,
				false);
		Log.d(TAG, "statistic");
		try {
			Road road = roadDataProvider.get(roadId);

			ArrayList<Track> tracks = (ArrayList<Track>) road.getTracks();

			// initialize our XYPlot reference:
			plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);

			// Create a couple arrays of y-values to plot:
			Number[] elevations = new Number[tracks.size()];
			Number[] times = new Number[tracks.size()];

			for (int i = 1; i < tracks.size()-2; i++) {
				elevations[i] = tracks.get(i).getAlt();
				times[i] = tracks.get(i).getCreatedAt().getTime();
				
			}

			// Turn the above arrays into XYSeries':
			XYSeries series1 = new SimpleXYSeries(
					Arrays.asList(times),
					Arrays.asList(elevations),
					"");

			// Create a formatter to use for drawing a series using
			// LineAndPointRenderer
			// and configure it from xml:
			LineAndPointFormatter series1Format = new LineAndPointFormatter();
			series1Format.setPointLabelFormatter(new PointLabelFormatter());
			series1Format.configure(getActivity().getApplicationContext(),
					R.xml.line_point_formatter_with_plf1);
			series1Format.getVertexPaint().setStrokeWidth(2);

			// add a new series' to the xyplot:
			plot.addSeries(series1, series1Format);

			// reduce the number of range labels
			plot.setTicksPerRangeLabel(2);
			plot.getGraphWidget().setDomainLabelOrientation(-45);
			plot.getLegendWidget().setVisible(false);
			
			//style
			plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
			plot.setPlotMargins(0, 0, 0, 0);
		    plot.setPlotPadding(0, 0, 0, 0);
		    plot.setGridPadding(0, 10, 5, 0);
			plot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
			plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
			plot.setTitle("");
			plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.TRANSPARENT);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}

}
