package com.example.kiosk;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kiosk.databinding.FragmentThirdBinding;

import java.util.ArrayList;
import java.util.List;

public class ThirdFrament extends Fragment {

    private FragmentThirdBinding binding;
    private ListView table;
    private List<String> mList;
    private ArrayAdapter<String> mAdapter;
    private ConexionSQLiteHelper conn;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ThirdFrament.this)
                        .navigate(R.id.action_thirdFrament_to_FirstFragment);
            }
        });
        table = view.findViewById(R.id.table);
        mList = new ArrayList<>();
        conn = new ConexionSQLiteHelper(getContext());

        Cursor cursor = conn.getAllProducts();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            mList.add(cursor.getString(1) + " " +cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4) + " " + cursor.getString(5) + " " + cursor.getString(6));
            mAdapter = new ArrayAdapter<>(getContext(), R.layout.text_view_layout, mList);
            table.setAdapter(mAdapter);
        }
    }

}