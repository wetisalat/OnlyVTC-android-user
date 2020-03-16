package com.onlyvtc.ui.fragment.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onlyvtc.R;
import com.onlyvtc.base.BaseFragment;
import com.onlyvtc.ui.activity.main.MainActivity;
import com.onlyvtc.ui.activity.recurrent.RepeatWeekdayActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;
import static com.onlyvtc.MvpApplication.RIDE_REQUEST;
import static com.onlyvtc.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.onlyvtc.common.Constants.Status.EMPTY;

public class ScheduleFragment extends BaseFragment implements ScheduleIView {

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.tvRepeat)
    TextView tvRepeat;

    private String selectedScheduledTime;
    private String selectedScheduledHour;
    private String AM_PM;
    private String selectedTime;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    //    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private ArrayList<String> aryRepeatDates = new ArrayList<>();
    private ArrayList<Integer> aryRepeatIntDatas = new ArrayList<>();

    public static final int REQ_CODE_REPEAT = 10001;

    private SchedulePresenter<ScheduleFragment> presenter = new SchedulePresenter<>();

    public ScheduleFragment() {

        dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(simpleDateFormat.format(myCalendar.getTime()));
        };

        timeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            selectedScheduledTime = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
            selectedScheduledHour = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
            selectedTime = selectedScheduledHour + ":" + selectedScheduledTime;
            time.setText(selectedTime);
//            AM_PM = selectedHour < 12 ? "AM" : "PM";
//            time.setText(String.format("%s %s", selectedTime, AM_PM));
        };
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.date, R.id.time, R.id.schedule_request, R.id.repeat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.date:
                datePicker(dateSetListener);
                break;
            case R.id.time:
                timePicker(timeSetListener);
                break;
            case R.id.schedule_request:
                if (date.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
                    Toast.makeText(baseActivity(), R.string.please_select_date_time, Toast.LENGTH_SHORT).show();
                    return;
                }
                showNoteDialog();
                break;
            case R.id.repeat:
                Intent intent = new Intent(getContext(), RepeatWeekdayActivity.class);
                intent.putExtra(RepeatWeekdayActivity.kWeekOfDay, aryRepeatDates);
                getActivity().startActivityForResult(intent, REQ_CODE_REPEAT);
                break;
        }
    }

    private void sendRequest(String note) {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("schedule_date", date.getText().toString());
        map.put("schedule_time", selectedTime);
        map.put("chbs_service_type_id", "2");
        map.put("note", note);
        showLoading();

        convIntFromStr(aryRepeatDates);

        if (aryRepeatIntDatas.size() > 0) {
            presenter.sendRequest(map, aryRepeatIntDatas);
        } else {
            presenter.sendRequest(map);
        }

        Log.d("REQUEST_", "send request " + map.toString());
    }

    private void convIntFromStr(ArrayList<String> datas) {

        for (int nIndex = 0; nIndex < datas.size(); nIndex++) {
            aryRepeatIntDatas.add(Integer.valueOf(datas.get(nIndex)));

        }
    }

    private void setAryRepeatDates(ArrayList<String> dates) {
        if (dates == null) return;
        aryRepeatDates = dates;
        tvRepeat.setText(getString(R.string.never));
        if (aryRepeatDates.size() > 0) {
//            String string = TextUtils.join(", ", aryRepeatDates);
            String string = "";
            for (String s : aryRepeatDates) {
                string += getWeekdayFromWeekID(s).substring(0, 3) + ",";
            }
            tvRepeat.setText(string.substring(0, string.length() - 1));
        }
    }

    public String getWeekdayFromWeekID(String strWeekID) {

        String strWeekday = "";

        switch (strWeekID) {
            case "0":
                strWeekday = getString(R.string.sunday);
                break;
            case "1":
                strWeekday = getString(R.string.monday);
                break;
            case "2":
                strWeekday = getString(R.string.tuesday);
                break;
            case "3":
                strWeekday = getString(R.string.wednesday);
                break;
            case "4":
                strWeekday = getString(R.string.thursday);
                break;
            case "5":
                strWeekday = getString(R.string.friday);
                break;
            case "6":
                strWeekday = getString(R.string.saturday);
                break;
        }
        return strWeekday;
    }

    private void showNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_note, null);

        Button submitBtn = view.findViewById(R.id.submit_btn);
        Button dismiss = view.findViewById(R.id.dismiss);
        final EditText note = view.findViewById(R.id.note);

        builder.setView(view);
        AlertDialog addTollDialog = builder.create();
        Objects.requireNonNull(addTollDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submitBtn.setOnClickListener(view1 -> {
            addTollDialog.dismiss();
            sendRequest(note.getText().toString());
            note.setText("");
        });

        dismiss.setOnClickListener(v -> {
            addTollDialog.dismiss();
            sendRequest("");
            note.setText("");
        });
        addTollDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_REPEAT && resultCode == RESULT_OK && data != null) {
            setAryRepeatDates(data.getStringArrayListExtra(RepeatWeekdayActivity.kWeekOfDay));
        }
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Toasty.success(Objects.requireNonNull(getActivity()), getString(R.string.success_schedule_ride_created), Toast.LENGTH_SHORT).show();
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY);

    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
