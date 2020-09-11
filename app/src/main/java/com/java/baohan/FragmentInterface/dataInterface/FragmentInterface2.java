package com.java.baohan.FragmentInterface.dataInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.java.baohan.R;
import com.java.baohan.backend.DataEntry;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//Fragment for Epidemic data
public class FragmentInterface2 extends Fragment  implements OnChartValueSelectedListener {

    private static FragmentInterface2 INSTANCE = null;

    private DataViewModel mViewModel;

    ArrayList<Fragment> fragments_china = new ArrayList<>();
    ArrayList<Fragment> fragments_world = new ArrayList<>();

    private static final String[] table_titles = new String[]{"现存确诊", "累计感染", "累计死亡", "累计治愈"};

    private LineChart chart;
    private TextView tvX, tvY;

    private FragmentInterface2() {}

    public static FragmentInterface2 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FragmentInterface2();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        //TODO: integrate this to start up
//        while(!mViewModel.dataReady) {
//            try {
//                Thread.sleep(1);
//            } catch (Exception e) {}
//        }
        if (!mViewModel.dataReady)
            return;

        loadData();
    }

    private void loadData() {
        // add charts
        ArrayList<Integer> xs;
        ArrayList<ArrayList<Integer>> ys;

        // world series
        xs = new ArrayList<>();
        ys = new ArrayList<>();
        for (int i = 0; i < table_titles.length; i++)
            ys.add(new ArrayList<>());
        for(DataEntry e: mViewModel.worldTimeSeries) {
            LocalDate d = e.date;
            xs.add(d.getMonthValue() * 100 + d.getDayOfMonth());
            ys.get(0).add(e.getCurConfirmed());
            ys.get(1).add(e.confirmed);
            ys.get(2).add(e.dead);
            ys.get(3).add(e.cured);
        }
        fragments_world.clear();
        for (int i = 0; i < table_titles.length; i++)
            fragments_world.add(ChartFragment.newInstance(table_titles[i], xs, ys.get(i)));

        // china series
        xs = new ArrayList<>();
        ys = new ArrayList<>();
        for (int i = 0; i < table_titles.length; i++)
            ys.add(new ArrayList<>());
        for(DataEntry e: mViewModel.chinaTimeSeries) {
            LocalDate d = e.date;
            xs.add(d.getMonthValue() * 100 + d.getDayOfMonth());
            ys.get(0).add(e.getCurConfirmed());
            ys.get(1).add(e.confirmed);
            ys.get(2).add(e.dead);
            ys.get(3).add(e.cured);
        }
        fragments_china.clear();
        for (int i = 0; i < table_titles.length; i++)
            fragments_china.add(ChartFragment.newInstance(table_titles[i], xs, ys.get(i)));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_interface2, container, false);
        if(!mViewModel.dataReady)
            return root;
        else if (fragments_world.isEmpty())
            loadData();

        // setup charts
        TabLayout tab = root.findViewById(R.id.charts_china);
        ViewPager pager = (ViewPager) root.findViewById(R.id.pager_china);
        ChartFragmentAdapter adapter = new ChartFragmentAdapter(getChildFragmentManager(), getContext(), fragments_china);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        for(int i = 0; i < table_titles.length; i++)
            tab.getTabAt(i).setText(table_titles[i]);

        tab = root.findViewById(R.id.charts_world);
        pager = (ViewPager) root.findViewById(R.id.pager_world);
        adapter = new ChartFragmentAdapter(getChildFragmentManager(), getContext(), fragments_world);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        for(int i = 0; i < table_titles.length; i++)
            tab.getTabAt(i).setText(table_titles[i]);

       // inflate tables
        inflateSnapShotTable(root.findViewById(R.id.dataTable_China), mViewModel.getChinaStats());
        inflateSnapShotTable(root.findViewById(R.id.dataTable_World), mViewModel.getWorldStats());

        inflateDetailTable1(root.findViewById(R.id.detail_table_china), mViewModel.provinceDataSnapshot, mViewModel.provinceDetailDataSnaphot);
        inflateDetailTable2(root.findViewById(R.id.detail_table_world), mViewModel.keyCountrySnapshot, 20);
        return root;
    }


    private void inflateSnapShotTable(TableLayout table, Map<String, Map<String, Integer>> data) {
        if(data == null)
            return;

        TextView t;

        Map<String, Integer> cum = data.get("cumulative");
        t = table.findViewById(R.id.textCurrentSick);
        t.setText(Integer.toString(cum.get(DataViewModel.STAT_ENTRIES[0])));
        t = table.findViewById(R.id.textTotalSick);
        t.setText(Integer.toString(cum.get(DataViewModel.STAT_ENTRIES[1])));
        t = table.findViewById(R.id.textTotalCured);
        t.setText(Integer.toString(cum.get(DataViewModel.STAT_ENTRIES[2])));
        t = table.findViewById(R.id.textTotalDeath);
        t.setText(Integer.toString(cum.get(DataViewModel.STAT_ENTRIES[2])));

        cum = data.get("change");
        t = table.findViewById(R.id.textNewCurSick);
        int val = cum.get(DataViewModel.STAT_ENTRIES[0]);
        t.setText((val > 0 ? "+" : "") + Integer.toString(val));
        t = table.findViewById(R.id.textNewTotalSick);
        val = cum.get(DataViewModel.STAT_ENTRIES[1]);
        t.setText((val > 0 ? "+" : "") + Integer.toString(val));
        t = table.findViewById(R.id.textNewTotalCured);
        val = cum.get(DataViewModel.STAT_ENTRIES[2]);
        t.setText((val > 0 ? "+" : "") + Integer.toString(val));
        t = table.findViewById(R.id.textNewTotalDeath);
        val = cum.get(DataViewModel.STAT_ENTRIES[3]);
        t.setText((val > 0 ? "+" : "") + Integer.toString(val));
    }

    private void inflateDetailTable1(TableLayout table, Map<String, DataEntry> sum, Map<String, LinkedHashMap<String, DataEntry>> details) {
        if (sum == null)
            return;

        boolean odd = false;
        for(String prov: sum.keySet()) {
            TableRow tbRow = new TableRow(this.getContext());
            tbRow.setPadding(0, 1, 0, 1);
            //#EAEFDD
            tbRow.setBackgroundColor(odd ? Color.WHITE : Color.rgb(0xEA, 0xEF, 0xDD));
            odd = !odd;

            DataEntry e = sum.get(prov);
            boolean hasDetail = details.containsKey(prov);
            tbRow.addView(hasDetail ? getTextView("\u25B6", prov) : getTextView(prov));
            tbRow.addView(getTextView(e.dispCurConfirmed()));
            tbRow.addView(getTextView(e.dispConfirmed()));
            tbRow.addView(getTextView(e.dispDead()));
            tbRow.addView(getTextView(e.dispCured()));

            table.addView(tbRow);

            if(hasDetail) {
                List<TableRow> subTable = new ArrayList<>();
                for (Map.Entry<String, DataEntry> entry: details.get(prov).entrySet()) {
                    TableRow subR = new TableRow(this.getContext());
                    subR.setPadding(0, 1, 0, 1);
                    subR.setBackgroundColor(Color.WHITE);

                    String city = entry.getKey();
                    city = city.length() > 8 ? city.substring(0, 8) : city;
                    subR.addView(getTextView("  " + city));

                    DataEntry en = entry.getValue();
                    subR.addView(getTextView(en.dispCurConfirmed()));
                    subR.addView(getTextView(en.dispConfirmed()));
                    subR.addView(getTextView(en.dispDead()));
                    subR.addView(getTextView(en.dispCured()));
                    subR.setVisibility(View.GONE);
                    table.addView(subR);
                    subTable.add(subR);
                }
                tbRow.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView t = (TextView)((LinearLayout)view).getChildAt(0);
                        if (t.getText().equals("\u25B6"))
                            t.setText("\u25BC");
                        else
                            t.setText("\u25B6");
                        for(TableRow row: subTable) {
                            row.setVisibility(8 - row.getVisibility());
                        }
                    }
                });
            }
        }
    }

    private void inflateDetailTable2(TableLayout table, Map<String, DataEntry> sum, int topN) {
        if (sum == null)
            return;

        int i = 0;
        for(String prov: sum.keySet()) {
            TableRow tbRow = new TableRow(this.getContext());
            tbRow.setPadding(0, 1, 0, 1);
            tbRow.setBackgroundColor(i%2==0 ? Color.WHITE : Color.rgb(0xEA, 0xEF, 0xDD));

            DataEntry e = sum.get(prov);
            tbRow.addView(getTextView(prov));
            tbRow.addView(getTextView(e.dispCurConfirmed()));
            tbRow.addView(getTextView(e.dispConfirmed()));
            tbRow.addView(getTextView(e.dispDead()));
            tbRow.addView(getTextView(e.dispCured()));

            table.addView(tbRow);
            if (++i >= topN) {
                break;
            }
        }
    }

    private LinearLayout getTextView(String prefix, String text) {
        TableRow.LayoutParams l = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        l.setMargins(1, 0, 0, 0);
        LinearLayout cell = new LinearLayout(this.getContext());
        cell.setLayoutParams(l);
        if(prefix != null) {
            TextView p = new TextView(this.getContext());
            p.setText(prefix);
            cell.addView(p);
        }
        TextView t = new TextView(this.getContext());
        t.setText(text);
        t.setPadding(5, 5, 5, 5);
        cell.addView(t);
        return cell;
    }

    private LinearLayout getTextView(String text) {
        return getTextView(null, text);
    }

    private final int[] colors = new int[] {
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {}
}
