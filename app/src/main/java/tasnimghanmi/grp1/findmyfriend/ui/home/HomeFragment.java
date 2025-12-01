package tasnimghanmi.grp1.findmyfriend.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tasnimghanmi.grp1.findmyfriend.Config;
import tasnimghanmi.grp1.findmyfriend.JSONParser;
import tasnimghanmi.grp1.findmyfriend.Position;
import tasnimghanmi.grp1.findmyfriend.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private final ArrayList<Position> data = new ArrayList<>();
    private PositionAdapter adapter; // RecyclerView adapter

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        adapter = new PositionAdapter(data, this::openMap);
        binding.recyclerLocations.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerLocations.setAdapter(adapter);

        binding.btndownload.setOnClickListener(v -> {
            data.clear(); // IMPORTANT: Avoid duplicated items
            new Download().execute();
        });

        return root;
    }

    /** When user clicks a row → open Google Maps */
    private void openMap(Position p) {
        String uri = "geo:" + p.getLatitude() + "," + p.getLongitude() +
                "?q=" + p.getLatitude() + "," + p.getLongitude() +
                "(" + p.getPseudo() + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    /** AsyncTask Download */
    class Download extends AsyncTask<Void, Void, Void> {

        AlertDialog alert;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Downloading...");
            dialog.setMessage("Please wait...");
            alert = dialog.create();
            alert.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            JSONParser parser = new JSONParser();
            JSONObject response = parser.makeRequest(Config.URL_get_all_location);
            Log.e("response", response.toString());

            try {
                int success = response.getInt("success");

                if (success == 1) {
                    JSONArray tableau = response.getJSONArray("positions");

                    for (int i = 0; i < tableau.length(); i++) {
                        JSONObject ligne = tableau.getJSONObject(i);

                        Position pos = new Position(
                                ligne.getInt("idposition"),
                                ligne.getString("pseudo"),
                                ligne.getString("numero"),
                                ligne.getString("longitude"),
                                ligne.getString("latitude")
                        );

                        data.add(pos);
                    }
                }

                Thread.sleep(500);

            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            adapter.notifyDataSetChanged(); // Refresh RecyclerView
            alert.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
