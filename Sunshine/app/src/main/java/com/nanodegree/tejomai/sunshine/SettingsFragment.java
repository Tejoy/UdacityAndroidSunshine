package com.nanodegree.tejomai.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import java.util.prefs.PreferenceChangeEvent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        addPreferencesFromResource(R.xml.preferences);

        ListPreference listPreference = (ListPreference) findPreference(SunshineUtil.KEY_PREF_UNITS);
        EditTextPreference editTextPref = (EditTextPreference) findPreference(SunshineUtil.KEY_PREF_LOCATION);

        listPreference.setOnPreferenceChangeListener(this);
        editTextPref.setOnPreferenceChangeListener(this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SunshineUtil.SHARED_PREF_NAME,Context.MODE_PRIVATE);
        String loc = sharedPreferences.getString(SunshineUtil.KEY_PREF_LOCATION,"");
        String unit = sharedPreferences.getString(SunshineUtil.KEY_PREF_UNITS,"");
        if(loc.length()>0)
        findPreference(SunshineUtil.KEY_PREF_LOCATION).setSummary(loc);
        if(unit.length()>0)
        findPreference(SunshineUtil.KEY_PREF_UNITS).setSummary(unit);

    }



    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    public void preferenceChange(PreferenceChangeEvent preferenceChangeEvent){
        String key = preferenceChangeEvent.getKey();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SunshineUtil.SHARED_PREF_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,preferenceChangeEvent.getNewValue()).commit();

    }


    public boolean onPreferenceChange(Preference preference, Object o) {
        String value = o.toString();
        String key = preference.getKey();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SunshineUtil.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        Preference pref = findPreference(key);
        if(pref instanceof ListPreference){
            ListPreference listPref = (ListPreference) pref;
            int index = listPref.findIndexOfValue(value);
            listPref.setSummary((listPref.getEntries())[index]);
            sharedPreferences.edit().putString(key,listPref.getSummary().toString()).commit();
        }else if(pref instanceof EditTextPreference){
            EditTextPreference editTextPref = (EditTextPreference) pref;
            editTextPref.setSummary(value);
            sharedPreferences.edit().putString(key,editTextPref.getSummary().toString()).commit();
        }
        return true;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
