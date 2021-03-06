package com.example.qwerty.vok_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiGetMessagesResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.dialogs.VKShareDialog;
import com.vk.sdk.util.VKUtil;

import java.util.ArrayList;


/**
 * Created by qwerty on 31.10.16.
 */

public class DialogFragment extends Fragment {
    private final static String TAG = MainActivity.class.getSimpleName();
    private String[] scope = new String[]{VKScope.MESSAGES, VKScope.FRIENDS, VKScope.WALL};
    protected View tView;
    private ListView listView;
    private VKList listFriends;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "ggggggggg");
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        tView = view;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VKSdk.login(getActivity(), scope);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "gohhj");
        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                listFriends = (VKList) response.parsedModel;
                VKRequest request1 = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 10));
                request1.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        listView = (ListView) tView.findViewById(R.id.list);
                        VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                        VKList<VKApiDialog> list = getMessagesResponse.items;

                        ArrayList<String> messages = new ArrayList<>();
                        ArrayList<String> users = new ArrayList<String>();

                        for (VKApiDialog msg : list) {
                            users.add(String.valueOf(listFriends.getById(msg.message.user_id)));
                            messages.add(msg.message.body);
                        }
                        listView.setAdapter(new VkDiagAdapter(getActivity(), users, messages, list));
                    }
                });
            }
        });
    }
}
