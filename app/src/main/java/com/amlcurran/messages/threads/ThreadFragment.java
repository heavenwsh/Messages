package com.amlcurran.messages.threads;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amlcurran.messages.ListeningCursorListFragment;
import com.amlcurran.messages.R;
import com.amlcurran.messages.adapters.AdaptiveCursorSource;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.espian.utils.SimpleBinder;
import com.espian.utils.SourceBinderAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadFragment extends ListeningCursorListFragment<ThreadMessage> {

    private static final String THREAD_ID = "threadId";
    private static final String ADDRESS = "address";

    public static ThreadFragment create(String threadId, String address) {
        Bundle bundle = new Bundle();
        bundle.putString(THREAD_ID, threadId);
        bundle.putString(ADDRESS, address);

        ThreadFragment fragment = new ThreadFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thread, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        source = new ThreadMessageAdaptiveCursorSource();
        adapter = new SourceBinderAdapter<ThreadMessage>(getActivity(), source, new ThreadBinder());
        setListAdapter(adapter);
        getListView().setStackFromBottom(true);

    }

    @Override
    public void loadData(MessagesLoader loader) {
        loader.loadThread(getArguments().getString(THREAD_ID), this);
    }

    private class ThreadBinder extends SimpleBinder<ThreadMessage> {

        private static final int ITEM_ME = 0;
        private static final int ITEM_THEM = 1;
        private final DateFormat formatter = new SimpleDateFormat("HH:mm dd-MMM-yy");
        private final Date date = new Date();

        @Override
        public View bindView(View convertView, ThreadMessage item, int position) {

            date.setTime(item.getTimestamp());

            getTextView(convertView, android.R.id.text1).setText(item.getBody());
            getTextView(convertView, android.R.id.text2).setText(formatter.format(date));

            return convertView;
        }

        @Override
        public View createView(Context context, int itemViewType) {
            if (itemViewType == ITEM_ME) {
                return LayoutInflater.from(context).inflate(R.layout.item_thread_item_me, getListView(), false);
            } else if (itemViewType == ITEM_THEM) {
                return LayoutInflater.from(context).inflate(R.layout.item_thread_item_them, getListView(), false);
            }
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position, ThreadMessage item) {
            return item.isFromMe() ? ITEM_ME : ITEM_THEM;
        }

        private TextView getTextView(View convertView, int text1) {
            return (TextView) convertView.findViewById(text1);
        }

    }

    private static class ThreadMessageAdaptiveCursorSource extends AdaptiveCursorSource<ThreadMessage> {

        @Override
        public ThreadMessage getFromCursorRow(Cursor cursor) {
            return ThreadMessage.fromCursor(cursor);
        }
    }

}
