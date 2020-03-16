package com.onlyvtc.ui.activity.recurrent;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.onlyvtc.R;
import com.onlyvtc.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepeatWeekdayActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    RecyclerView container;

    private ArrayList<String> m_aryData = new ArrayList<>();
    private ArrayList<String> m_arySelected = new ArrayList<>();
    private RepeatAdapter adapter;

    public static final String kWeekOfDay = "kWeekOfDay";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_repeat_weekday;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m_aryData.add(getString(R.string.sunday));
        m_aryData.add(getString(R.string.monday));
        m_aryData.add(getString(R.string.tuesday));
        m_aryData.add(getString(R.string.wednesday));
        m_aryData.add(getString(R.string.thursday));
        m_aryData.add(getString(R.string.friday));
        m_aryData.add(getString(R.string.saturday));

        container.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RepeatAdapter();
        container.setAdapter(adapter);

        if (getIntent().hasExtra(kWeekOfDay)) {
            m_arySelected = getIntent().getStringArrayListExtra(kWeekOfDay);
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repeat_done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent intent = getIntent();
            intent.putExtra(kWeekOfDay, m_arySelected);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class RepeatAdapter extends RecyclerView.Adapter<RepeatAdapter.MyViewHolder> {
        @NonNull
        @Override
        public RepeatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(RepeatWeekdayActivity.this)
                    .inflate(R.layout.list_item_recurrent, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RepeatAdapter.MyViewHolder viewHolder, int i) {
            viewHolder.chkDayOfWeek.setChecked(m_arySelected.contains(String.valueOf(i)));
            viewHolder.chkDayOfWeek.setText(m_aryData.get(i));
            viewHolder.chkDayOfWeek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        m_arySelected.add(String.valueOf(i));
                    } else {
                        m_arySelected.remove(String.valueOf(i));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return m_aryData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CheckBox chkDayOfWeek;

            public MyViewHolder(View itemView) {
                super(itemView);

                chkDayOfWeek = itemView.findViewById(R.id.checkbox);
            }
        }
    }


}
