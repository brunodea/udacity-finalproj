package br.brunodea.nevertoolate.frag.list;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.brunodea.nevertoolate.R;
import br.brunodea.nevertoolate.db.NeverTooLateDB;

public class CursorNotificationsRecyclerViewAdapter extends CursorRecyclerViewAdapter<NotificationsViewHolder> {

    public CursorNotificationsRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder viewHolder, Cursor cursor) {
        viewHolder.onBind(NeverTooLateDB.fromNotificationsTableCursor(cursor));
    }
}