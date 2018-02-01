package br.brunodea.nevertoolate.frag;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import br.brunodea.nevertoolate.R;
import br.brunodea.nevertoolate.db.NeverTooLateContract;
import br.brunodea.nevertoolate.db.NeverTooLateDB;
import br.brunodea.nevertoolate.db.NeverTooLateDBHelper;
import br.brunodea.nevertoolate.frag.list.CursorNotificationsRecyclerViewAdapter;
import br.brunodea.nevertoolate.model.NotificationModel;
import br.brunodea.nevertoolate.util.NotificationUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link NotificationsFragment}
 * interface.
 */
public class NotificationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 20;

    @BindView(R.id.cl_notification_root) ConstraintLayout mCLRoot;
    @BindView(R.id.rv_notifications) RecyclerView mRecyclerView;
    @BindView(R.id.tv_notifications_error_message) TextView mTVErrorMessage;

    CursorNotificationsRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_list, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new CursorNotificationsRecyclerViewAdapter(getContext(), null);

        setHasOptionsMenu(false);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());

        mRecyclerView.addItemDecoration(dividerItemDecoration);

        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        return view;
    }

    public void onFabClick() {
        View dialog_notification_type = LayoutInflater.from(getContext()).inflate(R.layout.dialog_notification_type, null);
        LinearLayout daily_notification = dialog_notification_type.findViewById(R.id.ll_daily_notification);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialog_notification_type)
                .setTitle(R.string.dialog_notification_title)
                .setCancelable(true)
                .create();

        daily_notification.setOnClickListener(view -> {
            // dismiss the dialog to choose the notification type
            dialog.dismiss();
            // open time picker dialog
            TimePickerDialog.OnTimeSetListener listener = (timePicker, hour_of_day, minute) -> {
                //schedule notification
                NotificationModel nm = new NotificationModel(
                        getString(R.string.daily_notification_info_text, hour_of_day, minute),
                        0);
                NeverTooLateDB.insertNotification(getContext(), nm);
                NotificationUtil.scheduleNotification(getContext(), hour_of_day, minute, nm);
                Snackbar.make(mCLRoot, getString(R.string.notification_scheduled),
                        Snackbar.LENGTH_LONG).show();
            };
            Calendar c = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    listener,
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(getActivity()));
            timePickerDialog.setCancelable(true);
            timePickerDialog.show();
        });

        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch(id) {
            case LOADER_ID:
                return new CursorLoader(
                        getContext(),
                        NeverTooLateContract.NOTIFICATIONS_CONTENT_URI,
                        NeverTooLateDBHelper.Notifications.PROJECTION_ALL,
                        null, null, null
                );
            default:
                throw new IllegalArgumentException("Illegal loader ID: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                mAdapter.changeCursor(cursor);
                if (mAdapter.getItemCount() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mTVErrorMessage.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTVErrorMessage.setVisibility(View.GONE);
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal loader ID: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
