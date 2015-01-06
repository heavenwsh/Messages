/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlcurran.messages.conversationlist;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.amlcurran.messages.DependencyRepository;
import com.amlcurran.messages.R;
import com.amlcurran.messages.SingletonManager;
import com.amlcurran.messages.conversationlist.adapter.CheckedStateProvider;
import com.amlcurran.messages.conversationlist.adapter.ConversationViewHolder;
import com.amlcurran.messages.conversationlist.adapter.ConversationsRecyclerBinder;
import com.amlcurran.messages.conversationlist.adapter.TextFormatter;
import com.amlcurran.messages.core.conversationlist.ConversationListView;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.threads.DefaultContactClickListener;
import com.amlcurran.messages.ui.control.Master;
import com.github.amlcurran.sourcebinder.ListSource;
import com.github.amlcurran.sourcebinder.recyclerview.RecyclerSourceBinderAdapter;

import java.util.HashMap;
import java.util.Map;

public class ConversationListFragment extends Fragment implements ConversationListView, Master, ConversationListView.ConversationSelectedListener {

    private View loadingView;
    private View emptyView;
    private ConversationListViewController conversationController;
    private ConversationSelectedListener conversationSelectedListener;
    private RecyclerView recyclerView;
    private final Map<String, Boolean> checkedItems = new HashMap<>();
    private RecyclerSourceBinderAdapter<Conversation, ConversationViewHolder> adapter;
    private final ListSource<Conversation> source = new ListSource<>();

    public ConversationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_recycler, container, false);
        loadingView = view.findViewById(R.id.loading);
        emptyView = view.findViewById(R.id.empty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DeleteThreadViewCallback deleteThreadsViewCallback = (DeleteThreadViewCallback) getActivity();
        DependencyRepository dependencyRepository = (DependencyRepository) getActivity();
        conversationController = new ConversationListViewController(this, source, dependencyRepository, SingletonManager.getConversationList(getActivity()));

        ConversationModalMarshall listener = new ConversationModalMarshall(source, new DefaultContactClickListener(dependencyRepository), deleteThreadsViewCallback,
                SingletonManager.getStatReporter(getActivity()), SingletonManager.getMessagesLoader(getActivity()));

        TextFormatter textFormatter = new TextFormatter(getActivity());
        ConversationsRecyclerBinder binder = new ConversationsRecyclerBinder(dependencyRepository.getDraftRepository(), getResources(), SingletonManager.getPhotoLoader(getActivity()), textFormatter, this, new CheckedStateProvider() {
            @Override
            public boolean isChecked(Conversation item) {
                if (checkedItems.containsKey(item.getThreadId())) {
                    return checkedItems.get(item.getThreadId());
                }
                return false;
            }
        }, dependencyRepository.getPreferenceStore());
//        ConversationsBinder binder = new ConversationsBinder(getActivity(), textFormatter, getResources(), SingletonManager.getPhotoLoader(getActivity()), dependencyRepository.getDraftRepository(), dependencyRepository.getPreferenceStore(),
//                new ModalBridge(listener, this, getListView()));
        adapter = new RecyclerSourceBinderAdapter<>(source, binder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
//        SourceBinderAdapter adapter = new SourceBinderAdapter<>(getActivity(), source, binder);
//        setListAdapter(adapter);
//        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        getListView().setDivider(null);
//        getListView().setOnItemClickListener(new NotifyControllerClickListener());
//        getListView().setMultiChoiceModeListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        conversationController.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationController.stop();
    }

    @Override
    public void showLoadingUi() {
        recyclerView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setConversationSelectedListener(ConversationSelectedListener conversationSelectedListener) {
        this.conversationSelectedListener = conversationSelectedListener;
    }

    @Override
    public void showEmptyUi() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyUi() {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoadingUi() {
        if (getView() != null) {
            recyclerView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void selectedPosition(int position) {
        conversationSelectedListener.selectedPosition(position);
    }

    @Override
    public void secondarySelected(int position) {
        Conversation item = source.getAtPosition(position);
        if (!checkedItems.containsKey(item.getThreadId())) {
            checkedItems.put(item.getThreadId(), true);
        } else {
            checkedItems.remove(item.getThreadId());
        }
        adapter.notifyItemChanged(position);
    }

    private class NotifyControllerClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            conversationSelectedListener.selectedPosition(position);
        }
    }
}
